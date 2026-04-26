# 低代码平台 (LowCode Platform)

基于 **Spring Cloud Alibaba + Vue 3** 构建的企业级低代码开发平台，面向业务人员实现零代码应用搭建。

## 技术栈

### 后端
- Spring Boot 3.2.x
- Spring Cloud Alibaba 2023.x
  - Nacos (服务注册/配置中心)
  - Sentinel (流量控制)
  - RocketMQ (消息队列)
- MyBatis Plus 3.5.x
- MySQL 8.x (多租户Schema隔离)
- Redis 7.x (缓存)
- Groovy 4.x (脚本引擎)

### 前端
- Vue 3.4.x
- TypeScript 5.x
- Element Plus 2.x
- Vite 5.x
- ECharts 5.x
- Vue Flow (流程设计)

## 核心功能

| 模块 | 功能 | 状态 |
|------|------|------|
| 系统管理 | 用户、角色、权限、字典、配置 | ✅ 已实现 |
| 数据引擎 | 数据模型设计、动态建表(DDL) | ✅ 已实现 |
| 表单设计 | 表单定义、字段配置、数据存储 | 🔄 进行中 |
| 流程设计 | 流程定义、审批流转、任务管理 | 🔄 进行中 |
| 页面搭建 | 页面布局、组件配置 | 🔄 进行中 |
| 报表设计 | 图表配置、仪表盘 | 🔄 进行中 |
| 命令管理 | Groovy脚本执行、定时任务 | 🔄 进行中 |

## 项目结构

```
lowcode-platform-server/        # 后端服务
├── gateway/                    # API网关 (8080)
├── system-service/             # 系统服务 (8081)
├── data-service/               # 数据服务 (8082)
├── form-service/               # 表单服务 (8083)
├── flow-service/               # 流程服务 (8084)
├── page-service/               # 页面服务 (8085)
├── report-service/             # 报表服务 (8086)
├── file-service/               # 文件服务 (8087)
├── common/                     # 公共模块
│   ├── common-core/            # 核心工具
│   ├── common-redis/           # Redis配置
│   ├── common-mybatis/         # 多租户数据源
│   ├── common-security/        # JWT认证
│   ├── common-groovy/          # Groovy引擎
│   └── common-rocketmq/        # MQ配置
└── sql/                        # 数据库脚本

lowcode-platform-web/           # 前端项目
├── src/
│   ├── modules/                # 功能模块
│   ├── components/             # 组件库
│   ├── api/                    # API层
│   ├── stores/                 # 状态管理
│   └── router/                 # 路由配置
└── vite.config.ts
```

## 快速开始

### 1. 环境要求
- JDK 17+
- MySQL 8.x
- Redis 7.x
- Nacos 2.3.x
- Node.js 18+

### 2. 初始化数据库
```bash
# 执行SQL脚本
mysql -u root -p < sql/01_sys_common.sql
mysql -u root -p < sql/02_tenant_template.sql
mysql -u root -p < sql/03_business_tables.sql
```

### 3. 启动后端服务
```bash
cd lowcode-platform-server

# 启动 Nacos (需提前安装)
# 启动 Redis
# 启动 MySQL

# Maven构建
mvn clean install -DskipTests

# 启动各服务
java -jar gateway/target/gateway.jar
java -jar system-service/system-core/target/system-core.jar
java -jar data-service/data-core/target/data-core.jar
```

### 4. 启动前端
```bash
cd lowcode-platform-web
npm install
npm run dev
```

访问: http://localhost:3000

### 5. 默认账号
- 用户名: `admin`
- 密码: `admin123`

## 多租户架构

采用 **Schema隔离** 模式，每个租户使用独立的数据库Schema：

```
MySQL实例
├── lowcode_platform    # 系统公共库
├── tenant_000000       # 默认租户
├── tenant_demo001      # 示例企业
```

租户识别方式：
- 请求头: `X-Tenant-Id`
- 子域名: `tenant001.platform.com`

## API文档

各服务启动后访问 Knife4j：
- Gateway: http://localhost:8080/doc.html
- System: http://localhost:8081/doc.html
- Data: http://localhost:8082/doc.html

## 开发路线

| 阶段 | 内容 | 时间 |
|------|------|------|
| Phase 1 | 基础设施、Gateway、多租户 | Week 1-2 |
| Phase 2 | 系统服务(用户、权限、字典) | Week 3-4 |
| Phase 3 | 数据引擎(模型、建表) | Week 5-6 |
| Phase 4 | Groovy命令管理 | Week 7 |
| Phase 5 | 表单引擎 | Week 8-9 |
| Phase 6 | 流程引擎 | Week 10-11 |
| Phase 7 | 页面引擎 | Week 12-13 |
| Phase 8 | 报表引擎 | Week 14-15 |
| Phase 9 | K8s部署、测试 | Week 16 |

## License

MIT