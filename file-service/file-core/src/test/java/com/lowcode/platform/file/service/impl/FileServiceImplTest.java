package com.lowcode.platform.file.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.file.config.MinioConfig;
import com.lowcode.platform.file.entity.FileRecord;
import com.lowcode.platform.file.mapper.FileRecordMapper;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 文件服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioConfig minioConfig;

    @Mock
    private FileRecordMapper fileRecordMapper;

    @InjectMocks
    private FileServiceImpl fileService;

    private FileRecord testRecord;

    @BeforeEach
    void setUp() {
        testRecord = new FileRecord();
        testRecord.setId(1L);
        testRecord.setFileName("test.pdf");
        testRecord.setFilePath("document/20240115/abc123.pdf");
        testRecord.setFileSize(1024L);
        testRecord.setFileType("application/pdf");
        testRecord.setBucketName("lowcode-files");
        testRecord.setDownloadCount(0);
        testRecord.setDelFlag(0);

        when(minioConfig.getBucketName()).thenReturn("lowcode-files");
    }

    @Test
    @DisplayName("分页查询文件列表")
    void listPage_success() {
        Page<FileRecord> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testRecord));

        when(fileRecordMapper.selectPage(any(), any())).thenReturn(mockPage);

        Page<FileRecord> result = fileService.listPage(1, 10, "document");

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    @DisplayName("获取文件信息-成功")
    void getFileInfo_success() {
        when(fileRecordMapper.selectById(1L)).thenReturn(testRecord);

        FileRecord result = fileService.getFileInfo(1L);

        assertNotNull(result);
        assertEquals("test.pdf", result.getFileName());
    }

    @Test
    @DisplayName("获取文件信息-不存在")
    void getFileInfo_notFound() {
        when(fileRecordMapper.selectById(1L)).thenReturn(null);

        FileRecord result = fileService.getFileInfo(1L);

        assertNull(result);
    }

    @Test
    @DisplayName("根据MD5查询文件")
    void getByMd5_found() {
        testRecord.setFileMd5("abc123def456");
        when(fileRecordMapper.selectOne(any())).thenReturn(testRecord);

        FileRecord result = fileService.getByMd5("abc123def456");

        assertNotNull(result);
    }

    @Test
    @DisplayName("根据MD5查询文件-不存在")
    void getByMd5_notFound() {
        when(fileRecordMapper.selectOne(any())).thenReturn(null);

        FileRecord result = fileService.getByMd5("notexist");

        assertNull(result);
    }

    @Test
    @DisplayName("删除文件-成功")
    void delete_success() throws Exception {
        when(fileRecordMapper.selectById(1L)).thenReturn(testRecord);
        when(fileRecordMapper.selectCount(any())).thenReturn(0L);
        when(fileRecordMapper.updateById(any())).thenReturn(1);

        // Mock MinIO operations - 使用更灵活的mock方式
        lenient().when(minioClient.bucketExists(any())).thenReturn(true);
        lenient().when(minioClient.removeObject(any())).thenReturn(null);

        fileService.delete(1L);

        assertEquals(1, testRecord.getDelFlag());
        verify(fileRecordMapper).updateById(any());
    }

    @Test
    @DisplayName("删除文件-不存在")
    void delete_notFound() {
        when(fileRecordMapper.selectById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> fileService.delete(1L));
    }
}