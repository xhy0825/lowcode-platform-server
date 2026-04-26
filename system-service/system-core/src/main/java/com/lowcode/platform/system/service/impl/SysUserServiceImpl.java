package com.lowcode.platform.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.platform.common.core.exception.BusinessException;
import com.lowcode.platform.system.entity.SysUser;
import com.lowcode.platform.system.entity.SysUserRole;
import com.lowcode.platform.system.mapper.SysUserMapper;
import com.lowcode.platform.system.mapper.SysUserRoleMapper;
import com.lowcode.platform.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Page<SysUser> selectPage(Page<SysUser> page, SysUser query) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDelFlag, 0);
        if (StringUtils.hasText(query.getUsername())) {
            wrapper.like(SysUser::getUsername, query.getUsername());
        }
        if (StringUtils.hasText(query.getRealName())) {
            wrapper.like(SysUser::getRealName, query.getRealName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, query.getStatus());
        }
        if (query.getDeptId() != null) {
            wrapper.eq(SysUser::getDeptId, query.getDeptId());
        }
        wrapper.orderByDesc(SysUser::getCreatedTime);
        return userMapper.selectPage(page, wrapper);
    }

    @Override
    public SysUser selectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public List<String> selectRoleCodesByUserId(Long userId) {
        return userMapper.selectRoleCodesByUserId(userId);
    }

    @Override
    public List<String> selectPermissionCodesByUserId(Long userId) {
        return userMapper.selectPermissionCodesByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUser(SysUser user, List<Long> roleIds) {
        // 检查用户名是否存在
        SysUser existUser = selectByUsername(user.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(0);
        user.setDelFlag(0);
        // 保存用户
        int result = userMapper.insert(user);
        if (result > 0 && roleIds != null && !roleIds.isEmpty()) {
            // 保存用户角色关联
            saveUserRole(user.getId(), roleIds);
        }
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(SysUser user, List<Long> roleIds) {
        // 检查用户是否存在
        SysUser existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        // 不允许修改密码
        user.setPassword(null);
        int result = userMapper.updateById(user);
        if (result > 0) {
            // 删除原有角色关联
            userRoleMapper.deleteByUserId(user.getId());
            if (roleIds != null && !roleIds.isEmpty()) {
                saveUserRole(user.getId(), roleIds);
            }
        }
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 逻辑删除
        user.setDelFlag(1);
        int result = userMapper.updateById(user);
        // 删除用户角色关联
        userRoleMapper.deleteByUserId(userId);
        return result > 0;
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return userMapper.updateById(user) > 0;
    }

    private void saveUserRole(Long userId, List<Long> roleIds) {
        List<SysUserRole> list = new ArrayList<>();
        for (Long roleId : roleIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        userRoleMapper.batchInsert(list);
    }
}