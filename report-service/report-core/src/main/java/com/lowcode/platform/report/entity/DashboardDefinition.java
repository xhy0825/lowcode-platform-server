package com.lowcode.platform.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 仪表盘定义实体
 */
@Data
@TableName("dashboard_definition")
public class DashboardDefinition {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 仪表盘名称
     */
    private String dashboardName;

    /**
     * 仪表盘编码
     */
    private String dashboardCode;

    /**
     * 图表布局配置(JSON)
     */
    private String layoutConfig;

    /**
     * 过滤条件配置(JSON)
     */
    private String filterConfig;

    /**
     * 刷新间隔（秒）
     */
    private Integer refreshInterval;

    /**
     * 状态：0-草稿，1-已发布
     */
    private Integer status;

    /**
     * 删除标记
     */
    @TableLogic
    private Integer delFlag;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}