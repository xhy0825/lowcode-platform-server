package com.lowcode.platform.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.platform.system.entity.SysUser;

import java.util.List;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /** 分页查询 */
    Page<SysUser> selectPage(Page<SysUser> page, SysUser query);

    /** 根据用户名查询 */
    SysUser selectByUsername(String username);

    /** 查询用户角色编码列表 */
    List<String> selectRoleCodesByUserId(Long userId);

    /** 查询用户权限编码列表 */
    List<String> selectPermissionCodesByUserId(Long userId);

    /** 创建用户 */
    boolean createUser(SysUser user, List<Long> roleIds);

    /** 更新用户 */
    boolean updateUser(SysUser user, List<Long> roleIds);

    /** 删除用户 */
    boolean deleteUser(Long userId);

    /** 重置密码 */
    boolean resetPassword(Long userId, String newPassword);
}