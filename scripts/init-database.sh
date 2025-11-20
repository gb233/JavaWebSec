#!/bin/bash

# =====================================================
# Java Web安全教学系统 - 数据库完整初始化脚本
# =====================================================
# 用途：一键初始化所有数据库表和数据
# 使用方法：./scripts/init-database.sh
# 参数：支持环境变量或命令行参数
# =====================================================

# 注意：不使用 set -e，因为我们需要手动处理错误并继续执行
# set -e 会在任何命令失败时立即退出，但我们希望捕获错误并继续

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

# 显示横幅
show_banner() {
    echo -e "${CYAN}"
    cat << "EOF"
========================================
  Java Web安全教学系统
  数据库完整初始化脚本
========================================
EOF
    echo -e "${NC}"
}

# 配置（可通过环境变量或命令行参数设置）
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-}"
DB_NAME="${DB_NAME:-security_teaching_system}"

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
            echo ""
            echo "示例:"
            echo "  $0 --user=root --password=mypass"
            echo "  DB_PASSWORD=mypass $0"
            exit 0
            ;;
        *)
            log_error "未知参数: $1"
            echo "使用 --help 查看帮助"
            exit 1
            ;;
    esac
done

# 脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# 显示横幅
show_banner

# 检查MySQL命令
log_step "检查MySQL客户端..."
if ! command -v mysql &> /dev/null; then
    log_error "未找到mysql命令，请先安装MySQL客户端"
    echo ""
    echo "安装方法："
    echo "  Ubuntu/Debian: sudo apt-get install mysql-client"
    echo "  CentOS/RHEL:   sudo yum install mysql"
    echo "  macOS:         brew install mysql-client"
    exit 1
fi
log_info "MySQL客户端已安装"

# 提示输入密码（如果未设置）
if [ -z "$DB_PASSWORD" ]; then
    echo ""
    echo -e "${YELLOW}请输入MySQL密码（用户: ${DB_USER}）：${NC}"
    read -s DB_PASSWORD
    echo ""
fi

# MySQL连接参数
MYSQL_CMD="mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASSWORD}"

# 显示配置信息
log_info "数据库配置："
echo "  主机: ${DB_HOST}"
echo "  端口: ${DB_PORT}"
echo "  用户: ${DB_USER}"
echo "  数据库: ${DB_NAME}"
echo ""

# 测试数据库连接
log_step "测试数据库连接..."
if ! $MYSQL_CMD -e "SELECT 1;" &> /dev/null; then
    log_error "无法连接到MySQL数据库"
    echo ""
    echo "请检查："
    echo "  1. MySQL服务是否运行"
    echo "  2. 主机、端口、用户名、密码是否正确"
    echo "  3. 用户是否有足够权限"
    exit 1
fi
log_info "数据库连接成功"

# 创建数据库（如果不存在）
log_step "创建数据库（如果不存在）..."
$MYSQL_CMD -e "CREATE DATABASE IF NOT EXISTS \`${DB_NAME}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>&1
log_info "数据库准备完成"

# 设置字符集（修复MySQL 8.0+字符集冲突问题）
log_step "设置字符集和排序规则..."
$MYSQL_CMD "${DB_NAME}" -e "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci; SET collation_connection = utf8mb4_unicode_ci;" 2>&1

# =====================================================
# 步骤1：导入核心表结构
# =====================================================
log_step "[1/12] 导入核心表结构（init-db.sql）..."

# 获取导入前的表列表
TABLES_BEFORE=$($MYSQL_CMD "${DB_NAME}" -N -e "SELECT table_name FROM information_schema.tables WHERE table_schema = '${DB_NAME}';" 2>/dev/null || echo "")

# 验证SQL文件是否存在且可读
if [ ! -r "${SCRIPT_DIR}/init-db.sql" ]; then
    log_error "SQL文件不存在或不可读: ${SCRIPT_DIR}/init-db.sql"
    exit 1
fi

