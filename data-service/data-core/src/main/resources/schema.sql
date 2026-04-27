-- H2数据库初始化脚本

-- 数据模型表
CREATE TABLE IF NOT EXISTS data_model (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) DEFAULT '000000',
    model_name VARCHAR(100) NOT NULL,
    model_code VARCHAR(50) NOT NULL,
    table_name VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    status INT DEFAULT 0,
    version INT DEFAULT 1,
    del_flag INT DEFAULT 0,
    created_by VARCHAR(50),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time TIMESTAMP
);

-- 数据模型字段表
CREATE TABLE IF NOT EXISTS data_model_field (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    model_id BIGINT NOT NULL,
    tenant_id VARCHAR(32) DEFAULT '000000',
    field_name VARCHAR(100) NOT NULL,
    field_code VARCHAR(50) NOT NULL,
    column_name VARCHAR(50) NOT NULL,
    field_type VARCHAR(20) NOT NULL,
    length INT,
    precision INT,
    scale INT,
    is_required INT DEFAULT 0,
    is_unique INT DEFAULT 0,
    is_indexed INT DEFAULT 0,
    is_primary INT DEFAULT 0,
    default_value VARCHAR(200),
    dict_type VARCHAR(50),
    relation_type VARCHAR(20),
    relation_model_id BIGINT,
    order_num INT DEFAULT 0,
    del_flag INT DEFAULT 0,
    created_by VARCHAR(50),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
