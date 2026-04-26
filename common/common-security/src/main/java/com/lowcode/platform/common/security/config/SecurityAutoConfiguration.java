package com.lowcode.platform.common.security.config;

import com.lowcode.platform.common.security.jwt.JwtProperties;
import com.lowcode.platform.common.security.jwt.JwtTokenProvider;
import com.lowcode.platform.common.security.captcha.CaptchaService;
import com.lowcode.platform.common.security.aop.PermissionAspect;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 安全模块自动配置
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
@ComponentScan(basePackages = "com.lowcode.platform.common.security")
public class SecurityAutoConfiguration {

}