#!/bin/bash

# =====================================================
# 详细字段级别对比脚本
# =====================================================
# 用途：对比指定表的字段定义差异
# 使用方法：./scripts/compare-schema-details.sh [表名]
# =====================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# 配置
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-}"
DB_NAME="${DB_NAME:-security_teaching_system}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 提示输入密码
if [ -z "$DB_PASSWORD" ]; then
    echo -e "${YELLOW}请输入MySQL密码：${NC}"
    read -s DB_PASSWORD
fi

MYSQL_CMD="mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASSWORD}"

# 获取表名参数
TABLE_NAME="${1:-}"

if [ -z "$TABLE_NAME" ]; then
    echo "用法: $0 <表名>"
    echo "示例: $0 users"
    exit 1
fi

echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}表结构详细对比: ${TABLE_NAME}${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

# 从数据库获取表结构
echo -e "${BLUE}[数据库表结构]${NC}"
DB_STRUCT=$($MYSQL_CMD "${DB_NAME}" -e "DESCRIBE \`${TABLE_NAME}\`;" 2>/dev/null || echo "")

if [ -z "$DB_STRUCT" ]; then
    echo -e "${RED}✗ 表 ${TABLE_NAME} 在数据库中不存在${NC}"
    exit 1
fi

echo "$DB_STRUCT"
echo ""

# 从SQL脚本查找表定义
echo -e "${BLUE}[SQL脚本表结构]${NC}"
SQL_FILES=(
    "${SCRIPT_DIR}/init-db.sql"
    "${SCRIPT_DIR}/challenge-scenarios-init.sql"
    "${SCRIPT_DIR}/migrations/core/V1.0.0__initial_schema.sql"
    "${SCRIPT_DIR}/migrations/collection/V1.4.0__collection_tables.sql"
    "${SCRIPT_DIR}/migrations/note/V1.2.0__note_tables.sql"
    "${SCRIPT_DIR}/migrations/badge/V1.3.0__badge_tables.sql"
    "${SCRIPT_DIR}/migrations/progress/V1.5.0__user_progress_tables.sql"
    "${SCRIPT_DIR}/migrations/guide/V1.6.0__guide_tables.sql"
    "${SCRIPT_DIR}/migrations/business/V1.7.0__business_tables.sql"
    "${SCRIPT_DIR}/migrations/test/V1.1.0__test_tables.sql"
)

FOUND=0
for sql_file in "${SQL_FILES[@]}"; do
    if [ -f "$sql_file" ]; then
        SQL_DEF=$(grep -A 100 "CREATE TABLE.*${TABLE_NAME}" "$sql_file" 2>/dev/null | head -50)
        if [ -n "$SQL_DEF" ]; then
            echo -e "${GREEN}✓ 在 $(basename "$sql_file") 中找到定义${NC}"
            echo "$SQL_DEF" | head -30
            FOUND=1
            break
        fi
    fi
done

if [ $FOUND -eq 0 ]; then
    echo -e "${YELLOW}⚠ 在SQL脚本中未找到表定义${NC}"
fi

echo ""

# 对比字段
echo -e "${CYAN}[字段对比]${NC}"
echo ""

# 提取数据库字段名
DB_FIELDS=$(echo "$DB_STRUCT" | tail -n +2 | awk '{print $1}')

# 从SQL提取字段名（简化版）
if [ $FOUND -eq 1 ]; then
    SQL_FIELDS=$(echo "$SQL_DEF" | grep -E "^\s+\`?\w+\`?\s+" | sed -E 's/^\s+//' | sed -E 's/\s+.*$//' | sed -E 's/`//g')
    
    echo -e "${BLUE}数据库字段数: $(echo "$DB_FIELDS" | wc -l)${NC}"
    echo -e "${BLUE}SQL脚本字段数: $(echo "$SQL_FIELDS" | wc -l)${NC}"
    echo ""
    
    # 检查缺失的字段
    for field in $DB_FIELDS; do
        if ! echo "$SQL_FIELDS" | grep -q "^${field}$"; then
            echo -e "${YELLOW}⚠ 字段 ${field} 在数据库中存在但SQL脚本中未定义${NC}"
        fi
    done
    
    for field in $SQL_FIELDS; do
        if ! echo "$DB_FIELDS" | grep -q "^${field}$"; then
            echo -e "${RED}✗ 字段 ${field} 在SQL脚本中定义但数据库中不存在${NC}"
        fi
    done
fi

echo ""
echo -e "${GREEN}对比完成${NC}"



