#!/bin/bash

# =====================================================
# 题目数据导入脚本（支持增量导入和扩展）
# =====================================================
# 用途：导入题目数据，支持按分类、批次选择性导入
# 使用方法：./scripts/import-questions.sh [--category=A01] [--batch=1] [--all]
# =====================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 配置
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-root}"
DB_NAME="${DB_NAME:-security_teaching_system}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
QUESTIONS_DIR="${SCRIPT_DIR}/data/questions"
MYSQL_CMD="mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASSWORD}"

# 解析参数
IMPORT_ALL=false
CATEGORY=""
BATCH=""

while [[ $# -gt 0 ]]; do
    case $1 in
        --all)
            IMPORT_ALL=true
            shift
            ;;
        --category=*)
            CATEGORY="${1#*=}"
            shift
            ;;
        --batch=*)
            BATCH="${1#*=}"
            shift
            ;;
        *)
            echo -e "${RED}未知参数: $1${NC}"
            exit 1
            ;;
    esac
done

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}题目数据导入脚本${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 检查数据库连接
if ! $MYSQL_CMD -e "SELECT 1;" &> /dev/null; then
    echo -e "${RED}错误：无法连接到MySQL数据库${NC}"
    exit 1
fi

# 导入逻辑
if [ "$IMPORT_ALL" = true ]; then
    # 导入所有题目
    echo -e "${BLUE}导入所有题目数据...${NC}"
    
    # 导入A01-A10完整题目（兼容现有结构）
    for category in A01 A02 A03 A04 A05 A06 A07 A08 A09 A10; do
        question_file="${SCRIPT_DIR}/${category,,}_complete_questions.sql"
        if [ -f "$question_file" ]; then
            echo -e "${BLUE}导入${category}题目数据（100题）...${NC}"
            $MYSQL_CMD "${DB_NAME}" < "$question_file" 2>&1
            echo -e "${GREEN}✓ ${category}题目导入完成${NC}"
        fi
    done
    
    # 导入按批次组织的题目（未来扩展）
    if [ -d "$QUESTIONS_DIR" ]; then
        for category_dir in "$QUESTIONS_DIR"/*; do
            if [ -d "$category_dir" ]; then
                category=$(basename "$category_dir")
                echo -e "${BLUE}导入${category}分类题目（按批次）...${NC}"
                
                for batch_file in "$category_dir"/batch_*.sql; do
                    if [ -f "$batch_file" ]; then
                        batch_name=$(basename "$batch_file")
                        echo -e "${BLUE}  导入批次: ${batch_name}...${NC}"
                        $MYSQL_CMD "${DB_NAME}" < "$batch_file" 2>&1
                        echo -e "${GREEN}  ✓ ${batch_name}导入完成${NC}"
                    fi
                done
            fi
        done
    fi
    
elif [ -n "$CATEGORY" ]; then
    # 导入指定分类的题目
    echo -e "${BLUE}导入${CATEGORY}分类题目...${NC}"
    
    # 优先使用完整题目文件（兼容现有）
    question_file="${SCRIPT_DIR}/${CATEGORY,,}_complete_questions.sql"
    if [ -f "$question_file" ]; then
        echo -e "${BLUE}从完整文件导入...${NC}"
        $MYSQL_CMD "${DB_NAME}" < "$question_file" 2>&1
        echo -e "${GREEN}✓ ${CATEGORY}题目导入完成${NC}"
    elif [ -d "${QUESTIONS_DIR}/${CATEGORY}" ]; then
        # 使用批次文件
        if [ -n "$BATCH" ]; then
            # 导入指定批次
            batch_file="${QUESTIONS_DIR}/${CATEGORY}/batch_${BATCH}.sql"
            if [ -f "$batch_file" ]; then
                echo -e "${BLUE}导入批次${BATCH}...${NC}"
                $MYSQL_CMD "${DB_NAME}" < "$batch_file" 2>&1
                echo -e "${GREEN}✓ 批次${BATCH}导入完成${NC}"
            else
                echo -e "${RED}错误：批次文件不存在: ${batch_file}${NC}"
                exit 1
            fi
        else
            # 导入该分类的所有批次
            for batch_file in "${QUESTIONS_DIR}/${CATEGORY}"/batch_*.sql; do
                if [ -f "$batch_file" ]; then
                    batch_name=$(basename "$batch_file")
                    echo -e "${BLUE}导入批次: ${batch_name}...${NC}"
                    $MYSQL_CMD "${DB_NAME}" < "$batch_file" 2>&1
                    echo -e "${GREEN}✓ ${batch_name}导入完成${NC}"
                fi
            done
        fi
    else
        echo -e "${RED}错误：未找到${CATEGORY}分类的题目文件${NC}"
        exit 1
    fi
else
    # 默认导入所有（兼容现有脚本）
    echo -e "${BLUE}导入所有题目数据（默认模式）...${NC}"
    
    for category in A01 A02 A03 A04 A05 A06 A07 A08 A09 A10; do
        question_file="${SCRIPT_DIR}/${category,,}_complete_questions.sql"
        if [ -f "$question_file" ]; then
            echo -e "${BLUE}导入${category}题目数据（100题）...${NC}"
            $MYSQL_CMD "${DB_NAME}" < "$question_file" 2>&1
            echo -e "${GREEN}✓ ${category}题目导入完成${NC}"
        fi
    done
fi

# 验证导入结果
echo ""
echo -e "${BLUE}验证导入结果...${NC}"
QUESTION_COUNT=$($MYSQL_CMD "${DB_NAME}" -N -e "SELECT COUNT(*) FROM vulnerability_questions;" 2>/dev/null || echo "0")
echo -e "${GREEN}题目总数: ${QUESTION_COUNT}${NC}"

# 按分类统计
echo -e "${BLUE}按分类统计：${NC}"
$MYSQL_CMD "${DB_NAME}" -e "SELECT category_code, COUNT(*) as count FROM vulnerability_questions GROUP BY category_code ORDER BY category_code;" 2>&1 | grep -v "category_code"

echo ""
echo -e "${GREEN}题目导入完成！${NC}"



