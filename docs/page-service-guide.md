# 页面服务 (Page Service) 功能文档

## 概述

页面服务提供低代码页面的定义、布局配置和组件管理功能。支持多种页面类型和可视化页面设计。

## 功能清单

### ✅ 已实现功能

| 功能 | API | 说明 |
|------|-----|------|
| 页面列表 | GET /page/list | 分页查询页面定义 |
| 页面详情 | GET /page/{id} | 查询页面配置和组件 |
| 创建页面 | POST /page | 创建新页面定义 |
| 更新页面 | PUT /page | 更新页面配置 |
| 发布页面 | POST /page/{id}/publish | 发布页面 |
| 删除页面 | DELETE /page/{id} | 删除页面定义 |
| 获取组件 | GET /page/{id}/components | 获取页面组件列表 |
| 更新组件 | PUT /page/{id}/components | 更新页面组件 |
| 更新布局 | PUT /page/{id}/layout | 更新页面布局 |
| 按编码查询 | GET /page/code/{code} | 根据编码查询已发布页面 |

## 页面类型

| pageType | 说明 |
|----------|------|
| list | 列表页面 |
| form | 表单页面 |
| detail | 详情页面 |
| dashboard | 仪表盘页面 |

## 组件类型

| componentType | 说明 |
|---------------|------|
| input | 输入框 |
| select | 下拉选择 |
| table | 数据表格 |
| chart | 图表组件 |
| button | 按钮 |
| container | 容器组件 |
| tabs | 标签页 |
| card | 卡片 |

## 数据结构

### PageDefinition (页面定义)

```json
{
    "id": 1,
    "pageName": "用户管理",
    "pageCode": "user_manage",
    "pageType": "list",
    "formId": 1,
    "modelId": 1,
    "layoutConfig": "{...}",
    "status": 1
}
```

### PageComponent (页面组件)

```json
{
    "id": 1,
    "pageId": 1,
    "componentCode": "search_form",
    "componentType": "form",
    "componentConfig": "{...}",
    "positionConfig": "{\"x\": 0, \"y\": 0, \"w\": 12}",
    "orderNum": 1
}
```

## 使用流程

### 1. 创建页面

```http
POST /page
Content-Type: application/json

{
    "pageName": "用户管理",
    "pageCode": "user_manage",
    "pageType": "list",
    "modelId": 1
}
```

### 2. 配置组件

```http
PUT /page/1/components
Content-Type: application/json

[
    {
        "componentCode": "search_input",
        "componentType": "input",
        "componentConfig": {"label": "用户名", "placeholder": "请输入"},
        "positionConfig": {"x": 0, "y": 0, "w": 4}
    },
    {
        "componentCode": "data_table",
        "componentType": "table",
        "componentConfig": {"columns": ["id", "name", "email"]},
        "positionConfig": {"x": 0, "y": 1, "w": 12}
    }
]
```

### 3. 发布页面

```http
POST /page/1/publish
```

### 4. 查询已发布页面（供前端渲染）

```http
GET /page/code/user_manage
```

## 开发环境

- 端口: 8087
- API文档: http://localhost:8087/doc.html
- H2控制台: http://localhost:8087/h2-console

## 后续计划

- [ ] 页面模板库
- [ ] 组件事件联动
- [ ] 页面权限控制
- [ ] 页面版本管理