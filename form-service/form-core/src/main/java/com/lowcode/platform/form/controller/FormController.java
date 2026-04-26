package com.lowcode.platform.form.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.form.entity.FieldConfig;
import com.lowcode.platform.form.entity.FormDefinition;
import com.lowcode.platform.form.entity.FormData;
import com.lowcode.platform.form.service.FormDefinitionService;
import com.lowcode.platform.form.service.FormDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 表单管理控制器
 */
@Tag(name = "表单管理")
@RestController
@RequestMapping("/form")
@RequiredArgsConstructor
public class FormController {

    private final FormDefinitionService formService;
    private final FormDataService dataService;

    @Operation(summary = "表单列表")
    @GetMapping("/list")
    public R<Page<FormDefinition>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            FormDefinition query) {
        Page<FormDefinition> page = new Page<>(pageNum, pageSize);
        return R.ok(formService.selectPage(page, query));
    }

    @Operation(summary = "表单详情")
    @GetMapping("/{id}")
    public R<FormDefinition> getInfo(@PathVariable Long id) {
        FormDefinition form = formService.getById(id);
        return R.ok(form);
    }

    @Operation(summary = "创建表单")
    @PostMapping
    public R<Void> create(@RequestBody FormDefinition form) {
        formService.createForm(form);
        return R.ok();
    }

    @Operation(summary = "修改表单")
    @PutMapping
    public R<Void> update(@RequestBody FormDefinition form) {
        formService.updateForm(form);
        return R.ok();
    }

    @Operation(summary = "发布表单")
    @PostMapping("/{id}/publish")
    public R<Void> publish(@PathVariable Long id) {
        formService.publishForm(id);
        return R.ok();
    }

    @Operation(summary = "删除表单")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        formService.deleteForm(id);
        return R.ok();
    }

    @Operation(summary = "获取字段配置")
    @GetMapping("/{id}/fields")
    public R<List<?>> getFields(@PathVariable Long id) {
        return R.ok(formService.getFieldConfig(id));
    }

    @Operation(summary = "更新字段配置")
    @PutMapping("/{id}/fields")
    public R<Void> updateFields(@PathVariable Long id, @RequestBody List<FieldConfig> fields) {
        formService.updateFieldConfig(id, fields);
        return R.ok();
    }

    @Operation(summary = "更新布局配置")
    @PutMapping("/{id}/layout")
    public R<Void> updateLayout(@PathVariable Long id, @RequestBody String layoutConfig) {
        formService.updateLayoutConfig(id, layoutConfig);
        return R.ok();
    }

    // ==================== 表单数据 ====================

    @Operation(summary = "提交表单数据")
    @PostMapping("/{id}/data")
    public R<Long> submitData(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        Long dataId = dataService.submitFormData(id, data);
        return R.ok(dataId);
    }

    @Operation(summary = "更新表单数据")
    @PutMapping("/{id}/data/{dataId}")
    public R<Void> updateData(@PathVariable Long id, @PathVariable Long dataId, @RequestBody Map<String, Object> data) {
        dataService.updateFormData(id, dataId, data);
        return R.ok();
    }

    @Operation(summary = "删除表单数据")
    @DeleteMapping("/{id}/data/{dataId}")
    public R<Void> deleteData(@PathVariable Long id, @PathVariable Long dataId) {
        dataService.deleteFormData(id, dataId);
        return R.ok();
    }

    @Operation(summary = "查询表单数据")
    @GetMapping("/{id}/data/{dataId}")
    public R<FormData> getData(@PathVariable Long id, @PathVariable Long dataId) {
        return R.ok(dataService.getFormData(id, dataId));
    }

    @Operation(summary = "分页查询表单数据")
    @GetMapping("/{id}/data")
    public R<Page<Map<String, Object>>> queryData(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Map<String, Object> query) {
        return R.ok(dataService.queryFormDataPage(id, query, pageNum, pageSize));
    }

    @Operation(summary = "查询表单数据列表")
    @GetMapping("/{id}/data/list")
    public R<List<Map<String, Object>>> queryDataList(@PathVariable Long id, @RequestParam(required = false) Map<String, Object> query) {
        return R.ok(dataService.queryFormDataList(id, query));
    }
}