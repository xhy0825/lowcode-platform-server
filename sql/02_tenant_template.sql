-- ========================================
-- 租户Schema表结构模板
-- 使用时替换 tenant_000000 为具体租户Schema
-- ========================================

USE tenant_000000;

-- ----------------------------------------
-- 1. 用户表
-- ----------------------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    salt VARCHAR(50) COMMENT '盐值',
    real_name VARCHAR(100) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(200) COMMENT '头像',
    gender TINYINT COMMENT '性别 0-未知 1-男 2-女',
    status TINYINT DEFAULT 0 COMMENT '状态 0-正常 1-禁用',
    dept_id BIGINT COMMENT '部门ID',
    login_ip VARCHAR(50) COMMENT '最后登录IP',
    login_time DATETIME COMMENT '最后登录时间',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标记',
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (tenant_id, username),
    KEY idx_dept_id (dept_id)
) ENGINE=InnoDB COMMENT='用户信息表';

-- ----------------------------------------
-- 2. 部门表
-- ----------------------------------------
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    dept_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    dept_code VARCHAR(50) COMMENT '部门编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    ancestors VARCHAR(500) COMMENT '祖级列表',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    leader VARCHAR(50) COMMENT '负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 0 COMMENT '状态',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB COMMENT='部门表';

-- ----------------------------------------
-- 3. 角色表
-- ----------------------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    data_scope VARCHAR(20) DEFAULT 'all' COMMENT '数据权限范围 all/custom/dept/dept_and_child/self',
    status TINYINT DEFAULT 0 COMMENT '状态',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (tenant_id, role_code)
) ENGINE=InnoDB COMMENT='角色信息表';

-- ----------------------------------------
-- 4. 权限表（菜单、按钮、API）
-- ----------------------------------------
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) COMMENT '权限编码',
    permission_type VARCHAR(20) NOT NULL COMMENT '权限类型 menu/button/api/data',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    redirect VARCHAR(200) COMMENT '重定向路径',
    icon VARCHAR(100) COMMENT '图标',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    visible TINYINT DEFAULT 1 COMMENT '是否可见',
    api_path VARCHAR(200) COMMENT 'API路径',
    api_method VARCHAR(10) COMMENT 'API方法 GET/POST/PUT/DELETE',
    status TINYINT DEFAULT 0 COMMENT '状态',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB COMMENT='权限表';

-- ----------------------------------------
-- 5. 用户角色关联表
-- ----------------------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB COMMENT='用户角色关联表';

-- ----------------------------------------
-- 6. 角色权限关联表
-- ----------------------------------------
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (role_id, permission_id)
) ENGINE=InnoDB COMMENT='角色权限关联表';

-- ----------------------------------------
-- 7. 角色部门关联表（数据权限）
-- ----------------------------------------
DROP TABLE IF EXISTS sys_role_dept;
CREATE TABLE sys_role_dept (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    dept_id BIGINT NOT NULL COMMENT '部门ID',
    PRIMARY KEY (role_id, dept_id)
) ENGINE=InnoDB COMMENT='角色部门关联表';

-- ----------------------------------------
-- 8. 字典类型表
-- ----------------------------------------
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典类型ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    dict_type VARCHAR(100) NOT NULL COMMENT '字典类型',
    dict_name VARCHAR(100) NOT NULL COMMENT '字典名称',
    status TINYINT DEFAULT 0 COMMENT '状态',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_type (tenant_id, dict_type)
) ENGINE=InnoDB COMMENT='字典类型表';

-- ----------------------------------------
-- 9. 字典数据表
-- ----------------------------------------
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典数据ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    dict_type VARCHAR(100) NOT NULL COMMENT '字典类型',
    dict_label VARCHAR(100) NOT NULL COMMENT '字典标签',
    dict_value VARCHAR(100) NOT NULL COMMENT '字典值',
    dict_sort INT DEFAULT 0 COMMENT '排序',
    css_class VARCHAR(100) COMMENT '样式属性',
    list_class VARCHAR(100) COMMENT '表格回显样式',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认',
    status TINYINT DEFAULT 0 COMMENT '状态',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id),
    KEY idx_dict_type (dict_type)
) ENGINE=InnoDB COMMENT='字典数据表';

-- ----------------------------------------
-- 10. 系统配置表
-- ----------------------------------------
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value VARCHAR(500) COMMENT '配置值',
    config_type VARCHAR(50) COMMENT '配置类型',
    status TINYINT DEFAULT 0 COMMENT '状态',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (tenant_id, config_key)
) ENGINE=InnoDB COMMENT='系统配置表';

