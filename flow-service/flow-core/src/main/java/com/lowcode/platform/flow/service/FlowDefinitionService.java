package com.lowcode.platform.flow.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.flow.entity.FlowDefinition;

import java.util.List;
import java.util.Map;

/**
 * 流程定义服务接口
 */
public interface FlowDefinitionService {

    /**
     * 分页查询流程定义
     */
    Page<FlowDefinition> listPage(Map<String, Object> params);

    /**
     * 根据ID获取流程定义
     */
    FlowDefinition getById(Long id);

    /**
     * 创建流程定义
     */
    Long create(FlowDefinition definition);

    /**
     * 更新流程定义
     */
    void update(FlowDefinition definition);

    /**
     * 删除流程定义
     */
    void delete(Long id);

    /**
     * 发布流程
     */
    void publish(Long id);

    /**
     * 获取可选表单列表
     */
    List<Map<String, Object>> getForms();

    /**
     * 获取审批人选项
     */
    List<Map<String, Object>> getAssignOptions();
}