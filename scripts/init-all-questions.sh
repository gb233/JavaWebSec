#!/bin/bash

# =====================================================
# 数据库完整初始化脚本（Linux/Mac）
# =====================================================
# 用途：一次性导入所有数据库内容（表结构 + 1000题 + 挑战场景）
# 使用方法：./scripts/init-all-questions.sh
# =====================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置（可根据实际情况修改）
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-root}"
DB_NAME="${DB_NAME:-security_teaching_system}"

# 脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}数据库完整初始化脚本${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 检查MySQL命令是否存在
if ! command -v mysql &> /dev/null; then
    echo -e "${RED}错误：未找到mysql命令，请先安装MySQL客户端${NC}"
    exit 1
fi

# 提示输入数据库密码（如果未设置）
if [ -z "$DB_PASSWORD" ] || [ "$DB_PASSWORD" = "root" ]; then
    echo -e "${YELLOW}请输入MySQL root密码（直接回车使用默认值：root）：${NC}"
    read -s PASSWORD_INPUT
    if [ -n "$PASSWORD_INPUT" ]; then
        DB_PASSWORD="$PASSWORD_INPUT"
    fi
fi

# MySQL连接参数
MYSQL_CMD="mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASSWORD}"

echo -e "${GREEN}数据库配置：${NC}"
echo "  主机: $DB_HOST"
echo "  端口: $DB_PORT"
echo "  用户: $DB_USER"
echo "  数据库: $DB_NAME"
echo ""

# 测试数据库连接
echo -e "${BLUE}[1/13] 测试数据库连接...${NC}"
if ! $MYSQL_CMD -e "SELECT 1;" &> /dev/null; then
    echo -e "${RED}错误：无法连接到MySQL数据库，请检查配置${NC}"
    exit 1
fi
echo -e "${GREEN}✓ 数据库连接成功${NC}"
echo ""

# 创建数据库（如果不存在）
echo -e "${BLUE}[2/13] 创建数据库（如果不存在）...${NC}"
$MYSQL_CMD -e "CREATE DATABASE IF NOT EXISTS \`${DB_NAME}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>&1
echo -e "${GREEN}✓ 数据库创建完成${NC}"
echo ""

# 导入表结构
echo -e "${BLUE}[3/13] 导入表结构...${NC}"
$MYSQL_CMD "${DB_NAME}" < "${SCRIPT_DIR}/init-db.sql" 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ 表结构导入完成${NC}"
else
    echo -e "${RED}✗ 表结构导入失败${NC}"
    exit 1
fi
echo ""

# 导入A01-A10题目数据
echo -e "${BLUE}[4/13] 导入A01题目数据（100题）...${NC}"
$MYSQL_CMD "${DB_NAME}" < "${SCRIPT_DIR}/a01_complete_questions.sql" 2>&1
echo -e "${GREEN}✓ A01题目导入完成${NC}"

echo -e "${BLUE}[5/13] 导入A02题目数据（100题）...${NC}"
$MYSQL_CMD "${DB_NAME}" < "${SCRIPT_DIR}/a02_complete_questions.sql" 2>&1
echo -e "${GREEN}✓ A02题目导入完成${NC}"

echo -e "${BLUE}[6/13] 导入A03题目数据（100题）...${NC}"
$MYSQL_CMD "${DB_NAME}" < "${SCRIPT_DIR}/a03_complete_questions.sql" 2>&1
echo -e "${GREEN}✓ A03题目导入完成${NC}"

echo -e "${BLUE}[7/13] 导入A04题目数据（100题）...${NC}"
$MYSQL_CMD "${DB_NAME}" < "${SCRIPT_DIR}/a04_complete_questions.sql" 2>&1
echo -e "${GREEN}✓ A04题目导入完成${NC}"

echo -e "${BLUE}[8/13] 导入A05题目数据（100题）...${NC}"
$MYSQL_CMD "${DB_NAME}" < "${SCRIPT_DIR}/a05_complete_questions.sql" 2>&1
echo -e "${GREEN}✓ A05题目导入完成${NC}"

echo -e "${BLUE}[9/13] 导入A06题目数据（100题）...${NC}"
$MYSQL_CMD "${DB_NAME}" < "${SCRIPT_DIR}/a06_complete_questions.sql" 2>&1
echo -e "${GREEN}✓ A06题目导入完成${NC}"

echo -e "${BLUE}[10/13] 导入A07题目数据（100题）...${NC}"
$MYSQL_CMD "${DB_NAME}" < "${SCRIPT_DIR}/a07_complete_questions.sql" 2>&1
echo -e "${GREEN}✓ A07题目导入完成${NC}"

echo -e "${BLUE}[11/13] 导入A08题目数据（100题）...${NC}"
$MYSQL_CMD "${DB_NAME}" < "${SCRIPT_DIR}/a08_complete_questions.sql" 2>&1
echo -e "${GREEN}✓ A08题目导入完成${NC}"

echo -e "${BLUE}[12/13] 导入A09题目数据（100题）...${NC}"
$MYSQL_CMD "${DB_NAME}" < "${SCRIPT_DIR}/a09_complete_questions.sql" 2>&1
echo -e "${GREEN}✓ A09题目导入完成${NC}"

echo -e "${BLUE}[13/13] 导入A10题目数据（100题）...${NC}"
$MYSQL_CMD "${DB_NAME}" < "${SCRIPT_DIR}/a10_complete_questions.sql" 2>&1
echo -e "${GREEN}✓ A10题目导入完成${NC}"
echo ""

# 导入挑战场景数据
echo -e "${BLUE}[14/14] 导入挑战场景数据...${NC}"
if [ -f "${SCRIPT_DIR}/challenge-scenarios-init.sql" ]; then
    $MYSQL_CMD "${DB_NAME}" < "${SCRIPT_DIR}/challenge-scenarios-init.sql" 2>&1
    echo -e "${GREEN}✓ 挑战场景数据导入完成${NC}"
else
    echo -e "${YELLOW}⚠ 挑战场景脚本不存在，跳过${NC}"
fi
echo ""

# 验证数据导入
echo -e "${BLUE}验证数据导入...${NC}"
QUESTION_COUNT=$($MYSQL_CMD "${DB_NAME}" -N -e "SELECT COUNT(*) FROM vulnerability_questions;" 2>/dev/null)
echo -e "${GREEN}题目总数: ${QUESTION_COUNT}${NC}"

if [ "$QUESTION_COUNT" -eq 1000 ]; then
    echo -e "${GREEN}✓ 数据导入成功！共导入1000题${NC}"
else
    echo -e "${YELLOW}⚠ 题目数量不正确，期望1000题，实际${QUESTION_COUNT}题${NC}"
    echo -e "${YELLOW}请检查导入日志${NC}"
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}数据库初始化完成！${NC}"
echo -e "${BLUE}========================================${NC}"

