package com.lowcode.platform.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.platform.common.core.exception.BusinessException;
import com.lowcode.platform.common.core.result.PageResult;
import com.lowcode.platform.system.entity.SysTenant;
import com.lowcode.platform.system.entity.SysPackage;
import com.lowcode.platform.system.entity.SysUser;
import com.lowcode.platform.system.entity.SysRole;
import com.lowcode.platform.system.entity.SysUserRole;
import com.lowcode.platform.system.entity.SysRolePermission;
import com.lowcode.platform.system.entity.SysPermission;
import com.lowcode.platform.system.mapper.SysTenantMapper;
import com.lowcode.platform.system.mapper.SysPackageMapper;
import com.lowcode.platform.system.mapper.SysUserMapper;
import com.lowcode.platform.system.mapper.SysRoleMapper;
import com.lowcode.platform.system.mapper.SysUserRoleMapper;
import com.lowcode.platform.system.mapper.SysRolePermissionMapper;
import com.lowcode.platform.system.mapper.SysPermissionMapper;
import com.lowcode.platform.system.service.SysTenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 租户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant> implements SysTenantService {

    private final SysPackageMapper packageMapper;
    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysPermissionMapper permissionMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public PageResult<SysTenant> queryPageList(SysTenant query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<SysTenant> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getTenantName())) {
            wrapper.like(SysTenant::getTenantName, query.getTenantName());
        }
        if (StringUtils.hasText(query.getTenantCode())) {
            wrapper.like(SysTenant::getTenantCode, query.getTenantCode());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysTenant::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SysTenant::getCreatedTime);

        IPage<SysTenant> page = new Page<>(pageNum, pageSize);
        IPage<SysTenant> result = this.page(page, wrapper);

        return new PageResult<>(result.getRecords(), result.getTotal(), pageNum, pageSize);
    }

    @Override
    public SysTenant getByTenantCode(String tenantCode) {
        return baseMapper.selectByTenantCode(tenantCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTenant(SysTenant tenant) {
        // 校验租户编码
        if (checkTenantCodeExists(tenant.getTenantCode(), null)) {
            throw new BusinessException("租户编码已存在");
        }

        // 校验套餐
        if (tenant.getPackageId() != null) {
            SysPackage pkg = packageMapper.selectById(tenant.getPackageId());
            if (pkg == null || pkg.getStatus() != 0) {
                throw new BusinessException("套餐不存在或已禁用");
            }
        }

        // 设置默认值
        if (tenant.getTenantCode() == null) {
            tenant.setTenantCode(UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        }
        if (tenant.getDbSchema() == null) {
            tenant.setDbSchema("tenant_" + tenant.getTenantCode());
        }
        if (tenant.getStatus() == null) {
            tenant.setStatus(0);
        }

        this.save(tenant);

        // 初始化租户数据（简化版，实际需要创建Schema和初始用户）
        try {
            initTenantData(tenant.getId(), "admin", "admin123");
        } catch (Exception e) {
            log.warn("初始化租户数据失败: {}", e.getMessage());
        }

        log.info("创建租户成功: tenantId={}, tenantCode={}", tenant.getId(), tenant.getTenantCode());
        return tenant.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTenant(SysTenant tenant) {
        // 校验租户编码
        if (checkTenantCodeExists(tenant.getTenantCode(), tenant.getId())) {
            throw new BusinessException("租户编码已存在");
        }

        this.updateById(tenant);
        log.info("更新租户成功: tenantId={}", tenant.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTenant(Long id) {
        SysTenant tenant = this.getById(id);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }

        // 逻辑删除
        tenant.setStatus(1);
        this.updateById(tenant);

        log.info("删除租户成功: tenantId={}", id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysTenant tenant = this.getById(id);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }

        tenant.setStatus(status);
        this.updateById(tenant);

        log.info("更新租户状态成功: tenantId={}, status={}", id, status);
    }

    @Override
    public boolean checkTenantCodeExists(String tenantCode, Long excludeId) {
        return baseMapper.checkTenantCodeExists(tenantCode, excludeId) > 0;
    }

    @Override
    public List<SysTenant> listEnabledTenants() {
        LambdaQueryWrapper<SysTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysTenant::getStatus, 0);
        wrapper.orderByDesc(SysTenant::getCreatedTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initTenantData(Long tenantId, String adminUsername, String adminPassword) {
        log.info("开始初始化租户数据: tenantId={}, adminUsername={}", tenantId, adminUsername);

        SysTenant tenant = this.getById(tenantId);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }

        // 1. 创建租户管理员角色
        SysRole adminRole = new SysRole();
        adminRole.setRoleName("租户管理员");
        adminRole.setRoleCode("tenant_admin");
        adminRole.setDataScope("all");
        adminRole.setStatus(0);
        roleMapper.insert(adminRole);
        log.info("创建租户管理员角色成功: roleId={}", adminRole.getId());

        // 2. 创建普通用户角色
        SysRole userRole = new SysRole();
        userRole.setRoleName("普通用户");
        userRole.setRoleCode("tenant_user");
        userRole.setDataScope("self");
        userRole.setStatus(0);
        roleMapper.insert(userRole);
        log.info("创建普通用户角色成功: roleId={}", userRole.getId());

        // 3. 为管理员角色分配基础权限
        List<SysPermission> permissions = permissionMapper.selectList(
            new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getStatus, 0)
        );
        List<SysRolePermission> rolePermissions = new ArrayList<>();
        for (SysPermission perm : permissions) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(adminRole.getId());
            rp.setPermissionId(perm.getId());
            rolePermissions.add(rp);
        }
        if (!rolePermissions.isEmpty()) {
            for (SysRolePermission rp : rolePermissions) {
                rolePermissionMapper.insert(rp);
            }
        }
        log.info("分配管理员权限成功: count={}", rolePermissions.size());

        // 4. 创建初始管理员用户
        SysUser adminUser = new SysUser();
        adminUser.setUsername(adminUsername);
        adminUser.setPassword(passwordEncoder.encode(adminPassword));
        adminUser.setRealName("管理员");
        adminUser.setStatus(0);
        adminUser.setDelFlag(0);
        userMapper.insert(adminUser);
        log.info("创建管理员用户成功: userId={}", adminUser.getId());

        // 5. 关联用户和管理员角色
        SysUserRole userRoleRel = new SysUserRole();
        userRoleRel.setUserId(adminUser.getId());
        userRoleRel.setRoleId(adminRole.getId());
        userRoleMapper.insert(userRoleRel);

        // 6. 更新租户管理员用户ID
        tenant.setAdminUserId(adminUser.getId());
        this.updateById(tenant);

        log.info("初始化租户数据完成: tenantId={}, adminUserId={}", tenantId, adminUser.getId());
    }
}