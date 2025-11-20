#!/bin/bash

# =====================================================
# 数据库表结构对比脚本
# =====================================================
# 用途：对比脚本中定义的表与实际数据库中的表
# 使用方法：./scripts/compare-database-schema.sh
# =====================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# 配置
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-}"
DB_NAME="${DB_NAME:-security_teaching_system}"

# 脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 提示输入密码（如果未设置）
if [ -z "$DB_PASSWORD" ]; then
    echo ""
    echo -e "${YELLOW}请输入MySQL密码（用户: ${DB_USER}）：${NC}"
    read -s DB_PASSWORD
    echo ""
fi

# MySQL连接参数
MYSQL_CMD="mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASSWORD}"

# 测试数据库连接
log_step "测试数据库连接..."
if ! $MYSQL_CMD -e "SELECT 1;" &> /dev/null; then
    log_error "无法连接到MySQL数据库"
    exit 1
fi
log_info "数据库连接成功"

# =====================================================
# 步骤1：从SQL文件中提取所有表定义
# =====================================================
log_step "从SQL文件中提取表定义..."

# 从init-db.sql提取表（改进的表名提取逻辑）
TABLES_FROM_INIT_DB=$(grep -h "^CREATE TABLE" "${SCRIPT_DIR}/init-db.sql" 2>/dev/null | \
    sed -E 's/^CREATE TABLE (IF NOT EXISTS )?//' | \
    sed -E 's/^[[:space:]]*//' | \
    sed -E 's/[[:space:]]*\(.*$//' | \
    sed -E 's/`//g' | \
    sed -E 's/^DROP TABLE IF EXISTS //' | \
    sed -E 's/^[[:space:]]+//' | \
    sed -E 's/[[:space:]]+$//' | \
    grep -v '^$' | \
    sort -u)

