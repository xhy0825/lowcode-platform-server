package com.lowcode.platform.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.data.entity.DataModel;
import com.lowcode.platform.data.entity.DataModelField;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 数据模型Mapper
 */
@Mapper
public interface DataModelMapper extends BaseMapper<DataModel> {

    /** 根据编码查询 */
    DataModel selectByModelCode(String modelCode);

    /** 查询模型的字段列表 */
    List<DataModelField> selectFieldsByModelId(Long modelId);
}