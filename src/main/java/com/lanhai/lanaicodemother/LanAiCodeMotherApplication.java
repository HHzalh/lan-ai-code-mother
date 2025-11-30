package com.lanhai.lanaicodemother;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class LanAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(LanAiCodeMotherApplication.class, args);
    }

}
