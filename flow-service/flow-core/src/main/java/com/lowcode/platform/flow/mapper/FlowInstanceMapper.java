package com.lowcode.platform.flow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.flow.entity.FlowInstance;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程实例Mapper
 */
@Mapper
public interface FlowInstanceMapper extends BaseMapper<FlowInstance> {
}