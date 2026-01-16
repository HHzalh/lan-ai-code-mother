package com.lanhai.lanaicodemother;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

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
