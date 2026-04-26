package com.lowcode.platform.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.form.entity.FormDefinition;
import org.apache.ibatis.annotations.Mapper;

/**
 * 表单定义Mapper
 */
@Mapper
public interface FormDefinitionMapper extends BaseMapper<FormDefinition> {

    /** 根据编码查询 */
    FormDefinition selectByFormCode(String formCode);
}