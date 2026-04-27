# 报表服务 (Report Service) 功能文档

## 概述

报表服务提供图表配置、数据可视化和仪表盘功能。支持多种图表类型和动态数据查询。

## 功能清单

### ✅ 已实现功能

| 功能 | API | 说明 |
|------|-----|------|
| 图表列表 | GET /chart/list | 分页查询图表定义 |
| 图表详情 | GET /chart/{id} | 查询图表配置 |
| 创建图表 | POST /chart | 创建新图表定义 |
| 更新图表 | PUT /chart | 更新图表配置 |
| 发布图表 | POST /chart/{id}/publish | 发布图表 |
| 获取图表数据 | GET /chart/{id}/data | 获取图表渲染数据 |
| 预览图表 | POST /chart/preview | 预览图表数据 |
| 仪表盘列表 | GET /dashboard/list | 分页查询仪表盘 |
| 仪表盘详情 | GET /dashboard/{id} | 查询仪表盘配置 |
| 创建仪表盘 | POST /dashboard | 创建仪表盘 |
| 更新仪表盘 | PUT /dashboard | 更新仪表盘配置 |
| 获取仪表盘数据 | GET /dashboard/{id}/data | 获取仪表盘完整数据 |
| 更新布局 | PUT /dashboard/{id}/layout | 更新图表布局 |

## 图表类型

| chartType | 说明 |
|-----------|------|
| line | 折线图 |
| bar | 柱状图 |
| pie | 饼图 |
| scatter | 散点图 |
| gauge | 仪表盘 |
| table | 数据表格 |

## 数据结构

### ChartDefinition (图表定义)

```json
{
    "id": 1,
    "chartName": "销售趋势图",
    "chartCode": "sales_trend",
    "chartType": "line",
    "dataSourceId": 1,
    "queryConfig": "{\"tableName\": \"sales_data\"}",
    "dimensionConfig": "[\"month\"]",
    "metricConfig": "[{\"field\": \"amount\", \"aggFunc\": \"SUM\"}]"
}
```

### DashboardDefinition (仪表盘定义)

```json
{
    "id": 1,
    "dashboardName": "业务概览",
    "dashboardCode": "business_overview",
    "layoutConfig": "[{\"chartId\": 1, \"position\": {\"x\": 0, \"y\": 0}, \"size\": {\"w\": 6, \"h\": 4}}]",
    "refreshInterval": 300
}
```

## 使用流程

### 1. 创建图表

```http
POST /chart
Content-Type: application/json

{
    "chartName": "销售趋势图",
    "chartCode": "sales_trend",
    "chartType": "line",
    "queryConfig": {"tableName": "sales_data"},
    "dimensionConfig": ["month"],
    "metricConfig": [{"field": "amount", "aggFunc": "SUM"}]
}
```

### 2. 创建仪表盘并添加图表

```http
POST /dashboard
Content-Type: application/json

{
    "dashboardName": "业务概览",
    "dashboardCode": "business_overview"
}

PUT /dashboard/1/layout
Content-Type: application/json

[
    {"chartId": 1, "position": {"x": 0, "y": 0}, "size": {"w": 6, "h": 4}},
    {"chartId": 2, "position": {"x": 6, "y": 0}, "size": {"w": 6, "h": 4}}
]
```

### 3. 获取仪表盘数据

```http
GET /dashboard/1/data
```

响应包含仪表盘配置和所有图表的数据。

## 开发环境

- 端口: 8086
- API文档: http://localhost:8086/doc.html
- H2控制台: http://localhost:8086/h2-console

## 后续计划

- [ ] 更多图表类型支持
- [ ] 数据钻取功能
- [ ] 报表导出功能
- [ ] 定时报表推送