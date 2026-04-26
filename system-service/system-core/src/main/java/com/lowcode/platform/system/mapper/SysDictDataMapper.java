package com.lowcode.platform.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.system.entity.SysDictData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 字典数据Mapper
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /** 根据字典类型查询 */
    List<SysDictData> selectByDictType(String dictType);
}