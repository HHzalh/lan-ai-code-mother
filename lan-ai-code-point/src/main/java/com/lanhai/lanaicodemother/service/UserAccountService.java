package com.lanhai.lanaicodemother.service;

import com.lanhai.lanaicodemother.model.dto.point.UserAccountQueryRequest;
import com.lanhai.lanaicodemother.model.entity.UserAccount;
import com.lanhai.lanaicodemother.model.vo.point.UserAccountVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

/**
 * 用户积分账户 服务层。
 *
 * @author 积分系统
 */
public interface UserAccountService extends IService<UserAccount> {

    /**
     * 根据用户ID获取积分账户
     *
     * @param userId 用户ID
     * @return 积分账户
     */
    UserAccount getByUserId(Long userId);

    /**
     * 根据用户ID获取积分账户VO
     *
     * @param userId 用户ID
     * @return 积分账户VO
     */
    UserAccountVO getAccountVO(Long userId);

    /**
     * 分页查询积分账户（管理员）
     *
     * @param queryRequest 查询请求
     * @return 分页结果
     */
    Page<UserAccountVO> pageAccounts(UserAccountQueryRequest queryRequest);

    /**
     * 创建或获取用户的积分账户（如果不存在则创建）
     *
     * @param userId 用户ID
     * @return 积分账户
     */
    UserAccount getOrCreateAccount(Long userId);

    /**
     * 获取或创建邀请码
     *
     * @param userId 用户ID
     * @return 邀请码
     */
    String getOrCreateInvitationCode(Long userId);

    /**
     * 增加积分（乐观锁）
     *
     * @param userId       用户ID
     * @param points       增加的积分数
     * @param businessType 业务类型
     * @param businessId   业务ID
     * @param remark       备注
     * @return 是否成功
     */
    boolean addPoints(Long userId, Long points, String businessType, String businessId, String remark);

    /**
     * 扣减积分（乐观锁）
     *
     * @param userId       用户ID
     * @param points       扣减的积分数
     * @param businessType 业务类型
     * @param businessId   业务ID
     * @param remark       备注
     * @return 是否成功
     */
    boolean deductPoints(Long userId, Long points, String businessType, String businessId, String remark);

}