# 显示SQL文件大小（用于调试）
SQL_FILE_SIZE=$(wc -c < "${SCRIPT_DIR}/init-db.sql" 2>/dev/null || echo "0")
log_info "SQL文件大小: ${SQL_FILE_SIZE} 字节"

if [ -f "${SCRIPT_DIR}/init-db.sql" ]; then
    # 执行导入（改进错误处理）
    log_info "正在导入 ${SCRIPT_DIR}/init-db.sql..."
    
    # 使用临时文件捕获输出
    TEMP_OUTPUT=$(mktemp /tmp/init-db-output.XXXXXX 2>/dev/null || echo "/tmp/init-db-output.$$")
    TEMP_ERROR=$(mktemp /tmp/init-db-error.XXXXXX 2>/dev/null || echo "/tmp/init-db-error.$$")
    
    # 确保临时文件存在
    touch "$TEMP_OUTPUT" "$TEMP_ERROR"
    
    # 执行导入，分别捕获标准输出和错误输出
    # 使用 --force 选项继续执行即使有错误，这样可以捕获所有错误
    # 注意：--force 会继续执行即使遇到错误，这对于批量导入很重要
    # 注意：不使用 set -e，因为我们需要继续执行后续步骤，即使导入有错误
    $MYSQL_CMD "${DB_NAME}" --force < "${SCRIPT_DIR}/init-db.sql" > "$TEMP_OUTPUT" 2> "$TEMP_ERROR"
    IMPORT_EXIT_CODE=$?
    
    # 读取错误内容（忽略警告）
    ERROR_CONTENT=$(cat "$TEMP_ERROR" 2>/dev/null | grep -v "Warning" | grep -v "^$" || true)
    
    # 检查是否有真正的错误（不是警告）
    if [ -n "$ERROR_CONTENT" ]; then
        log_error "✗ 核心表结构导入时发现错误"
        echo ""
        echo "错误详情："
        echo "$ERROR_CONTENT"
        echo ""
        echo "标准输出（最后30行）："
        tail -30 "$TEMP_OUTPUT" 2>/dev/null || echo "无输出"
        echo ""
        echo "注意：脚本将继续执行，但请检查上述错误"
        echo ""
    fi
    
    # 即使有错误也继续执行（因为使用了--force），但记录退出代码
    if [ $IMPORT_EXIT_CODE -ne 0 ] && [ -z "$ERROR_CONTENT" ]; then
        log_warn "导入退出代码: $IMPORT_EXIT_CODE（可能有问题）"
        echo "标准输出（最后30行）："
        tail -30 "$TEMP_OUTPUT" 2>/dev/null || echo "无输出"
        echo ""
    fi
    
    # 清理临时文件
    rm -f "$TEMP_OUTPUT" "$TEMP_ERROR"
    
    # 获取导入后的表列表
    TABLES_AFTER=$($MYSQL_CMD "${DB_NAME}" -N -e "SELECT table_name FROM information_schema.tables WHERE table_schema = '${DB_NAME}';" 2>/dev/null || echo "")
    
    # 计算新增的表
    NEW_TABLES=$(comm -13 <(echo "$TABLES_BEFORE" | sort) <(echo "$TABLES_AFTER" | sort))
    
    if [ -n "$NEW_TABLES" ]; then
        TABLE_COUNT=$(echo "$NEW_TABLES" | grep -v '^$' | wc -l | tr -d ' ')
        log_info "✓ 核心表结构导入完成"
        echo -e "${GREEN}  新增表（${TABLE_COUNT}个）：${NC}"
        while IFS= read -r table; do
            if [ -n "$table" ]; then
                echo -e "    ${GREEN}✓${NC} ${table}"
            fi
        done <<< "$NEW_TABLES"
    else
        log_info "✓ 核心表结构导入完成（表已存在）"
    fi
else
    log_error "未找到文件: ${SCRIPT_DIR}/init-db.sql"
    exit 1
fi

