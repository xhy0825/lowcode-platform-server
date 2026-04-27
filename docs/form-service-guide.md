# 表单服务 (Form Service) 功能文档

## 概述

表单服务提供表单定义、字段配置、布局设计以及表单数据提交功能。支持两种存储方式：
1. 关联数据模型：数据存储在动态创建的表中
2. 无关联模型：数据存储在form_data_template表（JSON格式）

## 功能清单

### ✅ 已实现功能

| 功能 | API | 说明 |
|------|-----|------|
| 表单列表查询 | GET /form/list | 分页查询表单定义 |
| 表单详情查询 | GET /form/{id} | 查询表单详细信息 |
| 创建表单 | POST /form | 创建新表单定义 |
| 修改表单 | PUT /form | 修改表单信息 |
| 发布表单 | POST /form/{id}/publish | 发布表单（状态变更） |
| 删除表单 | DELETE /form/{id} | 删除表单定义 |
| 获取字段配置 | GET /form/{id}/fields | 获取表单字段配置 |
| 更新字段配置 | PUT /form/{id}/fields | 更新字段配置 |
| 更新布局配置 | PUT /form/{id}/layout | 更新布局配置 |
| 提交表单数据 | POST /form/{id}/data | 提交表单数据 |
| 更新表单数据 | PUT /form/{id}/data/{dataId} | 更新已提交数据 |
| 删除表单数据 | DELETE /form/{id}/data/{dataId} | 删除数据记录 |
| 查询表单数据 | GET /form/{id}/data/{dataId} | 查询单条数据 |
| 分页查询数据 | GET /form/{id}/data | 分页查询数据列表 |

## 数据结构

### FormDefinition (表单定义)

```json
{
    "id": 1,
    "formName": "用户注册表",
    "formCode": "user_register",
    "modelId": null,
    "fieldConfig": "[...]",
    "layoutConfig": "{...}",
    "validateRules": "{...}",
    "status": 1,
    "version": 1
}
```

### FieldConfig (字段配置)

```json
{
    "fieldCode": "username",
    "fieldName": "用户名",
    "widgetType": "input",
    "placeholder": "请输入用户名",
    "isRequired": 1,
    "isReadonly": 0,
    "isHidden": 0,
    "colSpan": 12,
    "rowOrder": 1,
    "validateType": "required",
    "validateMessage": "用户名不能为空",
    "maxLength": 50
}
```

## 组件类型

| widgetType | 说明 |
|------------|------|
| input | 单行文本输入框 |
| textarea | 多行文本域 |
| select | 下拉选择框 |
| radio | 单选框组 |
| checkbox | 复选框组 |
| date | 日期选择器 |
| datetime | 日期时间选择器 |
| number | 数字输入框 |
| switch | 开关组件 |
| upload | 文件上传 |
| image | 图片上传 |
| richtext | 富文本编辑器 |

## 使用流程

### 1. 创建表单

```http
POST /form
Content-Type: application/json

{
    "formName": "员工信息表",
    "formCode": "employee_info",
    "description": "员工基本信息采集"
}
```

### 2. 配置字段

```http
PUT /form/1/fields
Content-Type: application/json

[
    {
        "fieldCode": "name",
        "fieldName": "姓名",
        "widgetType": "input",
        "isRequired": 1,
        "colSpan": 12,
        "rowOrder": 1,
        "validateMessage": "姓名不能为空"
    },
    {
        "fieldCode": "department",
        "fieldName": "部门",
        "widgetType": "select",
        "dictType": "department",
        "colSpan": 12,
        "rowOrder": 2
    },
    {
        "fieldCode": "hire_date",
        "fieldName": "入职日期",
        "widgetType": "date",
        "colSpan": 12,
        "rowOrder": 3
    }
]
```

### 3. 发布并提交数据

```http
POST /form/1/publish      # 发布表单
POST /form/1/data         # 提交数据
Content-Type: application/json

{
    "name": "张三",
    "department": "技术部",
    "hire_date": "2024-01-15"
}
```

## 开发环境

- 端口: 8083
- API文档: http://localhost:8083/doc.html
- H2控制台: http://localhost:8083/h2-console

## 测试覆盖

| 测试类 | 测试内容 |
|--------|----------|
| FormDefinitionServiceImplTest | 表单定义CRUD、字段配置、发布流程 |
| FormDataServiceImplTest | 数据提交、更新、删除、查询 |

运行测试：
```bash
mvn test -pl form-service/form-core
```

## 后续计划

- [ ] 表单模板复制功能
- [ ] 表单版本管理
- [ ] 动态校验规则扩展
- [ ] 表单数据导入导出