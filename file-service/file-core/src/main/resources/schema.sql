-- H2数据库初始化脚本 - 文件服务

-- 文件记录表
CREATE TABLE IF NOT EXISTS file_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(32) DEFAULT '000000',
    file_name VARCHAR(200) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    file_type VARCHAR(100),
    file_extension VARCHAR(20),
    bucket_name VARCHAR(50),
    file_md5 VARCHAR(32),
    business_type VARCHAR(50),
    business_id BIGINT,
    download_count INT DEFAULT 0,
    upload_user VARCHAR(50),
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    del_flag INT DEFAULT 0
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_file_md5 ON file_record(file_md5);
CREATE INDEX IF NOT EXISTS idx_file_business ON file_record(business_type, business_id);
CREATE INDEX IF NOT EXISTS idx_file_upload_time ON file_record(upload_time);