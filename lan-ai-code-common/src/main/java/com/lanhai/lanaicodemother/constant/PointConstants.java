package com.lanhai.lanaicodemother.constant;

/**
 * 积分系统常量
 * <p>
 * 集中管理积分模块的所有常量，避免魔法数字
 * </p>
 *
 * @author 积分系统
 * @since 2026-01-14
 */
public interface PointConstants {


    /**
     * 签到奖励相关常量
     */

    /**
     * 连续签到 3 天额外奖励
     */
    int SIGN_IN_BONUS_DAY_3 = 3;
    /**
     * 连续签到 7 天额外奖励
     */
    int SIGN_IN_BONUS_DAY_7 = 7;
    /**
     * 签到周期天数（每 7 天一个循环）
     */
    int SIGN_IN_CYCLE_DAYS = 7;

    /**
     * 邀请码相关常量
     */

    /**
     * 邀请码生成最大重试次数
     */
    int INVITE_CODE_MAX_RETRIES = 10;

    /**
     * 缓存相关常量
     */

    /**
     * 所有规则列表缓存Key（统一数据源）
     */
    String ALL_RULES_CACHE_KEY = "point:rules:all";

    /**
     * 签到状态缓存Key前缀
     */
    String SIGN_IN_STATUS_CACHE_PREFIX = "point:sign:status:";

    /**
     * 规则缓存过期时间（秒）：1 小时（已废弃，保留用于兼容）
     */
    @Deprecated
    long RULE_CACHE_EXPIRE_SECONDS = 3600L;

    /**
     * 分布式锁相关常量
     */

    /**
     * 签到锁过期时间（秒）
     */
    int SIGN_IN_LOCK_EXPIRE_SECONDS = 10;

    /**
     * 邀请锁过期时间（秒）
     */
    int INVITE_LOCK_EXPIRE_SECONDS = 10;
    /**
     * 锁等待时间（秒）
     */
    int LOCK_WAIT_SECONDS = 3;

    /**
     * 邀请奖励锁前缀
     */
    String INVITE_LOCK_PREFIX = "point:invite:";

    /**
     * 签到锁Key前缀
     */
    String SIGN_IN_LOCK_PREFIX = "point:sign:in:";


}
