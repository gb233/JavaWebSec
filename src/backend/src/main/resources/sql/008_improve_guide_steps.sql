-- 008_improve_guide_steps.sql
-- 改进新手指引步骤数据，提供更具体和实用的引导内容

-- 删除现有的指引步骤数据
DELETE FROM guide_steps;

-- 插入改进的指引步骤数据
INSERT INTO guide_steps (step_key, title_zh, title_en, description_zh, description_en, target_element, position, order_index, is_active, guide_version, created_at, updated_at) VALUES

-- 1. 欢迎页面
('welcome', '欢迎使用Java Web安全教学系统', 'Welcome to Java Web Security Teaching System', 
'欢迎来到Java Web安全教学系统！这是一个基于OWASP Top 10的安全学习平台，帮助您掌握Web安全知识。', 
'Welcome to the Java Web Security Teaching System! This is a security learning platform based on OWASP Top 10 to help you master web security knowledge.', 
'#welcome-section', 'bottom', 1, true, '1.0.0', NOW(), NOW()),

-- 2. 导航菜单
('navigation', '导航菜单', 'Navigation Menu', 
'左侧是系统的主要导航菜单，包含：\n• 控制台：查看学习统计和进度\n• 漏洞知识中心：学习OWASP Top 10漏洞\n• 挑战模式：实战安全挑战\n• 知识测试：测试您的安全知识\n• 个人中心：管理个人信息', 
'On the left is the main navigation menu, including:\n• Dashboard: View learning statistics and progress\n• Vulnerability Knowledge Center: Learn OWASP Top 10 vulnerabilities\n• Challenge Mode: Practical security challenges\n• Knowledge Test: Test your security knowledge\n• Personal Center: Manage personal information', 
'.nav-menu', 'right', 2, true, '1.0.0', NOW(), NOW()),

-- 3. 漏洞知识中心
('vulnerability_center', '漏洞知识中心', 'Vulnerability Knowledge Center', 
'漏洞知识中心是系统的核心功能，包含：\n• A01-A10：OWASP Top 10安全漏洞\n• 每个漏洞包含：理论知识、攻击演示、防护方法\n• 支持实时攻击演示和防护验证\n• 提供详细的代码示例和最佳实践', 
'The Vulnerability Knowledge Center is the core feature, including:\n• A01-A10: OWASP Top 10 security vulnerabilities\n• Each vulnerability includes: theoretical knowledge, attack demonstrations, protection methods\n• Supports real-time attack demonstrations and protection verification\n• Provides detailed code examples and best practices', 
'.vulnerability-center', 'bottom', 3, true, '1.0.0', NOW(), NOW()),

-- 4. 挑战模式
('challenge_mode', '挑战模式', 'Challenge Mode', 
'挑战模式提供实战安全挑战：\n• 综合挑战：多漏洞组合攻击场景\n• 实时反馈：即时获得攻击结果\n• 难度分级：从初级到高级\n• 排行榜：与其他学习者竞争', 
'Challenge Mode provides practical security challenges:\n• Comprehensive Challenges: Multi-vulnerability attack scenarios\n• Real-time Feedback: Get instant attack results\n• Difficulty Levels: From beginner to advanced\n• Leaderboard: Compete with other learners', 
'.challenge-mode', 'bottom', 4, true, '1.0.0', NOW(), NOW()),

-- 5. 知识测试
('knowledge_test', '知识测试', 'Knowledge Test', 
'知识测试帮助验证学习成果：\n• 实时反馈模式：立即获得答案反馈\n• 考试模式：模拟真实考试环境\n• 随机综合模式：随机题目组合\n• 测试记录：查看历史测试结果', 
'Knowledge Test helps verify learning achievements:\n• Real-time Feedback Mode: Get immediate answer feedback\n• Exam Mode: Simulate real exam environment\n• Random Comprehensive Mode: Random question combinations\n• Test Records: View historical test results', 
'.knowledge-test', 'bottom', 5, true, '1.0.0', NOW(), NOW()),

-- 6. 个人中心
('user_profile', '个人中心', 'User Profile', 
'个人中心管理您的学习信息：\n• 学习进度：查看各漏洞学习状态\n• 测试记录：历史测试成绩\n• 成就徽章：学习成就展示\n• 学习笔记：个人学习记录', 
'Personal Center manages your learning information:\n• Learning Progress: View learning status of each vulnerability\n• Test Records: Historical test scores\n• Achievement Badges: Learning achievement display\n• Learning Notes: Personal learning records', 
'.user-profile', 'bottom', 6, true, '1.0.0', NOW(), NOW()),

-- 7. 语言切换
('language_switch', '语言切换', 'Language Switch', 
'系统支持多语言切换：\n• 默认中文：适合中文用户学习\n• 英文支持：国际化学习体验\n• 用户偏好：记住您的语言选择\n• 实时切换：无需刷新页面', 
'System supports multi-language switching:\n• Default Chinese: Suitable for Chinese users\n• English Support: International learning experience\n• User Preferences: Remember your language choice\n• Real-time Switching: No page refresh required', 
'.language-switch', 'left', 7, true, '1.0.0', NOW(), NOW()),

-- 8. 新手指引
('guide_trigger', '新手指引', 'New User Guide', 
'新手指引帮助您快速上手：\n• 功能导览：了解系统主要功能\n• 操作指导：学习如何使用各功能\n• 随时查看：可重复查看指引内容\n• 个性化：根据您的学习进度调整', 
'New User Guide helps you get started quickly:\n• Feature Tour: Learn about main system features\n• Operation Guide: Learn how to use each feature\n• View Anytime: Can review guide content repeatedly\n• Personalized: Adjust based on your learning progress', 
'.guide-trigger', 'left', 8, true, '1.0.0', NOW(), NOW());
