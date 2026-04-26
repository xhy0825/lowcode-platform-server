package com.lowcode.platform.flow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowcode.platform.common.context.TenantContextHolder;
import com.lowcode.platform.common.exception.BusinessException;
import com.lowcode.platform.flow.entity.FlowDefinition;
import com.lowcode.platform.flow.entity.FlowInstance;
import com.lowcode.platform.flow.entity.FlowTask;
import com.lowcode.platform.flow.mapper.FlowDefinitionMapper;
import com.lowcode.platform.flow.mapper.FlowInstanceMapper;
import com.lowcode.platform.flow.mapper.FlowTaskMapper;
import com.lowcode.platform.flow.service.FlowInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程实例服务实现
 */
@Service
@RequiredArgsConstructor
public class FlowInstanceServiceImpl implements FlowInstanceService {

    private final FlowInstanceMapper flowInstanceMapper;
    private final FlowDefinitionMapper flowDefinitionMapper;
    private final FlowTaskMapper flowTaskMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Page<FlowInstance> listPage(Map<String, Object> params) {
        Page<FlowInstance> page = new Page<>(
            (Integer) params.getOrDefault("pageNum", 1),
            (Integer) params.getOrDefault("pageSize", 10)
        );

        LambdaQueryWrapper<FlowInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(params.containsKey("flowDefinitionId"), FlowInstance::getFlowDefinitionId, params.get("flowDefinitionId"))
            .like(params.containsKey("initiator"), FlowInstance::getInitiator, params.get("initiator"))
            .eq(params.containsKey("status"), FlowInstance::getStatus, params.get("status"))
            .orderByDesc(FlowInstance::getStartTime);

        return flowInstanceMapper.selectPage(page, wrapper);
    }

    @Override
    public Map<String, Object> getDetail(Long id) {
        FlowInstance instance = flowInstanceMapper.selectById(id);
        if (instance == null) {
            throw new BusinessException("流程实例不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("instance", instance);

        // 获取任务列表
        LambdaQueryWrapper<FlowTask> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(FlowTask::getInstanceId, id)
            .orderByAsc(FlowTask::getCreateTime);
        List<FlowTask> tasks = flowTaskMapper.selectList(taskWrapper);
        result.put("nodes", tasks);

        return result;
    }

    @Override
    @Transactional
    public Long start(Long flowDefinitionId, Map<String, Object> formData) {
        // 获取流程定义
        FlowDefinition definition = flowDefinitionMapper.selectById(flowDefinitionId);
        if (definition == null) {
            throw new BusinessException("流程定义不存在");
        }

        if (definition.getStatus() != 1) {
            throw new BusinessException("流程未发布，不能发起");
        }

        // 解析节点配置
        List<Map<String, Object>> nodes;
        try {
            nodes = objectMapper.readValue(definition.getNodes(), new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new BusinessException("流程节点配置解析失败");
        }

        // 找到开始节点和下一个用户任务节点
        Map<String, Object> startNode = nodes.stream()
            .filter(n -> "start".equals(n.get("nodeType")))
            .findFirst()
            .orElseThrow(() -> new BusinessException("流程缺少开始节点"));

        String startNodeId = (String) startNode.get("nodeId");

        // 解析流转配置，找到下一个节点
        List<Map<String, Object>> edges;
        try {
            edges = objectMapper.readValue(definition.getEdges(), new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new BusinessException("流程流转配置解析失败");
        }

        Map<String, Object> nextEdge = edges.stream()
            .filter(e -> startNodeId.equals(e.get("source")))
            .findFirst()
            .orElse(null);

        if (nextEdge == null) {
            throw new BusinessException("流程配置错误，开始节点没有后续节点");
        }

        String nextNodeId = (String) nextEdge.get("target");
        Map<String, Object> nextNode = nodes.stream()
            .filter(n -> nextNodeId.equals(n.get("nodeId")))
            .findFirst()
            .orElseThrow(() -> new BusinessException("流程节点配置错误"));

        // 创建流程实例
        FlowInstance instance = new FlowInstance();
        instance.setTenantId(TenantContextHolder.getTenantId());
        instance.setFlowDefinitionId(flowDefinitionId);
        instance.setFlowName(definition.getFlowName());
        instance.setFlowCode(definition.getFlowCode());
        instance.setFormDataId(0L); // TODO: 保存表单数据后获取ID
        instance.setInitiator(TenantContextHolder.getTenantId()); // TODO: 获取当前用户
        instance.setCurrentNode(nextNodeId);
        instance.setStatus("running");
        instance.setStartTime(LocalDateTime.now());
        flowInstanceMapper.insert(instance);

        // 创建第一个任务
        if ("user_task".equals(nextNode.get("nodeType"))) {
            FlowTask task = createTask(instance, nextNode);
            flowTaskMapper.insert(task);
        }

        return instance.getId();
    }

    private FlowTask createTask(FlowInstance instance, Map<String, Object> nodeConfig) {
        FlowTask task = new FlowTask();
        task.setTenantId(instance.getTenantId());
        task.setInstanceId(instance.getId());
        task.setNodeId((String) nodeConfig.get("nodeId"));
        task.setNodeName((String) nodeConfig.get("nodeName"));
        task.setAssignee(TenantContextHolder.getTenantId()); // TODO: 根据配置计算审批人

        Map<String, Object> config = (Map<String, Object>) nodeConfig.get("config");
        if (config != null) {
            task.setAssignType((String) config.get("assignType"));
            try {
                task.setAssignValue(objectMapper.writeValueAsString(config.get("assignValue")));
            } catch (Exception e) {
                task.setAssignValue("[]");
            }

            // 设置截止时间
            Integer timeout = (Integer) config.get("timeout");
            if (timeout != null && timeout > 0) {
                task.setDeadline(LocalDateTime.now().plusHours(timeout));
            }
        }

        task.setStatus("pending");
        task.setCreateTime(LocalDateTime.now());
        return task;
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        FlowInstance instance = flowInstanceMapper.selectById(id);
        if (instance == null) {
            throw new BusinessException("流程实例不存在");
        }

        if (!"running".equals(instance.getStatus())) {
            throw new BusinessException("只有进行中的流程可以取消");
        }

        instance.setStatus("cancelled");
        instance.setEndTime(LocalDateTime.now());
        instance.setDuration((int) ChronoUnit.SECONDS.between(instance.getStartTime(), instance.getEndTime()));
        flowInstanceMapper.updateById(instance);

        // 取消所有待处理任务
        LambdaQueryWrapper<FlowTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FlowTask::getInstanceId, id)
            .eq(FlowTask::getStatus, "pending");
        List<FlowTask> tasks = flowTaskMapper.selectList(wrapper);
        for (FlowTask task : tasks) {
            task.setStatus("cancelled");
            task.setActionTime(LocalDateTime.now());
            flowTaskMapper.updateById(task);
        }
    }

    @Override
    public Map<String, Object> getFormData(Long instanceId) {
        FlowInstance instance = flowInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new BusinessException("流程实例不存在");
        }

        // TODO: 根据formDataId获取表单数据
        Map<String, Object> result = new HashMap<>();
        result.put("instanceId", instanceId);
        result.put("flowName", instance.getFlowName());
        result.put("formData", new HashMap<>());
        return result;
    }
}