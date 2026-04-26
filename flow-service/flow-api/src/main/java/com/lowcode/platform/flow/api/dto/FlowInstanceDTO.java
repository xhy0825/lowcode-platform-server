package com.lowcode.platform.flow.api.dto;

import lombok.Data;

/**
 * 流程实例DTO
 */
@Data
public class FlowInstanceDTO {

    private Long id;
    private String tenantId;
    private Long flowDefinitionId;
    private String flowName;
    private String flowCode;
    private Long formDataId;
    private String initiator;
    private String currentNode;
    private String status;
    private String startTime;
    private String endTime;
    private Integer duration;
}