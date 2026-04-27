package com.lowcode.platform.system.service;

import lombok.Data;

import java.util.List;

/**
 * 菜单树VO
 */
@Data
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