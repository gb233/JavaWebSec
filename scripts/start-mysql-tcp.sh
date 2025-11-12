#!/bin/bash

# =============================================================================
# 临时启动MySQL TCP模式脚本
# =============================================================================
# 功能：临时重启MySQL，启用TCP连接模式
# 用法：./scripts/start-mysql-tcp.sh
# =============================================================================

echo "🔧 Java Web安全教学系统 - MySQL TCP启动"
echo "========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 停止当前MySQL服务
echo -e "${BLUE}📍 步骤1: 停止当前MySQL服务...${NC}"
if command -v brew >/dev/null 2>&1; then
    # macOS Homebrew
    brew services stop mysql
    sleep 2
else
    echo -e "${YELLOW}⚠️  请手动停止MySQL服务: sudo systemctl stop mysql${NC}"
    exit 1
fi

# 创建临时配置文件
echo -e "${BLUE}📍 步骤2: 创建临时配置...${NC}"
TEMP_CONFIG=$(mktemp /tmp/mysql_tcp.XXXXXX.cnf)
cat > "$TEMP_CONFIG" << 'EOF'
[mysqld]
port = 3306
bind-address = 127.0.0.1
skip-networking = 0
datadir = /usr/local/var/mysql
socket = /tmp/mysql.sock
pid-file = /usr/local/var/mysql/$(hostname).pid
log-error = /usr/local/var/mysql/$(hostname).err
secure-file-priv = /usr/local/var/mysql-files
EOF

echo "临时配置文件: $TEMP_CONFIG"

# 使用临时配置启动MySQL
echo -e "${BLUE}📍 步骤3: 使用TCP配置启动MySQL...${NC}"
mysqld_safe --defaults-file="$TEMP_CONFIG" --user=$(whoami) --daemonize

# 等待MySQL启动
echo -e "${BLUE}📍 步骤4: 等待MySQL启动...${NC}"
for i in {1..30}; do
    if mysql -h 127.0.0.1 -P 3306 -u root -proot -e "SELECT 1" >/dev/null 2>&1; then
        echo -e "${GREEN}✅ MySQL TCP连接成功!${NC}"
        echo ""
        echo "MySQL TCP模式已启动:"
        echo "  - 主机: 127.0.0.1"
        echo "  - 端口: 3306" 
        echo "  - 用户: root"
        echo "  - 密码: root"
        echo ""
        echo "现在可以启动Java应用程序:"
        echo "  cd src/backend && java -jar target/security-teaching-system.jar"
        echo ""
        echo -e "${YELLOW}⚠️  注意: 这是临时配置，重启电脑后需要重新运行此脚本${NC}"
        
        # 清理临时文件
        rm -f "$TEMP_CONFIG"
        exit 0
    fi
    echo -n "."
    sleep 1
done

echo -e "${RED}❌ MySQL TCP启动超时${NC}"
echo "请检查错误日志: /usr/local/var/mysql/$(hostname).err"

# 清理临时文件
rm -f "$TEMP_CONFIG"
exit 1

