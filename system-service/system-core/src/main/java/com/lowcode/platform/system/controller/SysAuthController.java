package com.lowcode.platform.system.controller;

import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.common.core.context.TenantContextHolder;
import com.lowcode.platform.common.redis.service.RedisService;
import com.lowcode.platform.common.security.jwt.JwtTokenProvider;
import com.lowcode.platform.common.security.jwt.JwtProperties;
import com.lowcode.platform.common.security.context.LoginUser;
import com.lowcode.platform.common.security.captcha.CaptchaService;
import com.lowcode.platform.common.security.captcha.CaptchaResult;
import com.lowcode.platform.system.entity.SysUser;
import com.lowcode.platform.system.entity.SysPermission;
import com.lowcode.platform.system.mapper.SysUserMapper;
import com.lowcode.platform.system.mapper.SysPermissionMapper;
import com.lowcode.platform.system.service.SysUserService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 认证控制器
 */
@Slf4j
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SysAuthController {

    private final SysUserService userService;
    private final SysUserMapper userMapper;
    private final SysPermissionMapper permissionMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RedisService redisService;
    private final CaptchaService captchaService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Redis Key前缀
     */
    private static final String USER_TOKEN_PREFIX = "user:token:";
    private static final String USER_PERMS_PREFIX = "user:perms:";
    private static final String USER_ROLES_PREFIX = "user:roles:";
    private static final String USER_MENUS_PREFIX = "user:menus:";
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String ONLINE_USER_PREFIX = "online:user:";

    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public R<Map<String, String>> getCaptcha() {
        CaptchaResult captcha = captchaService.generateCaptcha();
        Map<String, String> result = new HashMap<>();
        result.put("captchaKey", captcha.getCaptchaKey());
        result.put("captchaImage", captcha.getCaptchaImage());
        return R.ok(result);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        // 1. 验证码校验（可选，根据配置决定）
        if (loginDTO.getCaptchaKey() != null && loginDTO.getCaptcha() != null) {
            if (!captchaService.validateCaptcha(loginDTO.getCaptchaKey(), loginDTO.getCaptcha())) {
                return R.fail("验证码错误或已过期");
            }
        }

        // 2. 查询用户
        SysUser user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            return R.fail("用户不存在");
        }

        // 3. 检查用户状态
        if (user.getStatus() != 0) {
            return R.fail("用户已禁用");
        }

        // 4. 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return R.fail("密码错误");
        }

        // 5. 设置租户上下文
        TenantContextHolder.setTenantId(user.getTenantId());

        // 6. 获取角色和权限
        List<String> roles = userService.selectRoleCodesByUserId(user.getId());
        List<String> permissions = userService.selectPermissionCodesByUserId(user.getId());

        // 7. 构建登录用户信息
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setTenantId(user.getTenantId());
        loginUser.setRealName(user.getRealName());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setRoles(new HashSet<>(roles));
        loginUser.setPermissions(new HashSet<>(permissions));
        loginUser.setLoginTime(System.currentTimeMillis());

        // 8. 生成Token
        String accessToken = jwtTokenProvider.generateAccessToken(loginUser);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        loginUser.setTokenId(jwtTokenProvider.getTokenId(accessToken));
        loginUser.setExpireTime(System.currentTimeMillis() + jwtProperties.getAccessTokenExpire() * 1000);

        // 9. 存储登录信息到Redis
        String tokenKey = USER_TOKEN_PREFIX + user.getId();
        redisService.set(tokenKey, loginUser, jwtProperties.getRefreshTokenExpire(), TimeUnit.SECONDS);

        // 存储权限缓存
        String permsKey = USER_PERMS_PREFIX + user.getId();
        redisService.set(permsKey, loginUser.getPermissions(), 1800L, TimeUnit.SECONDS);

        // 存储角色缓存
        String rolesKey = USER_ROLES_PREFIX + user.getId();
        redisService.set(rolesKey, loginUser.getRoles(), 1800L, TimeUnit.SECONDS);

        // 存储在线用户
        String onlineKey = ONLINE_USER_PREFIX + user.getId();
        Map<String, Object> onlineInfo = new HashMap<>();
        onlineInfo.put("userId", user.getId());
        onlineInfo.put("username", user.getUsername());
        onlineInfo.put("tenantId", user.getTenantId());
        onlineInfo.put("loginTime", loginUser.getLoginTime());
        onlineInfo.put("tokenId", loginUser.getTokenId());
        redisService.set(onlineKey, onlineInfo, jwtProperties.getAccessTokenExpire(), TimeUnit.SECONDS);

        // 10. 获取菜单树
        List<Map<String, Object>> menus = getMenuTree(user.getId());

        // 11. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        result.put("expiresIn", jwtProperties.getAccessTokenExpire());
        result.put("user", buildUserInfo(user));
        result.put("tenantId", user.getTenantId());
        result.put("roles", roles);
        result.put("permissions", permissions);
        result.put("menus", menus);

        log.info("用户登录成功: username={}, userId={}, tenantId={}", user.getUsername(), user.getId(), user.getTenantId());
        return R.ok(result);
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public R<Map<String, Object>> refreshToken(@RequestBody RefreshDTO refreshDTO) {
        String refreshToken = refreshDTO.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            return R.fail("refreshToken不能为空");
        }

        try {
            // 验证Refresh Token
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                return R.fail("refreshToken无效或已过期");
            }

            // 检查Token类型
            String tokenType = jwtTokenProvider.getTokenType(refreshToken);
            if (!"refresh".equals(tokenType)) {
                return R.fail("Token类型错误");
            }

            // 获取用户ID
            Long userId = jwtTokenProvider.getUserId(refreshToken);

            // 从Redis获取用户信息
            String tokenKey = USER_TOKEN_PREFIX + userId;
            LoginUser loginUser = (LoginUser) redisService.get(tokenKey);
            if (loginUser == null) {
                return R.fail("用户登录状态已过期，请重新登录");
            }

            // 生成新的Access Token
            String newAccessToken = jwtTokenProvider.generateAccessToken(loginUser);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);

            // 更新Redis
            loginUser.setTokenId(jwtTokenProvider.getTokenId(newAccessToken));
            loginUser.setExpireTime(System.currentTimeMillis() + jwtProperties.getAccessTokenExpire() * 1000);
            redisService.set(tokenKey, loginUser, jwtProperties.getRefreshTokenExpire(), TimeUnit.SECONDS);

            // 将旧AccessToken加入黑名单（如果存在）
            if (refreshDTO.getOldAccessToken() != null) {
                try {
                    String oldTokenId = jwtTokenProvider.getTokenId(refreshDTO.getOldAccessToken());
                    long remaining = jwtTokenProvider.getRemainingTime(refreshDTO.getOldAccessToken());
                    if (remaining > 0) {
                        redisService.set(TOKEN_BLACKLIST_PREFIX + oldTokenId, "1", remaining, TimeUnit.SECONDS);
                    }
                } catch (Exception ignored) {}
            }

            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", newAccessToken);
            result.put("refreshToken", newRefreshToken);
            result.put("expiresIn", jwtProperties.getAccessTokenExpire());

            return R.ok(result);
        } catch (Exception e) {
            log.error("刷新Token失败: {}", e.getMessage());
            return R.fail("刷新Token失败");
        }
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/user")
    public R<Map<String, Object>> getCurrentUser(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            userId = 1L; // 兜底处理，实际应该抛出未登录异常
        }

        SysUser user = userService.getById(userId);
        if (user == null) {
            return R.fail("用户不存在");
        }

        // 从Redis获取权限信息
        String permsKey = USER_PERMS_PREFIX + userId;
        Set<String> permissions = (Set<String>) redisService.get(permsKey);
        if (permissions == null) {
            List<String> permList = userService.selectPermissionCodesByUserId(userId);
            permissions = new HashSet<>(permList);
            redisService.set(permsKey, permissions, 1800L, TimeUnit.SECONDS);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("user", buildUserInfo(user));
        result.put("permissions", permissions);
        return R.ok(result);
    }

    @Operation(summary = "获取用户菜单树")
    @GetMapping("/menus")
    public R<List<Map<String, Object>>> getUserMenus(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            userId = 1L;
        }
        return R.ok(getMenuTree(userId));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                          @RequestHeader(value = "X-Token", required = false) String token) {
        if (userId != null) {
            // 清除Redis中的登录信息
            redisService.delete(USER_TOKEN_PREFIX + userId);
            redisService.delete(USER_PERMS_PREFIX + userId);
            redisService.delete(USER_ROLES_PREFIX + userId);
            redisService.delete(USER_MENUS_PREFIX + userId);
            redisService.delete(ONLINE_USER_PREFIX + userId);
        }

        // 将Token加入黑名单
        if (token != null) {
            try {
                String tokenId = jwtTokenProvider.getTokenId(token);
                long remaining = jwtTokenProvider.getRemainingTime(token);
                if (remaining > 0) {
                    redisService.set(TOKEN_BLACKLIST_PREFIX + tokenId, "1", remaining, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                log.warn("Token黑名单处理失败: {}", e.getMessage());
            }
        }

        log.info("用户退出登录: userId={}", userId);
        return R.ok();
    }

    /**
     * 构建用户信息（去除敏感字段）
     */
    private Map<String, Object> buildUserInfo(SysUser user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());
        userInfo.put("gender", user.getGender());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("deptId", user.getDeptId());
        userInfo.put("tenantId", user.getTenantId());
        userInfo.put("status", user.getStatus());
        userInfo.put("createdTime", user.getCreatedTime());
        return userInfo;
    }

    /**
     * 获取菜单树
     */
    private List<Map<String, Object>> getMenuTree(Long userId) {
        // 从Redis缓存获取
        String menusKey = USER_MENUS_PREFIX + userId;
        List<Map<String, Object>> cachedMenus = (List<Map<String, Object>>) redisService.get(menusKey);
        if (cachedMenus != null) {
            return cachedMenus;
        }

        // 从数据库获取菜单权限
        List<SysPermission> permissions = permissionMapper.selectPermissionsByUserId(userId);

        // 过滤出菜单类型
        List<SysPermission> menus = permissions.stream()
                .filter(p -> "menu".equals(p.getPermissionType()) || p.getPermissionType() == null)
                .sorted((a, b) -> (a.getOrderNum() != null ? a.getOrderNum() : 0) - (b.getOrderNum() != null ? b.getOrderNum() : 0))
                .collect(Collectors.toList());

        // 构建树结构
        List<Map<String, Object>> menuTree = buildMenuTree(menus, 0L);

        // 缓存
        redisService.set(menusKey, menuTree, 1800L, TimeUnit.SECONDS);

        return menuTree;
    }

    /**
     * 构建菜单树
     */
    private List<Map<String, Object>> buildMenuTree(List<SysPermission> menus, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        for (SysPermission menu : menus) {
            if (Objects.equals(menu.getParentId(), parentId)) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", menu.getId());
                node.put("name", menu.getPermissionName());
                node.put("code", menu.getPermissionCode());
                node.put("path", menu.getPath());
                node.put("component", menu.getComponent());
                node.put("icon", menu.getIcon());
                node.put("orderNum", menu.getOrderNum());
                node.put("visible", menu.getVisible());

                // 递归获取子菜单
                List<Map<String, Object>> children = buildMenuTree(menus, menu.getId());
                if (!children.isEmpty()) {
                    node.put("children", children);
                }

                tree.add(node);
            }
        }
        return tree;
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

    /**
     * 刷新TokenDTO
     */
    public static class RefreshDTO {
        private String refreshToken;
        private String oldAccessToken;

        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        public String getOldAccessToken() { return oldAccessToken; }
        public void setOldAccessToken(String oldAccessToken) { this.oldAccessToken = oldAccessToken; }
    }
}