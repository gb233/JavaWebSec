-- 任务3：创建页面访问记录表
-- 文件: src/backend/src/main/resources/sql/003_create_page_visits.sql

CREATE TABLE page_visits (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    vulnerability_code VARCHAR(20) NOT NULL,
    page_type VARCHAR(50) NOT NULL COMMENT '页面类型: theory, knowledge, demo, repair',
    visit_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    duration INT DEFAULT 0 COMMENT '停留时长(秒)',
    scroll_depth INT DEFAULT 0 COMMENT '滚动深度百分比',
    click_count INT DEFAULT 0 COMMENT '点击次数',
    INDEX idx_user_vulnerability (user_id, vulnerability_code),
    INDEX idx_page_type (page_type),
    INDEX idx_visit_time (visit_time)
) COMMENT='页面访问记录表';