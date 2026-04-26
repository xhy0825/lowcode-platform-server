package com.lowcode.platform.flow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程实例
 */
@Data
@TableName("flow_instance")
public class FlowInstance {

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
     * 流程定义ID
     */
    private Long flowDefinitionId;

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 流程编码
     */
    private String flowCode;

    /**
     * 表单数据ID
     */
    private Long formDataId;

    /**
     * 发起人
     */
    private String initiator;

    /**
     * 当前节点ID
     */
    private String currentNode;

    /**
     * 状态：running-进行中，completed-已完成，rejected-已驳回，cancelled-已取消
     */
    private String status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 耗时（秒）
     */
    private Integer duration;

    /**
     * 删除标记
     */
    @TableLogic
    private Integer delFlag;
}