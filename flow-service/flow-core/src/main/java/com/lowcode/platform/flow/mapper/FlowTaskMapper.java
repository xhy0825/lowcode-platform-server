package com.lowcode.platform.flow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.flow.entity.FlowTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程任务Mapper
 */
@Mapper
public interface FlowTaskMapper extends BaseMapper<FlowTask> {
}