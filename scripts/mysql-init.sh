#!/bin/bash

# ========================================
# MySQL初始化脚本
# ========================================

set -e

# 颜色输出
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[MySQL-INIT]${NC} $1"
}

log_error() {
    echo -e "${RED}[MySQL-ERROR]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[MySQL-WARN]${NC} $1"
}

# MySQL配置
setup_mysql_config() {
    log_info "配置MySQL服务器..."
    
    # 创建MySQL配置文件
    cat > /etc/mysql/mysql.conf.d/custom.cnf << EOF
[mysqld]
# 基础配置
bind-address = 0.0.0.0
port = 3306
socket = /var/run/mysqld/mysqld.sock
pid-file = /var/run/mysqld/mysqld.pid
datadir = /var/lib/mysql
tmpdir = /tmp

# 字符集设置
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
init-connect = 'SET NAMES utf8mb4'

# InnoDB设置
default-storage-engine = INNODB
innodb_buffer_pool_size = 256M
innodb_log_file_size = 64M
innodb_flush_log_at_trx_commit = 1
innodb_lock_wait_timeout = 50

# 连接设置
max_connections = 200
max_user_connections = 100
max_connect_errors = 100000

# 查询缓存
query_cache_type = 1
query_cache_size = 32M

# 日志设置
general_log = 0
slow_query_log = 1
slow_query_log_file = /var/log/mysql/mysql-slow.log
long_query_time = 2

# 安全设置
skip-name-resolve
sql_mode = STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO

[mysql]
default-character-set = utf8mb4

[client]
default-character-set = utf8mb4
EOF

    log_info "MySQL配置完成"
}

# 初始化MySQL数据目录
init_mysql_data() {
    log_info "初始化MySQL数据目录..."
    
    if [ ! -d "/var/lib/mysql/mysql" ]; then
        log_info "MySQL数据目录不存在，开始初始化..."
        
        # 初始化数据目录
        mysqld --initialize-insecure --user=mysql --datadir=/var/lib/mysql
        
        log_info "MySQL数据目录初始化完成"
    else
        log_info "MySQL数据目录已存在，跳过初始化"
    fi
}

# 启动MySQL服务
start_mysql_service() {
    log_info "启动MySQL服务..."
    
    # 确保MySQL用户拥有数据目录
    chown -R mysql:mysql /var/lib/mysql
    chown -R mysql:mysql /var/run/mysqld
    
    # 启动MySQL守护进程
    mysqld_safe --user=mysql --datadir=/var/lib/mysql &
    
    # 等待MySQL启动
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if mysqladmin ping --silent; then
            log_info "MySQL服务启动成功"
            return 0
        fi
        
        log_info "等待MySQL启动... ($attempt/$max_attempts)"
        sleep 2
        attempt=$((attempt + 1))
    done
    
    log_error "MySQL服务启动超时"
    return 1
}

# 设置root密码和创建数据库
setup_database() {
    log_info "设置数据库和用户..."
    
    # 设置root密码
    mysql -u root << EOF
ALTER USER 'root'@'localhost' IDENTIFIED BY '${MYSQL_ROOT_PASSWORD}';
DELETE FROM mysql.user WHERE User='';
DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');
DROP DATABASE IF EXISTS test;
DELETE FROM mysql.db WHERE Db='test' OR Db='test_%';
FLUSH PRIVILEGES;
EOF

    # 创建应用数据库和用户
    mysql -u root -p"${MYSQL_ROOT_PASSWORD}" << EOF
CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE} 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS '${MYSQL_USER}'@'%' 
    IDENTIFIED BY '${MYSQL_PASSWORD}';

GRANT ALL PRIVILEGES ON ${MYSQL_DATABASE}.* TO '${MYSQL_USER}'@'%';

-- 创建只读用户(用于监控)
CREATE USER IF NOT EXISTS 'readonly'@'%' 
    IDENTIFIED BY 'readonly_pass';
    
GRANT SELECT ON ${MYSQL_DATABASE}.* TO 'readonly'@'%';

FLUSH PRIVILEGES;

-- 显示创建的数据库和用户
SHOW DATABASES;
SELECT User, Host FROM mysql.user WHERE User IN ('${MYSQL_USER}', 'readonly');
EOF

    log_info "数据库和用户创建完成"
}

# 导入初始化数据
import_initial_data() {
    log_info "导入初始化数据..."
    
    if [ -f "/docker-entrypoint-initdb.d/init-db.sql" ]; then
        log_info "找到初始化SQL文件，开始导入..."
        
        mysql -u root -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < /docker-entrypoint-initdb.d/init-db.sql
        
        if [ $? -eq 0 ]; then
            log_info "初始化数据导入成功"
        else
            log_error "初始化数据导入失败"
            return 1
        fi
    else
        log_warn "未找到初始化SQL文件: /docker-entrypoint-initdb.d/init-db.sql"
    fi
}

# 优化数据库
optimize_database() {
    log_info "优化数据库..."
    
    mysql -u root -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" << EOF
-- 分析表
ANALYZE TABLE users, vulnerability_categories, vulnerability_content;

-- 优化表
OPTIMIZE TABLE users, vulnerability_categories, vulnerability_content;

-- 检查表
CHECK TABLE users, vulnerability_categories, vulnerability_content;
EOF

    log_info "数据库优化完成"
}

# 创建数据库备份
create_backup() {
    log_info "创建初始数据备份..."
    
    local backup_dir="/app/backups"
    local backup_file="${backup_dir}/initial_backup_$(date +%Y%m%d_%H%M%S).sql"
    
    mkdir -p "${backup_dir}"
    
    mysqldump -u root -p"${MYSQL_ROOT_PASSWORD}" \
        --single-transaction \
        --routines \
        --triggers \
        --events \
        "${MYSQL_DATABASE}" > "${backup_file}"
    
    if [ $? -eq 0 ]; then
        log_info "初始备份创建成功: ${backup_file}"
    else
        log_error "初始备份创建失败"
    fi
}

# 主函数
main() {
    log_info "开始MySQL初始化流程..."
    
    # 设置环境变量默认值
    export MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD:-root}
    export MYSQL_DATABASE=${MYSQL_DATABASE:-javaweb_security}
    export MYSQL_USER=${MYSQL_USER:-security_user}
    export MYSQL_PASSWORD=${MYSQL_PASSWORD:-security_pass}
    
    # 执行初始化步骤
    setup_mysql_config
    init_mysql_data
    start_mysql_service
    setup_database
    import_initial_data
    optimize_database
    create_backup
    
    log_info "MySQL初始化完成！"
    log_info "数据库: ${MYSQL_DATABASE}"
    log_info "用户: ${MYSQL_USER}"
    log_info "MySQL服务已就绪"
}

# 错误处理
error_handler() {
    log_error "MySQL初始化过程中发生错误"
    exit 1
}

trap error_handler ERR

# 执行主函数
if [ "${BASH_SOURCE[0]}" = "${0}" ]; then
    main "$@"
fi

