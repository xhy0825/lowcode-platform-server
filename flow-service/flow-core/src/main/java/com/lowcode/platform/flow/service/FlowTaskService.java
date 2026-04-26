package com.lowcode.platform.flow.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.flow.entity.FlowTask;

import java.util.List;
import java.util.Map;

/**
 * 流程任务服务接口
 */
public interface FlowTaskService {

    /**
     * 分页查询任务
     */
    Page<FlowTask> listPage(Map<String, Object> params);

    /**
     * 处理任务
     */
    void handle(Long taskId, String action, String comment, String delegateUser);

    /**
     * 批量审批
     */
    void batchApprove(List<Long> taskIds);

    /**
     * 获取用户的待办任务数
     */
    int getPendingCount(String userId);
}