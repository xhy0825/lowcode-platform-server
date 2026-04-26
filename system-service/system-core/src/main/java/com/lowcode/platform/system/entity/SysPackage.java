package com.lowcode.platform.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 套餐实体
 */
@Data
@TableName("sys_package")
public class SysPackage {

    /**
     * 套餐ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 套餐名称
     */
    private String packageName;

    /**
     * 套餐编码
     */
    private String packageCode;

    /**
     * 最大用户数
     */
    private Integer maxUsers;

    /**
     * 最大表单数
     */
    private Integer maxForms;

    /**
     * 最大流程数
     */
    private Integer maxFlows;

    /**
     * 最大存储空间（MB）
     */
    private Integer maxStorage;

    /**
     * 功能配置（JSON）
     */
    private String features;

    /**
     * 价格
     */
    private Double price;

    /**
     * 有效期（天）
     */
    private Integer durationDays;

    /**
     * 状态（0正常 1禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}