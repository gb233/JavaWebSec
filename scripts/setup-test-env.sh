#!/bin/bash

# ========================================
# 测试环境设置脚本
# ========================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
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

log_debug() {
    echo -e "${BLUE}[DEBUG]${NC} $1"
}

log_step() {
    echo -e "${PURPLE}[STEP]${NC} $1"
}

# 显示横幅
show_banner() {
    echo -e "${CYAN}"
    cat << "EOF"
========================================
    测试环境设置
    Java Web安全教学系统
========================================
EOF
    echo -e "${NC}"
}

# 检查环境
check_environment() {
    log_step "检查测试环境..."
    
    # 检查必要的命令
    local commands=("docker" "docker-compose" "mysql")
    for cmd in "${commands[@]}"; do
        if ! command -v "$cmd" &> /dev/null; then
            log_error "命令不存在: $cmd"
            exit 1
        fi
    done
    
    # 检查Docker服务
    if ! docker info &> /dev/null; then
        log_error "Docker服务未运行"
        exit 1
    fi
    
    log_info "环境检查通过"
}

# 设置测试环境变量
setup_test_env() {
    log_step "设置测试环境变量..."
    
    # 创建测试环境配置文件
    cat > .env.test << EOF
# 测试环境配置
DEPLOY_ENV=test
COMPOSE_PROJECT_NAME=security-teaching-test

# 数据库配置
MYSQL_ROOT_PASSWORD=test_root_pass
MYSQL_DATABASE=javaweb_security_test
MYSQL_USER=test_user
MYSQL_PASSWORD=test_pass

# 应用配置
JWT_SECRET=testSecretKeyForJavaWebSecurityTeachingSystem2024
SERVER_PORT=8081
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8081

# 测试特定配置
SPRING_PROFILES_ACTIVE=test
LOG_LEVEL=DEBUG
ENABLE_TEST_DATA=true

# 外部端口（避免与生产环境冲突）
EXTERNAL_PORT=8081
MYSQL_EXTERNAL_PORT=3307
EOF
    
    # 加载环境变量
    set -a
    source .env.test
    set +a
    
    log_info "测试环境变量设置完成"
}

# 创建测试数据目录
create_test_directories() {
    log_step "创建测试数据目录..."
    
    local dirs=(
        "test-data/mysql"
        "test-data/logs"
        "test-data/uploads"
        "test-data/backups"
        "test-data/reports"
    )
    
    for dir in "${dirs[@]}"; do
        mkdir -p "$dir"
        log_debug "创建目录: $dir"
    done
    
    log_info "测试目录创建完成"
}

# 启动测试数据库
start_test_database() {
    log_step "启动测试数据库..."
    
    # 创建临时的docker-compose文件用于测试数据库
    cat > docker-compose.test.yml << EOF
version: '3.8'

services:
  mysql-test:
    image: mysql:8.0
    container_name: security-teaching-mysql-test
    restart: unless-stopped
    ports:
      - "${MYSQL_EXTERNAL_PORT}:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
      - MYSQL_DATABASE=${MYSQL_DATABASE}
      - MYSQL_USER=${MYSQL_USER}
      - MYSQL_PASSWORD=${MYSQL_PASSWORD}
      - MYSQL_CHARACTER_SET_SERVER=utf8mb4
      - MYSQL_COLLATION_SERVER=utf8mb4_unicode_ci
    volumes:
      - ./test-data/mysql:/var/lib/mysql
      - ./scripts/init-db.sql:/docker-entrypoint-initdb.d/01-init-db.sql:ro
      - ./scripts/init-test-data.sql:/docker-entrypoint-initdb.d/02-init-test-data.sql:ro
    networks:
      - test-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 10
      start_period: 60s

networks:
  test-network:
    driver: bridge
EOF
    
    # 启动测试数据库
    docker-compose -f docker-compose.test.yml up -d mysql-test
    
    log_info "测试数据库启动中..."
}

# 等待数据库就绪
wait_for_database() {
    log_step "等待数据库就绪..."
    
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if docker-compose -f docker-compose.test.yml exec -T mysql-test \
            mysqladmin ping -h localhost -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" &> /dev/null; then
            log_info "数据库连接成功"
            return 0
        fi
        
        log_debug "等待数据库启动... ($attempt/$max_attempts)"
        sleep 3
        attempt=$((attempt + 1))
    done
    
    log_error "数据库启动超时"
    return 1
}

