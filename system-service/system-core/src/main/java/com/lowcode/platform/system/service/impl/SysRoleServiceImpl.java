package com.lowcode.platform.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.platform.common.core.exception.BusinessException;
import com.lowcode.platform.system.entity.SysRole;
import com.lowcode.platform.system.entity.SysRoleDept;
import com.lowcode.platform.system.entity.SysRolePermission;
import com.lowcode.platform.system.mapper.SysRoleMapper;
import com.lowcode.platform.system.mapper.SysRoleDeptMapper;
import com.lowcode.platform.system.mapper.SysRolePermissionMapper;
import com.lowcode.platform.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务实现
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysRoleDeptMapper roleDeptMapper;

    @Override
    public Page<SysRole> selectPage(Page<SysRole> page, SysRole query) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getDelFlag, 0);
        if (StringUtils.hasText(query.getRoleName())) {
            wrapper.like(SysRole::getRoleName, query.getRoleName());
        }
        if (StringUtils.hasText(query.getRoleCode())) {
            wrapper.like(SysRole::getRoleCode, query.getRoleCode());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SysRole::getCreatedTime);
        return roleMapper.selectPage(page, wrapper);
    }

    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    @Override
    public List<Long> selectPermissionIdsByRoleId(Long roleId) {
        return roleMapper.selectPermissionIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(SysRole role, List<Long> permissionIds, List<Long> deptIds) {
        role.setStatus(0);
        role.setDelFlag(0);
        int result = roleMapper.insert(role);
        if (result > 0) {
            saveRolePermission(role.getId(), permissionIds);
            saveRoleDept(role.getId(), deptIds);
        }
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(SysRole role, List<Long> permissionIds, List<Long> deptIds) {
        SysRole existRole = roleMapper.selectById(role.getId());
        if (existRole == null) {
            throw new BusinessException("角色不存在");
        }
        int result = roleMapper.updateById(role);
        if (result > 0) {
            rolePermissionMapper.deleteByRoleId(role.getId());
            saveRolePermission(role.getId(), permissionIds);
            roleDeptMapper.deleteByRoleId(role.getId());
            saveRoleDept(role.getId(), deptIds);
        }
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId) {
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        role.setDelFlag(1);
        int result = roleMapper.updateById(role);
        rolePermissionMapper.deleteByRoleId(roleId);
        roleDeptMapper.deleteByRoleId(roleId);
        return result > 0;
    }

    private void saveRolePermission(Long roleId, List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        List<SysRolePermission> list = new ArrayList<>();
        for (Long permissionId : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            list.add(rp);
        }
        rolePermissionMapper.batchInsert(list);
    }

    private void saveRoleDept(Long roleId, List<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return;
        }
        List<SysRoleDept> list = new ArrayList<>();
        for (Long deptId : deptIds) {
            SysRoleDept rd = new SysRoleDept();
            rd.setRoleId(roleId);
            rd.setDeptId(deptId);
            list.add(rd);
        }
        // 需要实现批量插入
        for (SysRoleDept rd : list) {
            roleDeptMapper.insert(rd);
        }
    }
}