# =====================================================
# 步骤2：导入迁移脚本（如果存在）
# =====================================================
MIGRATIONS_DIR="${SCRIPT_DIR}/migrations"
if [ -d "$MIGRATIONS_DIR" ]; then
    log_step "[2/12] 检查并执行迁移脚本..."
    
    # 确保 schema_version 表存在（迁移脚本依赖此表）
    # 使用更可靠的检查方式
    SCHEMA_VERSION_EXISTS=$($MYSQL_CMD "${DB_NAME}" -N -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '${DB_NAME}' AND table_name = 'schema_version';" 2>/dev/null || echo "0")
    if [ "$SCHEMA_VERSION_EXISTS" != "1" ]; then
        log_info "  创建 schema_version 表（迁移脚本依赖此表）..."
        TEMP_SCHEMA_OUTPUT=$(mktemp /tmp/schema-output.XXXXXX 2>/dev/null || echo "/tmp/schema-output.$$")
        TEMP_SCHEMA_ERROR=$(mktemp /tmp/schema-error.XXXXXX 2>/dev/null || echo "/tmp/schema-error.$$")
        touch "$TEMP_SCHEMA_OUTPUT" "$TEMP_SCHEMA_ERROR"
        
        $MYSQL_CMD "${DB_NAME}" -e "CREATE TABLE IF NOT EXISTS schema_version (
            id BIGINT PRIMARY KEY AUTO_INCREMENT,
            version VARCHAR(20) NOT NULL UNIQUE COMMENT '版本号，如V1.0.0',
            description VARCHAR(200) COMMENT '版本描述',
            script_name VARCHAR(200) NOT NULL COMMENT '脚本文件名',
            execution_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',
            execution_status ENUM('SUCCESS', 'FAILED') DEFAULT 'SUCCESS' COMMENT '执行状态',
            execution_log TEXT COMMENT '执行日志',
            INDEX idx_version (version),
            INDEX idx_execution_time (execution_time)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据库版本管理表';" > "$TEMP_SCHEMA_OUTPUT" 2> "$TEMP_SCHEMA_ERROR"
        
        SCHEMA_ERROR=$(cat "$TEMP_SCHEMA_ERROR" 2>/dev/null | grep -v "Warning" | grep -v "^$" || true)
        if [ -n "$SCHEMA_ERROR" ]; then
            log_warn "  创建 schema_version 表时发现错误:"
            echo "$SCHEMA_ERROR" | sed 's/^/      /'
        else
            log_info "  schema_version 表已创建"
        fi
        
        rm -f "$TEMP_SCHEMA_OUTPUT" "$TEMP_SCHEMA_ERROR"
    else
        log_info "  schema_version 表已存在"
    fi
    
    # 获取执行迁移前的表列表
    TABLES_BEFORE_MIGRATION=$($MYSQL_CMD "${DB_NAME}" -N -e "SELECT table_name FROM information_schema.tables WHERE table_schema = '${DB_NAME}';" 2>/dev/null || echo "")
    
    # 查找所有迁移脚本并按版本排序
    MIGRATION_FILES=$(find "${MIGRATIONS_DIR}" -name "V*.sql" -type f | sort)
    
    if [ -n "$MIGRATION_FILES" ]; then
        MIGRATION_COUNT=0
        MIGRATION_TABLES=()
        
        while IFS= read -r migration_file; do
            migration_name=$(basename "$migration_file")
            log_info "  执行: ${migration_name}..."
            
            # 获取执行前的表列表
            TABLES_BEFORE_THIS=$($MYSQL_CMD "${DB_NAME}" -N -e "SELECT table_name FROM information_schema.tables WHERE table_schema = '${DB_NAME}';" 2>/dev/null || echo "")
            
            # 使用临时文件捕获输出和错误
            TEMP_MIGRATION_OUTPUT=$(mktemp /tmp/migration-output.XXXXXX 2>/dev/null || echo "/tmp/migration-output.$$")
            TEMP_MIGRATION_ERROR=$(mktemp /tmp/migration-error.XXXXXX 2>/dev/null || echo "/tmp/migration-error.$$")
            touch "$TEMP_MIGRATION_OUTPUT" "$TEMP_MIGRATION_ERROR"
            
            # 执行迁移脚本
            # 使用 --force 选项继续执行即使有错误，这对于批量导入很重要
            # 注意：不使用 set -e，因为我们需要继续执行所有迁移脚本，即使某个脚本失败
            $MYSQL_CMD "${DB_NAME}" --force < "$migration_file" > "$TEMP_MIGRATION_OUTPUT" 2> "$TEMP_MIGRATION_ERROR"
            IMPORT_EXIT_CODE=$?
            
            # 读取错误内容（忽略警告）
            ERROR_CONTENT=$(cat "$TEMP_MIGRATION_ERROR" 2>/dev/null | grep -v "Warning" | grep -v "^$" || true)
            
            # 获取执行后的表列表
            TABLES_AFTER_THIS=$($MYSQL_CMD "${DB_NAME}" -N -e "SELECT table_name FROM information_schema.tables WHERE table_schema = '${DB_NAME}';" 2>/dev/null || echo "")
            
            # 计算新增的表
            NEW_TABLES_THIS=$(comm -13 <(echo "$TABLES_BEFORE_THIS" | sort) <(echo "$TABLES_AFTER_THIS" | sort))
            
            # 显示错误内容（如果有）
            if [ -n "$ERROR_CONTENT" ]; then
                log_warn "    ⚠ 执行时发现错误（但可能已部分执行）"
                echo "      错误详情:"
                echo "$ERROR_CONTENT" | sed 's/^/      /' | head -20
                echo "      标准输出（最后10行）:"
                tail -10 "$TEMP_MIGRATION_OUTPUT" 2>/dev/null | sed 's/^/      /' || echo "      无输出"
            fi
            
            # 判断执行结果
            if [ -n "$NEW_TABLES_THIS" ]; then
                log_info "    ✓ 成功"
                while IFS= read -r table; do
                    if [ -n "$table" ]; then
                        echo -e "      ${GREEN}✓${NC} 新增表: ${table}"
                        MIGRATION_TABLES+=("$table")
                    fi
                done <<< "$NEW_TABLES_THIS"
                ((MIGRATION_COUNT++))
            elif [ $IMPORT_EXIT_CODE -eq 0 ] && [ -z "$ERROR_CONTENT" ]; then
                log_info "    ✓ 成功（无新表，可能已执行过）"
                ((MIGRATION_COUNT++))
            elif [ -n "$ERROR_CONTENT" ]; then
                # 有错误但可能已部分执行，继续执行下一个脚本
                log_warn "    ⚠ 执行时发现错误，但继续执行后续脚本"
                echo "      请检查上述错误信息"
                ((MIGRATION_COUNT++))
            else
                log_warn "    ⚠ 可能执行失败或已执行过"
                ((MIGRATION_COUNT++))
            fi
            
            # 清理临时文件
            rm -f "$TEMP_MIGRATION_OUTPUT" "$TEMP_MIGRATION_ERROR"
        done <<< "$MIGRATION_FILES"
        
        if [ $MIGRATION_COUNT -gt 0 ]; then
            log_info "✓ 迁移脚本执行完成（${MIGRATION_COUNT}个）"
            if [ ${#MIGRATION_TABLES[@]} -gt 0 ]; then
                log_info "  迁移新增表: ${#MIGRATION_TABLES[@]}个"
            fi
        else
            log_info "✓ 所有迁移脚本已是最新版本"
        fi
    else
        log_info "未找到迁移脚本，跳过"
    fi
else
    log_info "[2/12] 迁移目录不存在，跳过"
fi

# =====================================================
# 步骤3-12：导入题目数据（A01-A10，每个100题）
# =====================================================
QUESTION_FILES=(
    "a01_complete_questions.sql"
    "a02_complete_questions.sql"
    "a03_complete_questions.sql"
    "a04_complete_questions.sql"
    "a05_complete_questions.sql"
    "a06_complete_questions.sql"
    "a07_complete_questions.sql"
    "a08_complete_questions.sql"
    "a09_complete_questions.sql"
    "a10_complete_questions.sql"
)

STEP_NUM=3
for question_file in "${QUESTION_FILES[@]}"; do
    category=$(echo "$question_file" | sed 's/_complete_questions.sql//' | tr '[:lower:]' '[:upper:]')
    log_step "[${STEP_NUM}/12] 导入${category}题目数据（100题）..."
    
    if [ -f "${SCRIPT_DIR}/${question_file}" ]; then
        TEMP_QUESTION_OUTPUT=$(mktemp /tmp/question-output.XXXXXX 2>/dev/null || echo "/tmp/question-output.$$")
        TEMP_QUESTION_ERROR=$(mktemp /tmp/question-error.XXXXXX 2>/dev/null || echo "/tmp/question-error.$$")
        touch "$TEMP_QUESTION_OUTPUT" "$TEMP_QUESTION_ERROR"
        
        # 注意：不使用 set -e，因为我们需要继续执行后续题目导入，即使某个导入失败
        $MYSQL_CMD "${DB_NAME}" --force < "${SCRIPT_DIR}/${question_file}" > "$TEMP_QUESTION_OUTPUT" 2> "$TEMP_QUESTION_ERROR"
        QUESTION_EXIT_CODE=$?
        
        ERROR_CONTENT=$(cat "$TEMP_QUESTION_ERROR" 2>/dev/null | grep -v "Warning" | grep -v "^$" || true)
        
        if [ -n "$ERROR_CONTENT" ]; then
            log_warn "⚠ ${category}题目导入时发现错误"
            echo "$ERROR_CONTENT" | sed 's/^/      /'
        else
            log_info "✓ ${category}题目导入完成"
        fi
        
        rm -f "$TEMP_QUESTION_OUTPUT" "$TEMP_QUESTION_ERROR"
    else
        log_warn "⚠ 未找到文件: ${SCRIPT_DIR}/${question_file}，跳过"
    fi
    
    ((STEP_NUM++))
done

# =====================================================
# 步骤13：导入挑战场景数据
# =====================================================
log_step "[13/13] 导入挑战场景数据..."
if [ -f "${SCRIPT_DIR}/challenge-scenarios-init.sql" ]; then
    TEMP_CHALLENGE_OUTPUT=$(mktemp /tmp/challenge-output.XXXXXX 2>/dev/null || echo "/tmp/challenge-output.$$")
    TEMP_CHALLENGE_ERROR=$(mktemp /tmp/challenge-error.XXXXXX 2>/dev/null || echo "/tmp/challenge-error.$$")
    touch "$TEMP_CHALLENGE_OUTPUT" "$TEMP_CHALLENGE_ERROR"
    
    # 注意：不使用 set -e，因为我们需要继续执行后续步骤，即使导入有错误
    $MYSQL_CMD "${DB_NAME}" --force < "${SCRIPT_DIR}/challenge-scenarios-init.sql" > "$TEMP_CHALLENGE_OUTPUT" 2> "$TEMP_CHALLENGE_ERROR"
    CHALLENGE_EXIT_CODE=$?
    
    ERROR_CONTENT=$(cat "$TEMP_CHALLENGE_ERROR" 2>/dev/null | grep -v "Warning" | grep -v "^$" || true)
    
    if [ -n "$ERROR_CONTENT" ]; then
        log_warn "⚠ 挑战场景数据导入时发现错误"
        echo "$ERROR_CONTENT" | sed 's/^/      /'
    else
        log_info "✓ 挑战场景数据导入完成"
    fi
    
    rm -f "$TEMP_CHALLENGE_OUTPUT" "$TEMP_CHALLENGE_ERROR"
else
    log_warn "⚠ 未找到文件: ${SCRIPT_DIR}/challenge-scenarios-init.sql，跳过"
fi

# =====================================================
# 验证数据导入
# =====================================================
echo ""
log_step "验证数据导入..."

# 检查题目总数
QUESTION_COUNT=$($MYSQL_CMD "${DB_NAME}" -N -e "SELECT COUNT(*) FROM vulnerability_questions;" 2>/dev/null || echo "0")
log_info "题目总数: ${QUESTION_COUNT}"

if [ "$QUESTION_COUNT" -eq 1000 ]; then
    log_info "✓ 题目数据完整（1000题）"
else
    log_warn "⚠ 题目数量不正确，期望1000题，实际${QUESTION_COUNT}题"
fi

# 检查各分类题目数量
log_info "各分类题目数量："
$MYSQL_CMD "${DB_NAME}" -e "SELECT category_code, COUNT(*) as count FROM vulnerability_questions GROUP BY category_code ORDER BY category_code;" 2>/dev/null || true

# 检查表数量
TABLE_COUNT=$($MYSQL_CMD "${DB_NAME}" -N -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '${DB_NAME}';" 2>/dev/null || echo "0")
log_info "数据表总数: ${TABLE_COUNT}"

# 检查挑战场景数量
CHALLENGE_COUNT=$($MYSQL_CMD "${DB_NAME}" -N -e "SELECT COUNT(*) FROM challenge_scenarios;" 2>/dev/null || echo "0")
log_info "挑战场景数量: ${CHALLENGE_COUNT}"

# 获取所有表列表（可视化显示）
echo ""
log_step "数据库表结构总览..."
ALL_TABLES=$($MYSQL_CMD "${DB_NAME}" -N -e "SELECT table_name FROM information_schema.tables WHERE table_schema = '${DB_NAME}' ORDER BY table_name;" 2>/dev/null || echo "")

if [ -n "$ALL_TABLES" ]; then
    TABLE_COUNT=$(echo "$ALL_TABLES" | wc -l | tr -d ' ')
    echo -e "${GREEN}成功导入 ${TABLE_COUNT} 个数据表：${NC}"
    echo ""
    
    # 分类显示表
    echo -e "${CYAN}【核心业务表】${NC}"
    echo "$ALL_TABLES" | grep -E "^(users|user_profiles|user_notes|user_collections)$" | while read table; do
        echo -e "  ${GREEN}✓${NC} $table"
    done
    
    echo ""
    echo -e "${CYAN}【漏洞相关表】${NC}"
    echo "$ALL_TABLES" | grep -E "^(vulnerability_|learning_progress)$" | while read table; do
        echo -e "  ${GREEN}✓${NC} $table"
    done
    
    echo ""
    echo -e "${CYAN}【测试相关表】${NC}"
    echo "$ALL_TABLES" | grep -E "^(test_|vulnerability_question)$" | while read table; do
        echo -e "  ${GREEN}✓${NC} $table"
    done
    
    echo ""
    echo -e "${CYAN}【挑战相关表】${NC}"
    echo "$ALL_TABLES" | grep -E "^(challenge_)$" | while read table; do
        echo -e "  ${GREEN}✓${NC} $table"
    done
    
    echo ""
    echo -e "${CYAN}【系统管理表】${NC}"
    echo "$ALL_TABLES" | grep -E "^(system_|operation_|attack_|error_|file_|user_badges|schema_)$" | while read table; do
        echo -e "  ${GREEN}✓${NC} $table"
    done
    
    echo ""
    echo -e "${CYAN}【其他表】${NC}"
    echo "$ALL_TABLES" | grep -v -E "^(users|user_|vulnerability_|learning_|test_|challenge_|system_|operation_|attack_|error_|file_|schema_)" | while read table; do
        echo -e "  ${GREEN}✓${NC} $table"
    done
else
    log_warn "未能获取表列表"
fi

# 完成提示
echo ""
echo -e "${CYAN}========================================"${NC}
echo -e "${GREEN}数据库初始化完成！${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""
log_info "数据库名称: ${DB_NAME}"
log_info "数据表数量: ${TABLE_COUNT}"
log_info "题目数量: ${QUESTION_COUNT}"
log_info "挑战场景: ${CHALLENGE_COUNT}"
echo ""
log_info "下一步："
echo "  1. 检查应用配置文件中的数据库连接信息"
echo "  2. 启动应用：cd src/backend && mvn spring-boot:run"
echo "  3. 访问应用：http://localhost:8080"
echo ""
