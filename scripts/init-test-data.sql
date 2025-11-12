-- =====================================================
-- 测试数据初始化脚本
-- Java Web安全教学系统
-- =====================================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 清理现有测试数据
-- =====================================================
TRUNCATE TABLE user_badges;
TRUNCATE TABLE challenge_records;
TRUNCATE TABLE test_answer_details;
TRUNCATE TABLE test_records;
TRUNCATE TABLE learning_progress;
TRUNCATE TABLE user_collections;
TRUNCATE TABLE user_notes;
TRUNCATE TABLE user_profiles;
TRUNCATE TABLE attack_logs;
TRUNCATE TABLE operation_logs;
TRUNCATE TABLE file_uploads;
TRUNCATE TABLE error_logs;
TRUNCATE TABLE vulnerability_examples;
TRUNCATE TABLE vulnerability_content;
TRUNCATE TABLE test_questions;
TRUNCATE TABLE challenge_tasks;
TRUNCATE TABLE users;

-- 重置自增ID
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE vulnerability_content AUTO_INCREMENT = 1;
ALTER TABLE test_questions AUTO_INCREMENT = 1;
ALTER TABLE challenge_tasks AUTO_INCREMENT = 1;

-- =====================================================
-- 插入测试用户数据
-- =====================================================

-- 管理员用户
INSERT INTO users (id, username, email, password_hash, full_name, user_role, user_status, is_email_verified, created_at) VALUES
(1, 'admin', 'admin@javaweb-security.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyc6BjrVdpX9F8LgNGWOQ2', '系统管理员', 'admin', 'active', 1, NOW()),
(2, 'teacher', 'teacher@javaweb-security.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyc6BjrVdpX9F8LgNGWOQ2', '安全讲师', 'teacher', 'active', 1, NOW());

-- 学生用户
INSERT INTO users (id, username, email, password_hash, full_name, user_role, user_status, is_email_verified, created_at) VALUES
(3, 'student1', 'student1@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyc6BjrVdpX9F8LgNGWOQ2', '张三', 'student', 'active', 1, NOW()),
(4, 'student2', 'student2@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyc6BjrVdpX9F8LgNGWOQ2', '李四', 'student', 'active', 1, NOW()),
(5, 'student3', 'student3@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyc6BjrVdpX9F8LgNGWOQ2', '王五', 'student', 'active', 1, NOW()),
(6, 'newbie', 'newbie@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyc6BjrVdpX9F8LgNGWOQ2', '初学者', 'student', 'active', 1, NOW());

-- =====================================================
-- 插入漏洞内容数据
-- =====================================================

-- A01 - 访问控制失效
INSERT INTO vulnerability_content (id, vulnerability_category_id, title, subtitle, description, knowledge_content, vulnerable_code, secure_code, repair_suggestions, difficulty_level, estimated_time, author_id, created_at) VALUES
(1, 1, 'SQL注入漏洞基础', '最常见的Web应用安全问题', 'SQL注入是一种代码注入技术，攻击者通过在应用程序的数据库查询中插入恶意SQL代码来获取、修改或删除数据。', 
'# SQL注入漏洞详解

## 什么是SQL注入

SQL注入（SQL Injection）是一种常见的Web应用程序安全漏洞，攻击者通过在Web应用程序的输入字段中插入恶意的SQL代码，从而获得对数据库的未授权访问。

## 漏洞原理

当应用程序直接将用户输入拼接到SQL查询语句中，而没有进行适当的验证和转义时，就可能发生SQL注入。

## 攻击影响

- 数据泄露：获取敏感信息
- 数据篡改：修改或删除数据
- 权限提升：绕过身份验证
- 系统控制：在某些情况下可能获得系统控制权',

'// 存在漏洞的代码示例
String username = request.getParameter("username");
String password = request.getParameter("password");

String sql = "SELECT * FROM users WHERE username = \'" + username + "\' AND password = \'" + password + "\'";
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery(sql);',

'// 安全的代码示例
String username = request.getParameter("username");
String password = request.getParameter("password");

String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
PreparedStatement pstmt = connection.prepareStatement(sql);
pstmt.setString(1, username);
pstmt.setString(2, password);
ResultSet rs = pstmt.executeQuery();',

'## 修复建议

1. **使用参数化查询（推荐）**
   - 使用PreparedStatement代替Statement
   - 将用户输入作为参数传递，而不是直接拼接

2. **输入验证和过滤**
   - 验证用户输入的格式和类型
   - 过滤特殊字符

3. **最小权限原则**
   - 数据库用户只分配必要的权限
   - 避免使用管理员权限连接数据库

4. **错误处理**
   - 不要在错误信息中暴露数据库结构信息',
'beginner', 45, 2, NOW()),

