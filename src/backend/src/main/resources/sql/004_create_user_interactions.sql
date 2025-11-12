-- 任务4：创建用户交互记录表
-- 文件: src/backend/src/main/resources/sql/004_create_user_interactions.sql

CREATE TABLE user_interactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    vulnerability_code VARCHAR(20) NOT NULL,
    interaction_type VARCHAR(50) NOT NULL COMMENT '交互类型: scroll, click, demo_execution',
    interaction_data JSON COMMENT '交互数据',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_vulnerability (user_id, vulnerability_code),
    INDEX idx_interaction_type (interaction_type)
) COMMENT='用户交互记录表';