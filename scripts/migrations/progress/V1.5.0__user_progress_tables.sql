-- =====================================================
-- 版本: V1.5.0
-- 描述: 添加用户学习进度相关表
-- 依赖: V1.4.0
-- =====================================================

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET collation_connection = utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS = 0;

SET @version = 'V1.5.0';
SET @script_name = 'V1.5.0__user_progress_tables.sql';
SET @description = '添加用户学习进度相关表';

SELECT COUNT(*) INTO @version_exists 
FROM schema_version 
WHERE version = @version AND execution_status = 'SUCCESS';

SET @should_execute = IF(@version_exists = 0, 1, 0);

SET @sql = IF(@should_execute = 1,
    'SET NAMES utf8mb4;
     SET FOREIGN_KEY_CHECKS = 0;
     
     -- 用户漏洞学习进度表
     CREATE TABLE IF NOT EXISTS user_vulnerability_progress (
         id BIGINT PRIMARY KEY AUTO_INCREMENT,
         user_id BIGINT NOT NULL,
         vulnerability_code VARCHAR(20) NOT NULL,
         status ENUM(''NOT_STARTED'', ''IN_PROGRESS'', ''COMPLETED'') DEFAULT ''NOT_STARTED'',
         learning_completed BOOLEAN DEFAULT FALSE,
         test_passed BOOLEAN DEFAULT FALSE,
         challenge_completed BOOLEAN DEFAULT FALSE,
         total_study_time INT DEFAULT 0 COMMENT ''总学习时长(分钟)'',
         learning_score INT DEFAULT 0 COMMENT ''学习得分'',
         test_score INT DEFAULT 0 COMMENT ''测试得分'',
         challenge_score INT DEFAULT 0 COMMENT ''挑战得分'',
         started_at TIMESTAMP NULL,
         completed_at TIMESTAMP NULL,
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
         UNIQUE KEY uk_user_vulnerability (user_id, vulnerability_code),
         INDEX idx_user_id (user_id),
         INDEX idx_vulnerability_code (vulnerability_code),
         INDEX idx_status (status),
         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=''用户漏洞学习进度表'';
     
     -- 用户活动记录表
     CREATE TABLE IF NOT EXISTS user_activities (
         id BIGINT PRIMARY KEY AUTO_INCREMENT,
         user_id BIGINT NOT NULL,
         activity_type VARCHAR(50) NOT NULL COMMENT ''活动类型: LEARNING, TEST, CHALLENGE'',
         vulnerability_code VARCHAR(20) COMMENT ''漏洞代码'',
         title VARCHAR(200) NOT NULL COMMENT ''活动标题'',
         description TEXT COMMENT ''活动描述'',
         metadata JSON COMMENT ''活动元数据'',
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         INDEX idx_user_id (user_id),
         INDEX idx_activity_type (activity_type),
         INDEX idx_vulnerability_code (vulnerability_code),
         INDEX idx_created_at (created_at),
         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=''用户活动记录表'';
     
     -- 页面访问记录表
     CREATE TABLE IF NOT EXISTS page_visits (
         id BIGINT PRIMARY KEY AUTO_INCREMENT,
         user_id BIGINT NOT NULL,
         vulnerability_code VARCHAR(20) NOT NULL,
         page_type VARCHAR(50) NOT NULL COMMENT ''页面类型: theory, knowledge, demo, repair'',
         visit_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         duration INT DEFAULT 0 COMMENT ''停留时长(秒)'',
         scroll_depth INT DEFAULT 0 COMMENT ''滚动深度百分比'',
         click_count INT DEFAULT 0 COMMENT ''点击次数'',
         INDEX idx_user_vulnerability (user_id, vulnerability_code),
         INDEX idx_page_type (page_type),
         INDEX idx_visit_time (visit_time),
         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=''页面访问记录表'';
     
     -- 用户交互记录表
     CREATE TABLE IF NOT EXISTS user_interactions (
         id BIGINT PRIMARY KEY AUTO_INCREMENT,
         user_id BIGINT NOT NULL,
         vulnerability_code VARCHAR(20) NOT NULL,
         interaction_type VARCHAR(50) NOT NULL COMMENT ''交互类型: scroll, click, demo_execution'',
         interaction_data JSON COMMENT ''交互数据'',
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         INDEX idx_user_vulnerability (user_id, vulnerability_code),
         INDEX idx_interaction_type (interaction_type),
         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=''用户交互记录表'';
     
     -- 演示执行记录表
     CREATE TABLE IF NOT EXISTS demo_executions (
         id BIGINT PRIMARY KEY AUTO_INCREMENT,
         user_id BIGINT NOT NULL,
         vulnerability_code VARCHAR(20) NOT NULL,
         demo_type VARCHAR(50) NOT NULL COMMENT ''演示类型: attack, defense'',
         execution_data JSON COMMENT ''执行数据'',
         success BOOLEAN DEFAULT FALSE,
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         INDEX idx_user_vulnerability (user_id, vulnerability_code),
         INDEX idx_demo_type (demo_type),
         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=''演示执行记录表'';
     
     SET FOREIGN_KEY_CHECKS = 1;
     
     INSERT INTO schema_version (version, description, script_name, execution_status)
     VALUES (@version, @description, @script_name, ''SUCCESS'');',
    'SELECT CONCAT(''Version '', @version, '' already executed, skipping...'') AS message'
);

SET @execute_sql = IF(@should_execute = 1, @sql, 'SELECT CONCAT(''Version '', @version, '' already executed, skipping...'') AS message');

PREPARE stmt FROM @execute_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET FOREIGN_KEY_CHECKS = 1;

