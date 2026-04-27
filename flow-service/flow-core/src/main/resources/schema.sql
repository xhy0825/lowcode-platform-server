-- H2数据库初始化脚本 - 流程服务

-- 流程定义表
CREATE TABLE IF NOT EXISTS flow_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) DEFAULT '000000',
    flow_name VARCHAR(100) NOT NULL,
    flow_code VARCHAR(50) NOT NULL,
    form_id BIGINT,
    nodes TEXT,
    edges TEXT,
    status INT DEFAULT 0,
    version INT DEFAULT 1,
    description VARCHAR(500),
    del_flag INT DEFAULT 0,
    created_by VARCHAR(50),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_time TIMESTAMP
);

-- 流程实例表
CREATE TABLE IF NOT EXISTS flow_instance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) DEFAULT '000000',
    flow_definition_id BIGINT NOT NULL,
    flow_name VARCHAR(100),
    flow_code VARCHAR(50),
    form_data_id BIGINT,
    initiator VARCHAR(50),
    current_node VARCHAR(50),
    status VARCHAR(20) DEFAULT 'running',
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    duration INT,
    del_flag INT DEFAULT 0
);

-- 流程任务表
CREATE TABLE IF NOT EXISTS flow_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) DEFAULT '000000',
    instance_id BIGINT NOT NULL,
    node_id VARCHAR(50) NOT NULL,
    node_name VARCHAR(100),
    assign_type VARCHAR(20),
    assign_value TEXT,
    assignee VARCHAR(50),
    status VARCHAR(20) DEFAULT 'pending',
    action VARCHAR(20),
    comment TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deadline TIMESTAMP,
    action_time TIMESTAMP,
    delegate_user VARCHAR(50),
    del_flag INT DEFAULT 0
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_flow_instance_definition ON flow_instance(flow_definition_id);
CREATE INDEX IF NOT EXISTS idx_flow_instance_status ON flow_instance(status);
CREATE INDEX IF NOT EXISTS idx_flow_task_instance ON flow_task(instance_id);
CREATE INDEX IF NOT EXISTS idx_flow_task_status ON flow_task(status);
CREATE INDEX IF NOT EXISTS idx_flow_task_assignee ON flow_task(assignee);