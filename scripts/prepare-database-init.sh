#!/bin/bash

# =====================================================
# 准备Spring Boot数据库初始化文件
# =====================================================
# 用途：生成schema.sql和data.sql文件，供Spring Boot自动初始化使用
# 输出：
#   - src/backend/src/main/resources/schema.sql
#   - src/backend/src/main/resources/data.sql

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
RESOURCES_DIR="$PROJECT_ROOT/src/backend/src/main/resources"

echo "=========================================="
echo "准备Spring Boot数据库初始化文件"
echo "=========================================="

# 1. 创建schema.sql（表结构）
echo "📝 生成 schema.sql..."
cat > "$RESOURCES_DIR/schema.sql" << 'EOF'
-- =====================================================
-- Java Web安全教学系统 - 数据库表结构
-- =====================================================
-- 说明: Spring Boot启动时会自动执行此文件创建表结构
-- 注意: 此文件由脚本自动生成，请勿手动修改
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 注意：表结构由JPA实体类自动生成
-- 此文件主要用于文档说明，实际表结构由Hibernate根据@Entity注解创建
-- 如需手动创建表结构，请参考 scripts/init-db.sql

SET FOREIGN_KEY_CHECKS = 1;
EOF

# 2. 创建data.sql（初始数据）
echo "📝 生成 data.sql..."
cat > "$RESOURCES_DIR/data.sql" << 'EOF'
-- =====================================================
-- Java Web安全教学系统 - 初始数据
-- =====================================================
-- 说明: Spring Boot启动时会自动执行此文件插入初始数据
-- 注意: 此文件由脚本自动生成，请勿手动修改
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 注意：由于题目数据量较大（1000题），建议使用以下方式之一：
-- 1. 使用Java代码初始化（TestDataInitializer等）
-- 2. 手动执行 scripts/all-questions.sql
-- 3. 使用Docker Compose自动初始化

-- 基础配置数据（如果表已存在则跳过）
-- 漏洞分类数据
INSERT IGNORE INTO vulnerability_question_categories (category_code, category_name, description, total_questions, total_score) VALUES
('A01', '越权访问', '失效的访问控制漏洞，包括水平越权、垂直越权等', 100, 500),
('A02', '加密失败', '加密失败漏洞，包括弱加密算法、密钥管理不当等', 100, 500),
('A03', '注入漏洞', '注入漏洞，包括SQL注入、NoSQL注入、命令注入等', 100, 500),
('A04', '不安全设计', '不安全设计漏洞，包括业务逻辑缺陷、架构安全缺陷等', 100, 500),
('A05', '安全配置错误', '安全配置错误漏洞，包括默认配置、错误配置等', 100, 500),
('A06', '易受攻击组件', '易受攻击和过时的组件漏洞，包括已知CVE漏洞等', 100, 500),
('A07', '身份认证失败', '身份认证和会话管理功能实现不当', 100, 500),
('A08', '软件和数据完整性失效', '软件和数据完整性验证不足', 100, 500),
('A09', '安全日志记录和监控失效', '安全日志记录和监控不足', 100, 500),
('A10', '服务端请求伪造', '服务端请求伪造漏洞，包括SSRF攻击等', 100, 500);

-- 测试模式数据
INSERT IGNORE INTO test_modes (mode_code, mode_name, description, features) VALUES
('realtime', '实时反馈模式', '逐题实时反馈，适合学习巩固', '{"immediate_feedback": true, "navigation": true, "progress_save": true, "suitable_for": "learning"}'),
('exam', '考试模式', '完整答题后统一分析，适合能力测试', '{"batch_submit": true, "result_analysis": true, "score_calculation": true, "suitable_for": "assessment"}'),
('random', '随机综合模式', '全类型随机出题，适合综合练习', '{"cross_category": true, "random_selection": true, "comprehensive": true, "suitable_for": "practice"}');

SET FOREIGN_KEY_CHECKS = 1;

-- 注意：题目数据（1000题）请通过以下方式导入：
-- 1. 使用Java代码：TestDataInitializer（如果启用 app.demo.seed-test-data=true）
-- 2. 手动执行：mysql -u root -p security_teaching_system < scripts/all-questions.sql
-- 3. Docker Compose：自动执行初始化脚本
EOF

echo ""
echo "✅ 数据库初始化文件已生成："
echo "   - $RESOURCES_DIR/schema.sql"
echo "   - $RESOURCES_DIR/data.sql"
echo ""
echo "📌 重要提示："
echo "   1. 题目数据（1000题）需要通过其他方式导入"
echo "   2. 推荐方式：使用Java代码初始化（TestDataInitializer）"
echo "   3. 或手动执行：mysql -u root -p security_teaching_system < scripts/all-questions.sql"
echo "   4. 或使用Docker Compose自动初始化"
echo ""



