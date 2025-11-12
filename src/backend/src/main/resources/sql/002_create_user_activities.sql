-- 任务2：创建用户活动记录表
-- 文件: src/backend/src/main/resources/sql/002_create_user_activities.sql

CREATE TABLE user_activities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    activity_type VARCHAR(50) NOT NULL COMMENT '活动类型: LEARNING, TEST, CHALLENGE',
    vulnerability_code VARCHAR(20) COMMENT '漏洞代码',
    title VARCHAR(200) NOT NULL COMMENT '活动标题',
    description TEXT COMMENT '活动描述',
    metadata JSON COMMENT '活动元数据',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_activity_type (activity_type),
    INDEX idx_vulnerability_code (vulnerability_code),
    INDEX idx_created_at (created_at)
) COMMENT='用户活动记录表';