package com.lowcode.platform.page.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 页面组件实体
 */
@Data
@TableName("page_component")
public class PageComponent {

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
     * 页面ID
     */
    private Long pageId;

    /**
     * 组件编码
     */
    private String componentCode;

    /**
     * 组件类型：input/select/table/chart/button等
     */
    private String componentType;

    /**
     * 组件配置(JSON)
     */
    private String componentConfig;

    /**
     * 位置配置(JSON)
     */
    private String positionConfig;

    /**
     * 事件配置(JSON)
     */
    private String eventConfig;

    /**
     * 排序号
     */
    private Integer orderNum;

    /**
     * 删除标记
     */
    @TableLogic
    private Integer delFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}