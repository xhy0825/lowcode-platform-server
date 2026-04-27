package com.lowcode.platform.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.file.entity.FileRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件记录Mapper
 */
@Mapper
public interface FileRecordMapper extends BaseMapper<FileRecord> {

}