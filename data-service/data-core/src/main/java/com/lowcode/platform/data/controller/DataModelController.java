package com.lowcode.platform.data.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.data.entity.DataModel;
import com.lowcode.platform.data.entity.DataModelField;
import com.lowcode.platform.data.service.DataModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据模型控制器
 */
@Tag(name = "数据模型管理")
@RestController
@RequestMapping("/model")
@RequiredArgsConstructor
public class DataModelController {

    private final DataModelService modelService;

    @Operation(summary = "模型列表")
    @GetMapping("/list")
    public R<Page<DataModel>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            DataModel query) {
        Page<DataModel> page = new Page<>(pageNum, pageSize);
        return R.ok(modelService.selectPage(page, query));
    }

    @Operation(summary = "模型详情")
    @GetMapping("/{id}")
    public R<DataModel> getInfo(@PathVariable Long id) {
        return R.ok(modelService.getById(id));
    }

    @Operation(summary = "创建模型")
    @PostMapping
    public R<Void> create(@RequestBody DataModel model) {
        modelService.createModel(model);
        return R.ok();
    }

    @Operation(summary = "配置字段")
    @PutMapping("/{id}/fields")
    public R<Void> updateFields(@PathVariable Long id, @RequestBody List<DataModelField> fields) {
        modelService.updateFields(id, fields);
        return R.ok();
    }

    @Operation(summary = "生成DDL")
    @PostMapping("/{id}/generate")
    public R<String> generateDdl(@PathVariable Long id) {
        return R.ok(modelService.generateDdl(id));
    }

    @Operation(summary = "执行建表")
    @PostMapping("/{id}/execute")
    public R<Void> executeDdl(@PathVariable Long id) {
        modelService.executeDdl(id);
        return R.ok();
    }

    @Operation(summary = "预览表结构")
    @GetMapping("/{id}/preview")
    public R<String> preview(@PathVariable Long id) {
        return R.ok(modelService.previewTable(id));
    }

    @Operation(summary = "发布模型")
    @PostMapping("/{id}/publish")
    public R<Void> publish(@PathVariable Long id) {
        modelService.publishModel(id);
        return R.ok();
    }

    @Operation(summary = "删除模型")
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        modelService.deleteModel(id);
        return R.ok();
    }
}