-- ----------------------------------------
-- 11. 操作日志表
-- ----------------------------------------
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    module VARCHAR(100) COMMENT '模块',
    business_type VARCHAR(50) COMMENT '业务类型',
    method VARCHAR(200) COMMENT '方法',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    response_result TEXT COMMENT '响应结果',
    status TINYINT COMMENT '状态',
    error_msg TEXT COMMENT '错误消息',
    oper_ip VARCHAR(50) COMMENT '操作IP',
    oper_name VARCHAR(50) COMMENT '操作人',
    oper_time DATETIME COMMENT '操作时间',
    duration INT COMMENT '耗时(ms)',
    PRIMARY KEY (id),
    KEY idx_tenant_time (tenant_id, oper_time)
) ENGINE=InnoDB COMMENT='操作日志表';

-- ----------------------------------------
-- 初始化数据
-- ----------------------------------------

-- 初始化部门
INSERT INTO sys_dept (id, tenant_id, dept_name, dept_code, parent_id, ancestors, order_num, status, created_by) VALUES
(1, '000000', '总公司', 'root', 0, '0', 0, 0, 'system'),
(2, '000000', '研发部', 'dev', 1, '0,1', 1, 0, 'system'),
(3, '000000', '市场部', 'market', 1, '0,1', 2, 0, 'system'),
(4, '000000', '财务部', 'finance', 1, '0,1', 3, 0, 'system'),
(5, '000000', '人事部', 'hr', 1, '0,1', 4, 0, 'system');

-- 初始化用户 (密码: admin123 -> BCrypt加密)
INSERT INTO sys_user (id, tenant_id, username, password, salt, real_name, email, phone, status, dept_id, created_by) VALUES
(1, '000000', 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU.QQ03HMgg', '', '超级管理员', 'admin@lowcode.com', '13800138000', 0, 1, 'system'),
(2, '000000', 'test', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU.QQ03HMgg', '', '测试用户', 'test@lowcode.com', '13800138001', 0, 2, 'system');

-- 初始化角色
INSERT INTO sys_role (id, tenant_id, role_name, role_code, data_scope, status, created_by) VALUES
(1, '000000', '超级管理员', 'admin', 'all', 0, 'system'),
(2, '000000', '普通用户', 'user', 'self', 0, 'system'),
(3, '000000', '部门管理员', 'dept_admin', 'dept_and_child', 0, 'system');

-- 用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2);

-- 初始化权限菜单
INSERT INTO sys_permission (id, tenant_id, permission_name, permission_code, permission_type, parent_id, path, component, icon, order_num, visible, status, created_by) VALUES
(1, '000000', '系统管理', 'system', 'menu', 0, '/system', '', 'Setting', 1, 1, 0, 'system'),
(2, '000000', '用户管理', 'system:user', 'menu', 1, '/system/user', 'modules/system/user/index', 'User', 1, 1, 0, 'system'),
(3, '000000', '新增用户', 'system:user:add', 'button', 2, '', '', '', 1, 1, 0, 'system'),
(4, '000000', '编辑用户', 'system:user:edit', 'button', 2, '', '', '', 2, 1, 0, 'system'),
(5, '000000', '删除用户', 'system:user:delete', 'button', 2, '', '', '', 3, 1, 0, 'system'),
(6, '000000', '角色管理', 'system:role', 'menu', 1, '/system/role', 'modules/system/role/index', 'UserFilled', 2, 1, 0, 'system'),
(7, '000000', '权限管理', 'system:permission', 'menu', 1, '/system/permission', 'modules/system/permission/index', 'Lock', 3, 1, 0, 'system'),
(8, '000000', '数据字典', 'system:dict', 'menu', 1, '/system/dict', 'modules/system/dict/index', 'Collection', 4, 1, 0, 'system'),
(9, '000000', '系统配置', 'system:config', 'menu', 1, '/system/config', 'modules/system/config/index', 'Tools', 5, 1, 0, 'system'),
(10, '000000', '命令管理', 'system:command', 'menu', 1, '/system/command', 'modules/system/command/index', 'Cpu', 6, 1, 0, 'system'),
(11, '000000', '租户管理', 'system:tenant', 'menu', 1, '/system/tenant', 'modules/system/tenant/index', 'OfficeBuilding', 7, 1, 0, 'system'),
(12, '000000', '数据引擎', 'data', 'menu', 0, '/data', '', 'DataAnalysis', 2, 1, 0, 'system'),
(13, '000000', '数据模型', 'data:model', 'menu', 12, '/data/model', 'modules/data/model/index', 'Coin', 1, 1, 0, 'system'),
(14, '000000', '表结构管理', 'data:table', 'menu', 12, '/data/table', 'modules/data/table/index', 'Grid', 2, 1, 0, 'system'),
(15, '000000', '表单设计', 'form', 'menu', 0, '/form', '', 'EditPen', 3, 1, 0, 'system'),
(16, '000000', '表单列表', 'form:list', 'menu', 15, '/form/list', 'modules/form/list/index', 'List', 1, 1, 0, 'system'),
(17, '000000', '流程设计', 'flow', 'menu', 0, '/flow', '', 'Share', 4, 1, 0, 'system'),
(18, '000000', '流程列表', 'flow:list', 'menu', 17, '/flow/list', 'modules/flow/list/index', 'List', 1, 1, 0, 'system'),
(19, '000000', '页面搭建', 'page', 'menu', 0, '/page', '', 'Grid', 5, 1, 0, 'system'),
(20, '000000', '页面列表', 'page:list', 'menu', 19, '/page/list', 'modules/page/list/index', 'List', 1, 1, 0, 'system'),
(21, '000000', '报表设计', 'report', 'menu', 0, '/report', '', 'TrendCharts', 6, 1, 0, 'system'),
(22, '000000', '报表列表', 'report:list', 'menu', 21, '/report/list', 'modules/report/list/index', 'List', 1, 1, 0, 'system');

-- 角色权限关联（超级管理员拥有所有权限）
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11),
(1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18), (1, 19), (1, 20), (1, 21), (1, 22);

