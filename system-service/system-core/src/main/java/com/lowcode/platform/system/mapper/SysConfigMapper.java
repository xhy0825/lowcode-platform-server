package com.lowcode.platform.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.system.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 配置Mapper
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /** 根据配置键查询 */
    SysConfig selectByConfigKey(String configKey);
}