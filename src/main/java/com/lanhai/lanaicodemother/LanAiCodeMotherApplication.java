package com.lanhai.lanaicodemother;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 单体版 AI 代码生成平台的 Spring Boot 启动入口，启用 AOP、缓存、定时任务和 MyBatis Mapper 扫描。
 */
@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableCaching
@EnableScheduling
@MapperScan("com.lanhai.lanaicodemother.mapper")
public class LanAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(LanAiCodeMotherApplication.class, args);
    }

}
