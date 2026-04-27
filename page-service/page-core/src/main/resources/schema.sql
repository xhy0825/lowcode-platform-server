-- H2数据库初始化脚本 - 页面服务

-- 页面定义表
CREATE TABLE IF NOT EXISTS page_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) DEFAULT '000000',
    page_name VARCHAR(100) NOT NULL,
    page_code VARCHAR(50) NOT NULL,
    page_type VARCHAR(20) NOT NULL,
    form_id BIGINT,
    model_id BIGINT,
    layout_config TEXT,
    component_config TEXT,
    status INT DEFAULT 0,
    version INT DEFAULT 1,
    del_flag INT DEFAULT 0,
    created_by VARCHAR(50),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time TIMESTAMP
);

-- 页面组件表
CREATE TABLE IF NOT EXISTS page_component (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) DEFAULT '000000',
    page_id BIGINT NOT NULL,
    component_code VARCHAR(50) NOT NULL,
    component_type VARCHAR(30) NOT NULL,
    component_config TEXT,
    position_config TEXT,
    event_config TEXT,
    order_num INT DEFAULT 0,
    del_flag INT DEFAULT 0,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_page_type ON page_definition(page_type);
CREATE INDEX IF NOT EXISTS idx_page_status ON page_definition(status);
CREATE INDEX IF NOT EXISTS idx_component_page ON page_component(page_id);