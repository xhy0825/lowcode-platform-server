-- H2数据库初始化脚本 - 报表服务

-- 图表定义表
CREATE TABLE IF NOT EXISTS chart_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) DEFAULT '000000',
    chart_name VARCHAR(100) NOT NULL,
    chart_code VARCHAR(50) NOT NULL,
    chart_type VARCHAR(20) NOT NULL,
    data_source_id BIGINT,
    query_config TEXT,
    style_config TEXT,
    dimension_config TEXT,
    metric_config TEXT,
    status INT DEFAULT 0,
    version INT DEFAULT 1,
    del_flag INT DEFAULT 0,
    created_by VARCHAR(50),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time TIMESTAMP
);

-- 仪表盘定义表
CREATE TABLE IF NOT EXISTS dashboard_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) DEFAULT '000000',
    dashboard_name VARCHAR(100) NOT NULL,
    dashboard_code VARCHAR(50) NOT NULL,
    layout_config TEXT,
    filter_config TEXT,
    refresh_interval INT DEFAULT 0,
    status INT DEFAULT 0,
    del_flag INT DEFAULT 0,
    created_by VARCHAR(50),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_chart_type ON chart_definition(chart_type);
CREATE INDEX IF NOT EXISTS idx_chart_status ON chart_definition(status);
CREATE INDEX IF NOT EXISTS idx_dashboard_status ON dashboard_definition(status);