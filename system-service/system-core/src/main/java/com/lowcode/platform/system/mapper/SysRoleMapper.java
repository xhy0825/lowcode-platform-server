package com.lowcode.platform.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色Mapper
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /** 根据用户ID查询角色 */
    List<SysRole> selectRolesByUserId(Long userId);

    /** 查询角色权限ID列表 */
    List<Long> selectPermissionIdsByRoleId(Long roleId);
}