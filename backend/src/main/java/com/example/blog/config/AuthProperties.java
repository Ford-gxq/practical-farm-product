package com.example.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** 读取 application-dev.yml 中 app.auth 配置。 */
@Data
@Component
@ConfigurationProperties(prefix = "app.auth")
public class AuthProperties {
    private String passwordSalt;
    private String tokenPrefix;
}
