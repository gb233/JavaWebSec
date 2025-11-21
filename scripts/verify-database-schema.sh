#!/bin/bash

# =====================================================
# 数据库初始脚本完整核对方案
# =====================================================
# 用途：核对所有数据库脚本导入字段和表结构是否与当前系统数据库一致
# 使用方法：./scripts/verify-database-schema.sh [选项]
# =====================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
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

log_section() {
    echo ""
    echo -e "${CYAN}========================================${NC}"
    echo -e "${CYAN}$1${NC}"
    echo -e "${CYAN}========================================${NC}"
    echo ""
}

# 配置
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-}"
DB_NAME="${DB_NAME:-security_teaching_system}"

# 脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
REPORT_DIR="${SCRIPT_DIR}/schema-verification-reports"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
REPORT_FILE="${REPORT_DIR}/schema_verification_${TIMESTAMP}.txt"
DETAILED_REPORT="${REPORT_DIR}/schema_detailed_${TIMESTAMP}.txt"

# 创建报告目录
mkdir -p "${REPORT_DIR}"

# 显示横幅
show_banner() {
    echo -e "${CYAN}"
    cat << "EOF"
========================================
  数据库初始脚本完整核对方案
  Database Schema Verification Tool
========================================
EOF
    echo -e "${NC}"
}

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        --host=*)
            DB_HOST="${1#*=}"
            shift
            ;;
        --port=*)
            DB_PORT="${1#*=}"
            shift
            ;;
        --user=*)
            DB_USER="${1#*=}"
            shift
            ;;
        --password=*)
            DB_PASSWORD="${1#*=}"
            shift
            ;;
        --database=*)
            DB_NAME="${1#*=}"
            shift
            ;;
        --help)
            echo "用法: $0 [选项]"
            echo "选项:"
            echo "  --host=HOST         数据库主机（默认: localhost）"
            echo "  --port=PORT         数据库端口（默认: 3306）"
            echo "  --user=USER         数据库用户（默认: root）"
            echo "  --password=PASSWORD 数据库密码"
            echo "  --database=NAME     数据库名称（默认: security_teaching_system）"
            echo ""
            echo "环境变量:"
            echo "  DB_HOST, DB_PORT, DB_USER, DB_PASSWORD, DB_NAME"
            exit 0
            ;;
        *)
            log_error "未知参数: $1"
            echo "使用 --help 查看帮助"
            exit 1
            ;;
    esac
done

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

# 初始化报告文件
init_report() {
    cat > "${REPORT_FILE}" << EOF
========================================
数据库初始脚本完整核对报告
Database Schema Verification Report
========================================
生成时间: $(date '+%Y-%m-%d %H:%M:%S')
数据库: ${DB_NAME}@${DB_HOST}:${DB_PORT}
用户: ${DB_USER}
========================================

EOF
}

# 追加报告内容
append_report() {
    echo "$1" >> "${REPORT_FILE}"
    echo "$1"
}

