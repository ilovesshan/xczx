package com.xczx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.xczx.content.mapper")
@MapperScan("com.xczx.learning.mapper")
@MapperScan("com.xczx.messagesdk.mapper")
@EnableFeignClients(basePackages = {"com.xczx.feign"})
@SpringBootApplication
public class LearningApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningApiApplication.class, args);
    }

}
