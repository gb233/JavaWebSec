-- =====================================================
-- 版本: V1.3.0
-- 描述: 添加徽章功能相关表
-- 依赖: V1.2.0
-- =====================================================

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET collation_connection = utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS = 0;

SET @version = 'V1.3.0';
SET @script_name = 'V1.3.0__badge_tables.sql';
SET @description = '添加徽章功能相关表';

SELECT COUNT(*) INTO @version_exists 
FROM schema_version 
WHERE version = @version AND execution_status = 'SUCCESS';

SET @should_execute = IF(@version_exists = 0, 1, 0);

SET @sql = IF(@should_execute = 1,
    'SET NAMES utf8mb4;
     SET FOREIGN_KEY_CHECKS = 0;
     
     -- 成就徽章表
     CREATE TABLE IF NOT EXISTS achievement_badges (
         id BIGINT PRIMARY KEY AUTO_INCREMENT,
         badge_code VARCHAR(50) UNIQUE NOT NULL,
         badge_name VARCHAR(100) NOT NULL,
         badge_description TEXT,
         badge_icon VARCHAR(200),
         badge_category VARCHAR(50) NOT NULL,
         badge_rarity VARCHAR(20) DEFAULT ''COMMON'',
         requirements JSON,
         points_reward INT DEFAULT 0,
         is_active BOOLEAN DEFAULT TRUE,
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
     
     -- 用户徽章记录表
     CREATE TABLE IF NOT EXISTS user_badges (
         id BIGINT PRIMARY KEY AUTO_INCREMENT,
         user_id BIGINT NOT NULL,
         badge_id BIGINT NOT NULL,
         earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         is_displayed BOOLEAN DEFAULT TRUE,
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         UNIQUE KEY uk_user_badge (user_id, badge_id),
         FOREIGN KEY (badge_id) REFERENCES achievement_badges(id) ON DELETE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
     
     -- 徽章进度表
     CREATE TABLE IF NOT EXISTS badge_progress (
         id BIGINT PRIMARY KEY AUTO_INCREMENT,
         user_id BIGINT NOT NULL,
         badge_id BIGINT NOT NULL,
         current_progress INT DEFAULT 0,
         target_progress INT NOT NULL,
         progress_percentage DECIMAL(5,2) DEFAULT 0.00,
         is_completed BOOLEAN DEFAULT FALSE,
         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         UNIQUE KEY uk_user_badge_progress (user_id, badge_id),
         FOREIGN KEY (badge_id) REFERENCES achievement_badges(id) ON DELETE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
     
     -- 徽章分类表
     CREATE TABLE IF NOT EXISTS badge_categories (
         id BIGINT PRIMARY KEY AUTO_INCREMENT,
         category_code VARCHAR(50) UNIQUE NOT NULL,
         category_name VARCHAR(100) NOT NULL,
         category_description TEXT,
         category_icon VARCHAR(200),
         sort_order INT DEFAULT 0,
         is_active BOOLEAN DEFAULT TRUE,
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
     
     -- 徽章要求表
     CREATE TABLE IF NOT EXISTS badge_requirements (
         id BIGINT PRIMARY KEY AUTO_INCREMENT,
         badge_id BIGINT NOT NULL,
         requirement_type VARCHAR(50) NOT NULL,
         requirement_value VARCHAR(100) NOT NULL,
         requirement_description TEXT,
         sort_order INT DEFAULT 0,
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         FOREIGN KEY (badge_id) REFERENCES achievement_badges(id) ON DELETE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
     
     -- 插入徽章分类数据
     INSERT IGNORE INTO badge_categories (category_code, category_name, category_description, category_icon, sort_order) VALUES
     (''LEARNING'', ''学习类徽章'', ''与学习相关的成就徽章'', ''📚'', 1),
     (''TEST'', ''测试类徽章'', ''与测试相关的成就徽章'', ''📝'', 2),
     (''CHALLENGE'', ''挑战类徽章'', ''与挑战相关的成就徽章'', ''🏆'', 3),
     (''SPECIAL'', ''特殊类徽章'', ''特殊成就徽章'', ''⭐'', 4);
     
     -- 插入基础徽章数据
     INSERT IGNORE INTO achievement_badges (badge_code, badge_name, badge_description, badge_icon, badge_category, badge_rarity, points_reward) VALUES
     (''LEARNING_STREAK_3'', ''连续学习3天'', ''连续学习3天获得'', ''🔥'', ''LEARNING'', ''COMMON'', 10),
     (''LEARNING_STREAK_7'', ''连续学习7天'', ''连续学习7天获得'', ''🔥🔥'', ''LEARNING'', ''RARE'', 25),
     (''LEARNING_STREAK_30'', ''连续学习30天'', ''连续学习30天获得'', ''🔥🔥🔥'', ''LEARNING'', ''EPIC'', 100),
     (''STUDY_TIME_10'', ''学习10小时'', ''累计学习10小时获得'', ''⏰'', ''LEARNING'', ''COMMON'', 15),
     (''STUDY_TIME_50'', ''学习50小时'', ''累计学习50小时获得'', ''⏰⏰'', ''LEARNING'', ''RARE'', 50),
     (''VULNERABILITY_MASTER'', ''漏洞大师'', ''完成所有A01-A10学习获得'', ''🎓'', ''LEARNING'', ''LEGENDARY'', 200),
     (''TEST_MASTER'', ''测试大师'', ''通过所有测试获得'', ''✅'', ''TEST'', ''EPIC'', 100),
     (''PERFECT_SCORE'', ''满分达人'', ''获得满分获得'', ''💯'', ''TEST'', ''RARE'', 50),
     (''SPEED_DEMON'', ''速度恶魔'', ''快速完成测试获得'', ''⚡'', ''TEST'', ''RARE'', 30),
     (''CHALLENGE_MASTER'', ''挑战大师'', ''完成所有挑战获得'', ''🏆'', ''CHALLENGE'', ''LEGENDARY'', 300),
     (''FIRST_BLOOD'', ''首杀'', ''首次完成挑战获得'', ''🩸'', ''CHALLENGE'', ''RARE'', 25),
     (''PERFECT_CHALLENGE'', ''完美挑战'', ''完美完成挑战获得'', ''💎'', ''CHALLENGE'', ''EPIC'', 100),
     (''EARLY_BIRD'', ''早起鸟'', ''早起学习获得'', ''🐦'', ''SPECIAL'', ''RARE'', 20),
     (''NIGHT_OWL'', ''夜猫子'', ''夜间学习获得'', ''🦉'', ''SPECIAL'', ''RARE'', 20),
     (''NOTE_TAKER'', ''笔记达人'', ''创建学习笔记获得'', ''📝'', ''SPECIAL'', ''COMMON'', 15),
     (''COLLECTOR'', ''收藏家'', ''收藏学习内容获得'', ''📚'', ''SPECIAL'', ''COMMON'', 15);
     
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



