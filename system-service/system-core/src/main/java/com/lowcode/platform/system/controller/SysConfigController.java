package com.lowcode.platform.system.controller;

import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.system.entity.SysConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.platform.system.mapper.SysConfigMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置控制器
 */
@Tag(name = "系统配置")
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final IService<SysConfig> configService;
    private final SysConfigMapper configMapper;

    @Operation(summary = "配置列表")
    @GetMapping("/list")
    public R<List<SysConfig>> list() {
        return R.ok(configService.list());
    }

    @Operation(summary = "根据键获取值")
    @GetMapping("/key/{key}")
    public R<String> getValueByKey(@PathVariable String key) {
        SysConfig config = configMapper.selectByConfigKey(key);
        return R.ok(config != null ? config.getConfigValue() : null);
    }

    @Operation(summary = "新增配置")
    @PostMapping
    public R<Void> add(@RequestBody SysConfig config) {
        configService.save(config);
        return R.ok();
    }

    @Operation(summary = "修改配置")
    @PutMapping
    public R<Void> edit(@RequestBody SysConfig config) {
        configService.updateById(config);
        return R.ok();
    }

    @Operation(summary = "删除配置")
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        configService.removeById(id);
        return R.ok();
    }
}