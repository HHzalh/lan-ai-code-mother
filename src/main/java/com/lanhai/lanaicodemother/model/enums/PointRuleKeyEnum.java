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
    SIGN_IN_CONTINUOUS_3("SIGN_IN_CONTINUOUS_3", 5L, "连续3天额外奖励"),

    /**
     * 连续7天额外奖励
     */
    SIGN_IN_CONTINUOUS_7("SIGN_IN_CONTINUOUS_7", 10L, "连续7天额外奖励"),

    /**
     * 连续30天额外奖励
     */
    SIGN_IN_CONTINUOUS_30("SIGN_IN_CONTINUOUS_30", 30L, "连续30天额外奖励"),

    /**
     * 新用户通过邀请码注册获得积分
     */
    INVITE_NEW("INVITE_NEW", 20L, "新用户通过邀请码注册获得积分"),

    /**
     * 邀请人获得奖励积分
     */
    INVITE_REWARD("INVITE_REWARD", 50L, "邀请人获得奖励积分"),

    /**
     * 部署应用消耗积分
     */
    DEPLOY_COST("DEPLOY_COST", 20L, "部署应用消耗积分"),

    /**
     * 生成应用消耗积分
     */
    GENERATE_COST("GENERATE_COST", 10L, "生成应用消耗积分"),

    /**
     * 首次生成应用奖励积分
     */
    FIRST_GENERATE("FIRST_GENERATE", 50L, "首次生成应用奖励积分");

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

    /**
     * 根据规则键获取枚举
     *
     * @param key 规则键
     * @return 枚举
     */
    public static PointRuleKeyEnum getEnumByKey(String key) {
        if (key == null) {
            return null;
        }
        for (PointRuleKeyEnum ruleKeyEnum : values()) {
            if (ruleKeyEnum.getKey().equals(key)) {
                return ruleKeyEnum;
            }
        }
        return null;
    }

}

