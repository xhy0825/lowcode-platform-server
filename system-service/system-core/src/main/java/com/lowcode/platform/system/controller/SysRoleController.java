package com.lowcode.platform.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.system.entity.SysRole;
import com.lowcode.platform.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService roleService;

    @Operation(summary = "角色列表")
    @GetMapping("/list")
    public R<Page<SysRole>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SysRole query) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        return R.ok(roleService.selectPage(page, query));
    }

    @Operation(summary = "角色详情")
    @GetMapping("/{id}")
    public R<SysRole> getInfo(@PathVariable Long id) {
        SysRole role = roleService.getById(id);
        return R.ok(role);
    }

    @Operation(summary = "角色权限ID列表")
    @GetMapping("/{id}/permissions")
    public R<List<Long>> getPermissions(@PathVariable Long id) {
        return R.ok(roleService.selectPermissionIdsByRoleId(id));
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public R<Void> add(@RequestBody SysRole role,
                       @RequestParam(required = false) List<Long> permissionIds,
                       @RequestParam(required = false) List<Long> deptIds) {
        roleService.createRole(role, permissionIds, deptIds);
        return R.ok();
    }

    @Operation(summary = "修改角色")
    @PutMapping
    public R<Void> edit(@RequestBody SysRole role,
                        @RequestParam(required = false) List<Long> permissionIds,
                        @RequestParam(required = false) List<Long> deptIds) {
        roleService.updateRole(role, permissionIds, deptIds);
        return R.ok();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        roleService.deleteRole(id);
        return R.ok();
    }
}