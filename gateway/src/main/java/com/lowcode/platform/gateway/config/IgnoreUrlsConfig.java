package com.lowcode.platform.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 白名单配置
 */
@Data
@ConfigurationProperties(prefix = "security.ignore")
public class IgnoreUrlsConfig {

    /**
     * 白名单路径列表
     */
    private List<String> urls = new ArrayList<>();

    /**
     * 默认白名单
     */
    public IgnoreUrlsConfig() {
        // 认证相关
        urls.add("/auth/login");
        urls.add("/auth/captcha");
        urls.add("/auth/captcha/**");
        urls.add("/auth/refresh");
        urls.add("/auth/register");

        // Swagger文档
        urls.add("/doc.html");
        urls.add("/webjars/**");
        urls.add("/swagger-resources/**");
        urls.add("/v3/api-docs/**");
        urls.add("/v3/api-docs");

        // 监控
        urls.add("/actuator/**");
        urls.add("/actuator");

        // 静态资源
        urls.add("/static/**");
        urls.add("/favicon.ico");
        urls.add("/error");
    }
}