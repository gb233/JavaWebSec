-- =====================================================
-- 版本: V1.1.0
-- 描述: 添加测试功能相关表
-- 依赖: V1.0.0
-- 执行时间: 2025-01-XX
-- =====================================================

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET collation_connection = utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS = 0;

SET @version = 'V1.1.0';
SET @script_name = 'V1.1.0__test_tables.sql';
SET @description = '添加测试功能相关表';

-- 检查版本是否已执行（修复排序规则冲突）
SELECT COUNT(*) INTO @version_exists 
FROM schema_version 
WHERE version = @version COLLATE utf8mb4_unicode_ci AND execution_status = 'SUCCESS' COLLATE utf8mb4_unicode_ci;

-- 如果版本不存在，执行迁移
SET @should_execute = IF(@version_exists = 0, 1, 0);

-- 直接执行 SQL，不使用 PREPARE/EXECUTE
-- 漏洞题目分类表
CREATE TABLE IF NOT EXISTS vulnerability_question_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_code VARCHAR(10) NOT NULL UNIQUE COMMENT '漏洞分类代码，如A01、A02等',
    category_name VARCHAR(100) NOT NULL COMMENT '漏洞分类名称',
    description TEXT COMMENT '分类描述',
    total_questions INT DEFAULT 24 COMMENT '总题目数量',
    total_score INT DEFAULT 120 COMMENT '总分数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='漏洞题目分类表';

-- 答题模式表
CREATE TABLE IF NOT EXISTS test_modes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    mode_code VARCHAR(20) NOT NULL UNIQUE COMMENT '模式代码',
    mode_name VARCHAR(50) NOT NULL COMMENT '模式名称',
    description TEXT COMMENT '模式描述',
    features JSON COMMENT '模式特性配置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='答题模式表';

-- 题目表（如果不存在，注意：vulnerability_questions表可能已在V1.0.0中创建）
CREATE TABLE IF NOT EXISTS vulnerability_questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_code VARCHAR(10) NOT NULL COMMENT '漏洞分类代码',
    question_type ENUM('single', 'multiple', 'judge') NOT NULL COMMENT '题目类型：单选、多选、判断',
    difficulty ENUM('easy', 'medium', 'hard') NOT NULL COMMENT '难度等级',
    knowledge_source ENUM('principle', 'harm', 'exploit', 'vulnerable_code', 'secure_code', 'repair', 'detection') NOT NULL COMMENT '知识点来源',
    question_text TEXT NOT NULL COMMENT '题目内容',
    question_image VARCHAR(500) COMMENT '题目图片URL',
    options JSON COMMENT '选项内容（JSON格式）',
    correct_answer TEXT NOT NULL COMMENT '正确答案',
    explanation TEXT NOT NULL COMMENT '题目解析',
    score INT NOT NULL COMMENT '题目分值',
    tags JSON COMMENT '标签（JSON格式）',
    author_id BIGINT COMMENT '作者ID',
    status ENUM('draft', 'review', 'approved') DEFAULT 'draft' COMMENT '题目状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_code) REFERENCES vulnerability_question_categories(category_code) ON DELETE CASCADE,
    INDEX idx_category_code (category_code),
    INDEX idx_question_type (question_type),
    INDEX idx_difficulty (difficulty),
    INDEX idx_knowledge_source (knowledge_source),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='漏洞题目表';

-- 答题会话表
CREATE TABLE IF NOT EXISTS test_sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    mode_code VARCHAR(20) NOT NULL COMMENT '答题模式代码',
    category_code VARCHAR(10) COMMENT '漏洞分类代码（单类型模式时使用）',
    session_code VARCHAR(20) UNIQUE NOT NULL COMMENT '会话代码',
    status ENUM('active', 'completed', 'abandoned') DEFAULT 'active' COMMENT '会话状态',
    current_question_index INT DEFAULT 0 COMMENT '当前题目索引',
    total_questions INT NOT NULL COMMENT '总题目数量',
    answered_questions INT DEFAULT 0 COMMENT '已答题数量',
    correct_answers INT DEFAULT 0 COMMENT '正确答案数量',
    total_score INT DEFAULT 0 COMMENT '总得分',
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    end_time TIMESTAMP NULL COMMENT '结束时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (mode_code) REFERENCES test_modes(mode_code) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_mode_code (mode_code),
    INDEX idx_category_code (category_code),
    INDEX idx_session_code (session_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='答题会话表';

-- 答题记录表
CREATE TABLE IF NOT EXISTS test_answers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL COMMENT '会话ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    question_index INT NOT NULL COMMENT '题目在会话中的索引',
    user_answer TEXT COMMENT '用户答案',
    is_correct BOOLEAN COMMENT '是否正确',
    score INT DEFAULT 0 COMMENT '得分',
    feedback_shown BOOLEAN DEFAULT FALSE COMMENT '是否已显示反馈',
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',
    FOREIGN KEY (session_id) REFERENCES test_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES vulnerability_questions(id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id),
    INDEX idx_question_id (question_id),
    INDEX idx_question_index (question_index),
    INDEX idx_is_correct (is_correct),
    UNIQUE KEY unique_session_question (session_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='答题记录表';

-- 用户测试记录表
CREATE TABLE IF NOT EXISTS user_test_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    session_id BIGINT NOT NULL COMMENT '会话ID',
    mode_code VARCHAR(20) NOT NULL COMMENT '答题模式代码',
    category_code VARCHAR(10) COMMENT '漏洞分类代码',
    total_score INT DEFAULT 0 COMMENT '总得分',
    correct_count INT DEFAULT 0 COMMENT '正确题数',
    total_questions INT NOT NULL COMMENT '总题数',
    completion_rate DECIMAL(5,2) DEFAULT 0 COMMENT '完成率',
    time_spent INT DEFAULT 0 COMMENT '用时（秒）',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES test_sessions(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id),
    INDEX idx_mode_code (mode_code),
    INDEX idx_category_code (category_code),
    INDEX idx_completed_at (completed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户测试记录表';

SET FOREIGN_KEY_CHECKS = 1;

-- 记录版本（仅在未执行时插入）
INSERT INTO schema_version (version, description, script_name, execution_status)
SELECT @version, @description, @script_name, 'SUCCESS'
WHERE NOT EXISTS (
    SELECT 1 FROM schema_version WHERE version = @version COLLATE utf8mb4_unicode_ci AND execution_status = 'SUCCESS' COLLATE utf8mb4_unicode_ci
);

SET FOREIGN_KEY_CHECKS = 1;
