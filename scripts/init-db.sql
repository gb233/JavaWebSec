-- =====================================================
-- Java Web安全教学系统 - 数据库初始化脚本
-- =====================================================
-- 创建时间: 2024年9月24日
-- 版本: v1.0
-- 数据库: MySQL 8.0
-- 字符集: utf8mb4
-- =====================================================

-- 设置字符集和排序规则
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 注意：数据库已在脚本中创建，这里不需要再次创建
-- CREATE DATABASE IF NOT EXISTS security_teaching_system 
--   CHARACTER SET utf8mb4 
--   COLLATE utf8mb4_unicode_ci;

-- USE security_teaching_system;
-- 注意：数据库已在脚本中指定，这里不需要USE语句

-- =====================================================
-- 1. 用户管理模块表
-- =====================================================

-- 用户基本信息表
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    email VARCHAR(100) NOT NULL COMMENT '邮箱地址',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希值',
    full_name VARCHAR(100) COMMENT '用户真实姓名',
    phone VARCHAR(20) COMMENT '手机号码',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    user_role ENUM('student', 'teacher', 'admin') DEFAULT 'student' COMMENT '用户角色',
    user_status ENUM('active', 'inactive', 'suspended') DEFAULT 'active' COMMENT '用户状态',
    is_email_verified TINYINT(1) DEFAULT 0 COMMENT '邮箱是否已验证',
    last_login_at TIMESTAMP NULL COMMENT '最后登录时间',
    last_login_ip VARCHAR(45) COMMENT '最后登录IP',
    failed_login_attempts INT UNSIGNED DEFAULT 0 COMMENT '连续失败登录次数',
    locked_until TIMESTAMP NULL COMMENT '账户锁定到期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email),
    INDEX idx_users_status (user_status),
    INDEX idx_users_role (user_role),
    INDEX idx_users_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基本信息表';

-- 用户扩展信息表
DROP TABLE IF EXISTS user_profiles;
CREATE TABLE user_profiles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    bio TEXT COMMENT '个人简介',
    location VARCHAR(100) COMMENT '所在地区',
    website VARCHAR(255) COMMENT '个人网站',
    github_username VARCHAR(100) COMMENT 'GitHub用户名',
    learning_goals TEXT COMMENT '学习目标',
    professional_background VARCHAR(50) COMMENT '职业背景',
    years_of_experience INT COMMENT '工作经验年限',
    birth_date DATE COMMENT '生日',
    gender ENUM('MALE', 'FEMALE', 'OTHER') COMMENT '性别',
    country VARCHAR(50) COMMENT '所在国家',
    city VARCHAR(50) COMMENT '所在城市',
    skill_level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT') DEFAULT 'BEGINNER' COMMENT '技能水平',
    preferred_language VARCHAR(20) NOT NULL DEFAULT 'zh-CN' COMMENT '首选语言',
    timezone VARCHAR(50) NOT NULL DEFAULT 'Asia/Shanghai' COMMENT '时区设置',
    email_notifications BIT(1) NOT NULL DEFAULT b'1' COMMENT '是否接收邮件通知',
    learning_reminders BIT(1) NOT NULL DEFAULT b'1' COMMENT '是否接收学习提醒',
    total_study_time BIGINT NOT NULL DEFAULT 0 COMMENT '总学习时间（分钟）',
    total_points INT NOT NULL DEFAULT 0 COMMENT '总获得积分',
    completed_vulnerabilities INT NOT NULL DEFAULT 0 COMMENT '完成的漏洞数量',
    passed_tests INT NOT NULL DEFAULT 0 COMMENT '通过的测试数量',
    completed_challenges INT NOT NULL DEFAULT 0 COMMENT '完成的挑战数量',
    earned_badges INT NOT NULL DEFAULT 0 COMMENT '获得的徽章数量',
    current_streak INT NOT NULL DEFAULT 0 COMMENT '当前学习连续天数',
    longest_streak INT NOT NULL DEFAULT 0 COMMENT '最长学习连续天数',
    notification_settings JSON COMMENT '通知设置',
    privacy_settings JSON COMMENT '隐私设置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_profiles_user (user_id),
    UNIQUE KEY idx_user_id (user_id),
    KEY idx_skill_level (skill_level),
    KEY idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户扩展信息表';

