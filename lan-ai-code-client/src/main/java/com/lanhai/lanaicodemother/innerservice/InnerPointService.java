package com.lanhai.lanaicodemother.innerservice;

import com.lanhai.lanaicodemother.model.entity.UserAccount;

/**
 * 内部积分服务
 */
public interface InnerPointService {

    /**
     * 扣减积分
     *
     * @param userId       用户ID
     * @param points       扣减积分数（必须大于0）
     * @param businessType 业务类型（如：AI_MESSAGE、APP_DEPLOY、CODE_DOWNLOAD）
     * @param businessId   业务ID（如：appId、orderId）
     * @param remark       备注说明
     * @return 是否成功
     */
    boolean deductPoints(Long userId, Long points, String businessType, String businessId, String remark);

    /**
     * 增加积分
     *
     * @param userId       用户ID
     * @param points       增加积分数（必须大于0）
     * @param businessType 业务类型（如：SIGN_IN、INVITE、REGISTER）
     * @param businessId   业务ID（可选，部分业务类型不需要）
     * @param remark       备注说明（必填）
     * @return 是否成功
     */
    boolean addPoints(Long userId, Long points, String businessType, String businessId, String remark);

    /**
     * 获取用户积分账户
     *
     * @param userId 用户ID
     * @return 积分账户信息，不存在返回 null
     */
    UserAccount getUserAccount(Long userId);

    /**
     * 检查积分是否充足
     *
     * @param userId 用户ID
     * @param points 所需积分
     * @return 是否充足
     */
    boolean checkPointsSufficient(Long userId, Long points);
}