package com.lowcode.platform.flow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.flow.entity.FlowDefinition;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程定义Mapper
 */
@Mapper
public interface FlowDefinitionMapper extends BaseMapper<FlowDefinition> {
}