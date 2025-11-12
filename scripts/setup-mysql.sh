#!/bin/bash

# =============================================================================
# Java Web安全教学系统 - MySQL配置脚本
# =============================================================================
# 功能：为Java Web安全教学系统配置MySQL数据库
# 作者：Java Web Security Team
# 版本：v1.0
# =============================================================================

set -e

echo "🚀 Java Web安全教学系统 - MySQL配置向导"
echo "=============================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 检查MySQL是否安装
check_mysql() {
    echo -e "${BLUE}🔍 检查MySQL安装状态...${NC}"
    
    if command -v mysql >/dev/null 2>&1; then
        echo -e "${GREEN}✅ MySQL已安装${NC}"
        mysql --version
    else
        echo -e "${RED}❌ 未找到MySQL，请先安装MySQL${NC}"
        echo ""
        echo "安装建议："
        echo "  macOS: brew install mysql"
        echo "  Ubuntu: sudo apt-get install mysql-server"
        echo "  CentOS: sudo yum install mysql-server"
        exit 1
    fi
}

# 检查MySQL服务状态
check_mysql_service() {
    echo -e "${BLUE}🔍 检查MySQL服务状态...${NC}"
    
    # 尝试连接MySQL
    if mysql -u root -e "SELECT 1" >/dev/null 2>&1; then
        echo -e "${GREEN}✅ MySQL服务正常运行${NC}"
    else
        echo -e "${YELLOW}⚠️  MySQL服务可能未启动，尝试启动...${NC}"
        
        # 根据系统类型启动MySQL
        if [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS
            if command -v brew >/dev/null 2>&1; then
                brew services start mysql || brew services start mysql@8.4 || true
            fi
        elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
            # Linux
            sudo systemctl start mysql || sudo service mysql start || true
        fi
        
        sleep 3
        
        # 再次检查
        if mysql -u root -e "SELECT 1" >/dev/null 2>&1; then
            echo -e "${GREEN}✅ MySQL服务已启动${NC}"
        else
            echo -e "${RED}❌ 无法启动MySQL服务，请手动启动后重试${NC}"
            exit 1
        fi
    fi
}

# 检查TCP连接
check_tcp_connection() {
    echo -e "${BLUE}🔍 检查MySQL TCP连接...${NC}"
    
    # 获取MySQL网络配置
    MYSQL_PORT=$(mysql -u root -se "SELECT @@port" 2>/dev/null || echo "0")
    SKIP_NETWORKING=$(mysql -u root -se "SELECT @@skip_networking" 2>/dev/null || echo "1")
    
    echo "当前MySQL配置："
    echo "  端口: $MYSQL_PORT"
    echo "  网络连接: $([ "$SKIP_NETWORKING" = "1" ] && echo "禁用" || echo "启用")"
    
    if [ "$MYSQL_PORT" = "0" ] || [ "$SKIP_NETWORKING" = "1" ]; then
        echo -e "${YELLOW}⚠️  MySQL当前只允许socket连接，需要启用TCP连接${NC}"
        configure_mysql_tcp
    else
        echo -e "${GREEN}✅ MySQL TCP连接已启用${NC}"
    fi
}

# 配置MySQL TCP连接
configure_mysql_tcp() {
    echo -e "${BLUE}🔧 配置MySQL TCP连接...${NC}"
    
    # 创建临时配置文件
    TEMP_CONFIG="/tmp/mysql_tcp_config.cnf"
    cat > "$TEMP_CONFIG" << EOF
[mysqld]
port = 3306
bind-address = 127.0.0.1
skip-networking = 0
EOF

    echo "临时配置文件已创建: $TEMP_CONFIG"
    echo "内容："
    cat "$TEMP_CONFIG"
    echo ""
    
    echo -e "${YELLOW}⚠️  需要使用这个配置重启MySQL${NC}"
    echo ""
    echo "请选择操作方式："
    echo "1) 自动重启MySQL (推荐)"
    echo "2) 手动操作指导"
    echo "3) 跳过TCP配置 (使用socket连接)"
    
    read -p "请选择 [1-3]: " choice
    
    case $choice in
        1)
            restart_mysql_with_config "$TEMP_CONFIG"
            ;;
        2)
            show_manual_instructions "$TEMP_CONFIG"
            ;;
        3)
            echo -e "${YELLOW}⚠️  跳过TCP配置，应用将尝试socket连接${NC}"
            update_app_config_for_socket
            ;;
        *)
            echo -e "${RED}❌ 无效选择${NC}"
            exit 1
            ;;
    esac
}

# 自动重启MySQL
restart_mysql_with_config() {
    local config_file="$1"
    echo -e "${BLUE}🔄 重启MySQL服务...${NC}"
    
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        echo "停止MySQL服务..."
        brew services stop mysql 2>/dev/null || brew services stop mysql@8.4 2>/dev/null || true
        
        sleep 2
        
        echo "使用新配置启动MySQL..."
        mysqld_safe --defaults-file="$config_file" --user=mysql &
        
        sleep 5
        
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        echo "停止MySQL服务..."
        sudo systemctl stop mysql || sudo service mysql stop
        
        sleep 2
        
        echo "使用新配置启动MySQL..."
        sudo mysqld_safe --defaults-file="$config_file" --user=mysql &
        
        sleep 5
    fi
    
    # 检查是否启动成功
    if mysql -h 127.0.0.1 -P 3306 -u root -e "SELECT 1" >/dev/null 2>&1; then
        echo -e "${GREEN}✅ MySQL TCP连接配置成功${NC}"
    else
        echo -e "${RED}❌ MySQL重启失败，请手动配置${NC}"
        show_manual_instructions "$config_file"
    fi
}

