-- 任务5：创建演示执行记录表
-- 文件: src/backend/src/main/resources/sql/005_create_demo_executions.sql

CREATE TABLE demo_executions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    vulnerability_code VARCHAR(20) NOT NULL,
    demo_type VARCHAR(50) NOT NULL COMMENT '演示类型: attack, defense',
    execution_data JSON COMMENT '执行数据',
    success BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_vulnerability (user_id, vulnerability_code),
    INDEX idx_demo_type (demo_type)
) COMMENT='演示执行记录表';