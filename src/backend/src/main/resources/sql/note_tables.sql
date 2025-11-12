-- 学习笔记系统数据库表

-- learning_notes 表 - 学习笔记主表
CREATE TABLE IF NOT EXISTS learning_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(200) NOT NULL,
    content LONGTEXT NOT NULL,
    summary TEXT,
    note_type VARCHAR(50) DEFAULT 'PERSONAL', -- PERSONAL, SHARED, PUBLIC
    vulnerability_code VARCHAR(10), -- 关联的漏洞代码，如A01, A02等
    tags JSON, -- 标签数组，如["SQL注入", "安全", "学习"]
    is_public BOOLEAN DEFAULT FALSE,
    is_pinned BOOLEAN DEFAULT FALSE,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    share_count INT DEFAULT 0,
    word_count INT DEFAULT 0,
    reading_time INT DEFAULT 0, -- 预计阅读时间（分钟）
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
);

-- note_tags 表 - 笔记标签表
CREATE TABLE IF NOT EXISTS note_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(50) NOT NULL UNIQUE,
    tag_description TEXT,
    usage_count INT DEFAULT 0,
    color VARCHAR(7) DEFAULT '#007bff', -- 标签颜色
    is_system BOOLEAN DEFAULT FALSE, -- 是否为系统标签
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tag_name (tag_name),
    INDEX idx_usage_count (usage_count)
);

-- note_likes 表 - 笔记点赞表
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
);

-- note_comments 表 - 笔记评论表
CREATE TABLE IF NOT EXISTS note_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    note_id BIGINT NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    parent_id BIGINT NULL, -- 父评论ID，支持嵌套评论
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
);

-- note_shares 表 - 笔记分享表
CREATE TABLE IF NOT EXISTS note_shares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    note_id BIGINT NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    share_type VARCHAR(20) DEFAULT 'LINK', -- LINK, QR_CODE, EXPORT
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
);

-- note_versions 表 - 笔记版本历史表
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
);

-- note_collaborators 表 - 笔记协作者表
CREATE TABLE IF NOT EXISTS note_collaborators (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    note_id BIGINT NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    permission VARCHAR(20) DEFAULT 'READ', -- READ, WRITE, ADMIN
    invited_by BIGINT UNSIGNED NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (note_id) REFERENCES learning_notes(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (invited_by) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_note_user (note_id, user_id),
    INDEX idx_note_id (note_id),
    INDEX idx_user_id (user_id)
);
