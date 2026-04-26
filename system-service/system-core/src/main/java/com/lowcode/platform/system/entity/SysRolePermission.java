package com.lowcode.platform.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色权限关联
 */
@Data
@TableName("sys_role_permission")
public class SysRolePermission implements Serializable {

    /** 角色ID */
    private Long roleId;

    /** 权限ID */
    private Long permissionId;
}