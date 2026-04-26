package com.lowcode.platform.common.core.context;

/**
 * 租户上下文持有者
 */
public class TenantContextHolder {

    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> TENANT_SCHEMA = new ThreadLocal<>();

    /** 默认租户 */
    private static final String DEFAULT_TENANT = "000000";

    /** 系统租户（用于管理租户数据） */
    private static final String SYSTEM_TENANT = "system";

    /**
     * 设置租户ID
     */
    public static void setTenantId(String tenantId) {
        TENANT_ID.set(tenantId);
        TENANT_SCHEMA.set("tenant_" + tenantId);
    }

    /**
     * 获取租户ID
     */
    public static String getTenantId() {
        String tenantId = TENANT_ID.get();
        return tenantId != null ? tenantId : DEFAULT_TENANT;
    }

    /**
     * 获取租户Schema
     */
    public static String getTenantSchema() {
        String schema = TENANT_SCHEMA.get();
        return schema != null ? schema : "tenant_" + DEFAULT_TENANT;
    }

    /**
     * 是否系统租户
     */
    public static boolean isSystemTenant() {
        return SYSTEM_TENANT.equals(getTenantId());
    }

    /**
     * 清除租户上下文
     */
    public static void clear() {
        TENANT_ID.remove();
        TENANT_SCHEMA.remove();
    }
}