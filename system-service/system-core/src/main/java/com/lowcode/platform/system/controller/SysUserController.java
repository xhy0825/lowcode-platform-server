package com.lowcode.platform.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.system.entity.SysUser;
import com.lowcode.platform.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    @Operation(summary = "用户列表")
    @GetMapping("/list")
    public R<Page<SysUser>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SysUser query) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        return R.ok(userService.selectPage(page, query));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    public R<SysUser> getInfo(@PathVariable Long id) {
        return R.ok(userService.getById(id));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public R<Void> add(@RequestBody SysUser user, @RequestParam(required = false) List<Long> roleIds) {
        userService.createUser(user, roleIds);
        return R.ok();
    }

    @Operation(summary = "修改用户")
    @PutMapping
    public R<Void> edit(@RequestBody SysUser user, @RequestParam(required = false) List<Long> roleIds) {
        userService.updateUser(user, roleIds);
        return R.ok();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        userService.deleteUser(id);
        return R.ok();
    }

    @Operation(summary = "重置密码")
    @PutMapping("/{id}/password")
    public R<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return R.ok();
    }

    @Operation(summary = "修改状态")
    @PutMapping("/{id}/status")
    public R<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser user = userService.getById(id);
        user.setStatus(status);
        userService.updateById(user);
        return R.ok();
    }
}