package com.lowcode.platform.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件记录实体
 */
@Data
@TableName("file_record")
public class FileRecord {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 文件名称（原始名称）
     */
    private String fileName;

    /**
     * 文件路径（MinIO存储路径）
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（MIME类型）
     */
    private String fileType;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 文件MD5值
     */
    private String fileMd5;

    /**
     * 业务类型（如：avatar、document、image等）
     */
    private String businessType;

    /**
     * 关联业务ID
     */
    private Long businessId;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 上传用户
     */
    private String uploadUser;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 删除标记
     */
    @TableLogic
    private Integer delFlag;
}