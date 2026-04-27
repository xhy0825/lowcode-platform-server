# 低代码平台 (LowCode Platform)

基于 **Spring Cloud Alibaba + Vue 3** 构建的企业级低代码开发平台，面向业务人员实现零代码应用搭建。

## 技术栈

### 后端
- Spring Boot 3.2.x
- Spring Cloud Alibaba 2023.x
  - Nacos (服务注册/配置中心)
  - Sentinel (流量控制)
  - RocketMQ (消息队列)
- MyBatis Plus 3.5.x (多租户Schema隔离)
- MySQL 8.x
- Redis 7.x (缓存)
- Groovy 4.x (脚本引擎)
- JWT (无状态认证)
- Knife4j (API文档)

### 前端
- Vue 3.4.x
- TypeScript 5.x
- Element Plus 2.x
- Vite 5.x
- ECharts 5.x
- Vue Flow (流程设计)

---

## 功能实现清单

### ✅ 已完成功能

#### 1. 基础设施层
| 功能模块 | 实现内容 | 文件位置 |
|----------|----------|----------|
| **Gateway网关** | 路由配置、白名单、Header注入 | `gateway/src/main/resources/application.yml` |
| **多租户支持** | TenantLineHandler、Schema隔离 | `common/common-mybatis/MybatisPlusConfig.java` |
| **Redis缓存** | RedisTemplate封装、分布式锁 | `common/common-redis/RedisService.java` |
| **Nacos集成** | 服务注册、配置中心 | `common/common-nacos/` |
| **API文档** | Knife4j Swagger集成 | `common/common-swagger/` |
| **CI/CD** | GitHub Actions自动构建部署 | `.github/workflows/deploy.yml` |

#### 2. 安全认证模块 (common-security)
| 功能模块 | 实现内容 | 文件位置 |
|----------|----------|----------|
| **JWT认证** | Token生成/解析/验证、刷新机制 | `jwt/JwtTokenProvider.java` |
| **安全上下文** | ThreadLocal用户信息存储 | `context/SecurityContextHolder.java` |
| **权限注解** | @RequiresPermissions、@RequiresRoles | `annotation/` |
| **权限切面** | AOP权限校验拦截 | `aop/PermissionAspect.java` |
| **验证码服务** | 图形验证码生成、Redis存储 | `captcha/CaptchaService.java` |
| **异常处理** | UnauthorizedException、ForbiddenException | `exception/` |

#### 3. 系统管理服务 (system-service)
| 功能模块 | Controller | Service | 状态 |
|----------|------------|---------|------|
| **用户管理** | SysUserController | SysUserService | ✅ 完整实现 |
| **角色管理** | SysRoleController | SysRoleService | ✅ 完整实现 |
| **权限管理** | - | SysPermissionService | ✅ 菜单树/权限校验 |
| **字典管理** | SysDictController | SysDictDataService | ✅ 完整实现 |
| **配置管理** | SysConfigController | SysConfigService | ✅ 完整实现 |
| **租户管理** | SysTenantController | SysTenantService | ✅ 完整实现 |
| **命令管理** | SysCommandController | SysCommandService | ✅ Groovy脚本执行 |
| **认证接口** | SysAuthController | - | ✅ 登录/验证码/Token刷新 |

#### 4. Groovy脚本引擎 (common-groovy)
| 功能模块 | 实现内容 | 文件位置 |
|----------|----------|----------|
| **脚本执行器** | 异步执行、超时控制、结果封装 | `executor/GroovyExecutor.java` |
| **沙箱安全** | 白名单导入、安全检查 | `sandbox/GroovySandbox.java` |
| **命令日志** | 执行日志记录、重试机制 | `system-service/SysCommandLog` |

---

### 🔄 进行中功能

#### 5. 流程服务 (flow-service) - 33个Java文件
| 功能模块 | 实现状态 |
|----------|----------|
| 流程定义管理 | 🔄 基础CRUD完成 |
| 流程任务管理 | 🔄 基础结构完成 |
| 流程审批流转 | 🔄 待完善 |
| 流程历史记录 | 🔄 待完善 |

#### 6. 数据服务 (data-service) - 16个Java文件
| 功能模块 | 实现状态 |
|----------|----------|
| 数据模型定义 | 🔄 基础Entity完成 |
| 动态DDL建表 | 🔄 待完善 |
| 数据CRUD操作 | 🔄 待完善 |

#### 7. 表单服务 (form-service) - 11个Java文件
| 功能模块 | 实现状态 |
|----------|----------|
| 表单定义管理 | 🔄 基础完成 |
| 字段配置管理 | 🔄 基础完成 |
| 表单数据存储 | 🔄 JSON存储+动态表 |

