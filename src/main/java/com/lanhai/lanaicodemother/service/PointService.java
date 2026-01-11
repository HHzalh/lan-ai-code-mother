package com.lanhai.lanaicodemother.service;

/**
 * 积分系统综合服务层。
 * 提供积分消耗、邀请码处理等综合业务
 *
 * @author 积分系统
 */
public interface PointService {

    /**
     * 处理用户注册时的邀请码
     *
     * @param userId         用户ID
     * @param invitationCode 邀请码
     */
    void handleInvitationCode(Long userId, String invitationCode);

    /**
     * 计算签到积分
     *
     * @param userId 用户ID
     * @return 应得积分数
     */
    Long calculateSignInPoints(Long userId);

    /**
     * 根据连续签到天数计算应得积分
     *
     * @param continuousDays 连续签到天数
     * @return 应得积分数
     */
    Long calculateSignInPointsByDays(Integer continuousDays);

    /**
     * 管理员给用户发放积分
     *
     * @param userId 用户ID
     * @param points 发放积分数
     * @param remark 备注
     * @return 是否成功
     */
    boolean grantPoints(Long userId, Long points, String remark);

    /**
     * 管理员给所有用户发放积分
     *
     * @param points 发放积分数
     * @param remark 备注
     * @return 发放的用户数量
     */
    int grantPointsToAll(Long points, String remark);

}

