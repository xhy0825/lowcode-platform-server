# 数据服务 (Data Service) 功能文档

## 概述

数据服务是低代码平台的核心组件，提供动态数据模型定义和DDL建表功能。用户可以通过API定义数据结构，系统自动生成并执行建表SQL。

## 功能清单

### ✅ 已实现功能

| 功能 | API | 说明 |
|------|-----|------|
| 模型列表查询 | GET /model/list | 分页查询数据模型 |
| 模型详情查询 | GET /model/{id} | 查询模型及其字段配置 |
| 创建数据模型 | POST /model | 创建新的数据模型定义 |
| 配置模型字段 | PUT /model/{id}/fields | 配置模型的字段列表 |
| 生成DDL预览 | POST /model/{id}/generate | 生成建表SQL语句预览 |
| 执行DDL建表 | POST /model/{id}/execute | 执行建表SQL并发布模型 |
| 预览表结构 | GET /model/{id}/preview | 预览即将创建的表结构 |
| 发布模型 | POST /model/{id}/publish | 发布模型（执行建表） |
| 删除模型 | DELETE /model/{id} | 删除未发布的模型 |

## 数据结构

### DataModel (数据模型)

```json
{
    "id": 1,
    "modelName": "用户信息表",
    "modelCode": "user_info",
    "tableName": "tbl_user_info",
    "description": "存储用户基本信息",
    "status": 1,
    "version": 1,
    "fields": []
}
```

### DataModelField (数据模型字段)

```json
{
    "id": 1,
    "modelId": 1,
    "fieldName": "用户名",
    "fieldCode": "username",
    "columnName": "username",
    "fieldType": "string",
    "length": 50,
    "isRequired": 1,
    "isUnique": 1,
    "isIndexed": 1,
    "isPrimary": 0,
    "defaultValue": null,
    "orderNum": 1
}
```

## 字段类型映射

| 前端类型 | MySQL类型 | 说明 |
|----------|-----------|------|
| string | VARCHAR(n) | 字符串，默认255 |
| text | TEXT | 长文本 |
| integer | INT | 整数 |
| number | DECIMAL(p,s) | 数字，默认(10,2) |
| boolean | TINYINT(1) | 布尔值 |
| date | DATE | 日期 |
| datetime | DATETIME | 日期时间 |
| select | VARCHAR(50) | 下拉选择 |
| json | JSON | JSON对象 |

## 使用流程

### 1. 创建数据模型

```http
POST /model
Content-Type: application/json

{
    "modelName": "产品信息",
    "modelCode": "product_info",
    "tableName": "tbl_product",
    "description": "产品基本信息表"
}
```

### 2. 配置字段

```http
PUT /model/1/fields
Content-Type: application/json

[
    {
        "fieldName": "产品编码",
        "fieldCode": "product_code",
        "columnName": "product_code",
        "fieldType": "string",
        "length": 50,
        "isRequired": 1,
        "isUnique": 1,
        "isIndexed": 1,
        "orderNum": 1
    },
    {
        "fieldName": "产品名称",
        "fieldCode": "product_name",
        "columnName": "product_name",
        "fieldType": "string",
        "length": 100,
        "isRequired": 1,
        "orderNum": 2
    }
]
```

### 3. 预览DDL并发布

```http
GET /model/1/preview   # 预览生成的SQL
POST /model/1/publish  # 执行建表
```

## 开发环境

- 端口: 8082
- API文档: http://localhost:8082/doc.html
- H2控制台: http://localhost:8082/h2-console

## 后续计划

- [ ] 支持ALTER TABLE修改表结构
- [ ] 支持外键关联
- [ ] 支持字段校验规则配置