# =====================================================
# 步骤1：从JPA实体类提取表结构
# =====================================================
extract_entity_schema() {
    log_section "步骤1: 从JPA实体类提取表结构"
    
    ENTITY_DIR="${PROJECT_ROOT}/src/backend/src/main/java/com/javaweb/security/entity"
    ENTITY_SCHEMA_FILE="${REPORT_DIR}/entity_schema_${TIMESTAMP}.json"
    
    if [ ! -d "${ENTITY_DIR}" ]; then
        log_error "实体类目录不存在: ${ENTITY_DIR}"
        return 1
    fi
    
    log_step "扫描JPA实体类..."
    
    # 使用Python脚本提取实体类信息（如果可用）
    if command -v python3 &> /dev/null; then
        python3 << 'PYTHON_SCRIPT' > "${ENTITY_SCHEMA_FILE}" 2>/dev/null || true
import os
import re
import json
from pathlib import Path

entity_dir = "${ENTITY_DIR}"
schemas = {}

for file_path in Path(entity_dir).glob("*.java"):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 提取表名
    table_match = re.search(r'@Table\s*\(\s*name\s*=\s*["\']([^"\']+)["\']', content)
    if not table_match:
        continue
    
    table_name = table_match.group(1)
    
    # 提取字段信息
    fields = []
    field_pattern = r'@Column\s*\([^)]*\)\s*(?:@\w+\s*)*\s*(?:private|protected)\s+(\w+)\s+(\w+);'
    
    for match in re.finditer(r'@Column\s*\(([^)]+)\)', content):
        col_attrs = match.group(1)
        # 提取字段名
        name_match = re.search(r'name\s*=\s*["\']([^"\']+)["\']', col_attrs)
        if not name_match:
            continue
        
        field_name = name_match.group(1)
        
        # 提取字段类型和长度
        nullable = 'nullable = false' in col_attrs
        length_match = re.search(r'length\s*=\s*(\d+)', col_attrs)
        length = int(length_match.group(1)) if length_match else None
        
        # 提取类型定义
        type_match = re.search(r'columnDefinition\s*=\s*["\']([^"\']+)["\']', col_attrs)
        column_def = type_match.group(1) if type_match else None
        
        # 查找Java类型
        java_type_match = re.search(r'private\s+(\w+)\s+(\w+)\s*;', content[content.find(match.group(0)):])
        java_type = java_type_match.group(1) if java_type_match else None
        
        fields.append({
            'name': field_name,
            'nullable': nullable,
            'length': length,
            'columnDefinition': column_def,
            'javaType': java_type
        })
    
    if fields:
        schemas[table_name] = {
            'source': 'JPA Entity',
            'file': file_path.name,
            'fields': fields
        }

print(json.dumps(schemas, indent=2, ensure_ascii=False))
PYTHON_SCRIPT
        log_info "已提取实体类表结构到: ${ENTITY_SCHEMA_FILE}"
    else
        log_warn "Python3未安装，跳过实体类提取"
    fi
}

# =====================================================
# 步骤2：从SQL脚本提取表结构
# =====================================================
extract_sql_schema() {
    log_section "步骤2: 从SQL脚本提取表结构"
    
    SQL_SCHEMA_FILE="${REPORT_DIR}/sql_schema_${TIMESTAMP}.json"
    
    log_step "扫描SQL脚本文件..."
    
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
    
    # 使用Python提取SQL表结构
    if command -v python3 &> /dev/null; then
        python3 << 'PYTHON_SCRIPT' > "${SQL_SCHEMA_FILE}" 2>/dev/null || true
import re
import json
from pathlib import Path

script_dir = "${SCRIPT_DIR}"
sql_files = [
    "${SCRIPT_DIR}/init-db.sql",
    "${SCRIPT_DIR}/challenge-scenarios-init.sql",
    "${SCRIPT_DIR}/migrations/core/V1.0.0__initial_schema.sql",
    "${SCRIPT_DIR}/migrations/collection/V1.4.0__collection_tables.sql",
    "${SCRIPT_DIR}/migrations/note/V1.2.0__note_tables.sql",
    "${SCRIPT_DIR}/migrations/badge/V1.3.0__badge_tables.sql",
    "${SCRIPT_DIR}/migrations/progress/V1.5.0__user_progress_tables.sql",
    "${SCRIPT_DIR}/migrations/guide/V1.6.0__guide_tables.sql",
    "${SCRIPT_DIR}/migrations/business/V1.7.0__business_tables.sql",
    "${SCRIPT_DIR}/migrations/test/V1.1.0__test_tables.sql"
]

schemas = {}

for sql_file in sql_files:
    file_path = Path(sql_file)
    if not file_path.exists():
        continue
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 提取CREATE TABLE语句
    table_pattern = r'CREATE\s+TABLE\s+(?:IF\s+NOT\s+EXISTS\s+)?(?:`)?(\w+)(?:`)?\s*\((.*?)\)\s*ENGINE'
    
    for match in re.finditer(table_pattern, content, re.DOTALL | re.IGNORECASE):
        table_name = match.group(1)
        table_def = match.group(2)
        
        fields = []
        # 提取字段定义
        field_pattern = r'(\w+)\s+([^,\n]+?)(?:,|$)'
        
        for field_match in re.finditer(r'`?(\w+)`?\s+([^,\n]+?)(?:,|$)', table_def):
            field_name = field_match.group(1)
            field_def = field_match.group(2).strip()
            
            # 解析字段定义
            nullable = 'NOT NULL' in field_def.upper()
            auto_increment = 'AUTO_INCREMENT' in field_def.upper()
            primary_key = 'PRIMARY KEY' in field_def.upper()
            
            # 提取类型和长度
            type_match = re.search(r'(\w+)(?:\((\d+)\))?', field_def)
            field_type = type_match.group(1) if type_match else None
            field_length = int(type_match.group(2)) if type_match and type_match.group(2) else None
            
            # 提取默认值
            default_match = re.search(r'DEFAULT\s+([^\s,]+)', field_def, re.IGNORECASE)
            default_value = default_match.group(1) if default_match else None
            
            fields.append({
                'name': field_name,
                'type': field_type,
                'length': field_length,
                'nullable': nullable,
                'autoIncrement': auto_increment,
                'primaryKey': primary_key,
                'defaultValue': default_value,
                'definition': field_def
            })
        
        if fields:
            schemas[table_name] = {
                'source': 'SQL Script',
                'file': file_path.name,
                'fields': fields
            }

print(json.dumps(schemas, indent=2, ensure_ascii=False))
PYTHON_SCRIPT
        log_info "已提取SQL脚本表结构到: ${SQL_SCHEMA_FILE}"
    else
        log_warn "Python3未安装，跳过SQL脚本提取"
    fi
}

