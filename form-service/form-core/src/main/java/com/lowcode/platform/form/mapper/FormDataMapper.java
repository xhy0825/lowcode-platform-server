package com.lowcode.platform.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.form.entity.FormData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 表单数据Mapper
 */
@Mapper
public interface FormDataMapper extends BaseMapper<FormData> {

    /** 根据表单定义ID查询数据列表 */
    java.util.List<FormData> selectByFormDefinitionId(Long formDefinitionId, int pageNum, int pageSize);
}