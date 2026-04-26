package com.lowcode.platform.common.security.jwt;

import com.lowcode.platform.common.security.context.LoginUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT Token提供者
 * 负责Token的生成、解析、验证
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成Access Token
     */
    public String generateAccessToken(LoginUser loginUser) {
        return Jwts.builder()
                .subject(String.valueOf(loginUser.getUserId()))
                .claim("username", loginUser.getUsername())
                .claim("tenantId", loginUser.getTenantId())
                .claim("type", "access")
                .claim("tokenId", loginUser.getTokenId())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpire() * 1000))
                .id(UUID.randomUUID().toString())
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 生成Refresh Token
     */
    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "refresh")
                .issuer(jwtProperties.getIssuer())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpire() * 1000))
                .id(UUID.randomUUID().toString())
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析Token获取Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("Token解析失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 验证Token有效性
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 从Token获取用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从Token获取用户名
     */
    public String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    /**
     * 从Token获取租户ID
     */
    public String getTenantId(String token) {
        return parseToken(token).get("tenantId", String.class);
    }

    /**
     * 从Token获取TokenId
     */
    public String getTokenId(String token) {
        return parseToken(token).getId();
    }

    /**
     * 从Token获取Token类型
     */
    public String getTokenType(String token) {
        return parseToken(token).get("type", String.class);
    }

    /**
     * 获取Token剩余有效时间（秒）
     */
    public long getRemainingTime(String token) {
        Claims claims = parseToken(token);
        Date expiration = claims.getExpiration();
        long remaining = expiration.getTime() - System.currentTimeMillis();
        return remaining > 0 ? remaining / 1000 : 0;
    }

    /**
     * 判断Token是否即将过期（小于30分钟）
     */
    public boolean isTokenExpiringSoon(String token) {
        return getRemainingTime(token) < 1800;
    }

    /**
     * 获取Access Token有效期（秒）
     */
    public long getAccessTokenExpire() {
        return jwtProperties.getAccessTokenExpire();
    }

    /**
     * 获取Refresh Token有效期（秒）
     */
    public long getRefreshTokenExpire() {
        return jwtProperties.getRefreshTokenExpire();
    }
}