package com.lowcode.platform.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.system.entity.SysRoleDept;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色部门关联Mapper
 */
@Mapper
public interface SysRoleDeptMapper extends BaseMapper<SysRoleDept> {

    /** 删除角色部门关联 */
    int deleteByRoleId(Long roleId);
}