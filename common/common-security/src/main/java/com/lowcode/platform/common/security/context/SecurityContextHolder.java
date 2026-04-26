package com.lowcode.platform.common.security.context;

/**
 * 安全上下文持有者
 * 使用ThreadLocal存储当前登录用户信息
 */
public class SecurityContextHolder {

    private static final ThreadLocal<LoginUser> USER_HOLDER = new ThreadLocal<>();

    /**
     * 设置登录用户
     */
    public static void setLoginUser(LoginUser loginUser) {
        USER_HOLDER.set(loginUser);
    }

    /**
     * 获取登录用户
     */
    public static LoginUser getLoginUser() {
        return USER_HOLDER.get();
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        LoginUser user = getLoginUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        LoginUser user = getLoginUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 获取租户ID
     */
    public static String getTenantId() {
        LoginUser user = getLoginUser();
        return user != null ? user.getTenantId() : null;
    }

    /**
     * 获取用户权限集合
     */
    public static java.util.Set<String> getPermissions() {
        LoginUser user = getLoginUser();
        return user != null ? user.getPermissions() : java.util.Collections.emptySet();
    }

    /**
     * 获取用户角色集合
     */
    public static java.util.Set<String> getRoles() {
        LoginUser user = getLoginUser();
        return user != null ? user.getRoles() : java.util.Collections.emptySet();
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        USER_HOLDER.remove();
    }

    /**
     * 判断是否已登录
     */
    public static boolean isAuthenticated() {
        return getLoginUser() != null;
    }

    /**
     * 判断是否拥有指定权限
     */
    public static boolean hasPermission(String permission) {
        return getPermissions().contains(permission) || getPermissions().contains("*:*:*");
    }

    /**
     * 判断是否拥有指定角色
     */
    public static boolean hasRole(String role) {
        return getRoles().contains(role);
    }
}