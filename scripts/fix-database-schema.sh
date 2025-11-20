#!/bin/bash

# =====================================================
# 修复数据库表结构不匹配问题
# =====================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}修复数据库表结构不匹配问题${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 读取数据库配置
read -p "请输入MySQL用户名（默认: root）: " DB_USER
DB_USER=${DB_USER:-root}

read -sp "请输入MySQL密码: " DB_PASS
echo ""

read -p "请输入MySQL主机（默认: localhost）: " DB_HOST
DB_HOST=${DB_HOST:-localhost}

read -p "请输入MySQL端口（默认: 3306）: " DB_PORT
DB_PORT=${DB_PORT:-3306}

read -p "请输入数据库名（默认: security_teaching_system）: " DB_NAME
DB_NAME=${DB_NAME:-security_teaching_system}

echo ""
echo -e "${BLUE}[1/3] 检查数据库连接...${NC}"
if ! mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e "USE $DB_NAME;" 2>/dev/null; then
    echo -e "${RED}✗ 数据库连接失败${NC}"
    exit 1
fi
echo -e "${GREEN}✓ 数据库连接成功${NC}"

echo ""
echo -e "${BLUE}[2/3] 检查 test_questions 表结构...${NC}"
TABLE_INFO=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" -N -e "
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = '$DB_NAME'
  AND TABLE_NAME = 'test_questions'
ORDER BY ORDINAL_POSITION;
" 2>/dev/null)

if [ -z "$TABLE_INFO" ]; then
    echo -e "${RED}✗ test_questions 表不存在${NC}"
    echo -e "${YELLOW}建议：运行 ./scripts/init-database.sh 初始化数据库${NC}"
    exit 1
fi

echo "当前表字段："
echo "$TABLE_INFO" | while read -r col; do
    echo "  - $col"
done

HAS_QUESTION_TEXT=$(echo "$TABLE_INFO" | grep -c "question_text" || true)
HAS_QUESTION_TITLE=$(echo "$TABLE_INFO" | grep -c "question_title" || true)
HAS_QUESTION_CONTENT=$(echo "$TABLE_INFO" | grep -c "question_content" || true)

echo ""
if [ "$HAS_QUESTION_TEXT" -gt 0 ] && ([ "$HAS_QUESTION_TITLE" -eq 0 ] || [ "$HAS_QUESTION_CONTENT" -eq 0 ]); then
    echo -e "${YELLOW}⚠ 检测到表结构不匹配：存在 question_text 但缺少 question_title 或 question_content${NC}"
    echo ""
    read -p "是否执行修复？(y/n): " CONFIRM
    if [ "$CONFIRM" != "y" ] && [ "$CONFIRM" != "Y" ]; then
        echo "已取消"
        exit 0
    fi
    
    echo ""
    echo -e "${BLUE}[3/3] 执行表结构修复...${NC}"
    
    # 执行修复 SQL
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" <<EOF
-- 添加新字段（如果不存在）
ALTER TABLE test_questions 
  ADD COLUMN IF NOT EXISTS question_title VARCHAR(500) NULL AFTER question_type,
  ADD COLUMN IF NOT EXISTS question_content TEXT NULL AFTER question_title;

-- 迁移数据
UPDATE test_questions 
SET 
  question_title = COALESCE(question_title, question_text, ''),
  question_content = COALESCE(question_content, question_text, '')
WHERE (question_title IS NULL OR question_title = '')
   OR (question_content IS NULL OR question_content = '');

-- 检查是否有空值
SET @has_null_title = (SELECT COUNT(*) FROM test_questions WHERE question_title IS NULL OR question_title = '');
SET @has_null_content = (SELECT COUNT(*) FROM test_questions WHERE question_content IS NULL OR question_content = '');

-- 如果没有空值，设置为 NOT NULL
SET @sql = IF(@has_null_title = 0 AND @has_null_content = 0,
    CONCAT(
        'ALTER TABLE test_questions ',
        'MODIFY COLUMN question_title VARCHAR(500) NOT NULL, ',
        'MODIFY COLUMN question_content TEXT NOT NULL;'
    ),
    'SELECT "存在空值，请先处理数据" AS warning;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 删除旧的 question_text 字段
ALTER TABLE test_questions DROP COLUMN IF EXISTS question_text;
EOF

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ 表结构修复完成${NC}"
    else
        echo -e "${RED}✗ 表结构修复失败${NC}"
        exit 1
    fi
    
elif [ "$HAS_QUESTION_TITLE" -gt 0 ] && [ "$HAS_QUESTION_CONTENT" -gt 0 ]; then
    echo -e "${GREEN}✓ 表结构正确，无需修复${NC}"
else
    echo -e "${YELLOW}⚠ 表结构异常，建议重新初始化数据库${NC}"
    echo -e "${YELLOW}运行: ./scripts/init-database.sh${NC}"
fi

echo ""
echo -e "${BLUE}验证修复结果...${NC}"
FINAL_INFO=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" -N -e "
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = '$DB_NAME'
  AND TABLE_NAME = 'test_questions'
ORDER BY ORDINAL_POSITION;
" 2>/dev/null)

echo "修复后的表字段："
echo "$FINAL_INFO" | while read -r col; do
    echo "  - $col"
done

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}修复完成！${NC}"
echo -e "${GREEN}========================================${NC}"