# 显示手动操作指导
show_manual_instructions() {
    local config_file="$1"
    echo -e "${YELLOW}📋 手动配置指导${NC}"
    echo "=============================================="
    echo ""
    echo "1. 停止MySQL服务："
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "   brew services stop mysql"
    else
        echo "   sudo systemctl stop mysql"
    fi
    echo ""
    echo "2. 将以下配置添加到MySQL配置文件："
    echo "   配置文件通常位于: /etc/mysql/my.cnf 或 /usr/local/etc/my.cnf"
    echo ""
    echo "   [mysqld]"
    echo "   port = 3306"
    echo "   bind-address = 127.0.0.1"
    echo "   skip-networking = 0"
    echo ""
    echo "3. 重启MySQL服务："
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "   brew services start mysql"
    else
        echo "   sudo systemctl start mysql"
    fi
    echo ""
    echo "4. 验证TCP连接："
    echo "   mysql -h 127.0.0.1 -P 3306 -u root -e \"SELECT 1\""
    echo ""
    
    read -p "配置完成后按回车继续..."
}

# 为socket连接更新应用配置
update_app_config_for_socket() {
    echo -e "${BLUE}🔧 配置应用使用socket连接...${NC}"
    
    # 获取socket路径
    SOCKET_PATH=$(mysql -u root -se "SELECT @@socket" 2>/dev/null)
    
    if [ -n "$SOCKET_PATH" ]; then
        echo "MySQL socket路径: $SOCKET_PATH"
        echo "应用将配置为使用socket连接"
        echo ""
        echo "请在application.yml中使用以下配置："
        echo "spring:"
        echo "  datasource:"
        echo "    url: jdbc:mysql://localhost/security_teaching_system?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowMultiQueries=true&rewriteBatchedStatements=true&localSocket=$SOCKET_PATH"
    else
        echo -e "${RED}❌ 无法获取socket路径${NC}"
    fi
}

# 创建数据库
create_database() {
    echo -e "${BLUE}🗄️  创建数据库...${NC}"
    
    # 尝试TCP连接，如果失败则使用默认连接
    if mysql -h 127.0.0.1 -P 3306 -u root -e "SELECT 1" >/dev/null 2>&1; then
        MYSQL_CMD="mysql -h 127.0.0.1 -P 3306 -u root"
    else
        MYSQL_CMD="mysql -u root"
    fi
    
    echo "创建数据库 security_teaching_system..."
    $MYSQL_CMD -e "CREATE DATABASE IF NOT EXISTS security_teaching_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    
    echo "验证数据库创建..."
    $MYSQL_CMD -e "SHOW DATABASES LIKE 'security_teaching_system';"
    
    echo -e "${GREEN}✅ 数据库创建成功${NC}"
}

# 导入数据库表
import_database_schema() {
    echo -e "${BLUE}📊 导入数据库表结构...${NC}"
    
    if [ ! -f "../scripts/init-db.sql" ]; then
        echo -e "${RED}❌ 未找到数据库初始化脚本: ../scripts/init-db.sql${NC}"
        return 1
    fi
    
    # 尝试TCP连接，如果失败则使用默认连接
    if mysql -h 127.0.0.1 -P 3306 -u root security_teaching_system -e "SELECT 1" >/dev/null 2>&1; then
        MYSQL_CMD="mysql -h 127.0.0.1 -P 3306 -u root"
    else
        MYSQL_CMD="mysql -u root"
    fi
    
    echo "导入表结构..."
    $MYSQL_CMD security_teaching_system < ../scripts/init-db.sql
    
    echo "验证表创建..."
    TABLE_COUNT=$($MYSQL_CMD security_teaching_system -se "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'security_teaching_system';")
    echo "已创建 $TABLE_COUNT 个数据表"
    
    echo -e "${GREEN}✅ 数据库表结构导入成功${NC}"
}

# 测试连接
test_connection() {
    echo -e "${BLUE}🧪 测试应用数据库连接...${NC}"
    
    # 测试TCP连接
    echo "测试TCP连接 (127.0.0.1:3306)..."
    if mysql -h 127.0.0.1 -P 3306 -u root security_teaching_system -e "SELECT 'TCP连接成功' as status;" 2>/dev/null; then
        echo -e "${GREEN}✅ TCP连接正常${NC}"
        echo ""
        echo "应用配置建议："
        echo "spring.datasource.url: jdbc:mysql://127.0.0.1:3306/security_teaching_system"
    else
        echo -e "${YELLOW}⚠️  TCP连接失败，尝试socket连接...${NC}"
        
        # 测试socket连接
        SOCKET_PATH=$(mysql -u root -se "SELECT @@socket" 2>/dev/null)
        if mysql -u root security_teaching_system -e "SELECT 'Socket连接成功' as status;" 2>/dev/null; then
            echo -e "${GREEN}✅ Socket连接正常${NC}"
            echo ""
            echo "应用配置建议："
            echo "spring.datasource.url: jdbc:mysql://localhost/security_teaching_system?localSocket=$SOCKET_PATH"
        else
            echo -e "${RED}❌ 数据库连接失败${NC}"
            return 1
        fi
    fi
}

# 主流程
main() {
    echo ""
    check_mysql
    echo ""
    check_mysql_service
    echo ""
    check_tcp_connection
    echo ""
    create_database
    echo ""
    import_database_schema
    echo ""
    test_connection
    echo ""
    echo -e "${GREEN}🎉 MySQL配置完成！${NC}"
    echo ""
    echo "下一步:"
    echo "1. 确认 src/backend/src/main/resources/application.yml 中的数据库配置"
    echo "2. 运行 Java 应用: cd src/backend && java -jar target/security-teaching-system.jar"
    echo ""
}

# 运行主流程
main "$@"