(2, 3, 'XSS跨站脚本攻击', '客户端代码注入攻击', '跨站脚本（XSS）攻击是一种注入攻击，攻击者将恶意脚本注入到可信的网站中。',
'# XSS跨站脚本攻击

## 什么是XSS

跨站脚本（Cross-Site Scripting，XSS）是一种常见的Web应用漏洞，攻击者通过在网页中注入恶意脚本代码，当其他用户浏览该网页时，恶意脚本在用户浏览器中执行。

## XSS类型

### 1. 反射型XSS（非持久型）
恶意脚本通过URL参数等方式提交，服务器直接返回给客户端执行。

### 2. 存储型XSS（持久型）  
恶意脚本被存储在服务器端（如数据库），当用户访问包含恶意脚本的页面时执行。

### 3. DOM型XSS
通过修改页面DOM结构来实现攻击，脚本执行发生在客户端。',

'// 存在漏洞的代码
@RequestMapping("/search")
public String search(@RequestParam String keyword, Model model) {
    model.addAttribute("keyword", keyword);
    return "search";
}

<!-- 模板文件 search.html -->
<div>搜索关键词：<span th:utext="${keyword}"></span></div>',

'// 安全的代码
@RequestMapping("/search")
public String search(@RequestParam String keyword, Model model) {
    // 进行HTML编码
    String safeKeyword = HtmlUtils.htmlEscape(keyword);
    model.addAttribute("keyword", safeKeyword);
    return "search";
}

<!-- 模板文件 search.html -->
<div>搜索关键词：<span th:text="${keyword}"></span></div>',

'## 修复建议

1. **输出编码**
   - 对所有用户输入进行HTML编码
   - 使用安全的模板引擎

2. **输入验证**
   - 验证用户输入的格式
   - 过滤危险字符和标签

3. **Content Security Policy (CSP)**
   - 设置严格的CSP策略
   - 限制脚本来源

4. **HttpOnly Cookie**
   - 设置敏感Cookie为HttpOnly
   - 防止脚本访问Cookie',
'beginner', 40, 2, NOW());

-- =====================================================
-- 插入测试题目数据
-- =====================================================

-- SQL注入相关题目
INSERT INTO test_questions (id, vulnerability_category_id, question_type, question_title, question_content, options, correct_answer, explanation, difficulty_level, points, created_by, created_at) VALUES
(1, 3, 'single_choice', 'SQL注入基础概念', '以下哪个是SQL注入攻击的主要原因？', 
'["A. 服务器配置错误", "B. 用户输入未经验证直接拼接到SQL语句中", "C. 数据库权限设置过高", "D. 网络传输不安全"]',
'B', 'SQL注入的根本原因是应用程序将用户输入直接拼接到SQL查询中，而没有进行适当的验证和转义处理。', 'easy', 1, 2, NOW()),

(2, 3, 'single_choice', 'SQL注入防护', '以下哪种方法最能有效防止SQL注入攻击？', 
'["A. 使用HTTPS传输", "B. 定期更新数据库", "C. 使用参数化查询", "D. 限制数据库连接数"]',
'C', '参数化查询（PreparedStatement）是防止SQL注入最有效的方法，它将SQL代码和数据分离处理。', 'medium', 2, 2, NOW()),

(3, 3, 'multiple_choice', 'SQL注入检测', 'SQL注入攻击可能导致以下哪些后果？（多选）', 
'["A. 数据泄露", "B. 数据被篡改", "C. 服务器宕机", "D. 绕过身份验证", "E. 获得系统控制权"]',
'A,B,D,E', 'SQL注入可能导致数据泄露、篡改、绕过认证甚至获得系统控制权，但通常不会直接导致服务器宕机。', 'hard', 3, 2, NOW());

-- XSS相关题目  
INSERT INTO test_questions (id, vulnerability_category_id, question_type, question_title, question_content, options, correct_answer, explanation, difficulty_level, points, created_by, created_at) VALUES
(4, 3, 'single_choice', 'XSS攻击类型', '以下哪种XSS攻击类型的恶意脚本会被存储在服务器端？', 
'["A. 反射型XSS", "B. 存储型XSS", "C. DOM型XSS", "D. 文件型XSS"]',
'B', '存储型XSS（持久型XSS）的特点是恶意脚本被存储在服务器端数据库中，当用户访问时执行。', 'easy', 1, 2, NOW()),

