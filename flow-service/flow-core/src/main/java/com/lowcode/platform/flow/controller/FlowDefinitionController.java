package com.lowcode.platform.flow.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.flow.entity.FlowDefinition;
import com.lowcode.platform.flow.service.FlowDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 流程定义控制器
 */
@RestController
@RequestMapping("/flow")
@RequiredArgsConstructor
public class FlowDefinitionController {

    private final FlowDefinitionService flowDefinitionService;

    /**
     * 分页查询流程定义
     */
    @GetMapping("/list")
    public R<Page<FlowDefinition>> list(@RequestParam Map<String, Object> params) {
        return R.ok(flowDefinitionService.listPage(params));
    }

    /**
     * 根据ID获取流程定义
     */
    @GetMapping("/{id}")
    public R<FlowDefinition> get(@PathVariable Long id) {
        return R.ok(flowDefinitionService.getById(id));
    }

    /**
     * 创建流程定义
     */
    @PostMapping
    public R<Long> create(@RequestBody FlowDefinition definition) {
        return R.ok(flowDefinitionService.create(definition));
    }

    /**
     * 更新流程定义
     */
    @PutMapping
    public R<Void> update(@RequestBody FlowDefinition definition) {
        flowDefinitionService.update(definition);
        return R.ok();
    }

    /**
     * 删除流程定义
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        flowDefinitionService.delete(id);
        return R.ok();
    }

    /**
     * 发布流程
     */
    @PostMapping("/{id}/publish")
    public R<Void> publish(@PathVariable Long id) {
        flowDefinitionService.publish(id);
        return R.ok();
    }

    /**
     * 获取可选表单列表
     */
    @GetMapping("/forms")
    public R<List<Map<String, Object>>> getForms() {
        return R.ok(flowDefinitionService.getForms());
    }

    /**
     * 获取审批人选项
     */
    @GetMapping("/assign-options")
    public R<List<Map<String, Object>>> getAssignOptions() {
        return R.ok(flowDefinitionService.getAssignOptions());
    }
}