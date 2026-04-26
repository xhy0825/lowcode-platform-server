package com.lowcode.platform.common.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT配置属性
 */
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT签名密钥（至少256位）
     */
    private String secret = "lowcode-platform-secret-key-must-be-at-least-256-bits-for-hmac-sha256";

    /**
     * Access Token有效期（秒），默认2小时
     */
    private long accessTokenExpire = 7200L;

    /**
     * Refresh Token有效期（秒），默认7天
     */
    private long refreshTokenExpire = 604800L;

    /**
     * Token请求头名称
     */
    private String header = "Authorization";

    /**
     * Token前缀
     */
    private String prefix = "Bearer ";

    /**
     * Tokenissuer
     */
    private String issuer = "lowcode-platform";
}