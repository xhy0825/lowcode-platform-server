package com.lowcode.platform.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lowcode.platform.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /** 角色名称 */
    private String roleName;

    /** 角色编码 */
    private String roleCode;

    /** 数据权限范围 all/custom/dept/dept_and_child/self */
    private String dataScope;

    /** 状态 */
    private Integer status;
}