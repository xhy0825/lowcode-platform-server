package com.lowcode.platform.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.data.entity.DataModelField;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 数据模型字段Mapper
 */
@Mapper
public interface DataModelFieldMapper extends BaseMapper<DataModelField> {

    /** 根据模型ID删除字段 */
    int deleteByModelId(Long modelId);

    /** 批量插入 */
    int batchInsert(List<DataModelField> list);

    /** 根据模型ID查询字段列表 */
    List<DataModelField> selectByModelId(Long modelId);
}