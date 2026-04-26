package com.lowcode.platform.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.system.entity.SysDictData;
import com.lowcode.platform.system.entity.SysDictType;
import com.lowcode.platform.system.service.SysDictDataService;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典控制器
 */
@Tag(name = "数据字典")
@RestController
@RequestMapping("/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final IService<SysDictType> dictTypeService;
    private final SysDictDataService dictDataService;

    @Operation(summary = "字典类型列表")
    @GetMapping("/type/list")
    public R<Page<SysDictType>> typeList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysDictType> page = new Page<>(pageNum, pageSize);
        return R.ok(dictTypeService.page(page));
    }

    @Operation(summary = "新增字典类型")
    @PostMapping("/type")
    public R<Void> addType(@RequestBody SysDictType dictType) {
        dictTypeService.save(dictType);
        return R.ok();
    }

    @Operation(summary = "修改字典类型")
    @PutMapping("/type")
    public R<Void> editType(@RequestBody SysDictType dictType) {
        dictTypeService.updateById(dictType);
        return R.ok();
    }

    @Operation(summary = "删除字典类型")
    @DeleteMapping("/type/{id}")
    public R<Void> removeType(@PathVariable Long id) {
        dictTypeService.removeById(id);
        return R.ok();
    }

    @Operation(summary = "根据字典类型获取数据")
    @GetMapping("/data/{dictType}")
    public R<List<SysDictData>> getDataByType(@PathVariable String dictType) {
        return R.ok(dictDataService.selectByDictType(dictType));
    }

    @Operation(summary = "新增字典数据")
    @PostMapping("/data")
    public R<Void> addData(@RequestBody SysDictData dictData) {
        dictDataService.save(dictData);
        dictDataService.refreshDictCache(dictData.getDictType());
        return R.ok();
    }

    @Operation(summary = "修改字典数据")
    @PutMapping("/data")
    public R<Void> editData(@RequestBody SysDictData dictData) {
        dictDataService.updateById(dictData);
        dictDataService.refreshDictCache(dictData.getDictType());
        return R.ok();
    }

    @Operation(summary = "删除字典数据")
    @DeleteMapping("/data/{id}")
    public R<Void> removeData(@PathVariable Long id) {
        SysDictData dictData = dictDataService.getById(id);
        dictDataService.removeById(id);
        dictDataService.refreshDictCache(dictData.getDictType());
        return R.ok();
    }

    @Operation(summary = "刷新字典缓存")
    @PostMapping("/data/refresh/{dictType}")
    public R<Void> refreshCache(@PathVariable String dictType) {
        dictDataService.refreshDictCache(dictType);
        return R.ok();
    }
}