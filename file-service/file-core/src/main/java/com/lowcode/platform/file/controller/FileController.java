package com.lowcode.platform.file.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.file.entity.FileRecord;
import com.lowcode.platform.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件管理控制器
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public R<Long> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String businessType) {
        try {
            Long fileId = fileService.upload(
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    file.getSize(),
                    file.getContentType(),
                    businessType
            );
            return R.ok(fileId);
        } catch (Exception e) {
            return R.fail("文件上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "下载文件")
    @GetMapping("/download/{id}")
    public ResponseEntity<InputStream> download(@PathVariable Long id) {
        FileRecord record = fileService.getFileInfo(id);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }

        InputStream stream = fileService.download(id);
        String encodedFilename = URLEncoder.encode(record.getFileName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"")
                .contentType(MediaType.parseMediaType(record.getFileType()))
                .body(stream);
    }

    @Operation(summary = "获取预览URL")
    @GetMapping("/preview/{id}")
    public R<String> getPreviewUrl(
            @PathVariable Long id,
            @RequestParam(defaultValue = "3600") int expireSeconds) {
        String url = fileService.getPreviewUrl(id, expireSeconds);
        return R.ok(url);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        fileService.delete(id);
        return R.ok();
    }

    @Operation(summary = "文件列表")
    @GetMapping("/list")
    public R<Page<FileRecord>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String businessType) {
        return R.ok(fileService.listPage(pageNum, pageSize, businessType));
    }

    @Operation(summary = "文件详情")
    @GetMapping("/{id}")
    public R<FileRecord> getInfo(@PathVariable Long id) {
        return R.ok(fileService.getFileInfo(id));
    }

    @Operation(summary = "检查文件是否存在（MD5秒传）")
    @GetMapping("/check")
    public R<FileRecord> checkByMd5(@RequestParam String md5) {
        return R.ok(fileService.getByMd5(md5));
    }
}