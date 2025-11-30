package com.lanhai.lanaicodemother;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.lanhai.lanaicodemother.mapper")
public class LanAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(LanAiCodeMotherApplication.class, args);
    }

}
