package com.lanhai.lanaicodemother;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.lanhai.lanaicodemother.mapper")
@ComponentScan("com.lanhai")
@EnableDubbo
public class LanAiCodeUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(LanAiCodeUserApplication.class, args);
    }
}