# 验证测试数据
verify_test_data() {
    log_step "验证测试数据..."
    
    local mysql_cmd="docker-compose -f docker-compose.test.yml exec -T mysql-test mysql -h localhost -u $MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE"
    
    # 检查用户数据
    local user_count=$($mysql_cmd -e "SELECT COUNT(*) FROM users;" 2>/dev/null | tail -n 1)
    log_info "用户数据: $user_count 条记录"
    
    # 检查漏洞内容数据
    local vuln_count=$($mysql_cmd -e "SELECT COUNT(*) FROM vulnerability_content;" 2>/dev/null | tail -n 1)
    log_info "漏洞内容: $vuln_count 条记录"
    
    # 检查测试题目数据
    local question_count=$($mysql_cmd -e "SELECT COUNT(*) FROM test_questions;" 2>/dev/null | tail -n 1)
    log_info "测试题目: $question_count 条记录"
    
    # 检查挑战任务数据
    local challenge_count=$($mysql_cmd -e "SELECT COUNT(*) FROM challenge_tasks;" 2>/dev/null | tail -n 1)
    log_info "挑战任务: $challenge_count 条记录"
    
    if [ "$user_count" -gt 0 ] && [ "$vuln_count" -gt 0 ] && [ "$question_count" -gt 0 ]; then
        log_info "测试数据验证通过"
    else
        log_error "测试数据验证失败"
        return 1
    fi
}

# 创建测试用户
create_test_users() {
    log_step "创建测试用户..."
    
    local mysql_cmd="docker-compose -f docker-compose.test.yml exec -T mysql-test mysql -h localhost -u $MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE"
    
    # 显示创建的测试用户
    log_info "测试用户列表:"
    $mysql_cmd -e "SELECT id, username, email, full_name, user_role FROM users ORDER BY id;" 2>/dev/null | while read line; do
        if [ ! -z "$line" ] && [ "$line" != "id	username	email	full_name	user_role" ]; then
            log_debug "  $line"
        fi
    done
    
    log_info "测试用户创建完成"
    log_info "默认密码: password123 (所有用户)"
}

