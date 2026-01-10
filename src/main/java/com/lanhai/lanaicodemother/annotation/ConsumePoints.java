package com.lanhai.lanaicodemother.annotation;

import com.lanhai.lanaicodemother.model.enums.PointBusinessTypeEnum;
import com.lanhai.lanaicodemother.model.enums.PointRuleKeyEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消耗积分注解
 * 用于标注需要消耗积分的业务方法
 *
 * @author 积分系统
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConsumePoints {

    /**
     * 积分业务类型
     */
    PointBusinessTypeEnum businessType();

    /**
     * 积分规则键（从枚举中自动获取积分数）
     */
    PointRuleKeyEnum ruleKey();

    /**
     * 是否仅首次操作扣费
     * true: 同一用户、同一业务ID只扣费一次
     * false: 每次都扣费
     */
    boolean once() default false;

    /**
     * 业务ID参数名
     * 用于从方法参数中获取业务ID（如appId）
     */
    String businessIdParam() default "appId";

    /**
     * 用户ID参数名
     * 用于从方法参数中获取用户ID
     */
    String userIdParam() default "userId";
}