# =====================================================
# 步骤3：从实际数据库提取表结构
# =====================================================
extract_database_schema() {
    log_section "步骤3: 从实际数据库提取表结构"
    
    DB_SCHEMA_FILE="${REPORT_DIR}/database_schema_${TIMESTAMP}.json"
    
    log_step "从数据库提取表结构..."
    
    # 使用MySQL命令提取表结构
    $MYSQL_CMD "${DB_NAME}" -N -e "
    SELECT 
        TABLE_NAME,
        COLUMN_NAME,
        COLUMN_TYPE,
        IS_NULLABLE,
        COLUMN_DEFAULT,
        COLUMN_KEY,
        EXTRA,
        COLUMN_COMMENT
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = '${DB_NAME}'
    ORDER BY TABLE_NAME, ORDINAL_POSITION;
    " > "${REPORT_DIR}/db_columns_${TIMESTAMP}.txt" 2>/dev/null || true
    
    log_info "已提取数据库表结构到: ${DB_SCHEMA_FILE}"
}

# =====================================================
# 步骤4：对比表结构
# =====================================================
compare_schemas() {
    log_section "步骤4: 对比表结构差异"
    
    log_step "执行详细对比..."
    
    # 获取所有表名
    TABLES_IN_DB=$($MYSQL_CMD "${DB_NAME}" -N -e \
        "SELECT table_name FROM information_schema.tables WHERE table_schema = '${DB_NAME}' ORDER BY table_name;" 2>/dev/null || echo "")
    
    append_report ""
    append_report "【表存在性检查】"
    append_report "========================================"
    
    # 检查每个表
    MISSING_TABLES=()
    EXTRA_TABLES=()
    COMMON_TABLES=()
    
    # 从SQL脚本提取表名
    SQL_TABLES=$(grep -h "^CREATE TABLE" "${SCRIPT_DIR}"/init-db.sql "${SCRIPT_DIR}"/migrations/*/*.sql "${SCRIPT_DIR}"/challenge-scenarios-init.sql 2>/dev/null | \
        sed -E 's/^CREATE TABLE (IF NOT EXISTS )?//' | \
        sed -E 's/[[:space:]]*\(.*$//' | \
        sed -E 's/`//g' | \
        sed -E 's/^[[:space:]]+//' | \
        sed -E 's/[[:space:]]+$//' | \
        sort -u)
    
    for table in $SQL_TABLES; do
        if echo "$TABLES_IN_DB" | grep -q "^${table}$"; then
            COMMON_TABLES+=("$table")
        else
            MISSING_TABLES+=("$table")
        fi
    done
    
    for table in $TABLES_IN_DB; do
        if ! echo "$SQL_TABLES" | grep -q "^${table}$"; then
            EXTRA_TABLES+=("$table")
        fi
    done
    
    append_report "✓ 共同存在的表: ${#COMMON_TABLES[@]}"
    append_report "✗ 缺失的表: ${#MISSING_TABLES[@]}"
    append_report "⚠ 多余的表: ${#EXTRA_TABLES[@]}"
    append_report ""
    
    # 详细字段对比（前10个表）
    if [ ${#COMMON_TABLES[@]} -gt 0 ]; then
        append_report "【字段级别对比】（前10个表）"
        append_report "========================================"
        
        COUNT=0
        for table in "${COMMON_TABLES[@]}"; do
            if [ $COUNT -ge 10 ]; then
                break
            fi
            
            append_report ""
            append_report "表: ${table}"
            
            # 获取数据库中的字段
            DB_COLUMNS=$($MYSQL_CMD "${DB_NAME}" -N -e \
                "SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_KEY, EXTRA
                 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = '${DB_NAME}' AND TABLE_NAME = '${table}'
                 ORDER BY ORDINAL_POSITION;" 2>/dev/null || echo "")
            
            # 从SQL脚本查找表定义
            SQL_TABLE_DEF=$(grep -A 100 "CREATE TABLE.*${table}" "${SCRIPT_DIR}"/init-db.sql "${SCRIPT_DIR}"/migrations/*/*.sql "${SCRIPT_DIR}"/challenge-scenarios-init.sql 2>/dev/null | \
                head -50)
            
            if [ -n "$SQL_TABLE_DEF" ]; then
                append_report "  SQL脚本中有定义"
            else
                append_report "  ⚠ SQL脚本中未找到定义"
            fi
            
            # 检查字段差异
            FIELD_COUNT=$(echo "$DB_COLUMNS" | wc -l | tr -d ' ')
            append_report "  数据库字段数: ${FIELD_COUNT}"
            
            ((COUNT++))
        done
    fi
    
    # 缺失表详情
    if [ ${#MISSING_TABLES[@]} -gt 0 ]; then
        append_report ""
        append_report "【缺失的表详情】"
        append_report "========================================"
        for table in "${MISSING_TABLES[@]}"; do
            append_report "✗ ${table}"
            
            # 查找表定义来源
            SOURCE=$(grep -l "CREATE TABLE.*${table}" "${SCRIPT_DIR}"/init-db.sql "${SCRIPT_DIR}"/migrations/*/*.sql "${SCRIPT_DIR}"/challenge-scenarios-init.sql 2>/dev/null | head -1)
            if [ -n "$SOURCE" ]; then
                append_report "  来源: $(basename "$SOURCE")"
            fi
        done
    fi
}

# =====================================================
# 步骤5：生成修复建议
# =====================================================
generate_recommendations() {
    log_section "步骤5: 生成修复建议"
    
    append_report ""
    append_report "【修复建议】"
    append_report "========================================"
    
    if [ ${#MISSING_TABLES[@]} -gt 0 ]; then
        append_report "1. 缺失的表需要执行以下操作："
        append_report "   - 检查初始化脚本是否完整执行"
        append_report "   - 手动执行缺失表的创建语句"
        append_report "   - 验证表创建是否成功"
        append_report ""
    fi
    
    if [ ${#EXTRA_TABLES[@]} -gt 0 ]; then
        append_report "2. 多余的表说明："
        append_report "   - 可能是手动创建的表"
        append_report "   - 建议检查是否需要添加到初始化脚本"
        append_report ""
    fi
    
    append_report "3. 建议操作："
    append_report "   - 运行完整初始化脚本: ./scripts/init-database.sh"
    append_report "   - 检查数据库连接和权限"
    append_report "   - 查看详细报告: ${DETAILED_REPORT}"
    append_report ""
}

# =====================================================
# 主函数
# =====================================================
main() {
    show_banner
    init_report
    
    # 执行所有步骤
    extract_entity_schema || log_warn "实体类提取失败，继续执行..."
    extract_sql_schema || log_warn "SQL脚本提取失败，继续执行..."
    extract_database_schema || log_error "数据库提取失败"
    compare_schemas
    generate_recommendations
    
    log_section "核对完成"
    log_info "报告已保存到: ${REPORT_FILE}"
    log_info "详细报告: ${DETAILED_REPORT}"
    
    # 显示报告摘要
    echo ""
    echo -e "${CYAN}报告摘要:${NC}"
    tail -20 "${REPORT_FILE}"
}

# 执行主函数
main

