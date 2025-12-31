package com.lanhai.lanaicodemother.constant;

/**
 * 用户常量
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    // endregion

    /**
     * 密码重置验证码Redis key前缀
     */
    String PASSWORD_RESET_CODE_PREFIX = "password:reset:code:";

    /**
     * 验证码过期时间（秒）
     */
    Long CODE_EXPIRE_TIME = 300L; // 5分钟
}
