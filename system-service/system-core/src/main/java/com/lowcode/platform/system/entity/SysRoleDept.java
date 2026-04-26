package com.lowcode.platform.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色部门关联（数据权限）
 */
@Data
@TableName("sys_role_dept")
public class SysRoleDept implements Serializable {

    /** 角色ID */
    private Long roleId;

    /** 部门ID */
    private Long deptId;
}