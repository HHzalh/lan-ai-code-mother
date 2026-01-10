package com.lanhai.lanaicodemother.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 积分类型枚举
 *
 * @author 积分系统
 */
@Getter
@AllArgsConstructor
public enum PointTypeEnum {

    /**
     * 收入（增加积分）
     */
    INCOME("INCOME", "收入"),

    /**
     * 支出（减少积分）
     */
    EXPENSE("EXPENSE", "支出");

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
    public static PointTypeEnum getEnumByValue(String value) {
        if (value == null) {
            return null;
        }
        for (PointTypeEnum typeEnum : values()) {
            if (typeEnum.getValue().equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }

}