-- 用户学习笔记表
DROP TABLE IF EXISTS user_notes;
CREATE TABLE user_notes (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '笔记ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    vulnerability_id BIGINT UNSIGNED NULL COMMENT '关联漏洞ID',
    note_title VARCHAR(200) NOT NULL COMMENT '笔记标题',
    note_content LONGTEXT NOT NULL COMMENT '笔记内容',
    note_type ENUM('general', 'vulnerability', 'challenge') DEFAULT 'general' COMMENT '笔记类型',
    is_public TINYINT(1) DEFAULT 0 COMMENT '是否公开',
    tags JSON COMMENT '标签列表',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_notes_user (user_id),
    INDEX idx_notes_vulnerability (vulnerability_id),
    INDEX idx_notes_type (note_type),
    INDEX idx_notes_public (is_public),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户学习笔记表';

-- 用户收藏内容表
DROP TABLE IF EXISTS user_collections;
CREATE TABLE user_collections (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    vulnerability_id BIGINT UNSIGNED NULL COMMENT '漏洞内容ID',
    collection_type ENUM('vulnerability', 'note', 'challenge') NOT NULL COMMENT '收藏类型',
    reference_id BIGINT UNSIGNED NOT NULL COMMENT '引用对象ID',
    collection_note VARCHAR(500) COMMENT '收藏备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    
    INDEX idx_collections_user (user_id),
    INDEX idx_collections_type_ref (collection_type, reference_id),
    INDEX idx_collections_created (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_collections_user_type_ref (user_id, collection_type, reference_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏内容表';

-- =====================================================
-- 2. 漏洞内容模块表
-- =====================================================

-- 漏洞分类表
DROP TABLE IF EXISTS vulnerability_categories;
CREATE TABLE vulnerability_categories (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    category_code VARCHAR(10) NOT NULL COMMENT '分类代码(如A01, A02)',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    category_description TEXT COMMENT '分类描述',
    owasp_year YEAR NOT NULL DEFAULT 2021 COMMENT 'OWASP年份版本',
    severity_level ENUM('low', 'medium', 'high', 'critical') DEFAULT 'medium' COMMENT '严重程度',
    order_num INT UNSIGNED DEFAULT 0 COMMENT '显示顺序',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    icon_url VARCHAR(255) COMMENT '图标URL',
    color_theme VARCHAR(20) COMMENT '主题颜色',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_categories_code_year (category_code, owasp_year),
    INDEX idx_categories_active_order (is_active, order_num),
    INDEX idx_categories_severity (severity_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='漏洞分类表';

-- 漏洞详细内容表
DROP TABLE IF EXISTS vulnerability_content;
CREATE TABLE vulnerability_content (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '内容ID',
    vulnerability_category_id BIGINT UNSIGNED NOT NULL COMMENT '漏洞分类ID',
    title VARCHAR(200) NOT NULL COMMENT '漏洞标题',
    subtitle VARCHAR(300) COMMENT '副标题',
    description TEXT NOT NULL COMMENT '漏洞描述',
    knowledge_content LONGTEXT NOT NULL COMMENT '知识页面内容(Markdown)',
    demo_description TEXT COMMENT '演示说明',
    vulnerable_code LONGTEXT COMMENT '存在漏洞的代码',
    secure_code LONGTEXT COMMENT '修复后的安全代码',
    repair_suggestions LONGTEXT COMMENT '修复建议(Markdown)',
    real_world_examples JSON COMMENT '真实案例',
    reference_links JSON COMMENT '参考资料链接',
    difficulty_level ENUM('beginner', 'intermediate', 'advanced') DEFAULT 'beginner' COMMENT '难度等级',
    estimated_time INT UNSIGNED DEFAULT 30 COMMENT '预计学习时间(分钟)',
    view_count INT UNSIGNED DEFAULT 0 COMMENT '查看次数',
    like_count INT UNSIGNED DEFAULT 0 COMMENT '点赞次数',
    order_num INT UNSIGNED DEFAULT 0 COMMENT '显示顺序',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    author_id BIGINT UNSIGNED COMMENT '作者用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_content_category_active (vulnerability_category_id, is_active),
    INDEX idx_content_difficulty (difficulty_level),
    INDEX idx_content_order (order_num),
    INDEX idx_content_author (author_id),
    FULLTEXT idx_content_search (title, description),
    FOREIGN KEY (vulnerability_category_id) REFERENCES vulnerability_categories(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='漏洞详细内容表';

-- 漏洞代码示例表
DROP TABLE IF EXISTS vulnerability_examples;
CREATE TABLE vulnerability_examples (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '示例ID',
    vulnerability_content_id BIGINT UNSIGNED NOT NULL COMMENT '漏洞内容ID',
    example_title VARCHAR(200) NOT NULL COMMENT '示例标题',
    example_description TEXT COMMENT '示例描述',
    code_language VARCHAR(50) NOT NULL DEFAULT 'java' COMMENT '代码语言',
    vulnerable_code LONGTEXT NOT NULL COMMENT '漏洞代码',
    attack_payload TEXT COMMENT '攻击载荷',
    expected_result TEXT COMMENT '预期结果',
    secure_code LONGTEXT COMMENT '安全代码',
    explanation LONGTEXT COMMENT '代码解释',
    complexity_level ENUM('basic', 'intermediate', 'advanced') DEFAULT 'basic' COMMENT '复杂度',
    order_num INT UNSIGNED DEFAULT 0 COMMENT '显示顺序',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_examples_content (vulnerability_content_id),
    INDEX idx_examples_language (code_language),
    INDEX idx_examples_active_order (is_active, order_num),
    FOREIGN KEY (vulnerability_content_id) REFERENCES vulnerability_content(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='漏洞代码示例表';

-- =====================================================
-- 3. 学习记录模块表
-- =====================================================

-- 学习进度记录表
DROP TABLE IF EXISTS learning_progress;
CREATE TABLE learning_progress (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '进度ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    vulnerability_id BIGINT UNSIGNED NOT NULL COMMENT '漏洞内容ID',
    progress_status ENUM('not_started', 'learning', 'knowledge_read', 'demo_completed', 'repair_completed', 'test_passed') 
        DEFAULT 'not_started' COMMENT '学习状态',
    knowledge_progress TINYINT UNSIGNED DEFAULT 0 COMMENT '知识页面学习进度百分比(0-100)',
    demo_progress TINYINT UNSIGNED DEFAULT 0 COMMENT '演示页面学习进度百分比(0-100)',
    repair_progress TINYINT UNSIGNED DEFAULT 0 COMMENT '修复页面学习进度百分比(0-100)',
    total_time_spent INT UNSIGNED DEFAULT 0 COMMENT '总学习时长(秒)',
    demo_attempts INT UNSIGNED DEFAULT 0 COMMENT '演示尝试次数',
    last_access_at TIMESTAMP NULL COMMENT '最后访问时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_progress_user_vuln (user_id, vulnerability_id),
    INDEX idx_progress_user_status (user_id, progress_status),
    INDEX idx_progress_completed (completed_at),
    INDEX idx_progress_last_access (last_access_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (vulnerability_id) REFERENCES vulnerability_content(id) ON DELETE CASCADE,
    UNIQUE KEY uk_progress_user_vuln (user_id, vulnerability_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户学习进度记录表';

-- 测试题目表
DROP TABLE IF EXISTS test_questions;
CREATE TABLE test_questions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '题目ID',
    vulnerability_category_id BIGINT UNSIGNED NOT NULL COMMENT '漏洞分类ID',
    question_type ENUM('single_choice', 'multiple_choice', 'true_false', 'fill_blank') NOT NULL COMMENT '题目类型',
    question_title VARCHAR(500) NOT NULL COMMENT '题目标题',
    question_content TEXT NOT NULL COMMENT '题目内容(支持HTML/Markdown)',
    question_code LONGTEXT COMMENT '代码示例',
    options JSON NOT NULL COMMENT '选项内容(JSON格式)',
    correct_answer VARCHAR(200) NOT NULL COMMENT '正确答案',
    explanation TEXT COMMENT '答案解析',
    difficulty_level ENUM('easy', 'medium', 'hard') DEFAULT 'medium' COMMENT '难度等级',
    points TINYINT UNSIGNED DEFAULT 1 COMMENT '题目分值',
    time_limit INT UNSIGNED DEFAULT 0 COMMENT '答题时限(秒,0表示无限制)',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    usage_count INT UNSIGNED DEFAULT 0 COMMENT '使用次数',
    correct_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '正确率百分比',
    created_by BIGINT UNSIGNED COMMENT '创建者用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_questions_category (vulnerability_category_id),
    INDEX idx_questions_type_difficulty (question_type, difficulty_level),
    INDEX idx_questions_active (is_active),
    INDEX idx_questions_usage (usage_count),
    FOREIGN KEY (vulnerability_category_id) REFERENCES vulnerability_categories(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识测试题目表';

-- 测试记录表
DROP TABLE IF EXISTS test_records;
CREATE TABLE test_records (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '测试记录ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    vulnerability_category_id BIGINT UNSIGNED NULL COMMENT '漏洞分类ID(NULL表示综合测试)',
    test_type ENUM('category_test', 'comprehensive_test', 'random_test', 'custom_test') NOT NULL COMMENT '测试类型',
    test_name VARCHAR(200) COMMENT '测试名称',
    total_questions TINYINT UNSIGNED NOT NULL COMMENT '总题目数',
    correct_answers TINYINT UNSIGNED DEFAULT 0 COMMENT '正确题目数',
    wrong_answers TINYINT UNSIGNED DEFAULT 0 COMMENT '错误题目数',
    skipped_answers TINYINT UNSIGNED DEFAULT 0 COMMENT '跳过题目数',
    score DECIMAL(5,2) DEFAULT 0.00 COMMENT '得分',
    max_score DECIMAL(5,2) DEFAULT 100.00 COMMENT '满分',
    pass_score DECIMAL(5,2) DEFAULT 70.00 COMMENT '及格分数',
    total_time_spent INT UNSIGNED DEFAULT 0 COMMENT '总用时(秒)',
    is_passed TINYINT(1) DEFAULT 0 COMMENT '是否通过',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    
    INDEX idx_test_records_user_category (user_id, vulnerability_category_id),
    INDEX idx_test_records_user_type (user_id, test_type),
    INDEX idx_test_records_passed (is_passed),
    INDEX idx_test_records_score (score),
    INDEX idx_test_records_completed (completed_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (vulnerability_category_id) REFERENCES vulnerability_categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户测试记录表';

-- 测试答题详情表
DROP TABLE IF EXISTS test_answer_details;
CREATE TABLE test_answer_details (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '答题详情ID',
    test_record_id BIGINT UNSIGNED NOT NULL COMMENT '测试记录ID',
    question_id BIGINT UNSIGNED NOT NULL COMMENT '题目ID',
    question_order TINYINT UNSIGNED NOT NULL COMMENT '题目顺序',
    user_answer VARCHAR(500) COMMENT '用户答案',
    is_correct TINYINT(1) NOT NULL COMMENT '是否正确',
    points_earned DECIMAL(5,2) DEFAULT 0.00 COMMENT '获得分数',
    time_spent INT UNSIGNED DEFAULT 0 COMMENT '答题用时(秒)',
    answer_attempts INT UNSIGNED DEFAULT 1 COMMENT '答题尝试次数',
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',
    
    INDEX idx_answer_details_test_record (test_record_id),
    INDEX idx_answer_details_question_correct (question_id, is_correct),
    INDEX idx_answer_details_answered (answered_at),
    FOREIGN KEY (test_record_id) REFERENCES test_records(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES test_questions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测试答题详情表';

-- 挑战任务表
DROP TABLE IF EXISTS challenge_tasks;
CREATE TABLE challenge_tasks (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '挑战任务ID',
    challenge_name VARCHAR(100) NOT NULL COMMENT '挑战名称',
    challenge_description TEXT NOT NULL COMMENT '挑战描述',
    challenge_story LONGTEXT COMMENT '挑战背景故事',
    challenge_type ENUM('beginner', 'intermediate', 'advanced', 'expert') NOT NULL COMMENT '挑战难度',
    challenge_category VARCHAR(50) COMMENT '挑战分类',
    required_vulnerabilities JSON NOT NULL COMMENT '需要掌握的漏洞类型(JSON数组)',
    task_objectives JSON NOT NULL COMMENT '任务目标列表(JSON数组)',
    environment_config JSON COMMENT '环境配置信息',
    flag_format VARCHAR(100) NOT NULL COMMENT 'Flag格式说明',
    flag_value VARCHAR(500) COMMENT 'Flag值(加密存储)',
    max_attempts TINYINT UNSIGNED DEFAULT 3 COMMENT '最大尝试次数',
    time_limit INT UNSIGNED DEFAULT 0 COMMENT '时间限制(秒,0表示无限制)',
    points SMALLINT UNSIGNED DEFAULT 100 COMMENT '完成奖励积分',
    badge_reward VARCHAR(50) NULL COMMENT '完成奖励徽章',
    hints JSON COMMENT '提示信息列表',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    completion_count INT UNSIGNED DEFAULT 0 COMMENT '完成人数',
    order_num INT UNSIGNED DEFAULT 0 COMMENT '显示顺序',
    created_by BIGINT UNSIGNED COMMENT '创建者用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_challenges_type_active (challenge_type, is_active),
    INDEX idx_challenges_category (challenge_category),
    INDEX idx_challenges_order (order_num),
    INDEX idx_challenges_completion (completion_count),
    UNIQUE KEY uk_challenges_name (challenge_name),
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='挑战任务定义表';

-- 挑战记录表
DROP TABLE IF EXISTS challenge_records;
CREATE TABLE challenge_records (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '挑战记录ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    challenge_id BIGINT UNSIGNED NOT NULL COMMENT '挑战任务ID',
    challenge_status ENUM('not_started', 'in_progress', 'completed', 'failed', 'timeout') 
        DEFAULT 'not_started' COMMENT '挑战状态',
    current_attempts TINYINT UNSIGNED DEFAULT 0 COMMENT '当前尝试次数',
    objectives_completed JSON NULL COMMENT '已完成的目标(JSON数组)',
    flags_found JSON NULL COMMENT '找到的Flag(JSON数组)',
    hints_used JSON NULL COMMENT '使用的提示(JSON数组)',
    attack_methods JSON NULL COMMENT '使用的攻击方法记录',
    total_time_spent INT UNSIGNED DEFAULT 0 COMMENT '总用时(秒)',
    final_score SMALLINT UNSIGNED DEFAULT 0 COMMENT '最终得分',
    completion_percentage DECIMAL(5,2) DEFAULT 0.00 COMMENT '完成百分比',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    last_activity_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后活动时间',
    
    INDEX idx_challenge_records_user_challenge (user_id, challenge_id),
    INDEX idx_challenge_records_user_status (user_id, challenge_status),
    INDEX idx_challenge_records_completed (completed_at),
    INDEX idx_challenge_records_last_activity (last_activity_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (challenge_id) REFERENCES challenge_tasks(id) ON DELETE CASCADE,
    UNIQUE KEY uk_challenge_records_user_challenge (user_id, challenge_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户挑战记录表';

-- =====================================================
-- 4. 系统管理模块表
-- =====================================================

-- 系统配置表
DROP TABLE IF EXISTS system_config;
CREATE TABLE system_config (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键名',
    config_value TEXT NOT NULL COMMENT '配置值',
    config_type ENUM('string', 'number', 'boolean', 'json', 'text') DEFAULT 'string' COMMENT '配置类型',
    config_category VARCHAR(50) NOT NULL COMMENT '配置分类',
    config_description VARCHAR(255) NOT NULL COMMENT '配置说明',
    is_public TINYINT(1) DEFAULT 0 COMMENT '是否公开(前端可访问)',
    is_editable TINYINT(1) DEFAULT 1 COMMENT '是否可编辑',
    default_value TEXT COMMENT '默认值',
    validation_rule VARCHAR(255) COMMENT '验证规则',
    order_num INT UNSIGNED DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_config_key (config_key),
    INDEX idx_config_category (config_category),
    INDEX idx_config_public (is_public),
    INDEX idx_config_order (order_num)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置参数表';

-- 操作日志表
DROP TABLE IF EXISTS operation_logs;
CREATE TABLE operation_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT UNSIGNED NULL COMMENT '操作用户ID(NULL表示系统操作)',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    operation_module VARCHAR(50) NOT NULL COMMENT '操作模块',
    operation_description VARCHAR(255) NOT NULL COMMENT '操作描述',
    operation_details JSON NULL COMMENT '操作详情(JSON格式)',
    request_ip VARCHAR(45) NOT NULL COMMENT '请求IP地址',
    user_agent TEXT COMMENT '用户代理信息',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_params JSON COMMENT '请求参数',
    response_status INT COMMENT '响应状态码',
    execution_time INT UNSIGNED COMMENT '执行时间(毫秒)',
    trace_id VARCHAR(100) COMMENT '链路跟踪ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    
    INDEX idx_operation_logs_user_time (user_id, created_at),
    INDEX idx_operation_logs_type_module (operation_type, operation_module),
    INDEX idx_operation_logs_ip_time (request_ip, created_at),
    INDEX idx_operation_logs_created_at (created_at),
    INDEX idx_operation_logs_trace (trace_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统操作日志表';

-- 攻击日志表
DROP TABLE IF EXISTS attack_logs;
CREATE TABLE attack_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '攻击日志ID',
    user_id BIGINT UNSIGNED NULL COMMENT '用户ID',
    vulnerability_id BIGINT UNSIGNED NULL COMMENT '漏洞内容ID',
    module VARCHAR(100) NOT NULL DEFAULT 'demo:unknown' COMMENT '所属模块/场景',
    attack_type VARCHAR(50) NOT NULL COMMENT '攻击类型',
    attack_payload TEXT NOT NULL COMMENT '攻击载荷',
    request_method VARCHAR(10) NOT NULL COMMENT '请求方法',
    request_url VARCHAR(500) NOT NULL COMMENT '请求URL',
    request_headers JSON COMMENT '请求头信息',
    request_body LONGTEXT COMMENT '请求体内容',
    response_status INT COMMENT '响应状态码',
    response_headers JSON COMMENT '响应头信息',
    response_body LONGTEXT COMMENT '响应体内容',
    is_successful TINYINT(1) DEFAULT 0 COMMENT '攻击是否成功',
    risk_level ENUM('low', 'medium', 'high', 'critical') DEFAULT 'medium' COMMENT '风险等级',
    source_ip VARCHAR(45) NOT NULL COMMENT '来源IP',
    user_agent TEXT COMMENT '用户代理',
    execution_time INT UNSIGNED COMMENT '执行时间(毫秒)',
    error_message TEXT COMMENT '错误信息',
    session_id VARCHAR(100) COMMENT '会话ID',
    trace_id VARCHAR(100) COMMENT '链路跟踪ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '攻击时间',
    
    INDEX idx_attack_logs_user_vuln (user_id, vulnerability_id),
    INDEX idx_attack_logs_attack_type (attack_type),
    INDEX idx_attack_logs_module (module),
    INDEX idx_attack_logs_successful (is_successful),
    INDEX idx_attack_logs_risk_level (risk_level),
    INDEX idx_attack_logs_created_at (created_at),
    INDEX idx_attack_logs_ip_time (source_ip, created_at),
    INDEX idx_attack_logs_session (session_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (vulnerability_id) REFERENCES vulnerability_content(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='攻击行为日志表';

-- 用户徽章表
DROP TABLE IF EXISTS user_badges;
CREATE TABLE user_badges (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '徽章记录ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    badge_type VARCHAR(50) NOT NULL COMMENT '徽章类型',
    badge_name VARCHAR(100) NOT NULL COMMENT '徽章名称',
    badge_description VARCHAR(255) NOT NULL COMMENT '徽章描述',
    badge_icon VARCHAR(255) COMMENT '徽章图标URL',
    badge_level ENUM('bronze', 'silver', 'gold', 'platinum', 'diamond') DEFAULT 'bronze' COMMENT '徽章等级',
    badge_category VARCHAR(50) COMMENT '徽章分类',
    earn_condition TEXT COMMENT '获得条件',
    bonus_points INT UNSIGNED DEFAULT 0 COMMENT '奖励积分',
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
    is_displayed TINYINT(1) DEFAULT 1 COMMENT '是否在个人页面显示',
    rarity ENUM('common', 'rare', 'epic', 'legendary') DEFAULT 'common' COMMENT '稀有度',
    
    INDEX idx_user_badges_user_type (user_id, badge_type),
    INDEX idx_user_badges_user_level (user_id, badge_level),
    INDEX idx_user_badges_earned_at (earned_at),
    INDEX idx_user_badges_category (badge_category),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_badges_user_badge (user_id, badge_type, badge_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户徽章记录表';

-- 错误日志表
DROP TABLE IF EXISTS error_logs;
CREATE TABLE error_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '错误日志ID',
    error_level ENUM('DEBUG', 'INFO', 'WARN', 'ERROR', 'FATAL') NOT NULL COMMENT '错误级别',
    error_message TEXT NOT NULL COMMENT '错误消息',
    error_stack_trace LONGTEXT COMMENT '错误堆栈',
    error_class VARCHAR(255) COMMENT '错误类名',
    error_method VARCHAR(100) COMMENT '错误方法',
    error_line INT COMMENT '错误行号',
    request_id VARCHAR(50) COMMENT '请求ID',
    user_id BIGINT UNSIGNED NULL COMMENT '用户ID',
    request_ip VARCHAR(45) COMMENT '请求IP',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_params JSON COMMENT '请求参数',
    server_info JSON COMMENT '服务器信息',
    environment VARCHAR(20) COMMENT '运行环境',
    application_version VARCHAR(20) COMMENT '应用版本',
    trace_id VARCHAR(100) COMMENT '链路跟踪ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '错误时间',
    
    INDEX idx_error_logs_level_time (error_level, created_at),
    INDEX idx_error_logs_class_method (error_class, error_method),
    INDEX idx_error_logs_user_time (user_id, created_at),
    INDEX idx_error_logs_created_at (created_at),
    INDEX idx_error_logs_trace (trace_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统错误日志表';

-- 邮箱验证令牌表
DROP TABLE IF EXISTS email_verification_tokens;
CREATE TABLE email_verification_tokens (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '令牌ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    email VARCHAR(100) NOT NULL COMMENT '邮箱地址',
    code VARCHAR(32) NOT NULL COMMENT '验证码',
    token_type VARCHAR(32) NOT NULL COMMENT '令牌类型',
    used BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否已使用',
    expires_at DATETIME(6) NOT NULL COMMENT '过期时间',
    created_at DATETIME(6) NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_email_token_type (email, token_type),
    KEY idx_user_token_type (user_id, token_type),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮箱验证令牌表';

-- 系统配置表（扩展版本）
DROP TABLE IF EXISTS system_configs;
CREATE TABLE system_configs (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(255) NOT NULL COMMENT '配置键名',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(255) COMMENT '配置类型',
    description VARCHAR(255) COMMENT '配置描述',
    is_sensitive BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否敏感',
    created_at DATETIME(6) NOT NULL COMMENT '创建时间',
    updated_at DATETIME(6) COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY UK_pk5mof051xp5r3e75s2e23s8s (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表（扩展版本）';

-- 系统日志表
DROP TABLE IF EXISTS system_logs;
CREATE TABLE system_logs (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT UNSIGNED COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    action VARCHAR(100) NOT NULL COMMENT '操作动作',
    level VARCHAR(20) NOT NULL COMMENT '日志级别',
    module VARCHAR(50) COMMENT '模块名称',
    description TEXT COMMENT '日志描述',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    created_at DATETIME(6) NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id),
    INDEX idx_level (level),
    INDEX idx_module (module),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';

-- 文件上传记录表
DROP TABLE IF EXISTS file_uploads;
CREATE TABLE file_uploads (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '文件上传ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '上传用户ID',
    original_filename VARCHAR(255) NOT NULL COMMENT '原始文件名',
    stored_filename VARCHAR(255) NOT NULL COMMENT '存储文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    file_size BIGINT UNSIGNED NOT NULL COMMENT '文件大小(字节)',
    file_type VARCHAR(100) NOT NULL COMMENT '文件类型/MIME类型',
    file_extension VARCHAR(20) NOT NULL COMMENT '文件扩展名',
    file_hash VARCHAR(64) COMMENT '文件哈希值(SHA256)',
    upload_purpose VARCHAR(50) NOT NULL COMMENT '上传目的',
    upload_source VARCHAR(50) COMMENT '上传来源',
    is_safe TINYINT(1) DEFAULT 0 COMMENT '是否安全(已扫描)',
    scan_result JSON COMMENT '安全扫描结果',
    download_count INT UNSIGNED DEFAULT 0 COMMENT '下载次数',
    is_public TINYINT(1) DEFAULT 0 COMMENT '是否公开可访问',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否已删除',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    expires_at TIMESTAMP NULL COMMENT '过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_file_uploads_user_purpose (user_id, upload_purpose),
    INDEX idx_file_uploads_stored_filename (stored_filename),
    INDEX idx_file_uploads_safe_deleted (is_safe, is_deleted),
    INDEX idx_file_uploads_created_at (created_at),
    INDEX idx_file_uploads_hash (file_hash),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件上传记录表';

-- =====================================================
-- 初始化基础数据
-- =====================================================

-- 插入漏洞分类数据
INSERT INTO vulnerability_categories (category_code, category_name, category_description, owasp_year, severity_level, order_num, icon_url, color_theme) VALUES
('A01', '访问控制失效', '由于访问控制策略不当导致的安全问题，包括权限提升、越权访问等', 2021, 'high', 1, '/icons/access-control.svg', '#e74c3c'),
('A02', '加密失败', '数据传输和存储过程中的加密失败或加密措施不当', 2021, 'high', 2, '/icons/crypto-failure.svg', '#e67e22'),
('A03', '注入漏洞', 'SQL注入、命令注入、LDAP注入等各种注入攻击', 2021, 'critical', 3, '/icons/injection.svg', '#c0392b'),
('A04', '不安全设计', '应用程序架构和设计中的安全缺陷', 2021, 'medium', 4, '/icons/insecure-design.svg', '#f39c12'),
('A05', '安全配置错误', '系统、框架、库的安全配置不当', 2021, 'medium', 5, '/icons/security-misconfiguration.svg', '#d35400'),
('A06', '过时的组件', '使用已知漏洞的过时组件和库', 2021, 'medium', 6, '/icons/vulnerable-components.svg', '#e74c3c'),
('A07', '身份验证失败', '身份验证和会话管理相关的安全问题', 2021, 'high', 7, '/icons/identification-failures.svg', '#8e44ad'),
('A08', '软件和数据完整性', '不安全的反序列化和软件更新过程', 2021, 'medium', 8, '/icons/integrity-failures.svg', '#2980b9'),
('A09', '安全日志记录失败', '日志记录和监控不足导致的安全问题', 2021, 'low', 9, '/icons/logging-failures.svg', '#27ae60'),
('A10', '服务器请求伪造', 'SSRF攻击和服务器端请求伪造', 2021, 'medium', 10, '/icons/ssrf.svg', '#16a085');


-- 插入系统配置数据
INSERT INTO system_config (config_key, config_value, config_type, config_category, config_description, is_public, is_editable, default_value) VALUES
('system.title', 'Java Web安全教学系统', 'string', 'basic', '系统标题', 1, 1, 'Java Web安全教学系统'),
('system.version', '1.0.0', 'string', 'basic', '系统版本', 1, 0, '1.0.0'),
('system.description', '基于OWASP Top 10的Java Web安全教学平台', 'string', 'basic', '系统描述', 1, 1, '基于OWASP Top 10的Java Web安全教学平台'),
('system.max_users', '1000', 'number', 'basic', '最大用户数', 0, 1, '1000'),
('system.maintenance_mode', 'false', 'boolean', 'basic', '维护模式', 0, 1, 'false'),

('security.jwt_expire_hours', '24', 'number', 'security', 'JWT令牌过期时间(小时)', 0, 1, '24'),
('security.max_login_attempts', '5', 'number', 'security', '最大登录尝试次数', 0, 1, '5'),
('security.password_min_length', '8', 'number', 'security', '密码最小长度', 1, 1, '8'),
('security.session_timeout', '30', 'number', 'security', '会话超时时间(分钟)', 0, 1, '30'),
('security.enable_captcha', 'true', 'boolean', 'security', '是否启用验证码', 0, 1, 'false'),

('learning.test_pass_score', '70', 'number', 'learning', '测试及格分数', 1, 1, '70'),
('learning.challenge_time_limit', '3600', 'number', 'learning', '挑战时间限制(秒)', 1, 1, '3600'),
('learning.max_test_attempts', '3', 'number', 'learning', '最大测试尝试次数', 1, 1, '3'),
('learning.enable_hints', 'true', 'boolean', 'learning', '是否启用提示功能', 1, 1, 'true'),

('file.max_upload_size', '10485760', 'number', 'file', '最大上传文件大小(字节)', 1, 1, '10485760'),
('file.allowed_extensions', '["jpg","jpeg","png","gif","pdf","txt","zip","doc","docx"]', 'json', 'file', '允许的文件扩展名', 1, 1, '["jpg","jpeg","png","gif","pdf","txt"]'),
('file.scan_uploaded_files', 'true', 'boolean', 'file', '是否扫描上传文件', 0, 1, 'true'),

('notification.email_enabled', 'false', 'boolean', 'notification', '是否启用邮件通知', 0, 1, 'false'),
('notification.smtp_host', '', 'string', 'notification', 'SMTP服务器地址', 0, 1, ''),
('notification.smtp_port', '587', 'number', 'notification', 'SMTP端口', 0, 1, '587');

-- 创建默认管理员用户(密码: admin123)
INSERT INTO users (username, email, password_hash, full_name, user_role, user_status, is_email_verified) VALUES
('admin', 'admin@javaweb-security.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyc6BjrVdpX9F8LgNGWOQ2', '系统管理员', 'admin', 'active', 1);

-- 为管理员创建用户资料
INSERT INTO user_profiles (user_id, bio, skill_level, preferred_language, timezone) VALUES
(1, '系统管理员账户', 'ADVANCED', 'zh-CN', 'Asia/Shanghai');

-- 设置外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 创建视图
-- =====================================================

-- 用户学习统计视图
CREATE OR REPLACE VIEW v_user_learning_stats AS
SELECT 
    u.id as user_id,
    u.username,
    u.full_name,
    COUNT(lp.id) as total_learning_items,
    COUNT(CASE WHEN lp.progress_status = 'test_passed' THEN 1 END) as completed_items,
    COUNT(CASE WHEN lp.progress_status IN ('learning', 'knowledge_read', 'demo_completed', 'repair_completed') THEN 1 END) as in_progress_items,
    ROUND(AVG(lp.knowledge_progress + lp.demo_progress + lp.repair_progress) / 3, 2) as avg_progress,
    SUM(lp.total_time_spent) as total_study_time,
    COUNT(DISTINCT tr.id) as total_tests_taken,
    COUNT(CASE WHEN tr.is_passed = 1 THEN 1 END) as tests_passed,
    COUNT(DISTINCT ub.id) as total_badges
FROM users u
LEFT JOIN learning_progress lp ON u.id = lp.user_id
LEFT JOIN test_records tr ON u.id = tr.user_id
LEFT JOIN user_badges ub ON u.id = ub.user_id
WHERE u.user_status = 'active'
GROUP BY u.id, u.username, u.full_name;

-- 漏洞内容统计视图
CREATE OR REPLACE VIEW v_vulnerability_stats AS
SELECT 
    vc.id as vulnerability_id,
    vc.title,
    vcat.category_name,
    vc.difficulty_level,
    vc.view_count,
    vc.like_count,
    COUNT(DISTINCT lp.user_id) as learners_count,
    COUNT(CASE WHEN lp.progress_status = 'test_passed' THEN 1 END) as completed_count,
    AVG(lp.total_time_spent) as avg_learning_time,
    COUNT(DISTINCT al.id) as attack_attempts,
    COUNT(CASE WHEN al.is_successful = 1 THEN 1 END) as successful_attacks
FROM vulnerability_content vc
LEFT JOIN vulnerability_categories vcat ON vc.vulnerability_category_id = vcat.id
LEFT JOIN learning_progress lp ON vc.id = lp.vulnerability_id
LEFT JOIN attack_logs al ON vc.id = al.vulnerability_id
WHERE vc.is_active = 1
GROUP BY vc.id, vc.title, vcat.category_name, vc.difficulty_level, vc.view_count, vc.like_count;

-- =====================================================
-- 创建存储过程
-- =====================================================

DELIMITER //

-- 更新用户学习进度
DROP PROCEDURE IF EXISTS UpdateLearningProgress;
CREATE PROCEDURE UpdateLearningProgress(
    IN p_user_id BIGINT,
    IN p_vulnerability_id BIGINT,
    IN p_progress_type VARCHAR(20),
    IN p_progress_value INT
)
BEGIN
    DECLARE current_status VARCHAR(50);
    
    -- 获取当前状态
    SELECT progress_status INTO current_status 
    FROM learning_progress 
    WHERE user_id = p_user_id AND vulnerability_id = p_vulnerability_id;
    
    -- 如果记录不存在，创建新记录
    IF current_status IS NULL THEN
        INSERT INTO learning_progress (user_id, vulnerability_id, progress_status)
        VALUES (p_user_id, p_vulnerability_id, 'learning');
        SET current_status = 'learning';
    END IF;
    
    -- 更新对应的进度
    CASE p_progress_type
        WHEN 'knowledge' THEN
            UPDATE learning_progress 
            SET knowledge_progress = p_progress_value,
                progress_status = CASE WHEN p_progress_value >= 100 THEN 'knowledge_read' ELSE 'learning' END,
                last_access_at = CURRENT_TIMESTAMP
            WHERE user_id = p_user_id AND vulnerability_id = p_vulnerability_id;
            
        WHEN 'demo' THEN
            UPDATE learning_progress 
            SET demo_progress = p_progress_value,
                progress_status = CASE WHEN p_progress_value >= 100 THEN 'demo_completed' ELSE 'learning' END,
                demo_attempts = demo_attempts + 1,
                last_access_at = CURRENT_TIMESTAMP
            WHERE user_id = p_user_id AND vulnerability_id = p_vulnerability_id;
            
        WHEN 'repair' THEN
            UPDATE learning_progress 
            SET repair_progress = p_progress_value,
                progress_status = CASE WHEN p_progress_value >= 100 THEN 'repair_completed' ELSE 'learning' END,
                last_access_at = CURRENT_TIMESTAMP
            WHERE user_id = p_user_id AND vulnerability_id = p_vulnerability_id;
    END CASE;
    
    -- 检查是否所有部分都完成，更新为完成状态
    UPDATE learning_progress 
    SET progress_status = 'test_passed',
        completed_at = CURRENT_TIMESTAMP
    WHERE user_id = p_user_id 
      AND vulnerability_id = p_vulnerability_id
      AND knowledge_progress >= 100 
      AND demo_progress >= 100 
      AND repair_progress >= 100
      AND progress_status != 'test_passed';
      
END //

-- 计算用户徽章
DROP PROCEDURE IF EXISTS CalculateUserBadges;
CREATE PROCEDURE CalculateUserBadges(IN p_user_id BIGINT)
BEGIN
    DECLARE learning_count INT DEFAULT 0;
    DECLARE test_passed_count INT DEFAULT 0;
    DECLARE challenge_completed_count INT DEFAULT 0;
    
    -- 获取用户学习统计
    SELECT 
        COUNT(CASE WHEN progress_status = 'test_passed' THEN 1 END),
        (SELECT COUNT(*) FROM test_records WHERE user_id = p_user_id AND is_passed = 1),
        (SELECT COUNT(*) FROM challenge_records WHERE user_id = p_user_id AND challenge_status = 'completed')
    INTO learning_count, test_passed_count, challenge_completed_count
    FROM learning_progress 
    WHERE user_id = p_user_id;
    
    -- 学习成就徽章
    IF learning_count >= 1 AND NOT EXISTS (SELECT 1 FROM user_badges WHERE user_id = p_user_id AND badge_type = 'first_learn') THEN
        INSERT INTO user_badges (user_id, badge_type, badge_name, badge_description, badge_level, badge_category)
        VALUES (p_user_id, 'first_learn', '初学者', '完成第一个漏洞学习', 'bronze', 'learning');
    END IF;
    
    IF learning_count >= 5 AND NOT EXISTS (SELECT 1 FROM user_badges WHERE user_id = p_user_id AND badge_type = 'learner') THEN
        INSERT INTO user_badges (user_id, badge_type, badge_name, badge_description, badge_level, badge_category)
        VALUES (p_user_id, 'learner', '学习者', '完成5个漏洞学习', 'silver', 'learning');
    END IF;
    
    IF learning_count >= 10 AND NOT EXISTS (SELECT 1 FROM user_badges WHERE user_id = p_user_id AND badge_type = 'advanced_learner') THEN
        INSERT INTO user_badges (user_id, badge_type, badge_name, badge_description, badge_level, badge_category)
        VALUES (p_user_id, 'advanced_learner', '高级学习者', '完成10个漏洞学习', 'gold', 'learning');
    END IF;
    
    -- 测试成就徽章
    IF test_passed_count >= 1 AND NOT EXISTS (SELECT 1 FROM user_badges WHERE user_id = p_user_id AND badge_type = 'first_test') THEN
        INSERT INTO user_badges (user_id, badge_type, badge_name, badge_description, badge_level, badge_category)
        VALUES (p_user_id, 'first_test', '测试新手', '通过第一个测试', 'bronze', 'testing');
    END IF;
    
    -- 挑战成就徽章
    IF challenge_completed_count >= 1 AND NOT EXISTS (SELECT 1 FROM user_badges WHERE user_id = p_user_id AND badge_type = 'challenger') THEN
        INSERT INTO user_badges (user_id, badge_type, badge_name, badge_description, badge_level, badge_category)
        VALUES (p_user_id, 'challenger', '挑战者', '完成第一个挑战', 'silver', 'challenge');
    END IF;
    
END //

DELIMITER ;

-- =====================================================
-- 创建触发器
-- =====================================================

DELIMITER //

-- 用户注册后自动创建用户资料
CREATE TRIGGER tr_users_after_insert
    AFTER INSERT ON users
    FOR EACH ROW
BEGIN
    INSERT INTO user_profiles (user_id, skill_level, preferred_language, timezone)
    VALUES (NEW.id, 'beginner', 'zh-CN', 'Asia/Shanghai');
END //

-- 学习进度更新后计算徽章
CREATE TRIGGER tr_learning_progress_after_update
    AFTER UPDATE ON learning_progress
    FOR EACH ROW
BEGIN
    IF NEW.progress_status = 'test_passed' AND OLD.progress_status != 'test_passed' THEN
        CALL CalculateUserBadges(NEW.user_id);
    END IF;
END //

-- 测试完成后计算徽章
CREATE TRIGGER tr_test_records_after_insert
    AFTER INSERT ON test_records
    FOR EACH ROW
BEGIN
    IF NEW.is_passed = 1 THEN
        CALL CalculateUserBadges(NEW.user_id);
    END IF;
END //

-- 挑战完成后计算徽章和更新统计
CREATE TRIGGER tr_challenge_records_after_update
    AFTER UPDATE ON challenge_records
    FOR EACH ROW
BEGIN
    IF NEW.challenge_status = 'completed' AND OLD.challenge_status != 'completed' THEN
        -- 更新挑战完成统计
        UPDATE challenge_tasks 
        SET completion_count = completion_count + 1 
        WHERE id = NEW.challenge_id;
        
        -- 计算徽章
        CALL CalculateUserBadges(NEW.user_id);
    END IF;
END //

DELIMITER ;

-- =====================================================
-- 创建索引优化
-- =====================================================

-- 复合索引优化查询性能
CREATE INDEX idx_learning_progress_user_status ON learning_progress(user_id, progress_status, last_access_at);
CREATE INDEX idx_test_records_user_passed_date ON test_records(user_id, is_passed, completed_at);
CREATE INDEX idx_attack_logs_vuln_successful_date ON attack_logs(vulnerability_id, is_successful, created_at);
CREATE INDEX idx_challenge_records_status_date ON challenge_records(challenge_status, completed_at);

-- 全文索引用于搜索
ALTER TABLE vulnerability_content ADD FULLTEXT(title, description, knowledge_content);
ALTER TABLE test_questions ADD FULLTEXT(question_title, question_content);
ALTER TABLE challenge_tasks ADD FULLTEXT(challenge_name, challenge_description);

-- =====================================================
-- 数据库性能优化配置
-- =====================================================

-- 设置自动统计信息更新
ALTER TABLE users STATS_AUTO_RECALC = 1;
ALTER TABLE vulnerability_content STATS_AUTO_RECALC = 1;
ALTER TABLE learning_progress STATS_AUTO_RECALC = 1;
ALTER TABLE test_records STATS_AUTO_RECALC = 1;
ALTER TABLE attack_logs STATS_AUTO_RECALC = 1;

-- 完成初始化
SELECT 'Database initialization completed successfully!' as status;
