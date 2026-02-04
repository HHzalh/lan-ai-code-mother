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
    String EMAIL_PASSWORD_RESET_CODE_PREFIX = "email:passwordReset:";

    /**
     * 注册验证码Redis key前缀
     */
    String EMAIL_REGISTER_CODE_PREFIX = "email:register:";

    /**
     * 换绑邮箱验证码Redis key前缀
     */
    String EMAIL_CHANGE_EMAIL_CODE_PREFIX = "email:changeEmail:";

    /**
     * 验证码过期时间（秒）
     */
    Long CODE_EXPIRE_TIME = 300L; // 5分钟

    /**
     * 注册验证码标题
     */
    String CODE_TITLE = "蓝海智造 验证码";
}
