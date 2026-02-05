package com.lanhai.lanaicodemother.utils;

import com.lanhai.lanaicodemother.service.PointRuleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 积分规则缓存预热器
 * 应用启动时自动加载规则到Redis缓存
 * 避免Redis重启后的缓存雪崩
 * 首次请求不受冷启动影响
 * 日志可追溯预热状态
 *
 * @author 积分系统
 * @since 2026-01-16
 */
@Slf4j
@Component
@ConditionalOnProperty(
        prefix = "point",
        name = "cache-warm-enabled",
        havingValue = "true",
        matchIfMissing = false  // 默认不启用
)
public class PointRuleCacheWarmerUtils implements ApplicationRunner {

    @Resource
    private PointRuleService pointRuleService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            // 触发getAllRules()加载缓存
            pointRuleService.getAllRules();
            log.info("积分规则缓存预热完成");
        } catch (Exception e) {
            log.error("积分规则缓存预热失败：{}", e.getMessage(), e);
            // 预热失败不影响应用启动
            log.warn("应用将继续启动，但首次请求积分规则时会从数据库加载");
        }
    }
}
