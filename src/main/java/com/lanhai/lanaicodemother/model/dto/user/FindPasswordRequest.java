package com.lanhai.lanaicodemother.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 找回密码请求（发送验证码）
 */
@Data
public class FindPasswordRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 邮箱地址
     */
    private String email;
}

