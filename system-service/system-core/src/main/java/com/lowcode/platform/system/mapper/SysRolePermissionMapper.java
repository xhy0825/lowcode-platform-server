package com.lowcode.platform.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.system.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色权限关联Mapper
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /** 删除角色权限关联 */
    int deleteByRoleId(Long roleId);

    /** 批量插入 */
    int batchInsert(List<SysRolePermission> list);
}