(5, 3, 'true_false', 'XSS防护', 'Content Security Policy (CSP) 可以有效防止XSS攻击。', 
'["true", "false"]',
'true', 'CSP通过限制页面可以加载和执行的资源来源，能够有效减少XSS攻击的危害。', 'medium', 1, 2, NOW());

-- =====================================================
-- 插入挑战任务数据
-- =====================================================

INSERT INTO challenge_tasks (id, challenge_name, challenge_description, challenge_story, challenge_type, challenge_category, required_vulnerabilities, task_objectives, flag_format, flag_value, points, order_num, created_by, created_at) VALUES
(1, 'SQL注入初体验', '学习识别和利用基础的SQL注入漏洞', 
'你是一名安全测试工程师，发现了一个登录页面可能存在SQL注入漏洞。你的任务是通过SQL注入绕过登录验证，获取管理员权限。',
'beginner', 'injection', '["A03"]', 
'["分析登录表单的SQL查询逻辑", "构造SQL注入载荷", "绕过登录验证", "获取Flag"]',
'flag{...}', 'ZmxhZ3tzaW1wbGVfc3FsX2luamVjdGlvbl9ieXBhc3N9', 100, 1, 2, NOW()),

(2, 'XSS Cookie窃取', '利用XSS漏洞窃取用户Cookie', 
'网站的评论功能存在XSS漏洞，你需要构造恶意脚本来窃取管理员的Session Cookie。',
'intermediate', 'xss', '["A03"]', 
'["发现XSS注入点", "构造恶意JavaScript代码", "窃取Cookie信息", "获取管理员权限"]',
'flag{...}', 'ZmxhZ3t4c3NfY29va2llX3N0ZWFsaW5nX3N1Y2Nlc3N9', 150, 2, 2, NOW());

-- =====================================================
-- 插入学习进度数据
-- =====================================================

-- 学生1的学习进度
INSERT INTO learning_progress (user_id, vulnerability_id, progress_status, knowledge_progress, demo_progress, repair_progress, total_time_spent, demo_attempts, created_at) VALUES
(3, 1, 'demo_completed', 100, 100, 60, 1800, 3, NOW() - INTERVAL 2 DAY),
(3, 2, 'learning', 80, 30, 0, 900, 1, NOW() - INTERVAL 1 DAY);

-- 学生2的学习进度
INSERT INTO learning_progress (user_id, vulnerability_id, progress_status, knowledge_progress, demo_progress, repair_progress, total_time_spent, demo_attempts, created_at) VALUES
(4, 1, 'test_passed', 100, 100, 100, 2400, 2, NOW() - INTERVAL 3 DAY),
(4, 2, 'knowledge_read', 100, 0, 0, 600, 0, NOW() - INTERVAL 1 DAY);

-- =====================================================
-- 插入测试记录数据
-- =====================================================

-- 学生1的测试记录
INSERT INTO test_records (id, user_id, vulnerability_category_id, test_type, test_name, total_questions, correct_answers, wrong_answers, score, max_score, is_passed, started_at, completed_at) VALUES
(1, 3, 3, 'category_test', 'SQL注入知识测试', 3, 2, 1, 66.67, 100.00, 0, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY + INTERVAL 15 MINUTE);

-- 测试答题详情
INSERT INTO test_answer_details (test_record_id, question_id, question_order, user_answer, is_correct, points_earned, time_spent, answered_at) VALUES
(1, 1, 1, 'B', 1, 1.00, 120, NOW() - INTERVAL 1 DAY + INTERVAL 3 MINUTE),
(1, 2, 2, 'B', 0, 0.00, 180, NOW() - INTERVAL 1 DAY + INTERVAL 6 MINUTE),
(1, 3, 3, 'A,B,D,E', 1, 3.00, 360, NOW() - INTERVAL 1 DAY + INTERVAL 12 MINUTE);

-- 学生2的测试记录
INSERT INTO test_records (id, user_id, vulnerability_category_id, test_type, test_name, total_questions, correct_answers, wrong_answers, score, max_score, is_passed, started_at, completed_at) VALUES
(2, 4, 3, 'category_test', 'SQL注入知识测试', 3, 3, 0, 100.00, 100.00, 1, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY + INTERVAL 12 MINUTE);

-- =====================================================
-- 插入用户笔记数据
-- =====================================================

