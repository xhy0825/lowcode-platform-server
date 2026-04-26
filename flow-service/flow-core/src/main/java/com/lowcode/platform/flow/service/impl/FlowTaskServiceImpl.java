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
import com.lowcode.platform.flow.service.FlowTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * 流程任务服务实现
 */
@Service
@RequiredArgsConstructor
public class FlowTaskServiceImpl implements FlowTaskService {

    private final FlowTaskMapper flowTaskMapper;
    private final FlowInstanceMapper flowInstanceMapper;
    private final FlowDefinitionMapper flowDefinitionMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Page<FlowTask> listPage(Map<String, Object> params) {
        Page<FlowTask> page = new Page<>(
            (Integer) params.getOrDefault("pageNum", 1),
            (Integer) params.getOrDefault("pageSize", 10)
        );

        String taskType = (String) params.getOrDefault("taskType", "pending");
        LambdaQueryWrapper<FlowTask> wrapper = new LambdaQueryWrapper<>();

        // 根据任务类型筛选
        if ("pending".equals(taskType)) {
            wrapper.eq(FlowTask::getStatus, "pending");
            // TODO: 添加当前用户过滤
        } else if ("done".equals(taskType)) {
            wrapper.ne(FlowTask::getStatus, "pending");
        }

        wrapper.like(params.containsKey("flowName"), FlowTask::getNodeName, params.get("flowName"))
            .orderByDesc(FlowTask::getCreateTime);

        return flowTaskMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void handle(Long taskId, String action, String comment, String delegateUser) {
        FlowTask task = flowTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        if (!"pending".equals(task.getStatus())) {
            throw new BusinessException("任务已处理");
        }

        FlowInstance instance = flowInstanceMapper.selectById(task.getInstanceId());
        if (instance == null) {
            throw new BusinessException("流程实例不存在");
        }

        task.setAction(action);
        task.setComment(comment);
        task.setActionTime(LocalDateTime.now());

        switch (action) {
            case "approve":
                task.setStatus("approved");
                // 推进流程到下一个节点
                advanceFlow(instance, task);
                break;

            case "reject":
                task.setStatus("rejected");
                // 根据配置处理驳回
                handleReject(instance, task);
                break;

            case "delegate":
                if (delegateUser == null || delegateUser.isEmpty()) {
                    throw new BusinessException("请选择转办人员");
                }
                task.setStatus("delegated");
                task.setDelegateUser(delegateUser);
                // 创建新任务给转办人
                createDelegatedTask(task, delegateUser);
                break;

            default:
                throw new BusinessException("未知的处理动作");
        }

        flowTaskMapper.updateById(task);
    }

    private void advanceFlow(FlowInstance instance, FlowTask currentTask) {
        FlowDefinition definition = flowDefinitionMapper.selectById(instance.getFlowDefinitionId());
        if (definition == null) {
            return;
        }

        try {
            List<Map<String, Object>> nodes = objectMapper.readValue(definition.getNodes(), new TypeReference<List<Map<String, Object>>>() {});
            List<Map<String, Object>> edges = objectMapper.readValue(definition.getEdges(), new TypeReference<List<Map<String, Object>>>() {});

            // 找到当前节点的下一个节点
            Map<String, Object> nextEdge = edges.stream()
                .filter(e -> currentTask.getNodeId().equals(e.get("source")))
                .findFirst()
                .orElse(null);

            if (nextEdge == null) {
                // 没有下一个节点，流程结束
                completeFlow(instance);
                return;
            }

            String nextNodeId = (String) nextEdge.get("target");
            Map<String, Object> nextNode = nodes.stream()
                .filter(n -> nextNodeId.equals(n.get("nodeId")))
                .findFirst()
                .orElse(null);

            if (nextNode == null) {
                completeFlow(instance);
                return;
            }

            String nextNodeType = (String) nextNode.get("nodeType");

            if ("end".equals(nextNodeType)) {
                // 到达结束节点
                completeFlow(instance);
            } else if ("user_task".equals(nextNodeType)) {
                // 创建新的审批任务
                FlowTask newTask = createNewTask(instance, nextNode);
                flowTaskMapper.insert(newTask);

                instance.setCurrentNode(nextNodeId);
                flowInstanceMapper.updateById(instance);
            } else if ("condition".equals(nextNodeType)) {
                // 处理条件分支
                handleConditionBranch(instance, nodes, edges, nextNode);
            } else if ("parallel".equals(nextNodeType)) {
                // 处理并行分支
                handleParallelBranch(instance, nodes, edges, nextNode);
            } else if ("join".equals(nextNodeType)) {
                // 处理会签汇聚
                handleJoinNode(instance, nextNode);
            }

        } catch (Exception e) {
            throw new BusinessException("流程推进失败: " + e.getMessage());
        }
    }

    private void completeFlow(FlowInstance instance) {
        instance.setStatus("completed");
        instance.setEndTime(LocalDateTime.now());
        instance.setDuration((int) ChronoUnit.SECONDS.between(instance.getStartTime(), instance.getEndTime()));
        flowInstanceMapper.updateById(instance);
    }

    private void handleReject(FlowInstance instance, FlowTask task) {
        // 获取节点配置中的驳回动作
        FlowDefinition definition = flowDefinitionMapper.selectById(instance.getFlowDefinitionId());
        if (definition == null) {
            instance.setStatus("rejected");
            instance.setEndTime(LocalDateTime.now());
            instance.setDuration((int) ChronoUnit.SECONDS.between(instance.getStartTime(), instance.getEndTime()));
            flowInstanceMapper.updateById(instance);
            return;
        }

        try {
            List<Map<String, Object>> nodes = objectMapper.readValue(definition.getNodes(), new TypeReference<List<Map<String, Object>>>() {});

            Map<String, Object> currentNode = nodes.stream()
                .filter(n -> task.getNodeId().equals(n.get("nodeId")))
                .findFirst()
                .orElse(null);

            if (currentNode == null) {
                instance.setStatus("rejected");
                flowInstanceMapper.updateById(instance);
                return;
            }

            Map<String, Object> config = (Map<String, Object>) currentNode.get("config");
            String rejectAction = config != null ? (String) config.get("rejectAction") : "back";

            switch (rejectAction) {
                case "back":
                    // 退回到上一个节点
                    backToPreviousNode(instance, task);
                    break;
                case "restart":
                    // 退回到开始
                    instance.setStatus("rejected");
                    instance.setEndTime(LocalDateTime.now());
                    instance.setDuration((int) ChronoUnit.SECONDS.between(instance.getStartTime(), instance.getEndTime()));
                    flowInstanceMapper.updateById(instance);
                    break;
                case "end":
                    // 结束流程
                    instance.setStatus("rejected");
                    instance.setEndTime(LocalDateTime.now());
                    instance.setDuration((int) ChronoUnit.SECONDS.between(instance.getStartTime(), instance.getEndTime()));
                    flowInstanceMapper.updateById(instance);
                    break;
            }

        } catch (Exception e) {
            instance.setStatus("rejected");
            flowInstanceMapper.updateById(instance);
        }
    }

    private void backToPreviousNode(FlowInstance instance, FlowTask currentTask) {
        // 找到上一个已完成的任务
        LambdaQueryWrapper<FlowTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FlowTask::getInstanceId, instance.getId())
            .eq(FlowTask::getStatus, "approved")
            .orderByDesc(FlowTask::getActionTime)
            .last("LIMIT 1");

        FlowTask previousTask = flowTaskMapper.selectOne(wrapper);
        if (previousTask == null) {
            instance.setStatus("rejected");
            flowInstanceMapper.updateById(instance);
            return;
        }

        // 创建退回任务
        FlowTask backTask = new FlowTask();
        backTask.setTenantId(instance.getTenantId());
        backTask.setInstanceId(instance.getId());
        backTask.setNodeId(previousTask.getNodeId());
        backTask.setNodeName(previousTask.getNodeName());
        backTask.setAssignType(previousTask.getAssignType());
        backTask.setAssignValue(previousTask.getAssignValue());
        backTask.setAssignee(previousTask.getAssignee());
        backTask.setStatus("pending");
        backTask.setCreateTime(LocalDateTime.now());
        flowTaskMapper.insert(backTask);

        instance.setCurrentNode(previousTask.getNodeId());
        flowInstanceMapper.updateById(instance);
    }

