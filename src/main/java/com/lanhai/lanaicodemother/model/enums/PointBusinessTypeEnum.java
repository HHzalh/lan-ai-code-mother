package com.lanhai.lanaicodemother.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 积分业务类型枚举
 *
 * @author 积分系统
 */
@Getter
@AllArgsConstructor
public enum PointBusinessTypeEnum {

    /**
     * 签到
     */
    SIGN_IN("SIGN_IN", "签到"),

    /**
     * 注册奖励（所有新用户注册的基础奖励）
     */
    REGISTER_REWARD("REGISTER_REWARD", "注册奖励"),

    /**
     * 被邀请人奖励（通过邀请码注册的额外奖励）
     */
    INVITEE_BONUS("INVITEE_BONUS", "被邀请人奖励"),

    /**
     * 邀请人奖励（邀请人获得的奖励）
     */
    INVITER_BONUS("INVITER_BONUS", "邀请人奖励"),

    /**
     * 部署应用
     */
    DEPLOY("DEPLOY", "部署应用"),

    /**
     * 生成应用
     */
    GENERATE("GENERATE", "生成应用"),

    /**
     * 下载代码
     */
    DOWNLOAD("DOWNLOAD", "下载代码"),

    /**
     * 兑换
     */
    EXCHANGE("EXCHANGE", "兑换"),

    /**
     * 退款
     */
    REFUND("REFUND", "退款"),

    /**
     * 系统发放
     */
    SYSTEM_GRANT("SYSTEM_GRANT", "系统发放");

    /**
     * 枚举值
     */
    private final String value;

    /**
     * 枚举描述
     */
    private final String text;

    /**
     * 根据枚举值获取枚举
     *
     * @param value 枚举值
     * @return 枚举
     */
    public static PointBusinessTypeEnum getEnumByValue(String value) {
        if (value == null) {
            return null;
        }
        for (PointBusinessTypeEnum typeEnum : values()) {
            if (typeEnum.getValue().equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }

}