INSERT INTO user_notes (user_id, vulnerability_id, note_title, note_content, note_type, is_public, tags, created_at) VALUES
(3, 1, 'SQL注入学习笔记', 
'# SQL注入学习总结

## 学习要点
1. SQL注入的根本原因是用户输入未经验证
2. 使用PreparedStatement是最好的防护方法
3. 要始终遵循最小权限原则

## 实践心得
在演示环境中尝试了几种不同的注入方法：
- 联合查询注入
- 布尔盲注
- 时间盲注

## 待深入学习
- 高级SQL注入技术
- 不同数据库的注入特点', 'vulnerability', 1, '["sql注入", "学习笔记", "安全"]', NOW() - INTERVAL 1 DAY),

(4, 1, 'SQL注入防护实践', 
'## 代码审计要点

检查代码时需要重点关注：
1. 所有的数据库查询语句
2. 用户输入处理逻辑
3. 错误处理机制

## 最佳实践
- 永远不要信任用户输入
- 使用白名单而不是黑名单
- 定期进行安全测试', 'vulnerability', 1, '["代码审计", "最佳实践"]', NOW() - INTERVAL 2 DAY);

-- =====================================================
-- 插入操作日志数据
-- =====================================================

INSERT INTO operation_logs (user_id, operation_type, operation_module, operation_description, request_ip, user_agent, request_url, request_method, created_at) VALUES
(1, 'LOGIN', 'AUTH', '管理员登录系统', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '/api/v1/auth/login', 'POST', NOW() - INTERVAL 1 HOUR),
(3, 'VIEW', 'LEARNING', '查看漏洞详情', '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '/api/v1/vulnerabilities/1', 'GET', NOW() - INTERVAL 30 MINUTE),
(3, 'DEMO', 'LEARNING', '执行SQL注入演示', '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '/api/v1/demo/sql-injection', 'POST', NOW() - INTERVAL 25 MINUTE),
(4, 'TEST', 'LEARNING', '完成知识测试', '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '/api/v1/test/submit', 'POST', NOW() - INTERVAL 2 DAY);

-- =====================================================
-- 插入用户徽章数据
-- =====================================================

-- 学生2获得的徽章
INSERT INTO user_badges (user_id, badge_type, badge_name, badge_description, badge_level, badge_category, bonus_points, earned_at) VALUES
(4, 'first_learn', '初学者', '完成第一个漏洞学习', 'bronze', 'learning', 10, NOW() - INTERVAL 3 DAY),
(4, 'first_test', '测试新手', '通过第一个测试', 'bronze', 'testing', 20, NOW() - INTERVAL 2 DAY),
(4, 'perfect_score', '满分达人', '获得满分测试成绩', 'silver', 'testing', 50, NOW() - INTERVAL 2 DAY);

-- 学生1获得的徽章
INSERT INTO user_badges (user_id, badge_type, badge_name, badge_description, badge_level, badge_category, bonus_points, earned_at) VALUES
(3, 'first_learn', '初学者', '完成第一个漏洞学习', 'bronze', 'learning', 10, NOW() - INTERVAL 2 DAY);

-- =====================================================
-- 插入挑战记录数据
-- =====================================================

INSERT INTO challenge_records (user_id, challenge_id, challenge_status, current_attempts, objectives_completed, total_time_spent, final_score, completion_percentage, started_at, last_activity_at) VALUES
(4, 1, 'completed', 1, '["分析登录表单的SQL查询逻辑", "构造SQL注入载荷", "绕过登录验证", "获取Flag"]', 1800, 100, 100.00, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY + INTERVAL 30 MINUTE),
(3, 1, 'in_progress', 2, '["分析登录表单的SQL查询逻辑", "构造SQL注入载荷"]', 900, 0, 50.00, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 1 HOUR);

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 创建测试视图数据
-- =====================================================

-- 查看用户学习统计
SELECT 
    u.username,
    u.full_name,
    COUNT(lp.id) as total_learning_items,
    COUNT(CASE WHEN lp.progress_status = 'test_passed' THEN 1 END) as completed_items,
    AVG(lp.knowledge_progress + lp.demo_progress + lp.repair_progress) / 3 as avg_progress,
    COUNT(DISTINCT ub.id) as total_badges
FROM users u
LEFT JOIN learning_progress lp ON u.id = lp.user_id
LEFT JOIN user_badges ub ON u.id = ub.user_id
WHERE u.user_role = 'student'
GROUP BY u.id, u.username, u.full_name;

-- 显示初始化完成信息
SELECT 'Test data initialization completed successfully!' as status,
       COUNT(*) as total_users FROM users
UNION ALL
SELECT 'Total vulnerability content:', COUNT(*) FROM vulnerability_content
UNION ALL  
SELECT 'Total test questions:', COUNT(*) FROM test_questions
UNION ALL
SELECT 'Total challenge tasks:', COUNT(*) FROM challenge_tasks;

