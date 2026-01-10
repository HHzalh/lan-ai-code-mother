package com.lanhai.lanaicodemother.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 积分规则键枚举
 *
 * @author 积分系统
 */
@Getter
@AllArgsConstructor
public enum PointRuleKeyEnum {

    /**
     * 签到基础积分
     */
    SIGN_IN_BASE("SIGN_IN_BASE", 10L, "每日签到基础积分"),

    /**
     * 连续3天额外奖励
     */
    SIGN_IN_CONTINUOUS_3("SIGN_IN_CONTINUOUS_3", 10L, "连续3天额外奖励"),

    /**
     * 连续7天额外奖励
     */
    SIGN_IN_CONTINUOUS_7("SIGN_IN_CONTINUOUS_7", 50L, "连续7天额外奖励"),


    /**
     * 注册奖励（所有新用户注册的基础奖励）
     */
    REGISTER_REWARD("REGISTER_REWARD", 100L, "注册奖励"),

    /**
     * 被邀请人奖励（通过邀请码注册的额外奖励）
     */
    INVITE_NEW("INVITE_NEW", 50L, "被邀请人注册奖励"),

    /**
     * 邀请人奖励（邀请人获得的奖励）
     */
    INVITE_REWARD("INVITE_REWARD", 30L, "邀请人奖励"),

    /**
     * 部署应用消耗积分
     */
    DEPLOY_COST("DEPLOY_COST", 30L, "部署应用消耗积分"),

    /**
     * 生成应用消耗积分
     */
    GENERATE_COST("GENERATE_COST", 20L, "生成应用消耗积分");


    /**
     * 规则键
     */
    private final String key;

    /**
     * 默认规则值
     */
    private final Long defaultValue;

    /**
     * 规则描述
     */
    private final String desc;

}

