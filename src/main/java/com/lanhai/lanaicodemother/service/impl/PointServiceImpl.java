package com.lanhai.lanaicodemother.service.impl;


import cn.hutool.core.util.StrUtil;
import com.lanhai.lanaicodemother.constant.PointConstants;
import com.lanhai.lanaicodemother.exception.ErrorCode;
import com.lanhai.lanaicodemother.exception.ThrowUtils;
import com.lanhai.lanaicodemother.mapper.PointLogMapper;
import com.lanhai.lanaicodemother.mapper.UserAccountMapper;
import com.lanhai.lanaicodemother.model.entity.UserAccount;
import com.lanhai.lanaicodemother.model.enums.PointBusinessTypeEnum;
import com.lanhai.lanaicodemother.model.enums.PointRuleKeyEnum;
import com.lanhai.lanaicodemother.service.PointService;
import com.lanhai.lanaicodemother.service.UserAccountService;
import com.lanhai.lanaicodemother.utils.RedisDistributedLock;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 积分系统综合服务层实现。
 *
 * @author 积分系统
 */
@Slf4j
@Service
public class PointServiceImpl implements PointService {

    /**
     * 邀请奖励锁前缀
     */
    private static final String INVITE_LOCK_PREFIX = "point:invite:";
    @Resource
    private UserAccountService userAccountService;
    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private PointLogMapper pointLogMapper;
    @Resource
    private RedisDistributedLock redisDistributedLock;

    @Resource
    private com.lanhai.lanaicodemother.service.PointRuleService pointRuleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleInvitationCode(Long userId, String invitationCode) {
        if (StrUtil.isBlank(invitationCode)) {
            return;
        }

        // 1. 查询邀请人账户
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("invitation_code", invitationCode);
        UserAccount inviterAccount = userAccountMapper.selectOneByQuery(queryWrapper);
        ThrowUtils.throwIf(inviterAccount == null, ErrorCode.PARAMS_ERROR, "邀请码无效");

        // 2. 使用分布式锁处理邀请逻辑
        String lockKey = INVITE_LOCK_PREFIX + invitationCode + ":" + userId;
        redisDistributedLock.executeWithLock(lockKey, PointConstants.LOCK_WAIT_SECONDS,
                PointConstants.INVITE_LOCK_EXPIRE_SECONDS, () -> doHandleInvitation(userId, inviterAccount, invitationCode));
    }

    /**
     * 执行邀请逻辑
     */
    private void doHandleInvitation(Long userId, UserAccount inviterAccount, String invitationCode) {
        Long inviterId = inviterAccount.getUserId();

        // 1. 检查是否已经处理过（查询流水记录）
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("business_type", PointBusinessTypeEnum.INVITEE_BONUS.getValue());
        queryWrapper.eq("business_id", invitationCode);
        long count = pointLogMapper.selectCountByQuery(queryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.OPERATION_ERROR, "该邀请码已使用");

        // 2. 先更新邀请人邀请统计（使用乐观锁，在增加积分之前）
        UserAccount updateInviter = new UserAccount();
        updateInviter.setId(inviterAccount.getId());
        updateInviter.setVersion(inviterAccount.getVersion());

        Long inviterPoints = pointRuleService.getRuleValue(PointRuleKeyEnum.INVITE_REWARD);
        int updated = userAccountMapper.updateInviteStatsWithVersion(updateInviter, inviterPoints);
        ThrowUtils.throwIf(updated == 0, ErrorCode.OPERATION_ERROR, "更新邀请统计失败");

        // 3. 给被邀请人发放积分（通过邀请码注册的额外奖励）
        Long inviteePoints = pointRuleService.getRuleValue(PointRuleKeyEnum.INVITE_NEW);
        userAccountService.addPoints(userId, inviteePoints, PointBusinessTypeEnum.INVITEE_BONUS.getValue(), invitationCode, "被邀请人注册奖励");

        // 4. 给邀请人发放积分
        userAccountService.addPoints(inviterId, inviterPoints, PointBusinessTypeEnum.INVITER_BONUS.getValue(), String.valueOf(userId), "邀请人奖励");

        log.info("邀请奖励发放成功，被邀请人ID：{}，邀请人ID：{}，被邀请人获得积分：{}，邀请人获得积分：{}",
                userId, inviterId, inviteePoints, inviterPoints);
    }


