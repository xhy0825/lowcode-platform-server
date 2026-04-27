package com.lowcode.platform.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图表定义实体
 */
@Data
@TableName("chart_definition")
public class ChartDefinition {

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
     * 图表名称
     */
    private String chartName;

    /**
     * 图表编码
     */
    private String chartCode;

    /**
     * 图表类型：line/bar/pie/scatter/table/gauge
     */
    private String chartType;

    /**
     * 数据源ID（数据模型ID）
     */
    private Long dataSourceId;

    /**
     * 数据查询配置(JSON)
     */
    private String queryConfig;

    /**
     * 图表样式配置(JSON)
     */
    private String styleConfig;

    /**
     * 维度字段配置(JSON)
     */
    private String dimensionConfig;

    /**
     * 指标字段配置(JSON)
     */
    private String metricConfig;

    /**
     * 状态：0-草稿，1-已发布
     */
    private Integer status;

    /**
     * 版本号
     */
    private Integer version;

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