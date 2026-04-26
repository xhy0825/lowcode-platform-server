package com.lowcode.platform.flow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.exception.BusinessException;
import com.lowcode.platform.flow.entity.FlowDefinition;
import com.lowcode.platform.flow.mapper.FlowDefinitionMapper;
import com.lowcode.platform.flow.service.FlowDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.StringUtils;
import java.util.List;
import java.util.Map;

/**
 * 流程定义服务实现
 */
@Service
@RequiredArgsConstructor
public class FlowDefinitionServiceImpl implements FlowDefinitionService {

    private final FlowDefinitionMapper flowDefinitionMapper;

    @Override
    public Page<FlowDefinition> listPage(Map<String, Object> params) {
        Page<FlowDefinition> page = new Page<>(
            (Integer) params.getOrDefault("pageNum", 1),
            (Integer) params.getOrDefault("pageSize", 10)
        );

        LambdaQueryWrapper<FlowDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(params.containsKey("flowName"), FlowDefinition::getFlowName, params.get("flowName"))
            .like(params.containsKey("flowCode"), FlowDefinition::getFlowCode, params.get("flowCode"))
            .eq(params.containsKey("status"), FlowDefinition::getStatus, params.get("status"))
            .orderByDesc(FlowDefinition::getCreatedTime);

        return flowDefinitionMapper.selectPage(page, wrapper);
    }

    @Override
    public FlowDefinition getById(Long id) {
        return flowDefinitionMapper.selectById(id);
    }

    @Override
    @Transactional
    public Long create(FlowDefinition definition) {
        // 校验流程编码唯一性
        LambdaQueryWrapper<FlowDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FlowDefinition::getFlowCode, definition.getFlowCode());
        if (flowDefinitionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("流程编码已存在");
        }

        definition.setStatus(0);
        definition.setVersion(1);
        flowDefinitionMapper.insert(definition);
        return definition.getId();
    }

    @Override
    @Transactional
    public void update(FlowDefinition definition) {
        FlowDefinition existing = flowDefinitionMapper.selectById(definition.getId());
        if (existing == null) {
            throw new BusinessException("流程不存在");
        }

        // 已发布的流程不能修改
        if (existing.getStatus() == 1) {
            throw new BusinessException("已发布的流程不能修改，请创建新版本");
        }

        flowDefinitionMapper.updateById(definition);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        FlowDefinition definition = flowDefinitionMapper.selectById(id);
        if (definition == null) {
            throw new BusinessException("流程不存在");
        }

        if (definition.getStatus() == 1) {
            throw new BusinessException("已发布的流程不能删除");
        }

        flowDefinitionMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void publish(Long id) {
        FlowDefinition definition = flowDefinitionMapper.selectById(id);
        if (definition == null) {
            throw new BusinessException("流程不存在");
        }

        if (definition.getStatus() == 1) {
            throw new BusinessException("流程已发布");
        }

        // 校验节点配置
        if (!StringUtils.hasText(definition.getNodes())) {
            throw new BusinessException("请先配置流程节点");
        }

        definition.setStatus(1);
        flowDefinitionMapper.updateById(definition);
    }

    @Override
    public List<Map<String, Object>> getForms() {
        // TODO: 调用表单服务获取已发布的表单列表
        return List.of(
            Map.of("id", 1, "formName", "请假申请表"),
            Map.of("id", 2, "formName", "报销申请表"),
            Map.of("id", 3, "formName", "采购申请表")
        );
    }

    @Override
    public List<Map<String, Object>> getAssignOptions() {
        // TODO: 获取用户、角色、部门列表
        return List.of(
            Map.of("type", "user", "id", "user1", "name", "张三"),
            Map.of("type", "user", "id", "user2", "name", "李四"),
            Map.of("type", "role", "id", "admin", "name", "管理员"),
            Map.of("type", "role", "id", "dept_leader", "name", "部门主管"),
            Map.of("type", "dept", "id", "dept1", "name", "研发部"),
            Map.of("type", "dept", "id", "dept2", "name", "市场部")
        );
    }
}