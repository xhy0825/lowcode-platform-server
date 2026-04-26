package com.lowcode.platform.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lowcode.platform.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant")
public class SysTenant extends BaseEntity {

    /**
     * 租户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 租户编码
     */
    private String tenantCode;

    /**
     * 套餐ID
     */
    private Long packageId;

    /**
     * 过期时间
     */
    private String expireTime;

    /**
     * 数据库Schema
     */
    private String dbSchema;

    /**
     * 管理员用户ID
     */
    private Long adminUserId;

    /**
     * 状态（0正常 1禁用）
     */
    private Integer status;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 地址
     */
    private String address;
}