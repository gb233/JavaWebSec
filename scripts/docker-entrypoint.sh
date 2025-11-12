#!/bin/bash

# ========================================
# Java Web安全教学系统 - Docker启动脚本
# ========================================

set -e

# 颜色输出函数
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# 打印启动信息
print_banner() {
    echo -e "${BLUE}"
    echo "=========================================="
    echo " Java Web安全教学系统"
    echo " Docker容器启动中..."
    echo "=========================================="
    echo -e "${NC}"
}

# 检查环境变量
check_environment() {
    log_info "检查环境变量..."
    
    # 设置默认值
    export MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD:-root}
    export MYSQL_DATABASE=${MYSQL_DATABASE:-javaweb_security}
    export MYSQL_USER=${MYSQL_USER:-security_user}
    export MYSQL_PASSWORD=${MYSQL_PASSWORD:-security_pass}
    export JAVA_OPTS=${JAVA_OPTS:-"-Xms512m -Xmx1024m"}
    export SERVER_PORT=${SERVER_PORT:-8080}
    
    log_info "环境变量设置完成"
}

# 初始化MySQL
init_mysql() {
    log_info "初始化MySQL数据库..."
    
    # 启动MySQL服务
    service mysql start
    
    # 等待MySQL启动
    log_info "等待MySQL服务启动..."
    while ! mysqladmin ping -h"localhost" --silent; do
        sleep 1
    done
    
    # 创建数据库和用户
    mysql -u root -p"${MYSQL_ROOT_PASSWORD}" <<-EOSQL
        CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
        CREATE USER IF NOT EXISTS '${MYSQL_USER}'@'%' IDENTIFIED BY '${MYSQL_PASSWORD}';
        GRANT ALL PRIVILEGES ON ${MYSQL_DATABASE}.* TO '${MYSQL_USER}'@'%';
        FLUSH PRIVILEGES;
EOSQL
    
    # 导入初始化数据
    if [ -f "/docker-entrypoint-initdb.d/init-db.sql" ]; then
        log_info "导入数据库初始化脚本..."
        mysql -u root -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < /docker-entrypoint-initdb.d/init-db.sql
        log_info "数据库初始化完成"
    else
        log_warn "未找到数据库初始化脚本"
    fi
}

# 等待MySQL就绪
wait_for_mysql() {
    log_info "等待MySQL数据库就绪..."
    
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if mysql -h localhost -u "${MYSQL_USER}" -p"${MYSQL_PASSWORD}" -e "SELECT 1" >/dev/null 2>&1; then
            log_info "MySQL数据库连接成功"
            return 0
        fi
        
        log_debug "MySQL连接尝试 $attempt/$max_attempts 失败，等待重试..."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    log_error "MySQL数据库连接超时"
    return 1
}

# 启动应用
start_application() {
    log_info "启动Java Web安全教学系统..."
    
    # 设置JVM参数
    export JAVA_OPTS="${JAVA_OPTS} -Dspring.profiles.active=docker"
    export JAVA_OPTS="${JAVA_OPTS} -Dspring.datasource.url=jdbc:mysql://localhost:3306/${MYSQL_DATABASE}?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai"
    export JAVA_OPTS="${JAVA_OPTS} -Dspring.datasource.username=${MYSQL_USER}"
    export JAVA_OPTS="${JAVA_OPTS} -Dspring.datasource.password=${MYSQL_PASSWORD}"
    export JAVA_OPTS="${JAVA_OPTS} -Dserver.port=${SERVER_PORT}"
    export JAVA_OPTS="${JAVA_OPTS} -Dlogging.file.name=/var/log/security-teaching-system/application.log"
    
    # 启动应用
    log_info "使用以下JVM参数启动应用: ${JAVA_OPTS}"
    exec java ${JAVA_OPTS} -jar /app/app.jar
}

# 信号处理
handle_signal() {
    log_info "收到停止信号，正在关闭服务..."
    
    # 停止Java应用
    if [ ! -z "$APP_PID" ]; then
        kill -TERM "$APP_PID" 2>/dev/null || true
        wait "$APP_PID" 2>/dev/null || true
    fi
    
    # 停止MySQL
    service mysql stop || true
    
    log_info "服务已停止"
    exit 0
}

# 设置信号陷阱
trap 'handle_signal' TERM INT

# 主函数
main() {
    print_banner
    check_environment
    
    # 如果是root用户，切换到MySQL初始化
    if [ "$(id -u)" = "0" ]; then
        log_info "以root权限运行，初始化MySQL..."
        init_mysql
        
        # 切换到应用用户继续执行
        log_info "切换到应用用户..."
        exec gosu appuser "$0" "$@"
    fi
    
    # 等待MySQL就绪
    wait_for_mysql
    
    # 启动应用
    start_application &
    APP_PID=$!
    
    # 等待应用进程
    wait "$APP_PID"
}

# 检查是否为直接执行
if [ "${BASH_SOURCE[0]}" = "${0}" ]; then
    main "$@"
fi

