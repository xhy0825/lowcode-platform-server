package com.lowcode.platform.system.service;

import com.lowcode.platform.system.entity.SysUser;

import java.util.List;

/**
 * 权限服务接口
 */
public interface SysPermissionService {

    /** 根据用户ID查询权限列表 */
    List<String> selectPermissionCodesByUserId(Long userId);

    /** 查询用户菜单树 */
    List<MenuTreeVO> selectMenuTreeByUserId(Long userId);

    /** 检查用户是否有权限 */
    boolean hasPermission(Long userId, String permissionCode);

    /** 查询所有菜单 */
    List<MenuTreeVO> selectAllMenuTree();

    /** 获取用户信息 */
    SysUser getUserInfo(Long userId);
}

/**
 * 菜单树VO
 */
public class MenuTreeVO {
    private Long id;
    private String permissionName;
    private String permissionCode;
    private Long parentId;
    private String path;
    private String component;
    private String icon;
    private Integer orderNum;
    private List<MenuTreeVO> children;
}