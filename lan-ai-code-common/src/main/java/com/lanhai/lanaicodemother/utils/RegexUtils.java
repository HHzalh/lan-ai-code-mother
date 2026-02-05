package com.lanhai.lanaicodemother.utils;

import cn.hutool.core.util.StrUtil;
import com.lanhai.lanaicodemother.constant.RegePatternsConstant;


/**
 * 正则验证工具类
 */
public class RegexUtils {

    /**
     * 是否是无效手机格式
     *
     * @param phone 要校验的手机号
     * @return true:不符合，false：符合
     */
    public static boolean isPhoneInvalid(String phone) {
        return mismatch(phone, RegePatternsConstant.PHONE_REGEX);
    }

    /**
     * 是否是无效邮箱格式
     *
     * @param email 要校验的邮箱
     * @return true:不符合，false：符合
     */
    public static boolean isEmailInvalid(String email) {
        return mismatch(email, RegePatternsConstant.EMAIL_REGEX);
    }

    /**
     * 是否是无效密码格式
     *
     * @param password 要校验的密码
     * @return true:不符合，false：符合
     */
    public static boolean isPasswordInvalid(String password) {
        return mismatch(password, RegePatternsConstant.PASSWORD_REGEX);
    }

    /**
     * 校验是否不符合正则格式
     *
     * @param str   要校验的字符串
     * @param regex 正则表达式
     * @return true:不符合，false：符合
     */
    private static boolean mismatch(String str, String regex) {
        if (StrUtil.isBlank(str)) {
            return true;
        }
        return !str.matches(regex);
    }
}

