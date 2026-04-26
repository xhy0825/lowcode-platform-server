-- ========================================
-- 低代码平台数据库初始化脚本
-- ========================================

-- 创建系统公共库
CREATE DATABASE IF NOT EXISTS lowcode_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE lowcode_platform;

-- ----------------------------------------
-- 1. 租户管理表（系统公共库）
-- ----------------------------------------
DROP TABLE IF EXISTS sys_tenant;
CREATE TABLE sys_tenant (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '租户ID',
    tenant_name VARCHAR(100) NOT NULL COMMENT '租户名称',
    tenant_code VARCHAR(50) NOT NULL COMMENT '租户编码',
    package_id BIGINT COMMENT '套餐ID',
    expire_time DATETIME COMMENT '过期时间',
    db_schema VARCHAR(50) COMMENT '数据库Schema',
    admin_user_id BIGINT COMMENT '管理员用户ID',
    status TINYINT DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标记 0-正常 1-删除',
    created_by VARCHAR(50) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_tenant_code (tenant_code)
) ENGINE=InnoDB COMMENT='租户信息表';

-- ----------------------------------------
-- 2. 套餐定义表（系统公共库）
-- ----------------------------------------
DROP TABLE IF EXISTS sys_package;
CREATE TABLE sys_package (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
    package_name VARCHAR(100) NOT NULL COMMENT '套餐名称',
    package_code VARCHAR(50) NOT NULL COMMENT '套餐编码',
    max_users INT DEFAULT 10 COMMENT '最大用户数',
    max_forms INT DEFAULT 20 COMMENT '最大表单数',
    max_flows INT DEFAULT 10 COMMENT '最大流程数',
    features JSON COMMENT '功能权限配置',
    price DECIMAL(10,2) COMMENT '价格',
    duration_days INT COMMENT '有效期天数',
    status TINYINT DEFAULT 0 COMMENT '状态',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标记',
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='套餐定义表';

-- ----------------------------------------
-- 3. 命令定义表（系统公共库）
-- ----------------------------------------
DROP TABLE IF EXISTS sys_command;
CREATE TABLE sys_command (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '命令ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    command_name VARCHAR(100) NOT NULL COMMENT '命令名称',
    command_code VARCHAR(50) NOT NULL COMMENT '命令编码',
    command_type VARCHAR(20) DEFAULT 'script' COMMENT '命令类型 script/sql/http/shell',
    script_content TEXT COMMENT 'Groovy脚本内容',
    params_schema JSON COMMENT '参数定义',
    schedule_type VARCHAR(20) DEFAULT 'manual' COMMENT '触发类型 manual/cron/event',
    cron_expression VARCHAR(100) COMMENT '定时表达式',
    timeout INT DEFAULT 300 COMMENT '超时时间(秒)',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    status TINYINT DEFAULT 0 COMMENT '状态',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id),
    UNIQUE KEY uk_tenant_code (tenant_id, command_code)
) ENGINE=InnoDB COMMENT='后台命令定义表';

-- ----------------------------------------
-- 4. 命令执行日志表（系统公共库）
-- ----------------------------------------
DROP TABLE IF EXISTS sys_command_log;
CREATE TABLE sys_command_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    command_id BIGINT NOT NULL COMMENT '命令ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    trigger_type VARCHAR(20) NOT NULL COMMENT '触发类型 manual/schedule/event',
    params JSON COMMENT '执行参数',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    duration INT COMMENT '执行耗时(ms)',
    status VARCHAR(20) NOT NULL COMMENT '状态 running/success/failed',
    result TEXT COMMENT '执行结果',
    error_message TEXT COMMENT '异常信息',
    PRIMARY KEY (id),
    KEY idx_command_id (command_id),
    KEY idx_tenant_id (tenant_id),
    KEY idx_start_time (start_time)
) ENGINE=InnoDB COMMENT='命令执行日志表';

-- ----------------------------------------
-- 5. 初始化数据
-- ----------------------------------------

-- 初始化默认套餐
INSERT INTO sys_package (id, package_name, package_code, max_users, max_forms, max_flows, features, price, duration_days, status, created_by)
VALUES
(1, '免费版', 'free', 5, 10, 5, '{"form":true,"flow":true,"report":false}', 0, 365, 0, 'system'),
(2, '标准版', 'standard', 20, 50, 20, '{"form":true,"flow":true,"report":true,"page":true}', 999, 365, 0, 'system'),
(3, '企业版', 'enterprise', 100, 200, 50, '{"form":true,"flow":true,"report":true,"page":true,"groovy":true}', 2999, 365, 0, 'system');

-- 初始化默认租户
INSERT INTO sys_tenant (id, tenant_name, tenant_code, package_id, expire_time, db_schema, admin_user_id, status, created_by)
VALUES
(1, '默认租户', '000000', 2, DATE_ADD(NOW(), INTERVAL 365 DAY), 'tenant_000000', NULL, 0, 'system'),
(2, '示例企业', 'demo001', 3, DATE_ADD(NOW(), INTERVAL 365 DAY), 'tenant_demo001', NULL, 0, 'system');

-- ----------------------------------------
-- 创建租户Schema模板
-- ----------------------------------------

-- 创建租户000000的Schema
CREATE DATABASE IF NOT EXISTS tenant_000000 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;