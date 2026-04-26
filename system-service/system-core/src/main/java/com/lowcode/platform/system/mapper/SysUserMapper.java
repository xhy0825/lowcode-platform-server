package com.lowcode.platform.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /** 根据用户名查询 */
    SysUser selectByUsername(String username);

    /** 查询用户角色列表 */
    List<String> selectRoleCodesByUserId(Long userId);

    /** 查询用户权限列表 */
    List<String> selectPermissionCodesByUserId(Long userId);
}