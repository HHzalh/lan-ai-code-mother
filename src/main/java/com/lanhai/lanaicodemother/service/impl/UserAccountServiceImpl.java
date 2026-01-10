package com.lanhai.lanaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.lanhai.lanaicodemother.exception.ErrorCode;
import com.lanhai.lanaicodemother.exception.ThrowUtils;
import com.lanhai.lanaicodemother.mapper.PointLogMapper;
import com.lanhai.lanaicodemother.mapper.UserAccountMapper;
import com.lanhai.lanaicodemother.model.dto.point.UserAccountQueryRequest;
import com.lanhai.lanaicodemother.model.entity.UserAccount;
import com.lanhai.lanaicodemother.model.enums.PointTypeEnum;
import com.lanhai.lanaicodemother.model.vo.point.UserAccountVO;
import com.lanhai.lanaicodemother.service.PointLogService;
import com.lanhai.lanaicodemother.service.UserAccountService;
import com.lanhai.lanaicodemother.utils.InvitationCodeUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户积分账户 服务层实现。
 *
 * @author 积分系统
 */
@Slf4j
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Resource
    private PointLogService pointLogService;

    @Resource
    private PointLogMapper pointLogMapper;

    @Override
    public UserAccount getByUserId(Long userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        return this.mapper.selectOneByQuery(queryWrapper);
    }

    @Override
    public UserAccountVO getAccountVO(Long userId) {
        UserAccount account = getByUserId(userId);
        if (account == null) {
            return null;
        }
        UserAccountVO vo = new UserAccountVO();
        BeanUtil.copyProperties(account, vo);
        return vo;
    }

    @Override
    public Page<UserAccountVO> pageAccounts(UserAccountQueryRequest queryRequest) {
        QueryWrapper queryWrapper = new QueryWrapper();

        // 用户ID
        if (queryRequest.getUserId() != null) {
            queryWrapper.eq("user_id", queryRequest.getUserId());
        }

        // 邀请码
        if (StringUtils.isNotBlank(queryRequest.getInvitationCode())) {
            queryWrapper.eq("invitation_code", queryRequest.getInvitationCode());
        }

        // 可用积分范围
        if (queryRequest.getMinAvailablePoints() != null) {
            queryWrapper.ge("available_points", queryRequest.getMinAvailablePoints());
        }
        if (queryRequest.getMaxAvailablePoints() != null) {
            queryWrapper.le("available_points", queryRequest.getMaxAvailablePoints());
        }

        // 累计积分范围
        if (queryRequest.getMinTotalPoints() != null) {
            queryWrapper.ge("total_points", queryRequest.getMinTotalPoints());
        }
        if (queryRequest.getMaxTotalPoints() != null) {
            queryWrapper.le("total_points", queryRequest.getMaxTotalPoints());
        }

        // 按创建时间倒序
        queryWrapper.orderBy("createTime", false);

        // 分页查询
        int pageSize = queryRequest != null ? queryRequest.getPageSize() : 10;
        int pageNum = queryRequest != null ? queryRequest.getPageNum() : 1;
        Page<UserAccount> accountPage = this.mapper.paginate(pageNum, pageSize, queryWrapper);


        // 转换为VO
        Page<UserAccountVO> voPage = new Page<>();
        voPage.setPageNumber(accountPage.getPageNumber());
        voPage.setPageSize(accountPage.getPageSize());
        voPage.setTotalRow(accountPage.getTotalRow());
        voPage.setTotalPage(accountPage.getTotalPage());
        voPage.setRecords(accountPage.getRecords().stream()
                .map(account -> {
                    UserAccountVO vo = new UserAccountVO();
                    BeanUtil.copyProperties(account, vo);
                    return vo;
                })
                .toList());

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAccount getOrCreateAccount(Long userId) {
        UserAccount account = getByUserId(userId);
        if (account != null) {
            return account;
        }

        // 创建新账户
        account = new UserAccount();
        account.setUserId(userId);
        account.setInvitationCode(generateUniqueInvitationCode());
        account.setTotalPoints(0L);
        account.setAvailablePoints(0L);
        account.setFreezePoints(0L);
        account.setTotalConsume(0L);
        account.setContinuousDays(0);
        account.setInvitationCount(0);
        account.setTotalInvitePoints(0L);
        account.setVersion(0);
        account.setCreateTime(java.time.LocalDateTime.now());
        account.setIsDelete(0);
        boolean saved = this.save(account);
        ThrowUtils.throwIf(!saved, ErrorCode.SYSTEM_ERROR, "创建积分账户失败");
        log.info("创建积分账户成功，用户ID：{}，邀请码：{}", userId, account.getInvitationCode());
        return account;
    }

    @Override
    public String getOrCreateInvitationCode(Long userId) {
        UserAccount account = getOrCreateAccount(userId);
        return account.getInvitationCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPoints(Long userId, Long points, String businessType, String businessId, String remark) {
        // 1. 查询当前账户
        UserAccount account = getByUserId(userId);
        ThrowUtils.throwIf(account == null, ErrorCode.NOT_FOUND_ERROR, "积分账户不存在");

        // 2. 乐观锁增加积分
        UserAccount updateAccount = new UserAccount();
        updateAccount.setId(account.getId());
        updateAccount.setVersion(account.getVersion());

        int updated = this.mapper.addPointsWithVersion(updateAccount, points);
        ThrowUtils.throwIf(updated == 0, ErrorCode.TOO_MANY_REQUEST, "操作过于频繁，请重试");

        // 3. 查询更新后的账户
        UserAccount updatedAccount = this.getById(account.getId());

        // 4. 记录流水
        pointLogService.recordLog(
                userId,
                businessType,
                businessId,
                PointTypeEnum.INCOME.getValue(),
                points,
                account.getAvailablePoints(),
                updatedAccount.getAvailablePoints(),
                remark
        );

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductPoints(Long userId, Long points, String businessType, String businessId, String remark) {
        // 1. 查询当前账户
        UserAccount account = getByUserId(userId);
        ThrowUtils.throwIf(account == null, ErrorCode.NOT_FOUND_ERROR, "积分账户不存在");
        ThrowUtils.throwIf(account.getAvailablePoints() < points, ErrorCode.OPERATION_ERROR, "积分不足");

        // 2. 乐观锁扣减积分
        UserAccount updateAccount = new UserAccount();
        updateAccount.setId(account.getId());
        updateAccount.setVersion(account.getVersion());

        int updated = this.mapper.deductPointsWithVersion(updateAccount, points);
        ThrowUtils.throwIf(updated == 0, ErrorCode.TOO_MANY_REQUEST, "操作过于频繁，请重试");

        // 3. 查询更新后的账户
        UserAccount updatedAccount = this.getById(account.getId());

        // 4. 记录流水
        pointLogService.recordLog(
                userId,
                businessType,
                businessId,
                PointTypeEnum.EXPENSE.getValue(),
                -points,
                account.getAvailablePoints(),
                updatedAccount.getAvailablePoints(),
                remark
        );

        return true;
    }

    /**
     * 生成唯一邀请码
     */
    private String generateUniqueInvitationCode() {
        int maxRetries = 10;
        for (int i = 0; i < maxRetries; i++) {
            String code = InvitationCodeUtils.generateCode();
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("invitation_code", code);
            long count = this.mapper.selectCountByQuery(queryWrapper);
            if (count == 0) {
                return code;
            }
        }
        ThrowUtils.throwIf(true, ErrorCode.SYSTEM_ERROR, "生成邀请码失败，请重试");
        return null;
    }

}

