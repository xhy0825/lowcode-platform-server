package com.lowcode.platform.flow.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.flow.entity.FlowInstance;
import com.lowcode.platform.flow.entity.FlowTask;

import java.util.List;
import java.util.Map;

/**
 * 流程实例服务接口
 */
public interface FlowInstanceService {

    /**
     * 分页查询流程实例
     */
    Page<FlowInstance> listPage(Map<String, Object> params);

    /**
     * 根据ID获取流程实例详情
     */
    Map<String, Object> getDetail(Long id);

    /**
     * 发起流程
     */
    Long start(Long flowDefinitionId, Map<String, Object> formData);

    /**
     * 取消流程实例
     */
    void cancel(Long id);

    /**
     * 获取表单数据
     */
    Map<String, Object> getFormData(Long instanceId);
}