# 流程服务 (Flow Service) 功能文档

## 概述

流程服务提供工作流定义、流程发起、审批流转等功能。支持多种审批模式：
1. 串行审批：按顺序依次审批
2. 并行审批：多人同时审批
3. 会签审批：多人审批，全部通过才流转
4. 条件分支：根据条件选择审批路径

## 功能清单

### ✅ 已实现功能

| 功能 | API | 说明 |
|------|-----|------|
| 流程定义列表 | GET /flow/list | 分页查询流程定义 |
| 流程定义详情 | GET /flow/{id} | 查询流程详细信息 |
| 创建流程定义 | POST /flow | 创建新流程定义 |
| 发布流程定义 | POST /flow/{id}/publish | 发布流程定义 |
| 发起流程 | POST /flow/instance/start | 发起流程实例 |
| 流程实例列表 | GET /flow/instance/list | 分页查询流程实例 |
| 流程实例详情 | GET /flow/instance/{id} | 查询实例和任务列表 |
| 取消流程 | DELETE /flow/instance/{id} | 取消流程实例 |
| 待办任务列表 | GET /flow/task/list | 分页查询待办任务 |
| 处理任务 | POST /flow/task/{id}/handle | 审批/驳回/转办任务 |
| 批量审批 | POST /flow/task/batch | 批量审批多个任务 |

## 数据结构

### FlowDefinition (流程定义)

```json
{
    "id": 1,
    "flowName": "请假审批流程",
    "flowCode": "leave_approval",
    "formId": 1,
    "nodes": "[...]",
    "edges": "[...]",
    "status": 1,
    "version": 1
}
```

### FlowInstance (流程实例)

```json
{
    "id": 1,
    "flowDefinitionId": 1,
    "flowName": "请假审批流程",
    "initiator": "user001",
    "currentNode": "task1",
    "status": "running",
    "startTime": "2024-01-15T10:00:00"
}
```

### FlowTask (流程任务)

```json
{
    "id": 1,
    "instanceId": 1,
    "nodeId": "task1",
    "nodeName": "部门经理审批",
    "assignType": "user",
    "assignee": "manager001",
    "status": "pending",
    "createTime": "2024-01-15T10:00:00"
}
```

## 节点类型

| nodeType | 说明 |
|----------|------|
| start | 开始节点 |
| end | 结束节点 |
| user_task | 用户审批任务 |
| condition | 条件分支 |
| parallel | 并行分支 |
| join | 会签汇聚 |

## 审批动作

| action | 说明 |
|--------|------|
| approve | 同意，推进流程 |
| reject | 驳回，退回或结束流程 |
| delegate | 转办，委托他人处理 |

## 使用流程

### 1. 创建流程定义

```http
POST /flow
Content-Type: application/json

{
    "flowName": "请假审批流程",
    "flowCode": "leave_approval",
    "formId": 1,
    "nodes": [
        {"nodeId": "start", "nodeType": "start"},
        {"nodeId": "task1", "nodeType": "user_task", "nodeName": "部门经理审批", "config": {"assignType": "role", "assignValue": ["dept_manager"]}},
        {"nodeId": "task2", "nodeType": "user_task", "nodeName": "HR审批", "config": {"assignType": "role", "assignValue": ["hr"]}},
        {"nodeId": "end", "nodeType": "end"}
    ],
    "edges": [
        {"source": "start", "target": "task1"},
        {"source": "task1", "target": "task2"},
        {"source": "task2", "target": "end"}
    ]
}
```

### 2. 发布并发起流程

```http
POST /flow/1/publish       # 发布流程定义
POST /flow/instance/start  # 发起流程
Content-Type: application/json

{
    "flowDefinitionId": 1,
    "formData": {"leaveType": "年假", "days": 3, "reason": "休假"}
}
```

### 3. 处理审批任务

```http
POST /flow/task/1/handle
Content-Type: application/json

{
    "action": "approve",
    "comment": "同意请假申请"
}
```

## 开发环境

- 端口: 8084
- API文档: http://localhost:8084/doc.html
- H2控制台: http://localhost:8084/h2-console

## 测试覆盖

| 测试类 | 测试内容 |
|--------|----------|
| FlowInstanceServiceImplTest | 流程发起、取消、详情查询 |
| FlowTaskServiceImplTest | 任务审批、驳回、转办、批量审批 |

运行测试：
```bash
mvn test -pl flow-service/flow-core
```

## 后续计划

- [ ] 流程监控和统计
- [ ] 超时自动处理
- [ ] 流程催办功能
- [ ] 流程撤回功能