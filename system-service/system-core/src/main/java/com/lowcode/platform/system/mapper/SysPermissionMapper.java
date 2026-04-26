package com.lowcode.platform.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.system.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 权限Mapper
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /** 根据用户ID查询权限 */
    List<SysPermission> selectPermissionsByUserId(Long userId);

    /** 查询菜单树 */
    List<SysPermission> selectMenuTree();
}