    private FlowTask createNewTask(FlowInstance instance, Map<String, Object> nodeConfig) {
        FlowTask task = new FlowTask();
        task.setTenantId(instance.getTenantId());
        task.setInstanceId(instance.getId());
        task.setNodeId((String) nodeConfig.get("nodeId"));
        task.setNodeName((String) nodeConfig.get("nodeName"));
        task.setStatus("pending");
        task.setCreateTime(LocalDateTime.now());

        Map<String, Object> config = (Map<String, Object>) nodeConfig.get("config");
        if (config != null) {
            task.setAssignType((String) config.get("assignType"));
            try {
                task.setAssignValue(objectMapper.writeValueAsString(config.get("assignValue")));
            } catch (Exception e) {
                task.setAssignValue("[]");
            }

            Integer timeout = (Integer) config.get("timeout");
            if (timeout != null && timeout > 0) {
                task.setDeadline(LocalDateTime.now().plusHours(timeout));
            }
        }

        // TODO: 根据assignType计算实际审批人
        task.setAssignee(TenantContextHolder.getTenantId());

        return task;
    }

    private void createDelegatedTask(FlowTask originalTask, String delegateUser) {
        FlowTask delegatedTask = new FlowTask();
        delegatedTask.setTenantId(originalTask.getTenantId());
        delegatedTask.setInstanceId(originalTask.getInstanceId());
        delegatedTask.setNodeId(originalTask.getNodeId());
        delegatedTask.setNodeName(originalTask.getNodeName());
        delegatedTask.setAssignType("user");
        delegatedTask.setAssignee(delegateUser);
        delegatedTask.setStatus("pending");
        delegatedTask.setCreateTime(LocalDateTime.now());
        flowTaskMapper.insert(delegatedTask);
    }