-- 普通用户权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(2, 12), (2, 13), (2, 15), (2, 16), (2, 17), (2, 18);

-- 初始化字典类型
INSERT INTO sys_dict_type (id, tenant_id, dict_type, dict_name, status, created_by) VALUES
(1, '000000', 'sys_normal_disable', '系统状态', 0, 'system'),
(2, '000000', 'sys_yes_no', '是否', 0, 'system'),
(3, '000000', 'sys_gender', '性别', 0, 'system'),
(4, '000000', 'sys_data_scope', '数据权限范围', 0, 'system'),
(5, '000000', 'sys_permission_type', '权限类型', 0, 'system');

-- 初始化字典数据
INSERT INTO sys_dict_data (id, tenant_id, dict_type, dict_label, dict_value, dict_sort, list_class, is_default, status, created_by) VALUES
(1, '000000', 'sys_normal_disable', '正常', '0', 1, 'primary', 1, 0, 'system'),
(2, '000000', 'sys_normal_disable', '禁用', '1', 2, 'danger', 0, 0, 'system'),
(3, '000000', 'sys_yes_no', '是', 'true', 1, 'primary', 1, 0, 'system'),
(4, '000000', 'sys_yes_no', '否', 'false', 2, 'danger', 0, 0, 'system'),
(5, '000000', 'sys_gender', '未知', '0', 1, 'info', 1, 0, 'system'),
(6, '000000', 'sys_gender', '男', '1', 2, 'primary', 0, 0, 'system'),
(7, '000000', 'sys_gender', '女', '2', 3, 'danger', 0, 0, 'system'),
(8, '000000', 'sys_data_scope', '全部数据', 'all', 1, 'primary', 1, 0, 'system'),
(9, '000000', 'sys_data_scope', '自定义数据', 'custom', 2, 'warning', 0, 0, 'system'),
(10, '000000', 'sys_data_scope', '本部门数据', 'dept', 3, 'success', 0, 0, 'system'),
(11, '000000', 'sys_data_scope', '本部门及以下', 'dept_and_child', 4, 'info', 0, 0, 'system'),
(12, '000000', 'sys_data_scope', '仅本人数据', 'self', 5, 'danger', 0, 0, 'system'),
(13, '000000', 'sys_permission_type', '菜单', 'menu', 1, 'primary', 1, 0, 'system'),
(14, '000000', 'sys_permission_type', '按钮', 'button', 2, 'success', 0, 0, 'system'),
(15, '000000', 'sys_permission_type', 'API', 'api', 3, 'warning', 0, 0, 'system'),
(16, '000000', 'sys_permission_type', '数据', 'data', 4, 'danger', 0, 0, 'system');

-- 初始化系统配置
INSERT INTO sys_config (id, tenant_id, config_key, config_value, config_type, status, created_by) VALUES
(1, '000000', 'sys.login.captcha', 'true', 'login', 0, 'system'),
(2, '000000', 'sys.password.minLength', '8', 'security', 0, 'system'),
(3, '000000', 'sys.upload.maxSize', '10485760', 'upload', 0, 'system'),
(4, '000000', 'sys.flow.notify.email', 'true', 'flow', 0, 'system'),
(5, '000000', 'sys.dict.cache.expire', '3600', 'cache', 0, 'system');