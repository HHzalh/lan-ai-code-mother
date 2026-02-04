package com.lanhai.lanaicodemother.service.impl;

import com.lanhai.lanaicodemother.constant.PointConstants;
import com.lanhai.lanaicodemother.exception.ErrorCode;
import com.lanhai.lanaicodemother.exception.ThrowUtils;
import com.lanhai.lanaicodemother.mapper.PointSignInRecordMapper;
import com.lanhai.lanaicodemother.mapper.UserAccountMapper;
import com.lanhai.lanaicodemother.model.dto.point.PointSignInResponse;
import com.lanhai.lanaicodemother.model.entity.PointSignInRecord;
import com.lanhai.lanaicodemother.model.entity.UserAccount;
import com.lanhai.lanaicodemother.model.enums.PointBusinessTypeEnum;
import com.lanhai.lanaicodemother.model.enums.PointRuleKeyEnum;
import com.lanhai.lanaicodemother.service.PointService;
import com.lanhai.lanaicodemother.service.PointSignInService;
import com.lanhai.lanaicodemother.service.UserAccountService;
import com.lanhai.lanaicodemother.utils.RedisDistributedLockUtils;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.lanhai.lanaicodemother.constant.PointConstants.*;

/**
 * 签到 服务层实现。
 *
 * @author 积分系统
 */
@Slf4j
@Service
public class PointSignInServiceImpl implements PointSignInService {

    @Resource
    private UserAccountService userAccountService;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private PointSignInRecordMapper signInRecordMapper;

    @Resource
    private PointService pointService;

    @Resource
    private RedisDistributedLockUtils redisDistributedLockUtils;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private PointRuleServiceImpl pointRuleServiceImpl;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PointSignInResponse signIn(Long userId) {
        // 使用分布式锁防止重复签到
        String lockKey = PointConstants.SIGN_IN_LOCK_PREFIX + userId;
        return redisDistributedLockUtils.executeWithLock(lockKey, LOCK_WAIT_SECONDS, SIGN_IN_LOCK_EXPIRE_SECONDS, () -> doSignIn(userId));
    }

    /**
     * 执行签到逻辑
     */
    private PointSignInResponse doSignIn(Long userId) {
        // 1. 检查今日是否已签到
        ThrowUtils.throwIf(getTodaySignInStatus(userId), ErrorCode.OPERATION_ERROR, "今日已签到");

        // 2. 获取用户账户
        UserAccount account = userAccountService.getByUserId(userId);
        ThrowUtils.throwIf(account == null, ErrorCode.NOT_FOUND_ERROR, "积分账户不存在");

        // 3. 计算连续天数
        LocalDate today = LocalDate.now();
        LocalDate lastSignDate = account.getLastSignDate();
        Integer continuousDays = 1; // 默认为1天

        if (lastSignDate != null) {
            LocalDate yesterday = today.minusDays(1);
            if (lastSignDate.equals(yesterday)) {
                // 连续签到
                continuousDays = account.getContinuousDays() + 1;
            } else {
                // 断签了，重置为1
                continuousDays = 1;
            }
        }

        // 4. 计算应得积分（使用更新后的连续天数）
        Long points = pointService.calculateSignInPointsByDays(continuousDays);

        // 5. 更新签到状态（使用乐观锁，在增加积分之前）
        UserAccount updateAccount = new UserAccount();
        updateAccount.setId(account.getId());
        updateAccount.setVersion(account.getVersion());

        int updated = userAccountMapper.updateSignInStatusWithVersion(updateAccount, continuousDays);
        ThrowUtils.throwIf(updated == 0, ErrorCode.OPERATION_ERROR, "签到失败，请重试");

        // 6. 增加积分（一次性获取规则值，避免重复查询）
        Long basePoints = pointRuleServiceImpl.getRuleValue(PointRuleKeyEnum.SIGN_IN_BASE);

        // 6.1 发放基础签到积分（每天都有）
        userAccountService.addPoints(userId, basePoints,
                PointBusinessTypeEnum.SIGN_IN.getValue(), null, "签到获得积分");

        // 6.2 发放连续签到额外奖励（只在第3天和第7天）
        if (points > basePoints) {
            Long bonusPoints = points - basePoints;
            String bonusDesc = "连续签到" + continuousDays + "天额外获得积分";
            userAccountService.addPoints(userId, bonusPoints,
                    PointBusinessTypeEnum.SIGN_IN.getValue(), null, bonusDesc);
        }

        // 7. 记录签到记录
        PointSignInRecord record = PointSignInRecord.builder()
                .userId(userId)
                .signDate(today)
                .daysCount(continuousDays)
                .points(points)
                .isBonus(0)
                .createTime(LocalDateTime.now())
                .build();

        int saved = signInRecordMapper.insert(record);
        ThrowUtils.throwIf(saved <= 0, ErrorCode.SYSTEM_ERROR, "记录签到失败");

        // 8. 写入签到状态缓存
        String cacheKey = SIGN_IN_STATUS_CACHE_PREFIX + userId + ":" + today;
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        long ttlSeconds = java.time.Duration.between(
                LocalDateTime.now(),
                endOfDay
        ).getSeconds();

        if (ttlSeconds > 0) {
            stringRedisTemplate.opsForValue().set(
                    cacheKey,
                    "true",
                    ttlSeconds,
                    java.util.concurrent.TimeUnit.SECONDS
            );
            log.debug("写入签到状态缓存，userId：{}，过期时间：{}秒", userId, ttlSeconds);
        }

        log.info("用户签到成功，用户ID：{}，连续天数：{}，获得积分：{}", userId, continuousDays, points);

        // 9. 构建响应
        return PointSignInResponse.builder()
                .points(points)
                .continuousDays(continuousDays)
                .isBonus(points > 10)
                .availablePoints(account.getAvailablePoints() + points)
                .build();
    }

    @Override
    public Boolean getTodaySignInStatus(Long userId) {
        // 1. 计算缓存Key和过期时间
        LocalDate today = LocalDate.now();
        String cacheKey = SIGN_IN_STATUS_CACHE_PREFIX + userId + ":" + today;
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        long ttlSeconds = java.time.Duration.between(
                LocalDateTime.now(),
                endOfDay
        ).getSeconds();

        // 2. 尝试从Redis获取
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            boolean hasSigned = Boolean.parseBoolean(cached);
            log.debug("从缓存获取签到状态，userId：{}，已签到：{}", userId, hasSigned);
            return hasSigned;
        }

        // 3. 从数据库查询
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("sign_date", today);
        boolean hasSigned = signInRecordMapper.selectCountByQuery(queryWrapper) > 0;

        // 4. 写入Redis缓存（过期到今天23:59:59）
        if (ttlSeconds > 0) {
            stringRedisTemplate.opsForValue().set(
                    cacheKey,
                    String.valueOf(hasSigned),
                    ttlSeconds,
                    java.util.concurrent.TimeUnit.SECONDS
            );
            log.debug("写入签到状态缓存，userId：{}，已签到：{}，过期时间：{}秒", userId, hasSigned, ttlSeconds);
        }

        return hasSigned;
    }

}

