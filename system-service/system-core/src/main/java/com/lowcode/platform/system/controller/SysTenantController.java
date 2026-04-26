package com.lowcode.platform.system.controller;

import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.common.core.result.PageResult;
import com.lowcode.platform.system.entity.SysTenant;
import com.lowcode.platform.system.entity.SysPackage;
import com.lowcode.platform.system.service.SysTenantService;
import com.lowcode.platform.system.mapper.SysPackageMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户管理控制器
 */
@Tag(name = "租户管理")
@RestController
@RequestMapping("/system/tenant")
@RequiredArgsConstructor
public class SysTenantController {

    private final SysTenantService tenantService;
    private final SysPackageMapper packageMapper;

    @Operation(summary = "分页查询租户")
    @GetMapping("/list")
    public R<PageResult<SysTenant>> list(
            @RequestParam(required = false) String tenantName,
            @RequestParam(required = false) String tenantCode,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        SysTenant query = new SysTenant();
        query.setTenantName(tenantName);
        query.setTenantCode(tenantCode);
        query.setStatus(status);
        return R.ok(tenantService.queryPageList(query, pageNum, pageSize));
    }

    @Operation(summary = "获取租户详情")
    @GetMapping("/{id}")
    public R<SysTenant> getInfo(@PathVariable Long id) {
        return R.ok(tenantService.getById(id));
    }

    @Operation(summary = "新增租户")
    @PostMapping
    public R<Long> add(@RequestBody SysTenant tenant) {
        Long id = tenantService.createTenant(tenant);
        return R.ok(id);
    }

    @Operation(summary = "修改租户")
    @PutMapping
    public R<Void> edit(@RequestBody SysTenant tenant) {
        tenantService.updateTenant(tenant);
        return R.ok();
    }

    @Operation(summary = "删除租户")
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return R.ok();
    }

    @Operation(summary = "启用租户")
    @PutMapping("/{id}/enable")
    public R<Void> enable(@PathVariable Long id) {
        tenantService.updateStatus(id, 0);
        return R.ok();
    }

    @Operation(summary = "禁用租户")
    @PutMapping("/{id}/disable")
    public R<Void> disable(@PathVariable Long id) {
        tenantService.updateStatus(id, 1);
        return R.ok();
    }

    @Operation(summary = "获取所有套餐")
    @GetMapping("/packages")
    public R<List<SysPackage>> listPackages() {
        List<SysPackage> packages = packageMapper.selectList(null);
        return R.ok(packages);
    }

    @Operation(summary = "获取所有启用的租户")
    @GetMapping("/enabled")
    public R<List<SysTenant>> listEnabled() {
        return R.ok(tenantService.listEnabledTenants());
    }

    @Operation(summary = "检查租户编码是否存在")
    @GetMapping("/checkCode")
    public R<Boolean> checkCode(@RequestParam String tenantCode, @RequestParam(required = false) Long excludeId) {
        return R.ok(tenantService.checkTenantCodeExists(tenantCode, excludeId));
    }

    @Operation(summary = "初始化租户数据")
    @PostMapping("/{id}/init")
    public R<Void> initTenant(@PathVariable Long id,
                              @RequestParam(defaultValue = "admin") String adminUsername,
                              @RequestParam(defaultValue = "admin123") String adminPassword) {
        tenantService.initTenantData(id, adminUsername, adminPassword);
        return R.ok();
    }
}