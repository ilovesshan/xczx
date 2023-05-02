package com.xczx;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/22
 * @description:
 */

@MapperScan("com.xczx.content.mapper")
@MapperScan("com.xczx.messagesdk.mapper")
@EnableSwagger2Doc
@EnableFeignClients(basePackages = {"com.xczx.feign"})
@SpringBootApplication
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
