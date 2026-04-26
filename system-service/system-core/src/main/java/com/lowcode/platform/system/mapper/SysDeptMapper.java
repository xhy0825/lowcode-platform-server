package com.lowcode.platform.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.system.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 部门Mapper
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /** 查询子部门 */
    List<SysDept> selectChildrenByParentId(Long parentId);

    /** 查询部门树 */
    List<SysDept> selectDeptTree();
}