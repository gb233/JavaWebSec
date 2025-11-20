-- =====================================================
-- 版本: V1.0.0
-- 描述: 初始数据库表结构（核心表）
-- 依赖: 无
-- 执行时间: 2025-01-XX
-- =====================================================

-- 设置字符集和排序规则（修复MySQL 8.0+字符集冲突问题）
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET collation_connection = utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建版本管理表（如果不存在）
CREATE TABLE IF NOT EXISTS schema_version (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    version VARCHAR(20) NOT NULL UNIQUE COMMENT '版本号，如V1.0.0',
    description VARCHAR(200) COMMENT '版本描述',
    script_name VARCHAR(200) NOT NULL COMMENT '脚本文件名',
    execution_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',
    execution_status ENUM('SUCCESS', 'FAILED') DEFAULT 'SUCCESS' COMMENT '执行状态',
    execution_log TEXT COMMENT '执行日志',
    INDEX idx_version (version),
    INDEX idx_execution_time (execution_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据库版本管理表';

-- 检查版本是否已执行（修复排序规则冲突）
SET @version = 'V1.0.0';
SET @script_name = 'V1.0.0__initial_schema.sql';
SET @description = '初始数据库表结构（核心表）';

SELECT COUNT(*) INTO @version_exists 
FROM schema_version 
WHERE version = @version COLLATE utf8mb4_unicode_ci AND execution_status = 'SUCCESS' COLLATE utf8mb4_unicode_ci;

-- 如果版本不存在，执行迁移
SET @should_execute = IF(@version_exists = 0, 1, 0);

-- 直接执行 SQL，不使用 PREPARE/EXECUTE
-- 用户基本信息表
CREATE TABLE IF NOT EXISTS users (
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
CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    bio TEXT COMMENT '个人简介',
    location VARCHAR(100) COMMENT '所在地区',
    website VARCHAR(255) COMMENT '个人网站',
    github_username VARCHAR(100) COMMENT 'GitHub用户名',
    learning_goals TEXT COMMENT '学习目标',
    professional_background VARCHAR(50) COMMENT '职业背景',
    years_of_experience INT COMMENT '工作经验年限',
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
    INDEX idx_user_id (user_id),
    KEY idx_skill_level (skill_level),
    KEY idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户扩展信息表';

-- 漏洞分类表
CREATE TABLE IF NOT EXISTS vulnerability_categories (
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
CREATE TABLE IF NOT EXISTS vulnerability_content (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '内容ID',
    category_code VARCHAR(10) NOT NULL COMMENT '分类代码',
    content_type ENUM('principle', 'harm', 'exploit', 'vulnerable_code', 'secure_code', 'repair', 'detection') NOT NULL COMMENT '内容类型',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content LONGTEXT NOT NULL COMMENT '内容',
    code_examples JSON COMMENT '代码示例',
    attack_vectors JSON COMMENT '攻击向量',
    defense_measures JSON COMMENT '防护措施',
    references JSON COMMENT '参考资料',
    order_num INT UNSIGNED DEFAULT 0 COMMENT '显示顺序',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category_code (category_code),
    INDEX idx_content_type (content_type),
    INDEX idx_is_active (is_active),
    FOREIGN KEY (category_code) REFERENCES vulnerability_categories(category_code) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='漏洞详细内容表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(50) DEFAULT 'string' COMMENT '配置类型',
    description VARCHAR(500) COMMENT '配置描述',
    is_public TINYINT(1) DEFAULT 0 COMMENT '是否公开',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 攻击日志表
CREATE TABLE IF NOT EXISTS attack_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT UNSIGNED COMMENT '用户ID（可为空，匿名攻击）',
    vulnerability_code VARCHAR(10) NOT NULL COMMENT '漏洞代码',
    attack_type VARCHAR(50) NOT NULL COMMENT '攻击类型',
    attack_payload TEXT COMMENT '攻击载荷',
    attack_result TEXT COMMENT '攻击结果',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_headers JSON COMMENT '请求头',
    response_status INT COMMENT '响应状态码',
    is_successful TINYINT(1) DEFAULT 0 COMMENT '是否成功',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_vulnerability_code (vulnerability_code),
    INDEX idx_attack_type (attack_type),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='攻击日志表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT UNSIGNED COMMENT '用户ID',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    operation_target VARCHAR(100) COMMENT '操作目标',
    operation_description TEXT COMMENT '操作描述',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_method VARCHAR(10) COMMENT '请求方法',
    response_status INT COMMENT '响应状态码',
    execution_time INT COMMENT '执行时间（毫秒）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 错误日志表
CREATE TABLE IF NOT EXISTS error_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT UNSIGNED COMMENT '用户ID',
    error_type VARCHAR(100) NOT NULL COMMENT '错误类型',
    error_message TEXT NOT NULL COMMENT '错误消息',
    error_stack TEXT COMMENT '错误堆栈',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_params JSON COMMENT '请求参数',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_error_type (error_type),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错误日志表';

-- 文件上传表
CREATE TABLE IF NOT EXISTS file_uploads (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '文件ID',
    user_id BIGINT UNSIGNED COMMENT '用户ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT UNSIGNED COMMENT '文件大小（字节）',
    file_type VARCHAR(100) COMMENT '文件类型',
    mime_type VARCHAR(100) COMMENT 'MIME类型',
    upload_type VARCHAR(50) COMMENT '上传类型（avatar, document, image等）',
    is_public TINYINT(1) DEFAULT 0 COMMENT '是否公开',
    download_count INT UNSIGNED DEFAULT 0 COMMENT '下载次数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_upload_type (upload_type),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件上传表';

SET FOREIGN_KEY_CHECKS = 1;

-- 记录版本（仅在未执行时插入）
INSERT INTO schema_version (version, description, script_name, execution_status)
SELECT @version, @description, @script_name, 'SUCCESS'
WHERE NOT EXISTS (
    SELECT 1 FROM schema_version WHERE version = @version COLLATE utf8mb4_unicode_ci AND execution_status = 'SUCCESS' COLLATE utf8mb4_unicode_ci
);

SET FOREIGN_KEY_CHECKS = 1;
