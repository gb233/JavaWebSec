-- 007_clean_duplicate_guide_steps.sql

-- 删除重复的指引步骤数据，只保留最新的
DELETE g1 FROM guide_steps g1
INNER JOIN guide_steps g2 
WHERE g1.id < g2.id 
AND g1.step_key = g2.step_key;

-- 确保指引步骤表有正确的数据
DELETE FROM guide_steps;

-- 重新插入指引步骤数据
INSERT INTO guide_steps (step_key, title_zh, title_en, description_zh, description_en, target_element, position, order_index, is_active, guide_version, created_at, updated_at) VALUES
('welcome', '欢迎使用Java Web安全教学系统', 'Welcome to Java Web Security Teaching System', '这是一个专门为安全学习设计的教学平台，让我们开始探索吧！', 'This is a teaching platform designed specifically for security learning. Let''s start exploring!', '#welcome-section', 'bottom', 1, true, '1.0.0', NOW(), NOW()),
('navigation', '导航菜单', 'Navigation Menu', '这里是系统的主要导航菜单，您可以访问不同的功能模块。', 'This is the main navigation menu where you can access different functional modules.', '.nav-menu', 'bottom', 2, true, '1.0.0', NOW(), NOW()),
('vulnerability_center', '漏洞知识中心', 'Vulnerability Knowledge Center', '在这里您可以学习各种Web安全漏洞的知识和防护方法。', 'Here you can learn about various web security vulnerabilities and protection methods.', '.vulnerability-center', 'bottom', 3, true, '1.0.0', NOW(), NOW()),
('challenge_mode', '挑战模式', 'Challenge Mode', '通过实战挑战来测试您的安全技能。', 'Test your security skills through practical challenges.', '.challenge-mode', 'bottom', 4, true, '1.0.0', NOW(), NOW()),
('user_profile', '个人中心', 'User Profile', '管理您的个人信息、学习进度和成就。', 'Manage your personal information, learning progress and achievements.', '.user-profile', 'bottom', 5, true, '1.0.0', NOW(), NOW()),
('language_switch', '语言切换', 'Language Switch', '点击这里可以切换系统语言。', 'Click here to switch the system language.', '.language-switch', 'bottom', 6, true, '1.0.0', NOW(), NOW()),
('guide_trigger', '新手指引', 'New User Guide', '点击这里可以重新查看新手指引。', 'Click here to review the new user guide again.', '.guide-trigger', 'bottom', 7, true, '1.0.0', NOW(), NOW());
