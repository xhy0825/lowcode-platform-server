package com.lowcode.platform.flow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程任务
 */
@Data
@TableName("flow_task")
public class FlowTask {

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
     * 流程实例ID
     */
    private Long instanceId;

    /**
     * 节点ID
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 审批人类型：user-用户，role-角色，dept-部门，leader-上级
     */
    private String assignType;

    /**
     * 审批人值(JSON数组)
     */
    private String assignValue;

    /**
     * 当前处理人
     */
    private String assignee;

    /**
     * 状态：pending-待处理，approved-已同意，rejected-已驳回，delegated-已转办
     */
    private String status;

    /**
     * 处理动作：approve-同意，reject-驳回，delegate-转办
     */
    private String action;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 处理时间
     */
    private LocalDateTime actionTime;

    /**
     * 转办目标用户
     */
    private String delegateUser;

    /**
     * 删除标记
     */
    @TableLogic
    private Integer delFlag;
}