package com.lowcode.platform.file.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.file.entity.FileRecord;

import java.io.InputStream;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 上传文件
     * @param inputStream 文件流
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @param fileType 文件类型
     * @param businessType 业务类型
     * @return 文件记录ID
     */
    Long upload(InputStream inputStream, String fileName, long fileSize, String fileType, String businessType);

    /**
     * 下载文件
     * @param fileId 文件ID
     * @return 文件流
     */
    InputStream download(Long fileId);

    /**
     * 获取文件预览URL
     * @param fileId 文件ID
     * @param expireSeconds 过期时间（秒）
     * @return 预览URL
     */
    String getPreviewUrl(Long fileId, int expireSeconds);

    /**
     * 删除文件
     * @param fileId 文件ID
     */
    void delete(Long fileId);

    /**
     * 分页查询文件列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param businessType 业务类型
     * @return 文件列表
     */
    Page<FileRecord> listPage(int pageNum, int pageSize, String businessType);

    /**
     * 获取文件信息
     * @param fileId 文件ID
     * @return 文件记录
     */
    FileRecord getFileInfo(Long fileId);

    /**
     * 根据MD5查询文件
     * @param fileMd5 文件MD5
     * @return 文件记录
     */
    FileRecord getByMd5(String fileMd5);
}