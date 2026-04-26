package com.lowcode.platform.flow.api.dto;

import lombok.Data;

import java.util.Map;

/**
 * 发起流程请求DTO
 */
@Data
public class StartFlowRequest {

    /**
     * 流程定义ID
     */
    private Long flowDefinitionId;

    /**
     * 表单数据
     */
    private Map<String, Object> formData;
}