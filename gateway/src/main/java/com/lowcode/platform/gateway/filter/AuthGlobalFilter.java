package com.lowcode.platform.gateway.filter;

import com.lowcode.platform.gateway.config.IgnoreUrlsConfig;
import com.lowcode.platform.common.redis.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证全局过滤器
 * 负责Token验证和用户信息注入
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final IgnoreUrlsConfig ignoreUrlsConfig;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Redis Token黑名单Key前缀
     */
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    /**
     * 用户登录信息Key前缀
     */
    private static final String USER_TOKEN_PREFIX = "user:token:";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 白名单放行
        if (isWhitePath(path)) {
            log.debug("白名单路径放行: {}", path);
            return chain.filter(exchange);
        }

        // 2. 获取Token
        String token = extractToken(request);
        if (!StringUtils.hasText(token)) {
            log.warn("请求缺少Token: {}", path);
            return unauthorized(exchange.getResponse(), "未提供认证Token");
        }

        // 3. 验证Token格式（简单校验，详细校验在服务端）
        if (!isValidTokenFormat(token)) {
            log.warn("Token格式无效: {}", token.substring(0, Math.min(20, token.length())));
            return unauthorized(exchange.getResponse(), "Token格式无效");
        }

        // 4. 检查Token是否被注销（黑名单）
        try {
            // 从Token中提取tokenId（JWT的jti）
            String tokenId = extractTokenId(token);
            if (tokenId != null && Boolean.TRUE.equals(redisService.hasKey(TOKEN_BLACKLIST_PREFIX + tokenId))) {
                log.warn("Token已被注销: {}", tokenId);
                return unauthorized(exchange.getResponse(), "Token已失效，请重新登录");
            }
        } catch (Exception e) {
            log.warn("Token解析失败: {}", e.getMessage());
            return unauthorized(exchange.getResponse(), "Token无效");
        }

        // 5. 从Redis获取用户信息
        try {
            Long userId = extractUserId(token);
            String tenantId = extractTenantId(token);

            if (userId == null) {
                return unauthorized(exchange.getResponse(), "Token解析失败");
            }

            // 检查用户登录状态
            String userTokenKey = USER_TOKEN_PREFIX + userId;
            Object storedToken = redisService.get(userTokenKey);
            if (storedToken == null) {
                log.warn("用户登录状态不存在: userId={}", userId);
                return unauthorized(exchange.getResponse(), "登录已过期，请重新登录");
            }

            // 6. 注入用户信息到请求头
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-Tenant-Id", tenantId != null ? tenantId : "000000")
                    .header("X-Token", token)
                    .build();

            log.debug("认证成功: userId={}, tenantId={}, path={}", userId, tenantId, path);

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.error("认证处理异常: {}", e.getMessage(), e);
            return unauthorized(exchange.getResponse(), "认证处理异常");
        }
    }

    /**
     * 判断是否为白名单路径
     */
    private boolean isWhitePath(String path) {
        return ignoreUrlsConfig.getUrls().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * 从请求中提取Token
     */
    private String extractToken(ServerHttpRequest request) {
        // 从Authorization头获取
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 从查询参数获取（可选）
        String tokenParam = request.getQueryParams().getFirst("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }

        return null;
    }

    /**
     * 验证Token格式（简单校验）
     */
    private boolean isValidTokenFormat(String token) {
        // JWT格式：三段用.分隔的字符串
        if (token == null || token.isEmpty()) {
            return false;
        }
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }

    /**
     * 从Token提取TokenId（简单实现）
     */
    private String extractTokenId(String token) {
        // 实际应该用JwtTokenProvider解析
        // 这里先用简单方式，后续优化
        try {
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                // 解析payload（第二段）
                String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
                Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
                return (String) claims.get("jti");
            }
        } catch (Exception e) {
            log.warn("解析TokenId失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 从Token提取UserId
     */
    private Long extractUserId(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
                Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
                String sub = (String) claims.get("sub");
                return sub != null ? Long.parseLong(sub) : null;
            }
        } catch (Exception e) {
            log.warn("解析UserId失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 从Token提取TenantId
     */
    private String extractTenantId(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
                Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
                return (String) claims.get("tenantId");
            }
        } catch (Exception e) {
            log.warn("解析TenantId失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 返回401未授权响应
     */
    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("msg", message);
        result.put("data", null);
        result.put("timestamp", System.currentTimeMillis());

        try {
            String body = objectMapper.writeValueAsString(result);
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            String body = "{\"code\":401,\"msg\":\"认证失败\",\"data\":null}";
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }
    }

    @Override
    public int getOrder() {
        return -100; // 最高优先级
    }
}