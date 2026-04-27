package com.lowcode.platform.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.system.entity.SysCommandLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 命令日志Mapper
 */
@Mapper
public interface SysCommandLogMapper extends BaseMapper<SysCommandLog> {

    /** 查询命令执行日志 */
    List<SysCommandLog> selectByCommandId(Long commandId);

    /** 查询最近执行日志 */
    SysCommandLog selectLatestLog(Long commandId);
}