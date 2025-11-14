-- =====================================================
-- 版本: V1.4.0
-- 描述: 添加收藏功能相关表
-- 依赖: V1.3.0
-- =====================================================

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET collation_connection = utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS = 0;

SET @version = 'V1.4.0';
SET @script_name = 'V1.4.0__collection_tables.sql';
SET @description = '添加收藏功能相关表';

SELECT COUNT(*) INTO @version_exists 
FROM schema_version 
WHERE version = @version AND execution_status = 'SUCCESS';

SET @should_execute = IF(@version_exists = 0, 1, 0);

SET @sql = IF(@should_execute = 1,
    'SET NAMES utf8mb4;
     SET FOREIGN_KEY_CHECKS = 0;
     
     -- 收藏夹表
     CREATE TABLE IF NOT EXISTS collections (
         id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
         user_id BIGINT UNSIGNED NOT NULL,
         name VARCHAR(100) NOT NULL COMMENT ''收藏夹名称'',
         description TEXT COMMENT ''收藏夹描述'',
         is_public BOOLEAN DEFAULT FALSE COMMENT ''是否公开'',
         is_default BOOLEAN DEFAULT FALSE COMMENT ''是否为默认收藏夹'',
         view_count BIGINT UNSIGNED DEFAULT 0 COMMENT ''浏览次数'',
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
         INDEX idx_user_id (user_id),
         INDEX idx_is_public (is_public),
         INDEX idx_created_at (created_at),
         INDEX idx_view_count (view_count),
         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=''用户收藏夹表'';
     
     -- 收藏项表
     CREATE TABLE IF NOT EXISTS collection_items (
         id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
         collection_id BIGINT UNSIGNED NOT NULL,
         item_type VARCHAR(50) NOT NULL COMMENT ''收藏项类型：vulnerability, note, challenge, test'',
         item_id BIGINT UNSIGNED NOT NULL COMMENT ''收藏项ID'',
         item_title VARCHAR(200) NOT NULL COMMENT ''收藏项标题'',
         item_description TEXT COMMENT ''收藏项描述'',
         item_url VARCHAR(500) COMMENT ''收藏项URL'',
         item_metadata JSON COMMENT ''收藏项元数据'',
         added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         INDEX idx_collection_id (collection_id),
         INDEX idx_item_type (item_type),
         INDEX idx_item_id (item_id),
         INDEX idx_added_at (added_at),
         UNIQUE KEY uk_collection_item (collection_id, item_type, item_id),
         FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=''收藏项表'';
     
     -- 收藏标签表
     CREATE TABLE IF NOT EXISTS collection_tags (
         id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
         name VARCHAR(50) NOT NULL UNIQUE COMMENT ''标签名称'',
         description TEXT COMMENT ''标签描述'',
         color VARCHAR(20) DEFAULT ''#1890ff'' COMMENT ''标签颜色'',
         usage_count INT DEFAULT 0 COMMENT ''使用次数'',
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
         INDEX idx_name (name),
         INDEX idx_usage_count (usage_count)
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=''收藏标签表'';
     
     -- 收藏项标签关联表
     CREATE TABLE IF NOT EXISTS collection_item_tags (
         id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
         collection_item_id BIGINT UNSIGNED NOT NULL,
         tag_id BIGINT UNSIGNED NOT NULL,
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         UNIQUE KEY uk_item_tag (collection_item_id, tag_id),
         FOREIGN KEY (collection_item_id) REFERENCES collection_items(id) ON DELETE CASCADE,
         FOREIGN KEY (tag_id) REFERENCES collection_tags(id) ON DELETE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=''收藏项标签关联表'';
     
     -- 收藏分享表
     CREATE TABLE IF NOT EXISTS collection_shares (
         id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
         collection_id BIGINT UNSIGNED NOT NULL,
         share_code VARCHAR(32) NOT NULL UNIQUE COMMENT ''分享码'',
         share_type VARCHAR(20) DEFAULT ''public'' COMMENT ''分享类型：public, private, password'',
         password VARCHAR(100) COMMENT ''分享密码'',
         expires_at TIMESTAMP COMMENT ''过期时间'',
         access_count INT DEFAULT 0 COMMENT ''访问次数'',
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
         INDEX idx_collection_id (collection_id),
         INDEX idx_share_code (share_code),
         INDEX idx_expires_at (expires_at),
         FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=''收藏分享表'';
     
     -- 收藏访问记录表
     CREATE TABLE IF NOT EXISTS collection_access_logs (
         id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
         collection_id BIGINT UNSIGNED NOT NULL,
         user_id BIGINT UNSIGNED COMMENT ''访问用户ID，NULL表示匿名访问'',
         access_type VARCHAR(20) DEFAULT ''view'' COMMENT ''访问类型：view, share, export'',
         ip_address VARCHAR(45) COMMENT ''访问IP'',
         user_agent TEXT COMMENT ''用户代理'',
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         INDEX idx_collection_id (collection_id),
         INDEX idx_user_id (user_id),
         INDEX idx_access_type (access_type),
         INDEX idx_created_at (created_at),
         FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE CASCADE
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT=''收藏访问记录表'';
     
     -- 插入默认收藏标签
     INSERT IGNORE INTO collection_tags (name, description, color, usage_count) VALUES
     (''漏洞学习'', ''与漏洞学习相关的收藏'', ''#ff4d4f'', 0),
     (''安全测试'', ''与安全测试相关的收藏'', ''#52c41a'', 0),
     (''挑战模式'', ''与挑战模式相关的收藏'', ''#1890ff'', 0),
     (''学习笔记'', ''与学习笔记相关的收藏'', ''#722ed1'', 0),
     (''重要'', ''重要的收藏内容'', ''#fa8c16'', 0),
     (''待学习'', ''待学习的内容'', ''#13c2c2'', 0),
     (''已掌握'', ''已掌握的内容'', ''#52c41a'', 0),
     (''复习'', ''需要复习的内容'', ''#eb2f96'', 0),
     (''工具'', ''安全工具相关'', ''#2f54eb'', 0),
     (''文档'', ''技术文档相关'', ''#fa541c'', 0);
     
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

