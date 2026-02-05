package com.lanhai.lanaicodemother.utils;

import cn.hutool.core.util.RandomUtil;

/**
 * 邀请码生成工具类
 * 使用Hutool工具包生成8位随机邀请码（排除易混淆字符）
 *
 * @author 积分系统
 */
public class InvitationCodeUtils {

    /**
     * 排除的易混淆字符：I,1,O,0
     */
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    /**
     * 邀请码长度
     */
    private static final int CODE_LENGTH = 8;

    /**
     * 生成邀请码
     * 使用Hutool的RandomUtil随机生成8位字符
     *
     * @return 8位邀请码
     */
    public static String generateCode() {
        return RandomUtil.randomString(CHARS, CODE_LENGTH);
    }

    /**
     * 生成邀请码（自定义长度）
     *
     * @param length 邀请码长度
     * @return 邀请码
     */
    public static String generateCode(int length) {
        if (length <= 0 || length > 32) {
            throw new IllegalArgumentException("邀请码长度必须在1-32之间");
        }
        return RandomUtil.randomString(CHARS, length);
    }

    /**
     * 校验邀请码格式
     *
     * @param code 邀请码
     * @return 是否有效
     */
    public static boolean isValidCode(String code) {
        if (code == null || code.length() != CODE_LENGTH) {
            return false;
        }
        // 校验所有字符是否在允许的字符集中
        return code.matches("[" + CHARS + "]+");
    }

}

