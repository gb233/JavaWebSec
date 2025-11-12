-- 十个综合挑战场景数据库初始化脚本
-- 创建时间: 2024-01-15
-- 描述: 为综合挑战模式创建数据库表结构和初始数据

-- 1. 创建挑战场景配置表
CREATE TABLE IF NOT EXISTS challenge_scenarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    scenario_name VARCHAR(100) NOT NULL UNIQUE COMMENT '场景名称',
    title VARCHAR(200) NOT NULL COMMENT '场景标题',
    description TEXT COMMENT '场景描述',
    vulnerability_chain JSON NOT NULL COMMENT '漏洞链数组',
    difficulty_level VARCHAR(20) NOT NULL COMMENT '难度等级',
    estimated_time INT NOT NULL COMMENT '预估时间(分钟)',
    points INT NOT NULL COMMENT '积分',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_scenarios_difficulty (difficulty_level),
    INDEX idx_scenarios_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='挑战场景配置表';

-- 2. 创建挑战进度表
CREATE TABLE IF NOT EXISTS challenge_progress (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    scenario_id BIGINT NOT NULL COMMENT '场景ID',
    current_step INT DEFAULT 0 COMMENT '当前步骤',
    completed_steps JSON COMMENT '已完成步骤',
    progress_percentage DECIMAL(5,2) DEFAULT 0.00 COMMENT '完成百分比',
    is_completed TINYINT(1) DEFAULT 0 COMMENT '是否完成',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    
    UNIQUE KEY uk_user_scenario (user_id, scenario_id),
    INDEX idx_progress_user (user_id),
    INDEX idx_progress_scenario (scenario_id),
    INDEX idx_progress_completed (is_completed),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (scenario_id) REFERENCES challenge_scenarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='挑战进度表';

-- 3. 插入十个挑战场景数据
INSERT INTO challenge_scenarios (scenario_name, title, description, vulnerability_chain, difficulty_level, estimated_time, points) VALUES
('ecommerce_vulnerability_chain', '电商平台漏洞链', '模拟电商平台，包含用户管理、商品管理、订单处理等功能。需要发现并利用越权访问、SQL注入、业务逻辑缺陷等漏洞获得Flag。', '["A01-越权访问", "A03-注入漏洞", "A04-逻辑缺陷"]', 'intermediate', 60, 100),

('blog_comprehensive_test', '博客系统综合测试', '模拟博客系统，包含文章发布、评论管理、用户互动等功能。需要发现并利用XSS、CSRF、文件上传等漏洞获得Flag。', '["A03-XSS", "A05-CSRF", "A01-文件上传"]', 'intermediate', 45, 100),

('admin_backend_penetration', '管理后台渗透', '模拟企业级管理后台，包含用户管理、系统配置、数据统计等功能。需要发现并利用SQL注入、反序列化、权限提升等漏洞获得Flag。', '["A03-SQL注入", "A08-反序列化", "A01-权限提升"]', 'advanced', 75, 120),

('api_security_test', 'API安全测试', '模拟RESTful API服务，包含用户认证、数据查询、业务操作等功能。需要发现并利用JWT漏洞、业务逻辑缺陷、数据泄露等漏洞获得Flag。', '["A07-JWT漏洞", "A04-业务逻辑", "A01-数据泄露"]', 'intermediate', 50, 100),

('file_management_system', '文件管理系统', '模拟文件管理系统，包含文件上传、下载、管理等功能。需要发现并利用路径穿越、文件上传、任意文件读取等漏洞获得Flag。', '["A01-路径穿越", "A01-文件上传", "A01-任意读取"]', 'beginner', 40, 80),

('social_platform_security', '社交平台安全', '模拟社交平台，包含用户互动、内容分享、私信功能等。需要发现并利用XSS、SSRF、信息收集等漏洞获得Flag。', '["A03-XSS", "A10-SSRF", "A01-信息收集"]', 'advanced', 70, 120),

('payment_system_test', '支付系统测试', '模拟支付系统，包含订单处理、支付流程、资金管理等功能。需要发现并利用逻辑漏洞、条件竞争、金额篡改等漏洞获得Flag。', '["A04-逻辑漏洞", "A04-条件竞争", "A04-金额篡改"]', 'advanced', 80, 130),

('content_management_system', '内容管理系统', '模拟CMS系统，包含内容管理、模板系统、插件管理等功能。需要发现并利用XXE、反序列化、命令执行等漏洞获得Flag。', '["A03-XXE", "A08-反序列化", "A03-命令执行"]', 'expert', 90, 150),

('online_education_platform', '在线教育平台', '模拟在线教育平台，包含课程管理、学习进度、考试系统等功能。需要发现并利用访问控制、业务逻辑、数据篡改等漏洞获得Flag。', '["A01-访问控制", "A04-业务逻辑", "A01-数据篡改"]', 'intermediate', 55, 100),

('enterprise_app_comprehensive', '企业应用综合', '模拟企业级应用系统，包含LDAP认证、数据库服务、文件系统等功能。需要发现并利用LDAP注入、配置错误、权限提升等漏洞获得Flag。', '["A03-LDAP注入", "A05-配置错误", "A01-权限提升"]', 'expert', 100, 160);

-- 4. 创建索引优化查询性能
CREATE INDEX idx_scenarios_difficulty_active ON challenge_scenarios(difficulty_level, is_active);
CREATE INDEX idx_progress_user_completed ON challenge_progress(user_id, is_completed);
CREATE INDEX idx_progress_scenario_completed ON challenge_progress(scenario_id, is_completed);

-- 5. 验证数据插入
SELECT 
    id,
    scenario_name,
    title,
    difficulty_level,
    estimated_time,
    points,
    is_active
FROM challenge_scenarios 
ORDER BY id;

-- 6. 显示统计信息
SELECT 
    difficulty_level,
    COUNT(*) as count,
    AVG(estimated_time) as avg_time,
    AVG(points) as avg_points
FROM challenge_scenarios 
WHERE is_active = 1
GROUP BY difficulty_level;
