-- H2数据库初始化脚本 - 表单服务

-- 表单定义表
CREATE TABLE IF NOT EXISTS form_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) DEFAULT '000000',
    form_name VARCHAR(100) NOT NULL,
    form_code VARCHAR(50) NOT NULL,
    model_id BIGINT,
    field_config TEXT,
    layout_config TEXT,
    validate_rules TEXT,
    status INT DEFAULT 0,
    version INT DEFAULT 1,
    del_flag INT DEFAULT 0,
    created_by VARCHAR(50),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time TIMESTAMP
);

-- 表单数据模板表
CREATE TABLE IF NOT EXISTS form_data_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) DEFAULT '000000',
    form_definition_id BIGINT NOT NULL,
    data TEXT,
    created_by VARCHAR(50),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time TIMESTAMP,
    del_flag INT DEFAULT 0
);
