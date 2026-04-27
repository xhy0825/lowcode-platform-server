# 文件服务 (File Service) 功能文档

## 概述

文件服务提供文件上传、下载、预览和管理功能，基于MinIO对象存储实现。支持：
1. 文件上传与下载
2. 文件预览（临时URL）
3. MD5秒传检测
4. 业务分类管理
5. 下载次数统计

## 功能清单

### ✅ 已实现功能

| 功能 | API | 说明 |
|------|-----|------|
| 上传文件 | POST /file/upload | 上传文件到MinIO存储 |
| 下载文件 | GET /file/download/{id} | 下载指定文件 |
| 获取预览URL | GET /file/preview/{id} | 获取临时预览链接 |
| 删除文件 | DELETE /file/{id} | 删除指定文件 |
| 文件列表 | GET /file/list | 分页查询文件列表 |
| 文件详情 | GET /file/{id} | 获取文件详细信息 |
| MD5检查 | GET /file/check | 根据MD5检查文件是否存在 |

## 数据结构

### FileRecord (文件记录)

```json
{
    "id": 1,
    "fileName": "report.pdf",
    "filePath": "document/20240115/abc123.pdf",
    "fileSize": 102400,
    "fileType": "application/pdf",
    "fileExtension": "pdf",
    "bucketName": "lowcode-files",
    "fileMd5": "abc123def456...",
    "businessType": "document",
    "downloadCount": 5,
    "uploadTime": "2024-01-15T10:00:00"
}
```

## 业务类型

| businessType | 说明 |
|--------------|------|
| avatar | 用户头像 |
| document | 文档文件 |
| image | 图片文件 |
| video | 视频文件 |
| attachment | 附件文件 |

## 使用流程

### 1. 上传文件

```http
POST /file/upload?businessType=document
Content-Type: multipart/form-data

file: (binary)
```

响应：
```json
{
    "code": 200,
    "data": 1  // 文件ID
}
```

### 2. 下载文件

```http
GET /file/download/1
```

返回文件流，Content-Disposition包含原始文件名。

### 3. 获取预览URL

```http
GET /file/preview/1?expireSeconds=3600
```

响应：
```json
{
    "code": 200,
    "data": "http://minio-server/bucket/path?token=xxx"
}
```

### 4. MD5秒传

前端在上传前先计算文件MD5，调用检查接口：
```http
GET /file/check?md5=abc123def456
```

如果返回文件记录，说明文件已存在，可直接使用该文件ID，无需重复上传。

## MinIO配置

```yaml
minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket-name: lowcode-files
  secure: false
```

## 开发环境

- 端口: 8085
- API文档: http://localhost:8085/doc.html
- H2控制台: http://localhost:8085/h2-console
- 文件大小限制: 100MB

## 测试覆盖

| 测试类 | 测试内容 |
|--------|----------|
| FileServiceImplTest | 文件上传、下载、删除、MD5查询 |

运行测试：
```bash
mvn test -pl file-service/file-core
```

## 后续计划

- [ ] 图片缩略图生成
- [ ] 文件压缩打包下载
- [ ] 文件版本管理
- [ ] 文件权限控制