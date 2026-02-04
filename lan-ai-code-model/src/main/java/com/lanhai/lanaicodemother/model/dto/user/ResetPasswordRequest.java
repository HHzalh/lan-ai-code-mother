package com.lanhai.lanaicodemother.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 重置密码请求
 */
@Data
public class ResetPasswordRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 验证码
     */
    private String code;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 确认新密码
     */
    private String checkPassword;
}