# 生成测试报告
generate_test_report() {
    log_step "生成测试环境报告..."
    
    local report_file="test-data/reports/test_env_report_$(date +%Y%m%d_%H%M%S).html"
    
    cat > "$report_file" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>测试环境报告 - $(date +%Y-%m-%d)</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .header { background: #f0f0f0; padding: 10px; border-radius: 5px; }
        .section { margin: 20px 0; }
        .code { background: #f5f5f5; padding: 10px; border-radius: 3px; font-family: monospace; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Java Web安全教学系统 - 测试环境报告</h1>
        <p>生成时间: $(date)</p>
    </div>
    
    <div class="section">
        <h2>环境配置</h2>
        <table>
            <tr><th>配置项</th><th>值</th></tr>
            <tr><td>数据库端口</td><td>$MYSQL_EXTERNAL_PORT</td></tr>
            <tr><td>应用端口</td><td>$EXTERNAL_PORT</td></tr>
            <tr><td>数据库名</td><td>$MYSQL_DATABASE</td></tr>
            <tr><td>数据库用户</td><td>$MYSQL_USER</td></tr>
        </table>
    </div>
    
    <div class="section">
        <h2>连接信息</h2>
        <div class="code">
            数据库连接: mysql -h localhost -P $MYSQL_EXTERNAL_PORT -u $MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE<br>
            应用访问: http://localhost:$EXTERNAL_PORT<br>
        </div>
    </div>
    
    <div class="section">
        <h2>测试用户</h2>
        <table>
            <tr><th>用户名</th><th>邮箱</th><th>角色</th><th>密码</th></tr>
            <tr><td>admin</td><td>admin@javaweb-security.com</td><td>管理员</td><td>password123</td></tr>
            <tr><td>teacher</td><td>teacher@javaweb-security.com</td><td>教师</td><td>password123</td></tr>
            <tr><td>student1</td><td>student1@example.com</td><td>学生</td><td>password123</td></tr>
            <tr><td>student2</td><td>student2@example.com</td><td>学生</td><td>password123</td></tr>
        </table>
    </div>
    
    <div class="section">
        <h2>管理命令</h2>
        <div class="code">
            # 查看容器状态<br>
            docker-compose -f docker-compose.test.yml ps<br><br>
            
            # 查看日志<br>
            docker-compose -f docker-compose.test.yml logs -f<br><br>
            
            # 停止测试环境<br>
            docker-compose -f docker-compose.test.yml down<br><br>
            
            # 清理测试数据<br>
            docker-compose -f docker-compose.test.yml down -v<br>
        </div>
    </div>
</body>
</html>
EOF
    
    log_info "测试环境报告已生成: $report_file"
}

# 创建管理脚本
create_management_scripts() {
    log_step "创建测试环境管理脚本..."
    
    # 启动测试环境脚本
    cat > start-test.sh << 'EOF'
#!/bin/bash
echo "启动测试环境..."
docker-compose -f docker-compose.test.yml up -d
echo "测试环境启动完成"
echo "数据库端口: 3307"
echo "应用端口: 8081"
EOF
    
    # 停止测试环境脚本
    cat > stop-test.sh << 'EOF'
#!/bin/bash
echo "停止测试环境..."
docker-compose -f docker-compose.test.yml down
echo "测试环境停止完成"
EOF
    
    # 重置测试数据脚本
    cat > reset-test-data.sh << 'EOF'
#!/bin/bash
echo "重置测试数据..."
docker-compose -f docker-compose.test.yml down -v
rm -rf test-data/mysql/*
docker-compose -f docker-compose.test.yml up -d
echo "测试数据重置完成"
EOF
    
    # 连接测试数据库脚本
    cat > connect-test-db.sh << 'EOF'
#!/bin/bash
source .env.test
mysql -h localhost -P $MYSQL_EXTERNAL_PORT -u $MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE
EOF
    
    # 设置执行权限
    chmod +x start-test.sh stop-test.sh reset-test-data.sh connect-test-db.sh
    
    log_info "管理脚本创建完成"
}

# 运行基础测试
run_basic_tests() {
    log_step "运行基础测试..."
    
    local mysql_cmd="docker-compose -f docker-compose.test.yml exec -T mysql-test mysql -h localhost -u $MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE"
    
    # 测试数据库连接
    if $mysql_cmd -e "SELECT 1;" &> /dev/null; then
        log_info "✓ 数据库连接测试通过"
    else
        log_error "✗ 数据库连接测试失败"
        return 1
    fi
    
    # 测试用户认证
    if $mysql_cmd -e "SELECT COUNT(*) FROM users WHERE user_role = 'admin';" | grep -q "1"; then
        log_info "✓ 管理员用户测试通过"
    else
        log_error "✗ 管理员用户测试失败"
        return 1
    fi
    
    # 测试数据完整性
    local tables=("users" "vulnerability_categories" "vulnerability_content" "test_questions" "challenge_tasks")
    for table in "${tables[@]}"; do
        local count=$($mysql_cmd -e "SELECT COUNT(*) FROM $table;" 2>/dev/null | tail -n 1)
        if [ "$count" -gt 0 ]; then
            log_info "✓ 表 $table 测试通过 ($count 条记录)"
        else
            log_warn "⚠ 表 $table 为空"
        fi
    done
    
    log_info "基础测试完成"
}

# 清理测试环境
cleanup_test_env() {
    log_step "清理测试环境..."
    
    # 停止容器
    docker-compose -f docker-compose.test.yml down -v 2>/dev/null || true
    
    # 清理临时文件
    rm -f docker-compose.test.yml
    
    # 清理测试数据（可选）
    if [ "$CLEAN_DATA" = "true" ]; then
        rm -rf test-data/
        log_info "测试数据已清理"
    fi
    
    log_info "测试环境清理完成"
}

# 主函数
main() {
    # 解析命令行参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            --clean)
                CLEAN_DATA=true
                shift
                ;;
            --no-verify)
                SKIP_VERIFY=true
                shift
                ;;
            --port)
                MYSQL_EXTERNAL_PORT="$2"
                EXTERNAL_PORT="$((MYSQL_EXTERNAL_PORT + 1))"
                shift 2
                ;;
            --cleanup)
                cleanup_test_env
                exit 0
                ;;
            --help)
                echo "用法: $0 [选项]"
                echo "选项:"
                echo "  --clean      清理现有测试数据"
                echo "  --no-verify  跳过数据验证"
                echo "  --port PORT  设置MySQL端口（默认3307）"
                echo "  --cleanup    清理测试环境"
                echo "  --help       显示帮助"
                exit 0
                ;;
            *)
                log_error "未知参数: $1"
                exit 1
                ;;
        esac
    done
    
    # 记录开始时间
    local start_time=$(date +%s)
    
    # 显示横幅
    show_banner
    
    # 执行设置步骤
    check_environment
    setup_test_env
    create_test_directories
    start_test_database
    wait_for_database
    
    if [ "$SKIP_VERIFY" != "true" ]; then
        verify_test_data
    fi
    
    create_test_users
    run_basic_tests
    create_management_scripts
    generate_test_report
    
    # 计算设置时间
    local end_time=$(date +%s)
    local setup_time=$((end_time - start_time))
    
    # 显示完成信息
    echo -e "${CYAN}========================================${NC}"
    echo -e "${CYAN}测试环境设置完成${NC}"
    echo -e "${CYAN}========================================${NC}"
    echo -e "数据库访问: mysql -h localhost -P $MYSQL_EXTERNAL_PORT -u $MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE"
    echo -e "应用端口: $EXTERNAL_PORT"
    echo -e "设置耗时: ${setup_time}秒"
    echo -e "${CYAN}========================================${NC}"
    
    log_info "使用以下命令管理测试环境:"
    echo -e "${GREEN}启动: ./start-test.sh${NC}"
    echo -e "${GREEN}停止: ./stop-test.sh${NC}"
    echo -e "${GREEN}重置: ./reset-test-data.sh${NC}"
    echo -e "${GREEN}连接: ./connect-test-db.sh${NC}"
}

# 错误处理
error_handler() {
    log_error "测试环境设置过程中发生错误"
    cleanup_test_env
    exit 1
}

trap error_handler ERR

# 检查是否为直接执行
if [ "${BASH_SOURCE[0]}" = "${0}" ]; then
    main "$@"
fi

