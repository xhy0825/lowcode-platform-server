package com.lowcode.platform.common.mybatis.interceptor;

import com.lowcode.platform.common.core.context.TenantContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户拦截器
 * 从请求Header中提取租户ID并设置到上下文
 */
@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor {

    /**
     * 租户ID请求头
     */
    private static final String TENANT_ID_HEADER = "X-Tenant-Id";

    /**
     * 默认租户ID
     */
    private static final String DEFAULT_TENANT_ID = "000000";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从Header获取租户ID
        String tenantId = request.getHeader(TENANT_ID_HEADER);

        if (tenantId == null || tenantId.isEmpty()) {
            // 从请求参数获取
            tenantId = request.getParameter("tenantId");
        }

        if (tenantId == null || tenantId.isEmpty()) {
            tenantId = DEFAULT_TENANT_ID;
        }

        // 设置租户上下文
        TenantContextHolder.setTenantId(tenantId);
        log.debug("设置租户上下文: tenantId={}, path={}", tenantId, request.getRequestURI());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除租户上下文
        TenantContextHolder.clear();
        log.debug("清除租户上下文");
    }
}