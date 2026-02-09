#!/bin/bash

# =====================================================
# 部署前数据库结构核对脚本
# =====================================================
# 用途：在部署前执行数据库结构核对，确保数据库结构正确
# 使用方法：./scripts/pre-deployment-check.sh [选项]
# =====================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

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
REPORT_DIR="${SCRIPT_DIR}/schema-verification-reports"

# 解析命令行参数
SKIP_VERIFY=false
AUTO_FIX=false

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
        --skip-verify)
            SKIP_VERIFY=true
            shift
            ;;
        --auto-fix)
            AUTO_FIX=true
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
            echo "  --skip-verify       跳过核对（不推荐）"
            echo "  --auto-fix          自动生成修复脚本"
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

# 显示横幅
show_banner() {
    echo -e "${CYAN}"
    cat << "EOF"
========================================
  部署前数据库结构核对
  Pre-Deployment Schema Verification
========================================
EOF
    echo -e "${NC}"
}

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
test_database_connection() {
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
}

# 执行数据库结构核对
verify_schema() {
    log_section "执行数据库结构核对"
    
    if [ "$SKIP_VERIFY" = true ]; then
        log_warn "跳过数据库结构核对（不推荐）"
        return 0
    fi
    
    log_step "运行核对脚本..."
    
    # 执行核对脚本
    export DB_HOST DB_PORT DB_USER DB_PASSWORD DB_NAME
    if ! ./scripts/verify-database-schema.sh \
        --host="${DB_HOST}" \
        --port="${DB_PORT}" \
        --user="${DB_USER}" \
        --password="${DB_PASSWORD}" \
        --database="${DB_NAME}" 2>&1 | tee /tmp/verify-output.log; then
        log_error "数据库结构核对失败"
        return 1
    fi
    
    # 检查核对结果
    REPORT_FILE=$(ls -t "${REPORT_DIR}"/schema_verification_*.txt 2>/dev/null | head -1)
    
    if [ -z "$REPORT_FILE" ]; then
        log_error "未找到核对报告"
        return 1
    fi
    
    log_info "核对报告: ${REPORT_FILE}"
    
    # 检查是否有缺失的表
    if grep -q "✗ 缺失的表" "$REPORT_FILE"; then
        MISSING_COUNT=$(grep -A 100 "✗ 缺失的表" "$REPORT_FILE" | grep -c "✗" || echo "0")
        if [ "$MISSING_COUNT" -gt 0 ]; then
            log_error "发现 ${MISSING_COUNT} 个缺失的表"
            echo ""
            echo "缺失的表列表："
            grep -A 100 "✗ 缺失的表" "$REPORT_FILE" | grep "✗" | head -10
            echo ""
            
            if [ "$AUTO_FIX" = true ]; then
                log_step "自动生成修复脚本..."
                ./scripts/generate-fix-script.sh
                log_info "修复脚本已生成，请检查并执行"
            else
                log_warn "建议执行以下操作："
                echo "  1. 运行修复脚本生成器: ./scripts/generate-fix-script.sh"
                echo "  2. 检查并执行生成的修复脚本"
                echo "  3. 重新运行核对验证"
            fi
            
            return 1
        fi
    fi
    
    log_info "✓ 数据库结构核对通过"
    return 0
}

# 检查关键表
check_critical_tables() {
    log_section "检查关键表"
    
    CRITICAL_TABLES=(
        "users"
        "user_profiles"
        "vulnerability_categories"
        "questions"
        "test_sessions"
    )
    
    MISSING_CRITICAL=0
    
    for table in "${CRITICAL_TABLES[@]}"; do
        if ! $MYSQL_CMD "${DB_NAME}" -e "SHOW TABLES LIKE '${table}';" 2>/dev/null | grep -q "${table}"; then
            log_error "✗ 关键表缺失: ${table}"
            ((MISSING_CRITICAL++))
        else
            log_info "✓ 关键表存在: ${table}"
        fi
    done
    
    if [ $MISSING_CRITICAL -gt 0 ]; then
        log_error "发现 ${MISSING_CRITICAL} 个关键表缺失，部署将被阻止"
        return 1
    fi
    
    log_info "✓ 所有关键表都存在"
    return 0
}

# 检查数据库版本
check_database_version() {
    log_section "检查数据库版本"
    
    MYSQL_VERSION=$($MYSQL_CMD -e "SELECT VERSION();" 2>/dev/null | tail -1)
    log_info "MySQL版本: ${MYSQL_VERSION}"
    
    # 检查是否支持utf8mb4
    if ! $MYSQL_CMD -e "SHOW VARIABLES LIKE 'character_set_server';" 2>/dev/null | grep -q "utf8mb4"; then
        log_warn "数据库字符集不是utf8mb4，建议修改"
    else
        log_info "✓ 字符集配置正确"
    fi
}

# 主函数
main() {
    show_banner
    
    log_info "数据库配置："
    echo "  主机: ${DB_HOST}"
    echo "  端口: ${DB_PORT}"
    echo "  用户: ${DB_USER}"
    echo "  数据库: ${DB_NAME}"
    echo ""
    
    # 执行检查
    test_database_connection
    check_database_version
    check_critical_tables || exit 1
    verify_schema || exit 1
    
    log_section "部署前检查完成"
    log_info "✓ 所有检查通过，可以继续部署"
    echo ""
    echo "下一步操作："
    echo "  1. 执行部署脚本: ./scripts/deploy.sh"
    echo "  2. 验证部署结果"
    echo ""
}

# 执行主函数
main



