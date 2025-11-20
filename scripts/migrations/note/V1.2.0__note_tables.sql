-- =====================================================
-- 版本: V1.2.0
-- 描述: 添加笔记功能相关表
-- 依赖: V1.1.0
-- =====================================================

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET collation_connection = utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS = 0;

SET @version = 'V1.2.0';
SET @script_name = 'V1.2.0__note_tables.sql';
SET @description = '添加笔记功能相关表';

-- 检查版本是否已执行（修复排序规则冲突）
SELECT COUNT(*) INTO @version_exists 
FROM schema_version 
WHERE version = @version COLLATE utf8mb4_unicode_ci AND execution_status = 'SUCCESS' COLLATE utf8mb4_unicode_ci;

-- 如果版本不存在，执行迁移
SET @should_execute = IF(@version_exists = 0, 1, 0);

-- 直接执行 SQL，不使用 PREPARE/EXECUTE
-- 学习笔记表
CREATE TABLE IF NOT EXISTS learning_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(200) NOT NULL,
    content LONGTEXT NOT NULL,
    summary TEXT,
    note_type VARCHAR(50) DEFAULT 'PERSONAL',
    vulnerability_code VARCHAR(10),
    tags JSON,
    is_public BOOLEAN DEFAULT FALSE,
    is_pinned BOOLEAN DEFAULT FALSE,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    share_count INT DEFAULT 0,
    word_count INT DEFAULT 0,
    reading_time INT DEFAULT 0,
    last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_vulnerability_code (vulnerability_code),
    INDEX idx_note_type (note_type),
    INDEX idx_is_public (is_public),
    INDEX idx_created_at (created_at),
    FULLTEXT idx_title_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 笔记标签表
CREATE TABLE IF NOT EXISTS note_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(50) NOT NULL UNIQUE,
    tag_description TEXT,
    usage_count INT DEFAULT 0,
    color VARCHAR(7) DEFAULT '#007bff',
    is_system BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tag_name (tag_name),
    INDEX idx_usage_count (usage_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 笔记点赞表
CREATE TABLE IF NOT EXISTS note_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    note_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (note_id) REFERENCES learning_notes(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_note (user_id, note_id),
    INDEX idx_note_id (note_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 笔记评论表
CREATE TABLE IF NOT EXISTS note_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    note_id BIGINT NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    parent_id BIGINT NULL,
    content TEXT NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    like_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (note_id) REFERENCES learning_notes(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES note_comments(id) ON DELETE CASCADE,
    INDEX idx_note_id (note_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 笔记分享表
CREATE TABLE IF NOT EXISTS note_shares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    note_id BIGINT NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    share_type VARCHAR(20) DEFAULT 'LINK',
    share_token VARCHAR(100) UNIQUE,
    access_count INT DEFAULT 0,
    expires_at TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (note_id) REFERENCES learning_notes(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_note_id (note_id),
    INDEX idx_user_id (user_id),
    INDEX idx_share_token (share_token),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 笔记版本历史表
CREATE TABLE IF NOT EXISTS note_versions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    note_id BIGINT NOT NULL,
    version_number INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content LONGTEXT NOT NULL,
    change_summary TEXT,
    created_by BIGINT UNSIGNED NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (note_id) REFERENCES learning_notes(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_note_id (note_id),
    INDEX idx_version_number (version_number),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 笔记协作者表
CREATE TABLE IF NOT EXISTS note_collaborators (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    note_id BIGINT NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    permission VARCHAR(20) DEFAULT 'READ',
    invited_by BIGINT UNSIGNED NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (note_id) REFERENCES learning_notes(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (invited_by) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_note_user (note_id, user_id),
    INDEX idx_note_id (note_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- 记录版本（仅在未执行时插入）
INSERT INTO schema_version (version, description, script_name, execution_status)
SELECT @version, @description, @script_name, 'SUCCESS'
WHERE NOT EXISTS (
    SELECT 1 FROM schema_version WHERE version = @version COLLATE utf8mb4_unicode_ci AND execution_status = 'SUCCESS' COLLATE utf8mb4_unicode_ci
);

SET FOREIGN_KEY_CHECKS = 1;
