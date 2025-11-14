-- =====================================================
-- 版本: V1.6.0
-- 描述: 添加语言和指引功能相关表
-- 依赖: V1.5.0
-- =====================================================

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET collation_connection = utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS = 0;

SET @version = 'V1.6.0';
SET @script_name = 'V1.6.0__guide_tables.sql';
SET @description = '添加语言和指引功能相关表';

SELECT COUNT(*) INTO @version_exists 
FROM schema_version 
WHERE version = @version AND execution_status = 'SUCCESS';

SET @should_execute = IF(@version_exists = 0, 1, 0);

SET @sql = IF(@should_execute = 1,
    'SET NAMES utf8mb4;
     SET FOREIGN_KEY_CHECKS = 0;
     
     -- 语言偏好表
     CREATE TABLE IF NOT EXISTS language_preferences (
         id BIGINT AUTO_INCREMENT PRIMARY KEY,
         user_id BIGINT NOT NULL,
         language_code VARCHAR(10) NOT NULL,
         is_active BOOLEAN NOT NULL DEFAULT TRUE,
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
         UNIQUE KEY uk_user_language (user_id, language_code),
         INDEX idx_user_active (user_id, is_active),
         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
     
     -- 用户指引偏好表
     CREATE TABLE IF NOT EXISTS user_guide_preferences (
         id BIGINT AUTO_INCREMENT PRIMARY KEY,
         user_id BIGINT NOT NULL,
         has_completed_initial_guide BOOLEAN NOT NULL DEFAULT FALSE,
         guide_version VARCHAR(20),
         last_guide_shown_at TIMESTAMP NULL,
         auto_show_guide BOOLEAN NOT NULL DEFAULT TRUE,
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
         UNIQUE KEY uk_user_guide (user_id),
         INDEX idx_user_completed (user_id, has_completed_initial_guide),
         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
     
     -- 指引步骤表
     CREATE TABLE IF NOT EXISTS guide_steps (
         id BIGINT AUTO_INCREMENT PRIMARY KEY,
         step_key VARCHAR(50) NOT NULL,
         title_zh VARCHAR(200) NOT NULL,
         title_en VARCHAR(200) NOT NULL,
         description_zh TEXT,
         description_en TEXT,
         target_element VARCHAR(100),
         position VARCHAR(20),
         order_index INT NOT NULL,
         is_active BOOLEAN NOT NULL DEFAULT TRUE,
         guide_version VARCHAR(20),
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
         UNIQUE KEY uk_step_key (step_key),
         INDEX idx_version_active (guide_version, is_active),
         INDEX idx_order (order_index)
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
     
     -- 插入默认指引步骤数据
     INSERT IGNORE INTO guide_steps (step_key, title_zh, title_en, description_zh, description_en, target_element, position, order_index, guide_version) VALUES
     (''welcome'', ''欢迎使用Java Web安全教学系统'', ''Welcome to Java Web Security Teaching System'', ''这是一个专门为安全学习设计的教学平台，让我们开始探索吧！'', ''This is a teaching platform designed specifically for security learning. Let''''s start exploring!'', ''#welcome-section'', ''bottom'', 1, ''1.0.0''),
     (''navigation'', ''导航菜单'', ''Navigation Menu'', ''这里是系统的主要导航菜单，包含所有核心功能模块。'', ''This is the main navigation menu containing all core functional modules.'', ''.el-menu'', ''right'', 2, ''1.0.0''),
     (''vulnerability_center'', ''漏洞知识中心'', ''Vulnerability Knowledge Center'', ''在这里您可以学习OWASP Top 10的各种安全漏洞。'', ''Here you can learn about various security vulnerabilities from OWASP Top 10.'', ''#vulnerability-center'', ''bottom'', 3, ''1.0.0''),
     (''challenge_mode'', ''挑战模式'', ''Challenge Mode'', ''通过实战挑战来测试您的安全技能。'', ''Test your security skills through practical challenges.'', ''#challenge-mode'', ''bottom'', 4, ''1.0.0''),
     (''knowledge_test'', ''知识测试'', ''Knowledge Test'', ''通过测试来验证您的学习成果。'', ''Verify your learning achievements through tests.'', ''#knowledge-test'', ''bottom'', 5, ''1.0.0''),
     (''user_profile'', ''个人中心'', ''User Profile'', ''管理您的个人信息和学习进度。'', ''Manage your personal information and learning progress.'', ''#user-profile'', ''left'', 6, ''1.0.0''),
     (''language_switch'', ''语言切换'', ''Language Switch'', ''点击这里可以切换系统语言。'', ''Click here to switch the system language.'', ''#language-switch'', ''left'', 7, ''1.0.0''),
     (''guide_trigger'', ''新手指引'', ''New User Guide'', ''点击这里可以重新查看新手指引。'', ''Click here to review the new user guide again.'', ''#guide-trigger'', ''left'', 8, ''1.0.0'');
     
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



