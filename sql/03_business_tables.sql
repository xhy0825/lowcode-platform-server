-- ========================================
-- 业务表结构（表单、流程、页面、报表、数据模型）
-- 在租户Schema中创建
-- ========================================

USE tenant_000000;

-- ----------------------------------------
-- 1. 数据模型定义表
-- ----------------------------------------
DROP TABLE IF EXISTS data_model;
CREATE TABLE data_model (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '模型ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    model_name VARCHAR(100) NOT NULL COMMENT '模型名称',
    model_code VARCHAR(50) NOT NULL COMMENT '模型编码',
    table_name VARCHAR(50) NOT NULL COMMENT '物理表名',
    description VARCHAR(500) COMMENT '描述',
    status TINYINT DEFAULT 0 COMMENT '状态 0-草稿 1-已发布',
    version INT DEFAULT 1 COMMENT '版本号',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id),
    UNIQUE KEY uk_model_code (tenant_id, model_code)
) ENGINE=InnoDB COMMENT='数据模型定义表';

-- ----------------------------------------
-- 2. 数据模型字段表
-- ----------------------------------------
DROP TABLE IF EXISTS data_model_field;
CREATE TABLE data_model_field (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '字段ID',
    model_id BIGINT NOT NULL COMMENT '模型ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    field_name VARCHAR(100) NOT NULL COMMENT '字段名称',
    field_code VARCHAR(50) NOT NULL COMMENT '字段编码',
    column_name VARCHAR(50) NOT NULL COMMENT '数据库列名',
    field_type VARCHAR(50) NOT NULL COMMENT '字段类型 string/number/date/datetime/select/textarea...',
    length INT COMMENT '长度',
    precision INT COMMENT '精度',
    scale INT COMMENT '小数位',
    is_required TINYINT DEFAULT 0 COMMENT '是否必填',
    is_unique TINYINT DEFAULT 0 COMMENT '是否唯一',
    is_indexed TINYINT DEFAULT 0 COMMENT '是否索引',
    is_primary TINYINT DEFAULT 0 COMMENT '是否主键',
    default_value VARCHAR(200) COMMENT '默认值',
    dict_type VARCHAR(100) COMMENT '关联字典',
    relation_type VARCHAR(20) COMMENT '关联类型',
    relation_model_id BIGINT COMMENT '关联模型ID',
    order_num INT DEFAULT 0 COMMENT '排序',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id),
    KEY idx_model_id (model_id)
) ENGINE=InnoDB COMMENT='数据模型字段表';

-- ----------------------------------------
-- 3. 数据模型索引表
-- ----------------------------------------
DROP TABLE IF EXISTS data_model_index;
CREATE TABLE data_model_index (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '索引ID',
    model_id BIGINT NOT NULL COMMENT '模型ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    index_name VARCHAR(100) NOT NULL COMMENT '索引名称',
    index_type VARCHAR(20) DEFAULT 'normal' COMMENT '索引类型 normal/unique',
    index_columns JSON COMMENT '索引列',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_model_id (model_id)
) ENGINE=InnoDB COMMENT='数据模型索引表';

-- ----------------------------------------
-- 4. 表单定义表
-- ----------------------------------------
DROP TABLE IF EXISTS form_definition;
CREATE TABLE form_definition (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '表单ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    form_name VARCHAR(100) NOT NULL COMMENT '表单名称',
    form_code VARCHAR(50) NOT NULL COMMENT '表单编码',
    model_id BIGINT COMMENT '关联数据模型',
    field_config JSON COMMENT '字段配置',
    layout_config JSON COMMENT '布局配置',
    validate_rules JSON COMMENT '校验规则',
    status TINYINT DEFAULT 0 COMMENT '状态',
    version INT DEFAULT 1 COMMENT '版本',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id),
    UNIQUE KEY uk_form_code (tenant_id, form_code)
) ENGINE=InnoDB COMMENT='表单定义表';

-- ----------------------------------------
-- 5. 表单数据表（动态表由数据模型生成，此处为模板示例）
-- ----------------------------------------
DROP TABLE IF EXISTS form_data_template;
CREATE TABLE form_data_template (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '数据ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    form_definition_id BIGINT NOT NULL COMMENT '表单定义ID',
    data JSON COMMENT '表单数据',
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_form_def_id (form_definition_id)
) ENGINE=InnoDB COMMENT='表单数据模板表';

-- ----------------------------------------
-- 6. 流程定义表
-- ----------------------------------------
DROP TABLE IF EXISTS flow_definition;
CREATE TABLE flow_definition (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '流程ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    flow_name VARCHAR(100) NOT NULL COMMENT '流程名称',
    flow_code VARCHAR(50) NOT NULL COMMENT '流程编码',
    form_id BIGINT COMMENT '关联表单',
    nodes JSON COMMENT '节点配置',
    transitions JSON COMMENT '流转规则',
    status TINYINT DEFAULT 0 COMMENT '状态',
    version INT DEFAULT 1 COMMENT '版本',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id),
    UNIQUE KEY uk_flow_code (tenant_id, flow_code)
) ENGINE=InnoDB COMMENT='流程定义表';

