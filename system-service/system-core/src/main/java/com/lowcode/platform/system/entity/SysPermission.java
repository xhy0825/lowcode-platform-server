package com.lowcode.platform.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lowcode.platform.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class SysPermission extends BaseEntity {

    /** 权限名称 */
    private String permissionName;

    /** 权限编码 */
    private String permissionCode;

    /** 权限类型 menu/button/api/data */
    private String permissionType;

    /** 父级ID */
    private Long parentId;

    /** 路由路径 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 重定向路径 */
    private String redirect;

    /** 图标 */
    private String icon;

    /** 显示顺序 */
    private Integer orderNum;

    /** 是否可见 */
    private Integer visible;

    /** API路径 */
    private String apiPath;

    /** API方法 */
    private String apiMethod;

    /** 状态 */
    private Integer status;
}