    @Override
    public Long calculateSignInPoints(Long userId) {
        // 1. 获取签到基础积分
        Long basePoints = pointRuleService.getRuleValue(PointRuleKeyEnum.SIGN_IN_BASE);

        // 2. 获取用户连续天数
        UserAccount account = userAccountService.getByUserId(userId);
        ThrowUtils.throwIf(account == null, ErrorCode.NOT_FOUND_ERROR, "积分账户不存在");
        Integer continuousDays = account.getContinuousDays();

        // 3. 计算连续奖励（固定规则，只可修改不可新增）
        Long bonusPoints = 0L;
        if (continuousDays >= PointConstants.SIGN_IN_BONUS_DAY_3) {
            bonusPoints += pointRuleService.getRuleValue(PointRuleKeyEnum.SIGN_IN_CONTINUOUS_3);
        }
        if (continuousDays >= PointConstants.SIGN_IN_BONUS_DAY_7) {
            bonusPoints += pointRuleService.getRuleValue(PointRuleKeyEnum.SIGN_IN_CONTINUOUS_7);
        }

        return basePoints + bonusPoints;
    }

    /**
     * 根据连续签到天数计算应得积分（使用更新后的天数）
     * 每7天为一个周期，循环发放额外奖励：
     * - 周期第3天：发放3天额外奖励
     * - 周期第7天：发放7天额外奖励
     *
     * @param continuousDays 连续签到天数（更新后的值）
     * @return 应得积分
     */
    public Long calculateSignInPointsByDays(Integer continuousDays) {
        // 1. 获取签到基础积分
        Long basePoints = pointRuleService.getRuleValue(PointRuleKeyEnum.SIGN_IN_BASE);

        // 2. 计算在7天周期中的天数（支持循环奖励）
        int daysInCycle = continuousDays % PointConstants.SIGN_IN_CYCLE_DAYS;
        if (daysInCycle == 0) {
            daysInCycle = PointConstants.SIGN_IN_CYCLE_DAYS; // 如果是7的倍数，说明是周期第7天
        }

        // 3. 计算连续奖励（只在周期第3天和第7天发放额外奖励）
        Long bonusPoints = 0L;
        if (daysInCycle == PointConstants.SIGN_IN_BONUS_DAY_3) {
            // 周期第3天，发放3天额外奖励
            bonusPoints = pointRuleService.getRuleValue(PointRuleKeyEnum.SIGN_IN_CONTINUOUS_3);
            log.info("触发连续签到{}天（周期第3天）额外奖励，额外积分：{}", continuousDays, bonusPoints);
        } else if (daysInCycle == PointConstants.SIGN_IN_BONUS_DAY_7) {
            // 周期第7天，发放7天额外奖励
            bonusPoints = pointRuleService.getRuleValue(PointRuleKeyEnum.SIGN_IN_CONTINUOUS_7);
            log.info("触发连续签到{}天（周期第7天）额外奖励，额外积分：{}", continuousDays, bonusPoints);
        }

        return basePoints + bonusPoints;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean grantPoints(Long userId, Long points, String remark) {
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        ThrowUtils.throwIf(points == null || points <= 0, ErrorCode.PARAMS_ERROR, "积分数必须大于0");
        ThrowUtils.throwIf(StrUtil.isBlank(remark), ErrorCode.PARAMS_ERROR, "备注不能为空");

        // 检查用户账户是否存在，不存在则创建
        UserAccount account = userAccountService.getByUserId(userId);
        if (account == null) {
            log.warn("用户积分账户不存在，自动创建，用户ID：{}", userId);
            account = userAccountService.getOrCreateAccount(userId);
        }

        // 发放积分
        boolean result = userAccountService.addPoints(userId, points,
                PointBusinessTypeEnum.SYSTEM_GRANT.getValue(), null, remark);

        log.info("管理员发放积分成功，用户ID：{}，积分：{}，备注：{}", userId, points, remark);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int grantPointsToAll(Long points, String remark) {
        ThrowUtils.throwIf(points == null || points <= 0, ErrorCode.PARAMS_ERROR, "积分数必须大于0");
        ThrowUtils.throwIf(StrUtil.isBlank(remark), ErrorCode.PARAMS_ERROR, "备注不能为空");

        // 查询所有有效的用户账户
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("isDelete", 0);
        List<UserAccount> accounts = userAccountService.list(queryWrapper);

        ThrowUtils.throwIf(accounts.isEmpty(), ErrorCode.NOT_FOUND_ERROR, "没有找到用户账户");

        int successCount = 0;
        for (UserAccount account : accounts) {
            try {
                userAccountService.addPoints(account.getUserId(), points,
                        PointBusinessTypeEnum.SYSTEM_GRANT.getValue(), null, remark);
                successCount++;
            } catch (Exception e) {
                log.error("给用户发放积分失败，用户ID：{}，错误：{}", account.getUserId(), e.getMessage());
            }
        }

        log.info("管理员批量发放积分完成，总用户数：{}，成功数：{}，积分：{}，备注：{}",
                accounts.size(), successCount, points, remark);
        return successCount;
    }

}