-- ----------------------------------------
-- 7. 流程实例表
-- ----------------------------------------
DROP TABLE IF EXISTS flow_instance;
CREATE TABLE flow_instance (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '实例ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    flow_definition_id BIGINT NOT NULL COMMENT '流程定义ID',
    form_data_id BIGINT COMMENT '表单数据ID',
    business_key VARCHAR(100) COMMENT '业务标识',
    initiator VARCHAR(50) COMMENT '发起人',
    current_node VARCHAR(100) COMMENT '当前节点',
    status VARCHAR(20) DEFAULT 'running' COMMENT '状态 running/completed/cancelled/rejected',
    end_time DATETIME COMMENT '结束时间',
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id),
    KEY idx_flow_def_id (flow_definition_id),
    KEY idx_business_key (business_key)
) ENGINE=InnoDB COMMENT='流程实例表';

-- ----------------------------------------
-- 8. 流程任务表
-- ----------------------------------------
DROP TABLE IF EXISTS flow_task;
CREATE TABLE flow_task (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    flow_instance_id BIGINT NOT NULL COMMENT '流程实例ID',
    node_id VARCHAR(100) NOT NULL COMMENT '节点ID',
    node_name VARCHAR(100) COMMENT '节点名称',
    assignee VARCHAR(50) COMMENT '处理人',
    assign_type VARCHAR(20) COMMENT '分配类型 user/role/dept',
    assign_value VARCHAR(200) COMMENT '分配值',
    claim_time DATETIME COMMENT '认领时间',
    action VARCHAR(20) COMMENT '操作 approve/reject/transfer',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态 pending/approved/rejected/transferred',
    comment VARCHAR(500) COMMENT '审批意见',
    action_time DATETIME COMMENT '处理时间',
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_flow_inst_id (flow_instance_id),
    KEY idx_assignee (assignee)
) ENGINE=InnoDB COMMENT='流程任务表';

-- ----------------------------------------
-- 9. 页面定义表
-- ----------------------------------------
DROP TABLE IF EXISTS page_definition;
CREATE TABLE page_definition (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '页面ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    page_name VARCHAR(100) NOT NULL COMMENT '页面名称',
    page_code VARCHAR(50) NOT NULL COMMENT '页面编码',
    page_type VARCHAR(20) DEFAULT 'list' COMMENT '页面类型 list/detail/form/dashboard',
    model_id BIGINT COMMENT '关联数据模型',
    components JSON COMMENT '组件配置',
    events JSON COMMENT '事件绑定',
    permissions JSON COMMENT '权限配置',
    status TINYINT DEFAULT 0 COMMENT '状态',
    version INT DEFAULT 1 COMMENT '版本',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id),
    UNIQUE KEY uk_page_code (tenant_id, page_code)
) ENGINE=InnoDB COMMENT='页面定义表';

-- ----------------------------------------
-- 10. 报表定义表
-- ----------------------------------------
DROP TABLE IF EXISTS report_definition;
CREATE TABLE report_definition (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '报表ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    report_name VARCHAR(100) NOT NULL COMMENT '报表名称',
    report_code VARCHAR(50) NOT NULL COMMENT '报表编码',
    report_type VARCHAR(20) DEFAULT 'chart' COMMENT '报表类型 chart/table/card',
    data_source JSON COMMENT '数据源配置',
    chart_config JSON COMMENT '图表配置',
    filters JSON COMMENT '筛选条件',
    refresh_interval INT COMMENT '刷新间隔(秒)',
    status TINYINT DEFAULT 0 COMMENT '状态',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id),
    UNIQUE KEY uk_report_code (tenant_id, report_code)
) ENGINE=InnoDB COMMENT='报表定义表';

-- ----------------------------------------
-- 11. 仪表盘表
-- ----------------------------------------
DROP TABLE IF EXISTS dashboard;
CREATE TABLE dashboard (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '仪表盘ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    dashboard_name VARCHAR(100) NOT NULL COMMENT '仪表盘名称',
    dashboard_code VARCHAR(50) COMMENT '仪表盘编码',
    layout_config JSON COMMENT '布局配置',
    reports JSON COMMENT '报表列表',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认',
    status TINYINT DEFAULT 0 COMMENT '状态',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='仪表盘表';

-- ----------------------------------------
-- 12. 文件信息表
-- ----------------------------------------
DROP TABLE IF EXISTS sys_file;
CREATE TABLE sys_file (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '租户ID',
    file_name VARCHAR(200) NOT NULL COMMENT '文件名',
    original_name VARCHAR(200) COMMENT '原始文件名',
    file_type VARCHAR(50) COMMENT '文件类型',
    file_size BIGINT COMMENT '文件大小',
    storage_type VARCHAR(20) DEFAULT 'minio' COMMENT '存储类型',
    storage_path VARCHAR(500) COMMENT '存储路径',
    bucket_name VARCHAR(100) COMMENT '桶名',
    download_url VARCHAR(500) COMMENT '下载地址',
    preview_url VARCHAR(500) COMMENT '预览地址',
    md5 VARCHAR(100) COMMENT '文件MD5',
    status TINYINT DEFAULT 0 COMMENT '状态',
    del_flag TINYINT DEFAULT 0,
    created_by VARCHAR(50),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500),
    PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='文件信息表';