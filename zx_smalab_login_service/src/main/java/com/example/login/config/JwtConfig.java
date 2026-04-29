package com.example.login.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * JWT密钥
     */
    private String secret = "zx_smalab_login_service_jwt_secret_key_2024";

    /**
     * 过期时间（秒），默认2小时
     */
    private Long expiration = 7200L;
}