#### 8. 文件服务 (file-service)
| 功能模块 | 实现状态 |
|----------|----------|
| MinIO集成 | 🔄 配置完成 |
| 文件上传下载 | 🔄 待完善 |

---

### 📋 待开发功能

| 服务 | 功能模块 | 计划 |
|------|----------|------|
| **page-service** | 页面布局设计、组件配置 | Phase 7 |
| **report-service** | 图表配置、仪表盘 | Phase 8 |
| **流程引擎** | 审批流转、条件分支 | Phase 6 |
| **表单引擎** | 表单渲染、字段校验 | Phase 5 |

---

## 项目结构

```
lowcode-platform-server/        # 后端服务 (Maven多模块)
├── gateway/                    # API网关 (8080) ✅
├── system-service/             # 系统服务 (8081) ✅
│   ├── system-api/             # API接口定义
│   └── system-core/            # 业务实现 (49个Java文件)
├── data-service/               # 数据服务 (8082) 🔄
│   ├── data-api/
│   └── data-core/              # (16个Java文件)
├── form-service/               # 表单服务 (8083) 🔄
│   ├── form-api/
│   └── form-core/              # (11个Java文件)
├── flow-service/               # 流程服务 (8084) 🔄
│   ├── flow-api/
│   └── flow-core/              # (33个Java文件)
├── page-service/               # 页面服务 (8085) 📋
│   ├── page-api/
│   └── page-core/
├── report-service/             # 报表服务 (8086) 📋
│   ├── report-api/
│   └── report-core/
├── file-service/               # 文件服务 (8087) 🔄
│   ├── file-api/
│   └── file-core/
├── common/                     # 公共模块 (8个Java文件)
│   ├── common-core/            # 工具类、异常、基类
│   ├── common-redis/           # Redis封装
│   ├── common-mybatis/         # 多租户MyBatis配置
│   ├── common-security/        # JWT认证、权限注解 ✅
│   ├── common-groovy/          # Groovy执行引擎 ✅
│   ├── common-rocketmq/        # MQ配置
│   ├── common-nacos/           # Nacos配置
│   └── common-swagger/         # API文档配置
├── sql/                        # 数据库脚本
│   ├── 01_sys_common.sql       # 系统公共表
│   ├── 02_tenant_template.sql  # 租户模板表
│   └── 03_business_tables.sql  # 业务表
├── docker-compose.yml          # Docker编排配置
├── .github/workflows/          # CI/CD配置 ✅
└── QUICKSTART.md               # 快速启动指南
```

---

## 快速开始

### 方式一：IDEA快速启动（推荐）

1. 打开IntelliJ IDEA，导入项目
2. **File → Project Structure → SDKs → Download JDK (Version: 17)**
3. 运行 `SystemApplication.java`（dev profile自动启动内嵌H2+Redis）

详见 [QUICKSTART.md](QUICKSTART.md)

### 方式二：Docker Compose启动

```bash
cd lowcode-platform-server
docker-compose up -d
```

启动：MySQL → Redis → Nacos → Gateway → 各微服务

### 方式三：本地开发环境

```bash
# 1. 初始化数据库
mysql -u root -p < sql/01_sys_common.sql
mysql -u root -p < sql/02_tenant_template.sql
mysql -u root -p < sql/03_business_tables.sql

# 2. 启动基础设施
# Nacos: https://nacos.io 下载并启动
# Redis: redis-server
# MySQL: 确保运行

# 3. Maven构建
mvn clean install -DskipTests

# 4. 启动服务
java -jar gateway/target/gateway.jar
java -jar system-service/system-core/target/system-core.jar
```

---

## 默认账号

- 用户名: `admin`
- 密码: `admin123`

---

## API文档

各服务启动后访问 Knife4j：
- Gateway: http://localhost:8080/doc.html
- System: http://localhost:8081/doc.html

---

## 多租户架构

采用 **Schema隔离** 模式：

```
MySQL实例
├── lowcode_platform    # 系统公共库
├── tenant_000000       # 默认租户
├── tenant_xxx          # 其他租户
```

租户识别：请求头 `X-Tenant-Id`

---

## 开发进度

| 阶段 | 内容 | 状态 |
|------|------|------|
| Phase 1 | 基础设施、Gateway、多租户 | ✅ 完成 |
| Phase 2 | 系统服务(用户、权限、字典) | ✅ 完成 |
| Phase 3 | JWT认证、权限注解、验证码 | ✅ 完成 |
| Phase 4 | Groovy命令引擎 | ✅ 完成 |
| Phase 5 | CI/CD Pipeline | ✅ 完成 |
| Phase 6 | 数据引擎 | 🔄 进行中 |
| Phase 7 | 表单引擎 | 🔄 进行中 |
| Phase 8 | 流程引擎 | 🔄 进行中 |
| Phase 9 | 页面/报表引擎 | 📋 待开发 |

---

## License

MIT