package com.lowcode.platform.flow.api.dto;

import lombok.Data;

/**
 * 流程任务DTO
 */
@Data
public class FlowTaskDTO {

    private Long id;
    private String tenantId;
    private Long instanceId;
    private String nodeId;
    private String nodeName;
    private String assignType;
    private String assignValue;
    private String assignee;
    private String status;
    private String action;
    private String comment;
    private String createTime;
    private String deadline;
    private String actionTime;
    private String delegateUser;

    /**
     * 流程名称（关联查询）
     */
    private String flowName;

    /**
     * 发起人（关联查询）
     */
    private String initiator;

    /**
     * 是否超时
     */
    private Boolean isOverdue;
}