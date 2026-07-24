package com.lanhai.lanaicodemother.ratelimter.enums;

/**
 * 限流维度枚举，支持按接口、登录用户或客户端 IP 生成限流键。
 */
public enum RateLimitType {

    /**
     * 接口级别限流
     */
    API,

    /**
     * 用户级别限流
     */
    USER,

    /**
     * IP级别限流
     */
    IP
}
