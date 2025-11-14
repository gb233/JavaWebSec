#!/bin/bash

# =====================================================
# 数据库迁移脚本（支持版本管理和增量更新）
# =====================================================
# 用途：执行数据库迁移，支持版本检查和增量更新
# 使用方法：./scripts/migrate-database.sh [--force] [--version=VERSION]
# =====================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-root}"
DB_NAME="${DB_NAME:-security_teaching_system}"

# 脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
MIGRATIONS_DIR="${SCRIPT_DIR}/migrations"

# MySQL命令
MYSQL_CMD="mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASSWORD}"

# 解析参数
FORCE=false
TARGET_VERSION=""

while [[ $# -gt 0 ]]; do
    case $1 in
        --force)
            FORCE=true
            shift
            ;;
        --version=*)
            TARGET_VERSION="${1#*=}"
            shift
            ;;
        *)
            echo -e "${RED}未知参数: $1${NC}"
            exit 1
            ;;
    esac
done

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}数据库迁移脚本${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 检查MySQL命令
if ! command -v mysql &> /dev/null; then
    echo -e "${RED}错误：未找到mysql命令${NC}"
    exit 1
fi

# 测试数据库连接
echo -e "${BLUE}[1/5] 测试数据库连接...${NC}"
if ! $MYSQL_CMD -e "SELECT 1;" &> /dev/null; then
    echo -e "${RED}错误：无法连接到MySQL数据库${NC}"
    exit 1
fi
echo -e "${GREEN}✓ 数据库连接成功${NC}"
echo ""

# 创建数据库（如果不存在）
echo -e "${BLUE}[2/5] 创建数据库（如果不存在）...${NC}"
$MYSQL_CMD -e "CREATE DATABASE IF NOT EXISTS \`${DB_NAME}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>&1
echo -e "${GREEN}✓ 数据库准备完成${NC}"
echo ""

# 获取当前数据库版本
echo -e "${BLUE}[3/5] 检查当前数据库版本...${NC}"
CURRENT_VERSION=$($MYSQL_CMD "${DB_NAME}" -N -e "SELECT version FROM schema_version WHERE execution_status = 'SUCCESS' ORDER BY execution_time DESC LIMIT 1;" 2>/dev/null || echo "")

if [ -z "$CURRENT_VERSION" ]; then
    echo -e "${YELLOW}⚠ 数据库未初始化，将执行完整初始化${NC}"
    CURRENT_VERSION="V0.0.0"
else
    echo -e "${GREEN}当前版本: ${CURRENT_VERSION}${NC}"
fi
echo ""

# 查找需要执行的迁移脚本
echo -e "${BLUE}[4/5] 查找需要执行的迁移脚本...${NC}"

# 查找所有迁移脚本并按版本排序
MIGRATION_FILES=$(find "${MIGRATIONS_DIR}" -name "V*.sql" -type f | sort)

if [ -z "$MIGRATION_FILES" ]; then
    echo -e "${YELLOW}⚠ 未找到迁移脚本${NC}"
    exit 0
fi

PENDING_MIGRATIONS=()
while IFS= read -r file; do
    # 提取版本号（文件名格式：V1.0.0__description.sql）
    VERSION=$(basename "$file" | sed -n 's/^V\([0-9.]*\)__.*/\1/p' | sed 's/^/V/')
    
    if [ -z "$VERSION" ]; then
        continue
    fi
    
    # 检查版本是否需要执行
    if [ "$FORCE" = true ] || [ -z "$CURRENT_VERSION" ] || [ "$VERSION" \> "$CURRENT_VERSION" ]; then
        if [ -n "$TARGET_VERSION" ] && [ "$VERSION" \> "$TARGET_VERSION" ]; then
            continue
        fi
        PENDING_MIGRATIONS+=("$file")
        echo -e "${GREEN}  ✓ 待执行: $(basename "$file")${NC}"
    else
        echo -e "${BLUE}  - 已执行: $(basename "$file")${NC}"
    fi
done <<< "$MIGRATION_FILES"

if [ ${#PENDING_MIGRATIONS[@]} -eq 0 ]; then
    echo -e "${GREEN}✓ 所有迁移已是最新版本${NC}"
    echo ""
    exit 0
fi

echo -e "${GREEN}找到 ${#PENDING_MIGRATIONS[@]} 个待执行的迁移${NC}"
echo ""

# 执行迁移
echo -e "${BLUE}[5/5] 执行迁移...${NC}"
SUCCESS_COUNT=0
FAILED_COUNT=0

for migration_file in "${PENDING_MIGRATIONS[@]}"; do
    migration_name=$(basename "$migration_file")
    echo -e "${BLUE}执行: ${migration_name}...${NC}"
    
    if $MYSQL_CMD "${DB_NAME}" < "$migration_file" 2>&1; then
        echo -e "${GREEN}  ✓ 成功: ${migration_name}${NC}"
        ((SUCCESS_COUNT++))
    else
        echo -e "${RED}  ✗ 失败: ${migration_name}${NC}"
        ((FAILED_COUNT++))
        
        if [ "$FORCE" != true ]; then
            echo -e "${RED}迁移失败，停止执行${NC}"
            exit 1
        fi
    fi
done

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}迁移完成！${NC}"
echo -e "${BLUE}成功: ${SUCCESS_COUNT}${NC}"
if [ $FAILED_COUNT -gt 0 ]; then
    echo -e "${RED}失败: ${FAILED_COUNT}${NC}"
fi
echo -e "${BLUE}========================================${NC}"

# 显示最新版本
LATEST_VERSION=$($MYSQL_CMD "${DB_NAME}" -N -e "SELECT version FROM schema_version WHERE execution_status = 'SUCCESS' ORDER BY execution_time DESC LIMIT 1;" 2>/dev/null || echo "")
if [ -n "$LATEST_VERSION" ]; then
    echo -e "${GREEN}当前数据库版本: ${LATEST_VERSION}${NC}"
fi



