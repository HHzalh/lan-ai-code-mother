package com.lanhai.lanaicodemother.service.impl;

import cn.hutool.core.util.StrUtil;
import com.lanhai.lanaicodemother.exception.BusinessException;
import com.lanhai.lanaicodemother.exception.ErrorCode;
import com.lanhai.lanaicodemother.innerservice.InnerPointService;
import com.lanhai.lanaicodemother.model.entity.UserAccount;
import com.lanhai.lanaicodemother.service.UserAccountService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 内部积分服务实现
 */
@Slf4j
@DubboService
public class InnerPointServiceImpl implements InnerPointService {

    @Resource
    private UserAccountService userAccountService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductPoints(Long userId, Long points, String businessType, String businessId, String remark) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }
        if (points == null || points <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "扣减积分必须大于0");
        }
        if (StrUtil.isBlank(businessType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "业务类型不能为空");
        }
        if (StrUtil.isBlank(businessId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "业务ID不能为空");
        }

        // 2. 业务校验：检查积分是否充足
        if (!checkPointsSufficient(userId, points)) {
            log.warn("积分扣减失败，积分不足，用户ID：{}，所需积分：{}", userId, points);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "积分不足");
        }

        // 3. 执行扣减
        boolean result = userAccountService.deductPoints(userId, points, businessType, businessId, remark);

        if (result) {
            log.info("Dubbo积分扣减成功，用户ID：{}，业务类型：{}，业务ID：{}，积分：{}，备注：{}",
                    userId, businessType, businessId, points, remark);
        } else {
            log.error("Dubbo积分扣减失败，用户ID：{}，业务类型：{}，业务ID：{}，积分：{}",
                    userId, businessType, businessId, points);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPoints(Long userId, Long points, String businessType, String businessId, String remark) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }
        if (points == null || points <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "增加积分必须大于0");
        }
        if (StrUtil.isBlank(businessType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "业务类型不能为空");
        }
        if (StrUtil.isBlank(remark)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "备注不能为空");
        }

        // 2. 执行增加
        boolean result = userAccountService.addPoints(userId, points, businessType, businessId, remark);

        if (result) {
            log.info("Dubbo积分增加成功，用户ID：{}，业务类型：{}，业务ID：{}，积分：{}，备注：{}",
                    userId, businessType, businessId, points, remark);
        } else {
            log.error("Dubbo积分增加失败，用户ID：{}，业务类型：{}，业务ID：{}，积分：{}",
                    userId, businessType, businessId, points);
        }

        return result;
    }

    @Override
    public UserAccount getUserAccount(Long userId) {
        if (userId == null || userId <= 0) {
            log.warn("用户ID无效，无法获取积分账户，用户ID：{}", userId);
            return null;
        }

        UserAccount account = userAccountService.getByUserId(userId);

        if (account == null) {
            log.warn("积分账户不存在，用户ID：{}", userId);
        }

        return account;
    }

    @Override
    public boolean checkPointsSufficient(Long userId, Long points) {
        if (userId == null || userId <= 0) {
            log.warn("用户ID无效，无法检查积分，用户ID：{}", userId);
            return false;
        }

        if (points == null || points <= 0) {
            log.warn("积分数无效，无法检查积分，用户ID：{}，积分数：{}", userId, points);
            return false;
        }

        UserAccount account = getUserAccount(userId);
        if (account == null) {
            return false;
        }

        boolean sufficient = account.getAvailablePoints() >= points;
        if (!sufficient) {
            log.debug("积分不足，用户ID：{}，当前积分：{}，所需积分：{}",
                    userId, account.getAvailablePoints(), points);
        }

        return sufficient;
    }
}
