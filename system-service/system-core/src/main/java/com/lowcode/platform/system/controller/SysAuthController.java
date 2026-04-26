package com.lowcode.platform.system.controller;

import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.common.core.context.TenantContextHolder;
import com.lowcode.platform.system.entity.SysUser;
import com.lowcode.platform.system.mapper.SysUserMapper;
import com.lowcode.platform.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证控制器
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SysAuthController {

    private final SysUserService userService;
    private final SysUserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        // 查询用户
        SysUser user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            return R.fail("用户不存在");
        }
        if (user.getStatus() != 0) {
            return R.fail("用户已禁用");
        }
        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return R.fail("密码错误");
        }
        // 设置租户上下文
        TenantContextHolder.setTenantId(user.getTenantId());
        // 获取权限
        List<String> roles = userService.selectRoleCodesByUserId(user.getId());
        List<String> permissions = userService.selectPermissionCodesByUserId(user.getId());
        // 生成Token (简化版，实际应使用JWT)
        String token = generateToken(user);
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        result.put("tenantId", user.getTenantId());
        result.put("roles", roles);
        result.put("permissions", permissions);
        return R.ok(result);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/user")
    public R<SysUser> getCurrentUser() {
        // 从Token解析用户ID (简化版)
        Long userId = 1L; // TODO: 从安全上下文获取
        return R.ok(userService.getById(userId));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public R<Void> logout() {
        // 清除Token相关信息
        return R.ok();
    }

    private String generateToken(SysUser user) {
        // 简化版Token生成，实际应使用JWT
        return "token_" + user.getId() + "_" + System.currentTimeMillis();
    }

    /**
     * 登录DTO
     */
    public static class LoginDTO {
        private String username;
        private String password;
        private String captcha;
        private String captchaKey;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getCaptcha() { return captcha; }
        public void setCaptcha(String captcha) { this.captcha = captcha; }
        public String getCaptchaKey() { return captchaKey; }
        public void setCaptchaKey(String captchaKey) { this.captchaKey = captchaKey; }
    }
}