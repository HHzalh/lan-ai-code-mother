package com.lanhai.lanaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.lanhai.lanaicodemother.exception.ErrorCode;
import com.lanhai.lanaicodemother.exception.ThrowUtils;
import com.lanhai.lanaicodemother.mapper.PointSignInRecordMapper;
import com.lanhai.lanaicodemother.mapper.UserAccountMapper;
import com.lanhai.lanaicodemother.model.dto.point.PointSignInResponse;
import com.lanhai.lanaicodemother.model.entity.PointSignInRecord;
import com.lanhai.lanaicodemother.model.entity.UserAccount;
import com.lanhai.lanaicodemother.model.enums.PointBusinessTypeEnum;
import com.lanhai.lanaicodemother.model.vo.point.PointSignInRecordVO;
import com.lanhai.lanaicodemother.service.PointService;
import com.lanhai.lanaicodemother.service.PointSignInService;
import com.lanhai.lanaicodemother.service.UserAccountService;
import com.lanhai.lanaicodemother.utils.RedisDistributedLock;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 签到 服务层实现。
 *
 * @author 积分系统
 */
@Slf4j
@Service
public class PointSignInServiceImpl implements PointSignInService {

    /**
     * 签到锁Key前缀
     */
    private static final String SIGN_IN_LOCK_PREFIX = "point:sign:in:";
    @Resource
    private UserAccountService userAccountService;
    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private PointSignInRecordMapper signInRecordMapper;
    @Resource
    private PointService pointService;
    @Resource
    private RedisDistributedLock redisDistributedLock;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PointSignInResponse signIn(Long userId) {
        // 使用分布式锁防止重复签到
        String lockKey = SIGN_IN_LOCK_PREFIX + userId;
        return redisDistributedLock.executeWithLock(lockKey, 3, 10, () -> doSignIn(userId));
    }

    /**
     * 执行签到逻辑
     */
    private PointSignInResponse doSignIn(Long userId) {
        // 1. 检查今日是否已签到
        if (getTodaySignInStatus(userId)) {
            throw new RuntimeException("今日已签到");
        }

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

        // 4. 计算应得积分
        Long points = pointService.calculateSignInPoints(userId);

        // 5. 更新签到状态（使用乐观锁，在增加积分之前）
        UserAccount updateAccount = new UserAccount();
        updateAccount.setId(account.getId());
        updateAccount.setVersion(account.getVersion());

        int updated = userAccountMapper.updateSignInStatusWithVersion(updateAccount, continuousDays);
        ThrowUtils.throwIf(updated == 0, ErrorCode.OPERATION_ERROR, "签到失败，请重试");

        // 6. 增加积分
        userAccountService.addPoints(userId, points, PointBusinessTypeEnum.SIGN_IN.getValue(), null, "签到获得积分");

        // 7. 记录签到记录
        PointSignInRecord record = new PointSignInRecord();
        record.setUserId(userId);
        record.setSignDate(today);
        record.setDaysCount(continuousDays);
        record.setPoints(points);
        record.setIsBonus(0);
        record.setCreateTime(LocalDateTime.now());
        int saved = signInRecordMapper.insert(record);
        ThrowUtils.throwIf(saved <= 0, ErrorCode.SYSTEM_ERROR, "记录签到失败");

        log.info("用户签到成功，用户ID：{}，连续天数：{}，获得积分：{}", userId, continuousDays, points);

        // 8. 构建响应
        return PointSignInResponse.builder()
                .points(points)
                .continuousDays(continuousDays)
                .isBonus(points > 10)
                .availablePoints(account.getAvailablePoints() + points)
                .build();
    }

    @Override
    public Boolean getTodaySignInStatus(Long userId) {
        LocalDate today = LocalDate.now();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("sign_date", today);
        return signInRecordMapper.selectCountByQuery(queryWrapper) > 0;
    }

    @Override
    public List<PointSignInRecordVO> getSignInCalendar(Long userId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        if (startDate != null) {
            queryWrapper.ge("sign_date", startDate);
        }
        if (endDate != null) {
            queryWrapper.le("sign_date", endDate);
        }
        queryWrapper.orderBy("sign_date", false);

        List<PointSignInRecord> records = signInRecordMapper.selectListByQuery(queryWrapper);
        return records.stream()
                .map(record -> {
                    PointSignInRecordVO vo = new PointSignInRecordVO();
                    BeanUtil.copyProperties(record, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

}