    private void handleConditionBranch(FlowInstance instance, List<Map<String, Object>> nodes, List<Map<String, Object>> edges, Map<String, Object> conditionNode) {
        // TODO: 根据条件表达式计算分支
        // 简化处理：选择第一个满足条件的分支
        String nodeId = (String) conditionNode.get("nodeId");

        List<Map<String, Object>> outgoingEdges = edges.stream()
            .filter(e -> nodeId.equals(e.get("source")))
            .toList();

        // 默认选择第一条边
        if (!outgoingEdges.isEmpty()) {
            String nextNodeId = (String) outgoingEdges.get(0).get("target");
            Map<String, Object> nextNode = nodes.stream()
                .filter(n -> nextNodeId.equals(n.get("nodeId")))
                .findFirst()
                .orElse(null);

            if (nextNode != null && "user_task".equals(nextNode.get("nodeType"))) {
                FlowTask newTask = createNewTask(instance, nextNode);
                flowTaskMapper.insert(newTask);
                instance.setCurrentNode(nextNodeId);
                flowInstanceMapper.updateById(instance);
            }
        }
    }

    private void handleParallelBranch(FlowInstance instance, List<Map<String, Object>> nodes, List<Map<String, Object>> edges, Map<String, Object> parallelNode) {
        // 为每个分支创建任务
        String nodeId = (String) parallelNode.get("nodeId");

        List<Map<String, Object>> outgoingEdges = edges.stream()
            .filter(e -> nodeId.equals(e.get("source")))
            .toList();

        for (Map<String, Object> edge : outgoingEdges) {
            String nextNodeId = (String) edge.get("target");
            Map<String, Object> nextNode = nodes.stream()
                .filter(n -> nextNodeId.equals(n.get("nodeId")))
                .findFirst()
                .orElse(null);

            if (nextNode != null && "user_task".equals(nextNode.get("nodeType"))) {
                FlowTask newTask = createNewTask(instance, nextNode);
                flowTaskMapper.insert(newTask);
            }
        }

        instance.setCurrentNode(nodeId);
        flowInstanceMapper.updateById(instance);
    }

    private void handleJoinNode(FlowInstance instance, Map<String, Object> joinNode) {
        // 检查所有并行任务是否完成
        String nodeId = (String) joinNode.get("nodeId");

        LambdaQueryWrapper<FlowTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FlowTask::getInstanceId, instance.getId())
            .ne(FlowTask::getStatus, "approved");

        int pendingCount = flowTaskMapper.selectCount(wrapper).intValue();

        if (pendingCount == 0) {
            // 所有任务已完成，推进流程
            advanceFlow(instance, null);
        } else {
            instance.setCurrentNode(nodeId);
            flowInstanceMapper.updateById(instance);
        }
    }

    @Override
    @Transactional
    public void batchApprove(List<Long> taskIds) {
        for (Long taskId : taskIds) {
            handle(taskId, "approve", "批量审批通过", null);
        }
    }

    @Override
    public int getPendingCount(String userId) {
        LambdaQueryWrapper<FlowTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FlowTask::getStatus, "pending")
            .eq(FlowTask::getAssignee, userId);
        return flowTaskMapper.selectCount(wrapper).intValue();
    }
}