package com.lanhai.lanaicodemother.config;

import com.lanhai.lanaicodemother.service.PointRuleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 积分规则缓存预热器
 * 应用启动时自动加载规则到Redis缓存
 *
 * <p>优势：
 * <ul>
 *   <li>避免Redis重启后的缓存雪崩</li>
 *   <li>首次请求不受冷启动影响</li>
 *   <li>日志可追溯预热状态</li>
 * </ul>
 *
 * @author 积分系统
 * @since 2026-01-16
 */
@Slf4j
@Component
public class PointRuleCacheWarmer implements ApplicationRunner {

    @Resource
    private PointRuleService pointRuleService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("开始预热积分规则缓存...");
            long startTime = System.currentTimeMillis();

            // 触发getAllRules()加载缓存
            pointRuleService.getAllRules();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            log.info("积分规则缓存预热完成，耗时：{}ms", duration);
        } catch (Exception e) {
            log.error("积分规则缓存预热失败：{}", e.getMessage(), e);
            // 预热失败不影响应用启动
            log.warn("应用将继续启动，但首次请求积分规则时会从数据库加载");
        }
    }
}
