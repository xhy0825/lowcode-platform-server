package com.lowcode.platform.common.security.service;

import java.util.Set;

/**
 * 权限校验服务接口
 */
public interface PermissionService {

    /**
     * 获取用户权限集合
     */
    Set<String> getUserPermissions(Long userId);

    /**
     * 获取用户角色集合
     */
    Set<String> getUserRoles(Long userId);

    /**
     * 判断用户是否拥有权限
     */
    boolean hasPermission(Long userId, String permission);

    /**
     * 判断用户是否拥有角色
     */
    boolean hasRole(Long userId, String role);

    /**
     * 判断用户是否拥有任意权限
     */
    boolean hasAnyPermission(Long userId, String... permissions);

    /**
     * 判断用户是否拥有任意角色
     */
    boolean hasAnyRole(Long userId, String... roles);
}