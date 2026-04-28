package com.lowcode.platform.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.exception.BusinessException;
import com.lowcode.platform.file.config.MinioConfig;
import com.lowcode.platform.file.entity.FileRecord;
import com.lowcode.platform.file.mapper.FileRecordMapper;
import com.lowcode.platform.file.service.FileService;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文件服务实现（MinIO存储）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    private final FileRecordMapper fileRecordMapper;

    @Override
    @Transactional
    public Long upload(InputStream inputStream, String fileName, long fileSize, String fileType, String businessType) {
        try {
            // 生成唯一文件路径
            String fileExtension = getFileExtension(fileName);
            String objectName = generateObjectName(businessType, fileExtension);
            String bucketName = minioConfig.getBucketName();

            // 确保桶存在
            ensureBucketExists(bucketName);

            // 计算文件MD5（用于秒传检测）
            String fileMd5 = DigestUtils.md5DigestAsHex(inputStream);

            // 检查是否已存在相同文件（秒传）
            FileRecord existFile = getByMd5(fileMd5);
            if (existFile != null) {
                // 创建新的文件记录，指向同一存储对象
                FileRecord record = createFileRecord(fileName, objectName, fileSize, fileType, fileExtension, bucketName, fileMd5, businessType);
                record.setFilePath(existFile.getFilePath()); // 使用已有文件路径
                fileRecordMapper.insert(record);
                return record.getId();
            }

            // 上传文件到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, fileSize, -1)
                            .contentType(fileType)
                            .build()
            );

            // 保存文件记录
            FileRecord record = createFileRecord(fileName, objectName, fileSize, fileType, fileExtension, bucketName, fileMd5, businessType);
            fileRecordMapper.insert(record);

            log.info("文件上传成功: {} -> {}", fileName, objectName);
            return record.getId();

        } catch (Exception e) {
            log.error("文件上传失败: {}", fileName, e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream download(Long fileId) {
        FileRecord record = getFileInfo(fileId);
        if (record == null) {
            throw new BusinessException("文件不存在");
        }

        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(record.getBucketName())
                            .object(record.getFilePath())
                            .build()
            );

            // 更新下载次数
            record.setDownloadCount(record.getDownloadCount() + 1);
            fileRecordMapper.updateById(record);

            return stream;

        } catch (Exception e) {
            log.error("文件下载失败: {}", fileId, e);
            throw new BusinessException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public String getPreviewUrl(Long fileId, int expireSeconds) {
        FileRecord record = getFileInfo(fileId);
        if (record == null) {
            throw new BusinessException("文件不存在");
        }

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(record.getBucketName())
                            .object(record.getFilePath())
                            .expiry(expireSeconds)
                            .build()
            );
        } catch (Exception e) {
            log.error("获取预览URL失败: {}", fileId, e);
            throw new BusinessException("获取预览URL失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void delete(Long fileId) {
        FileRecord record = getFileInfo(fileId);
        if (record == null) {
            throw new BusinessException("文件不存在");
        }

        try {
            // 检查是否有其他记录指向同一文件
            LambdaQueryWrapper<FileRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FileRecord::getFilePath, record.getFilePath())
                    .ne(FileRecord::getId, fileId)
                    .eq(FileRecord::getDelFlag, 0);
            long count = fileRecordMapper.selectCount(wrapper);

            // 如果没有其他记录，删除MinIO中的文件
            if (count == 0) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(record.getBucketName())
                                .object(record.getFilePath())
                                .build()
                );
            }

            // 删除记录（软删除）
            record.setDelFlag(1);
            fileRecordMapper.updateById(record);

            log.info("文件删除成功: {}", fileId);

        } catch (Exception e) {
            log.error("文件删除失败: {}", fileId, e);
            throw new BusinessException("文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public Page<FileRecord> listPage(int pageNum, int pageSize, String businessType) {
        Page<FileRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<FileRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileRecord::getDelFlag, 0);
        if (StringUtils.hasText(businessType)) {
            wrapper.eq(FileRecord::getBusinessType, businessType);
        }
        wrapper.orderByDesc(FileRecord::getUploadTime);
        return fileRecordMapper.selectPage(page, wrapper);
    }

    @Override
    public FileRecord getFileInfo(Long fileId) {
        return fileRecordMapper.selectById(fileId);
    }

    @Override
    public FileRecord getByMd5(String fileMd5) {
        LambdaQueryWrapper<FileRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileRecord::getFileMd5, fileMd5)
                .eq(FileRecord::getDelFlag, 0)
                .last("LIMIT 1");
        return fileRecordMapper.selectOne(wrapper);
    }

    /**
     * 确保存储桶存在
     */
    private void ensureBucketExists(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );
        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            log.info("创建存储桶: {}", bucketName);
        }
    }

    /**
     * 生成存储对象名称
     */
    private String generateObjectName(String businessType, String extension) {
        String prefix = StringUtils.hasText(businessType) ? businessType : "default";
        String datePath = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return String.format("%s/%s/%s.%s", prefix, datePath, uuid, extension);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 创建文件记录对象
     */
    private FileRecord createFileRecord(String fileName, String objectName, long fileSize, String fileType,
                                         String extension, String bucketName, String md5, String businessType) {
        FileRecord record = new FileRecord();
        record.setTenantId("000000"); // TODO: 从上下文获取
        record.setFileName(fileName);
        record.setFilePath(objectName);
        record.setFileSize(fileSize);
        record.setFileType(fileType);
        record.setFileExtension(extension);
        record.setBucketName(bucketName);
        record.setFileMd5(md5);
        record.setBusinessType(businessType);
        record.setDownloadCount(0);
        record.setUploadUser("system"); // TODO: 从上下文获取
        record.setUploadTime(LocalDateTime.now());
        record.setDelFlag(0);
        return record;
    }
}