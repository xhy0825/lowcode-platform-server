package com.lowcode.platform.flow.api.dto;

import lombok.Data;

/**
 * 处理任务请求DTO
 */
@Data
public class HandleTaskRequest {

    /**
     * 处理动作：approve-同意，reject-驳回，delegate-转办
     */
    private String action;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 转办目标用户（转办时必填）
     */
    private String delegateUser;
}