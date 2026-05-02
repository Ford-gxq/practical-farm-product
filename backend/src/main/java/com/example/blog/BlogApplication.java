package com.example.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动类。
 *
 * 面试常问：@SpringBootApplication 做了什么？
 * 1. @SpringBootConfiguration：声明这是 Spring Boot 配置类。
 * 2. @EnableAutoConfiguration：开启自动配置。
 * 3. @ComponentScan：扫描当前包及子包下的 Controller、Service、Mapper 等组件。
 */
@SpringBootApplication
public class BlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
