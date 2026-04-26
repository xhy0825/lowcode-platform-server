package com.lowcode.platform.common.security.aop;

import com.lowcode.platform.common.security.annotation.*;
import com.lowcode.platform.common.security.context.SecurityContextHolder;
import com.lowcode.platform.common.security.context.LoginUser;
import com.lowcode.platform.common.security.exception.UnauthorizedException;
import com.lowcode.platform.common.security.exception.ForbiddenException;
import com.lowcode.platform.common.security.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * 权限校验切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final PermissionService permissionService;

    /**
     * 登录校验
     */
    @Around("@annotation(com.lowcode.platform.common.security.annotation.RequiresLogin)")
    public Object checkLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        LoginUser loginUser = SecurityContextHolder.getLoginUser();
        if (loginUser == null) {
            throw new UnauthorizedException("未登录或登录已过期");
        }
        return joinPoint.proceed();
    }

    /**
     * 权限校验
     */
    @Around("@annotation(com.lowcode.platform.common.security.annotation.RequiresPermissions)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresPermissions annotation = method.getAnnotation(RequiresPermissions.class);

        // 获取当前用户
        LoginUser loginUser = SecurityContextHolder.getLoginUser();
        if (loginUser == null) {
            throw new UnauthorizedException("未登录或登录已过期");
        }

        // 获取需要的权限
        String[] requiredPermissions = annotation.value();
        Logical logical = annotation.logical();

        // 获取用户权限（优先从LoginUser获取，否则从服务获取）
        Set<String> userPermissions = loginUser.getPermissions();
        if (userPermissions == null || userPermissions.isEmpty()) {
            userPermissions = permissionService.getUserPermissions(loginUser.getUserId());
        }

        // 校验权限（超级权限 *:*:* 直接放行）
        if (userPermissions.contains("*:*:*")) {
            return joinPoint.proceed();
        }

        // 权限匹配
        boolean hasPermission;
        if (logical == Logical.AND) {
            hasPermission = Arrays.stream(requiredPermissions)
                    .allMatch(userPermissions::contains);
        } else {
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
        }

        if (!hasPermission) {
            log.warn("用户 {} 缺少权限: {}", loginUser.getUsername(), Arrays.toString(requiredPermissions));
            throw new ForbiddenException("缺少权限: " + Arrays.toString(requiredPermissions));
        }

        return joinPoint.proceed();
    }

    /**
     * 角色校验
     */
    @Around("@annotation(com.lowcode.platform.common.security.annotation.RequiresRoles)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresRoles annotation = method.getAnnotation(RequiresRoles.class);

        // 获取当前用户
        LoginUser loginUser = SecurityContextHolder.getLoginUser();
        if (loginUser == null) {
            throw new UnauthorizedException("未登录或登录已过期");
        }

        // 获取需要的角色
        String[] requiredRoles = annotation.value();
        Logical logical = annotation.logical();

        // 获取用户角色
        Set<String> userRoles = loginUser.getRoles();
        if (userRoles == null || userRoles.isEmpty()) {
            userRoles = permissionService.getUserRoles(loginUser.getUserId());
        }

        // 角色匹配
        boolean hasRole;
        if (logical == Logical.AND) {
            hasRole = Arrays.stream(requiredRoles)
                    .allMatch(userRoles::contains);
        } else {
            hasRole = Arrays.stream(requiredRoles)
                    .anyMatch(userRoles::contains);
        }

        if (!hasRole) {
            log.warn("用户 {} 缺少角色: {}", loginUser.getUsername(), Arrays.toString(requiredRoles));
            throw new ForbiddenException("缺少角色: " + Arrays.toString(requiredRoles));
        }

        return joinPoint.proceed();
    }
}