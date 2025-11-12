-- =====================================================
-- Java Web安全教学系统 - 完整数据库初始化脚本
-- =====================================================
-- 用途：统一初始化脚本，包含所有必需的表结构和数据
-- 说明：此脚本按顺序执行所有初始化步骤
-- =====================================================

-- 注意：此文件主要用于文档说明
-- 实际初始化通过Docker Compose自动执行以下脚本：
-- 1. scripts/init-db.sql - 创建表结构
-- 2. scripts/all-questions.sql - 导入题目数据（1000题）
-- 3. scripts/challenge-scenarios-init.sql - 导入挑战场景数据

-- =====================================================
-- 使用说明
-- =====================================================
-- 
-- 方式一：使用Docker Compose（推荐）
-- docker-compose up -d
-- 
-- 方式二：手动执行
-- mysql -u root -p security_teaching_system < scripts/init-db.sql
-- mysql -u root -p security_teaching_system < scripts/all-questions.sql
-- mysql -u root -p security_teaching_system < scripts/challenge-scenarios-init.sql
-- 
-- =====================================================

