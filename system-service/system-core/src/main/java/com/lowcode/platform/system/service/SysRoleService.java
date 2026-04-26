package com.lowcode.platform.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.platform.system.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 */
public interface SysRoleService extends IService<SysRole> {

    /** 分页查询 */
    Page<SysRole> selectPage(Page<SysRole> page, SysRole query);

    /** 根据用户ID查询角色 */
    List<SysRole> selectRolesByUserId(Long userId);

    /** 查询角色权限ID列表 */
    List<Long> selectPermissionIdsByRoleId(Long roleId);

    /** 创建角色 */
    boolean createRole(SysRole role, List<Long> permissionIds, List<Long> deptIds);

    /** 更新角色 */
    boolean updateRole(SysRole role, List<Long> permissionIds, List<Long> deptIds);

    /** 删除角色 */
    boolean deleteRole(Long roleId);
}