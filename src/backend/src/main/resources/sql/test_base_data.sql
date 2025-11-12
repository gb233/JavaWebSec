-- 知识测试功能基础数据插入脚本
-- 字符集：utf8mb4_unicode_ci

-- 1. 插入A01-A10漏洞分类数据
INSERT INTO vulnerability_question_categories (category_code, category_name, description, total_questions, total_score) VALUES
('A01', '越权访问', '失效的访问控制漏洞，包括水平越权、垂直越权等', 24, 120),
('A02', '加密失败', '加密失败漏洞，包括弱加密算法、密钥管理不当等', 24, 120),
('A03', '注入漏洞', '注入漏洞，包括SQL注入、NoSQL注入、命令注入等', 24, 120),
('A04', '不安全设计', '不安全设计漏洞，包括业务逻辑缺陷、架构安全缺陷等', 24, 120),
('A05', '安全配置错误', '安全配置错误漏洞，包括默认配置、错误配置等', 24, 120),
('A06', '易受攻击组件', '易受攻击和过时的组件漏洞，包括已知CVE漏洞等', 24, 120),
('A07', '身份认证失败', '身份认证和会话管理功能实现不当', 24, 120),
('A08', '软件和数据完整性失效', '软件和数据完整性验证不足', 24, 120),
('A09', '安全日志记录和监控失效', '安全日志记录和监控不足', 24, 120),
('A10', '服务端请求伪造', '服务端请求伪造漏洞，包括SSRF攻击等', 24, 120);

-- 2. 插入三种答题模式数据
INSERT INTO test_modes (mode_code, mode_name, description, features) VALUES
('realtime', '实时反馈模式', '逐题实时反馈，适合学习巩固', '{"immediate_feedback": true, "navigation": true, "progress_save": true, "suitable_for": "learning"}'),
('exam', '考试模式', '完整答题后统一分析，适合能力测试', '{"batch_submit": true, "result_analysis": true, "score_calculation": true, "suitable_for": "assessment"}'),
('random', '随机综合模式', '全类型随机出题，适合综合练习', '{"cross_category": true, "random_selection": true, "comprehensive": true, "suitable_for": "practice"}');

-- 3. 验证数据插入
SELECT 'vulnerability_question_categories' as table_name, COUNT(*) as record_count FROM vulnerability_question_categories
UNION ALL
SELECT 'test_modes' as table_name, COUNT(*) as record_count FROM test_modes;

