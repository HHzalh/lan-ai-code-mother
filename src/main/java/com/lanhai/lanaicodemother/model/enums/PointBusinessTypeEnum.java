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
     * 签到奖励（连续签到奖励）
     */
    SIGN_IN_BONUS("SIGN_IN_BONUS", "签到奖励"),

    /**
     * 邀请新用户
     */
    INVITE_NEW("INVITE_NEW", "邀请新用户"),

    /**
     * 邀请奖励（邀请人获得的奖励）
     */
    INVITE_REWARD("INVITE_REWARD", "邀请奖励"),

    /**
     * 首次使用
     */
    FIRST_USE("FIRST_USE", "首次使用"),

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
    SYSTEM("SYSTEM", "系统发放"),

    /**
     * 管理员发放
     */
    SYSTEM_GRANT("SYSTEM_GRANT", "管理员发放"),

    /**
     * 首次生成奖励
     */
    FIRST_GENERATE("FIRST_GENERATE", "首次生成奖励");

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

