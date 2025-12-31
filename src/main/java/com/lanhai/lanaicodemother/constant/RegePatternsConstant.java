package com.lanhai.lanaicodemother.constant;

/**
 * 正则表达式常量
 */
public interface RegePatternsConstant {

    /**
     * 手机号正则表达式
     */
    String PHONE_REGEX = "^1[3-9]\\d{9}$";

    /**
     * 邮箱正则表达式
     */
    String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    /**
     * 密码正则表达式（至少8位，包含字母和数字）
     */
    String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$";
}

