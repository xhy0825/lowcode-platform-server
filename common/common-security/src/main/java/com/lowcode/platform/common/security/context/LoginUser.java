package com.lowcode.platform.common.security.context;

import lombok.Data;
import java.io.Serializable;
import java.util.Set;

/**
 * 登录用户信息
 */
@Data
public class LoginUser implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 角色编码集合
     */
    private Set<String> roles;

    /**
     * 权限编码集合
     */
    private Set<String> permissions;

    /**
     * Token唯一标识
     */
    private String tokenId;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 用户真实姓名
     */
    private String realName;

    /**
     * 部门ID
     */
    private Long deptId;
}