package com.lowcode.platform.flow.api.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 流程定义DTO
 */
@Data
public class FlowDefinitionDTO {

    private Long id;
    private String tenantId;
    private String flowName;
    private String flowCode;
    private Long formId;
    private String nodes;
    private String edges;
    private Integer status;
    private Integer version;
    private String description;

    /**
     * 节点列表（解析后）
     */
    private List<Map<String, Object>> nodeList;

    /**
     * 边列表（解析后）
     */
    private List<Map<String, Object>> edgeList;
}