# 从migrations提取表（改进的表名提取逻辑）
TABLES_FROM_MIGRATIONS=$(grep -h "CREATE TABLE IF NOT EXISTS" "${SCRIPT_DIR}"/migrations/*/*.sql 2>/dev/null | \
    sed -E 's/.*CREATE TABLE IF NOT EXISTS[[:space:]]+//' | \
    sed -E 's/^[[:space:]]*//' | \
    sed -E 's/[[:space:]]*\(.*$//' | \
    sed -E 's/`//g' | \
    sed -E "s/''//g" | \
    sed -E 's/^[[:space:]]+//' | \
    sed -E 's/[[:space:]]+$//' | \
    grep -v '^$' | \
    sort -u)

# 从challenge-scenarios-init.sql提取表（改进的表名提取逻辑）
TABLES_FROM_CHALLENGE=$(grep -h "CREATE TABLE IF NOT EXISTS" "${SCRIPT_DIR}/challenge-scenarios-init.sql" 2>/dev/null | \
    sed -E 's/.*CREATE TABLE IF NOT EXISTS[[:space:]]+//' | \
    sed -E 's/^[[:space:]]*//' | \
    sed -E 's/[[:space:]]*\(.*$//' | \
    sed -E 's/`//g' | \
    sed -E 's/^[[:space:]]+//' | \
    sed -E 's/[[:space:]]+$//' | \
    grep -v '^$' | \
    sort -u)

# 合并所有表（去重）
ALL_EXPECTED_TABLES=$(echo -e "$TABLES_FROM_INIT_DB\n$TABLES_FROM_MIGRATIONS\n$TABLES_FROM_CHALLENGE" | \
    grep -v "^$" | sort -u)

EXPECTED_COUNT=$(echo "$ALL_EXPECTED_TABLES" | grep -v '^$' | wc -l | tr -d ' ' || echo "0")
log_info "从SQL文件中提取到 ${EXPECTED_COUNT} 个表定义"

# =====================================================
# 步骤2：从实际数据库获取所有表
# =====================================================
log_step "从实际数据库获取表列表..."
ACTUAL_TABLES=$($MYSQL_CMD "${DB_NAME}" -N -e \
    "SELECT table_name FROM information_schema.tables WHERE table_schema = '${DB_NAME}' ORDER BY table_name;" 2>/dev/null || echo "")

ACTUAL_COUNT=$(echo "$ACTUAL_TABLES" | grep -v '^$' | wc -l | tr -d ' ' || echo "0")
log_info "实际数据库中有 ${ACTUAL_COUNT} 个表"

# =====================================================
# 步骤3：对比差异
# =====================================================
log_step "对比表差异..."

# 清理表名（去除前后空格）
ALL_EXPECTED_TABLES_CLEAN=$(echo "$ALL_EXPECTED_TABLES" | sed -E 's/^[[:space:]]+//' | sed -E 's/[[:space:]]+$//' | grep -v '^$' | sort -u)
ACTUAL_TABLES_CLEAN=$(echo "$ACTUAL_TABLES" | sed -E 's/^[[:space:]]+//' | sed -E 's/[[:space:]]+$//' | grep -v '^$' | sort -u)

# 缺失的表（脚本中有但数据库中没有）
MISSING_TABLES=$(comm -23 <(echo "$ALL_EXPECTED_TABLES_CLEAN") <(echo "$ACTUAL_TABLES_CLEAN"))

# 多余的表（数据库中有但脚本中没有）
EXTRA_TABLES=$(comm -13 <(echo "$ALL_EXPECTED_TABLES_CLEAN") <(echo "$ACTUAL_TABLES_CLEAN"))

# 共同存在的表
COMMON_TABLES=$(comm -12 <(echo "$ALL_EXPECTED_TABLES_CLEAN") <(echo "$ACTUAL_TABLES_CLEAN"))

# =====================================================
# 步骤4：生成对比报告
# =====================================================
echo ""
echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}数据库表结构对比报告${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

# 统计信息
echo -e "${BLUE}【统计信息】${NC}"
echo "  脚本中定义的表数量: ${EXPECTED_COUNT}"
echo "  实际数据库中的表数量: ${ACTUAL_COUNT}"
echo "  共同存在的表数量: $(echo "$COMMON_TABLES" | grep -c "^" || echo "0")"
echo "  缺失的表数量: $(echo "$MISSING_TABLES" | grep -c "^" || echo "0")"
echo "  多余的表数量: $(echo "$EXTRA_TABLES" | grep -c "^" || echo "0")"
echo ""

# 缺失的表详情
if [ -n "$MISSING_TABLES" ]; then
    echo -e "${RED}【缺失的表】（脚本中有定义但数据库中没有）${NC}"
    MISSING_COUNT=0
    while IFS= read -r table; do
        TABLE_CLEAN=$(echo "$table" | sed -E 's/^[[:space:]]+//' | sed -E 's/[[:space:]]+$//')
        if [ -n "$TABLE_CLEAN" ]; then
            ((MISSING_COUNT++))
            echo -e "  ${RED}✗${NC} ${TABLE_CLEAN}"
            
            # 查找表定义来源（修复匹配逻辑）
            SOURCE=""
            if echo "$TABLES_FROM_INIT_DB" | grep -qxF "${TABLE_CLEAN}"; then
                SOURCE="${SOURCE}init-db.sql "
            fi
            if echo "$TABLES_FROM_MIGRATIONS" | grep -qxF "${TABLE_CLEAN}"; then
                SOURCE="${SOURCE}migrations/ "
            fi
            if echo "$TABLES_FROM_CHALLENGE" | grep -qxF "${TABLE_CLEAN}"; then
                SOURCE="${SOURCE}challenge-scenarios-init.sql "
            fi
            
            echo -e "      来源: ${SOURCE}"
            
            # 分析表的重要性（修复表名匹配）
            TABLE_CLEAN=$(echo "$table" | sed -E 's/^[[:space:]]+//' | sed -E 's/[[:space:]]+$//')
            IMPORTANCE=""
            if [[ "$TABLE_CLEAN" =~ ^(users|user_profiles)$ ]]; then
                IMPORTANCE="🔴 核心表 - 用户系统基础表，必须存在"
            elif [[ "$TABLE_CLEAN" =~ ^vulnerability_ ]]; then
                IMPORTANCE="🟠 重要表 - 漏洞相关功能依赖"
            elif [[ "$TABLE_CLEAN" =~ ^(test_|challenge_) ]]; then
                IMPORTANCE="🟡 功能表 - 测试/挑战功能依赖"
            elif [[ "$TABLE_CLEAN" =~ ^(system_|operation_|error_|attack_) ]]; then
                IMPORTANCE="🟢 辅助表 - 系统监控和日志，建议存在"
            else
                IMPORTANCE="⚪ 扩展表 - 功能扩展，可选"
            fi
            
            echo -e "      重要性: ${IMPORTANCE}"
            echo ""
        fi
    done <<< "$MISSING_TABLES"
else
    echo -e "${GREEN}【缺失的表】${NC}"
    echo "  ✓ 没有缺失的表"
    echo ""
fi

# 多余的表详情
if [ -n "$EXTRA_TABLES" ]; then
    echo -e "${YELLOW}【多余的表】（数据库中有但脚本中没有定义）${NC}"
    while IFS= read -r table; do
        if [ -n "$table" ]; then
            echo -e "  ${YELLOW}⚠${NC} ${table}"
            
            # 检查是否是系统表
            if [[ "$table" =~ ^(schema_version|information_schema|performance_schema|mysql|sys) ]]; then
                echo -e "      说明: 系统表，正常存在"
            else
                echo -e "      说明: 可能是手动创建或遗留表，建议检查"
            fi
            echo ""
        fi
    done <<< "$EXTRA_TABLES"
else
    echo -e "${GREEN}【多余的表】${NC}"
    echo "  ✓ 没有多余的表"
    echo ""
fi

# 共同存在的表详情
if [ -n "$COMMON_TABLES" ]; then
    echo -e "${GREEN}【共同存在的表】（脚本中有定义且数据库中也存在）${NC}"
    COMMON_COUNT=0
    while IFS= read -r table; do
        if [ -n "$table" ]; then
            ((COMMON_COUNT++))
            # 获取表的行数
            ROW_COUNT=$($MYSQL_CMD "${DB_NAME}" -N -e \
                "SELECT COUNT(*) FROM \`${table}\`;" 2>/dev/null || echo "N/A")
            
            if [ "$COMMON_COUNT" -le 20 ]; then
                echo -e "  ${GREEN}✓${NC} ${table} (${ROW_COUNT} 行)"
            elif [ "$COMMON_COUNT" -eq 21 ]; then
                echo -e "  ${GREEN}...${NC} (还有 $(($(echo "$COMMON_TABLES" | wc -l) - 20)) 个表)"
                break
            fi
        fi
    done <<< "$COMMON_TABLES"
    echo ""
fi

# =====================================================
# 步骤5：详细表结构对比（前5个缺失的表）
# =====================================================
if [ -n "$MISSING_TABLES" ]; then
    echo -e "${CYAN}【详细表结构分析】（前5个缺失的表）${NC}"
    MISSING_COUNT=0
    while IFS= read -r table; do
        if [ -n "$table" ] && [ $MISSING_COUNT -lt 5 ]; then
            ((MISSING_COUNT++))
            echo ""
            echo -e "${BLUE}表: ${table}${NC}"
            
            # 查找表定义（修复表名匹配）
            TABLE_CLEAN=$(echo "$table" | sed -E 's/^[[:space:]]+//' | sed -E 's/[[:space:]]+$//')
            TABLE_DEF=$(grep -A 50 "CREATE TABLE.*${TABLE_CLEAN}" "${SCRIPT_DIR}"/init-db.sql "${SCRIPT_DIR}"/migrations/*/*.sql "${SCRIPT_DIR}"/challenge-scenarios-init.sql 2>/dev/null | \
                head -30 | grep -E "CREATE TABLE|ENGINE|COMMENT|PRIMARY KEY|FOREIGN KEY|INDEX" | head -10)
            
            if [ -n "$TABLE_DEF" ]; then
                echo "  定义摘要:"
                echo "$TABLE_DEF" | sed 's/^/    /'
            fi
            
            # 检查是否有外键依赖
            FK_DEPENDENCIES=$(echo "$ALL_EXPECTED_TABLES" | sed -E 's/^[[:space:]]+//' | sed -E 's/[[:space:]]+$//' | grep -E "^(users|user_profiles)$" | head -2)
            if echo "$FK_DEPENDENCIES" | grep -q .; then
                echo "  依赖关系: 可能依赖以下表:"
                echo "$FK_DEPENDENCIES" | sed 's/^/    - /'
            fi
        fi
    done <<< "$MISSING_TABLES"
    echo ""
fi

# =====================================================
# 步骤6：建议和总结
# =====================================================
echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}建议和总结${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

# 以数据库为标准，检查脚本是否完整
if [ -n "$EXTRA_TABLES" ]; then
    EXTRA_COUNT=$(echo "$EXTRA_TABLES" | grep -v '^$' | wc -l | tr -d ' ')
    log_warn "发现 ${EXTRA_COUNT} 个表在数据库中存在但脚本中没有定义！"
    echo ""
    echo "这些表说明初始化脚本不完整，需要补充："
    echo "$EXTRA_TABLES" | head -10 | sed 's/^/  - /'
    if [ $EXTRA_COUNT -gt 10 ]; then
        echo "  ... (还有 $((EXTRA_COUNT - 10)) 个表)"
    fi
    echo ""
    echo "建议操作："
    echo "  1. 运行导出脚本获取缺失表的定义: ./scripts/export-missing-tables.sh"
    echo "  2. 将导出的表定义添加到初始化脚本中"
    echo "  3. 更新 init-database.sh，确保这些表也被创建"
    echo ""
fi

if [ -n "$MISSING_TABLES" ]; then
    CRITICAL_MISSING=$(echo "$MISSING_TABLES" | grep -E "^(users|user_profiles)" || true)
    if [ -n "$CRITICAL_MISSING" ]; then
        log_error "发现核心表缺失！必须立即修复："
        echo "$CRITICAL_MISSING" | sed 's/^/  - /'
        echo ""
        echo "建议操作："
        echo "  1. 运行初始化脚本: ./scripts/init-database.sh"
        echo "  2. 检查数据库连接和权限"
        echo "  3. 查看错误日志"
    else
        log_warn "发现非核心表缺失，建议检查："
        echo "$MISSING_TABLES" | head -5 | sed 's/^/  - /'
        echo ""
        echo "建议操作："
        echo "  1. 检查迁移脚本是否已执行"
        echo "  2. 手动执行缺失表的创建脚本"
    fi
else
    log_info "✓ 所有脚本中定义的表都已存在于数据库中"
fi

echo ""
echo "对比完成！"

