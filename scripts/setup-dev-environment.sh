#!/bin/bash

# 新开发者环境搭建脚本
# 用于自动化检查和设置开发环境

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "========================================="
echo "  新开发者环境搭建脚本"
echo "========================================="
echo ""

# 检查函数
check_command() {
    local cmd=$1
    local name=$2
    local required=$3
    
    if command -v $cmd &> /dev/null; then
        local version=$($cmd --version 2>&1 | head -n 1)
        echo -e "${GREEN}✓${NC} $name 已安装: $version"
        return 0
    else
        if [ "$required" = "required" ]; then
            echo -e "${RED}✗${NC} $name 未安装 (必需)"
            return 1
        else
            echo -e "${YELLOW}⚠${NC} $name 未安装 (可选)"
            return 0
        fi
    fi
}

check_version() {
    local cmd=$1
    local min_version=$2
    local name=$3
    
    if command -v $cmd &> /dev/null; then
        local version=$($cmd --version 2>&1 | head -n 1 | grep -oE '[0-9]+\.[0-9]+' | head -n 1)
        echo -e "${GREEN}✓${NC} $name 版本: $version"
        return 0
    else
        return 1
    fi
}

# 1. 检查Java环境
echo "1. 检查Java环境..."
if check_command java "Java" "required"; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ]; then
        echo -e "${GREEN}  ✓ Java版本符合要求 (>= 17)${NC}"
    else
        echo -e "${RED}  ✗ Java版本过低，需要17+${NC}"
    fi
else
    echo -e "${YELLOW}  安装Java 17: brew install openjdk@17${NC}"
fi

# 2. 检查Maven
echo ""
echo "2. 检查Maven..."
if check_command mvn "Maven" "required"; then
    MVN_VERSION=$(mvn -version | head -n 1 | grep -oE '[0-9]+\.[0-9]+' | head -n 1)
    echo -e "${GREEN}  ✓ Maven版本: $MVN_VERSION${NC}"
else
    echo -e "${YELLOW}  安装Maven: brew install maven${NC}"
fi

# 3. 检查Node.js
echo ""
echo "3. 检查Node.js..."
if check_command node "Node.js" "required"; then
    NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
    if [ "$NODE_VERSION" -ge 20 ]; then
        echo -e "${GREEN}  ✓ Node.js版本符合要求 (>= 20)${NC}"
    else
        echo -e "${RED}  ✗ Node.js版本过低，需要20+${NC}"
    fi
else
    echo -e "${YELLOW}  安装Node.js 20: brew install node@20${NC}"
fi

# 4. 检查npm
echo ""
echo "4. 检查npm..."
check_command npm "npm" "required"

# 5. 检查MySQL
echo ""
echo "5. 检查MySQL..."
if check_command mysql "MySQL" "required"; then
    # 检查MySQL服务是否运行
    if brew services list | grep -q "mysql.*started"; then
        echo -e "${GREEN}  ✓ MySQL服务正在运行${NC}"
    else
        echo -e "${YELLOW}  ⚠ MySQL服务未运行，请执行: brew services start mysql@8.0${NC}"
    fi
else
    echo -e "${YELLOW}  安装MySQL: brew install mysql@8.0${NC}"
fi

# 6. 检查Git
echo ""
echo "6. 检查Git..."
check_command git "Git" "required"

# 检查Git配置
if git config --global user.name &> /dev/null && git config --global user.email &> /dev/null; then
    echo -e "${GREEN}  ✓ Git用户信息已配置${NC}"
    echo -e "    用户名: $(git config --global user.name)"
    echo -e "    邮箱: $(git config --global user.email)"
else
    echo -e "${YELLOW}  ⚠ Git用户信息未配置${NC}"
    echo -e "    请执行:"
    echo -e "    git config --global user.name \"Your Name\""
    echo -e "    git config --global user.email \"your.email@example.com\""
fi

# 检查SSH密钥
if [ -f ~/.ssh/id_ed25519.pub ] || [ -f ~/.ssh/id_rsa.pub ]; then
    echo -e "${GREEN}  ✓ SSH密钥已存在${NC}"
else
    echo -e "${YELLOW}  ⚠ SSH密钥未找到${NC}"
    echo -e "    请执行: ssh-keygen -t ed25519 -C \"your.email@example.com\""
fi

# 7. 检查Docker (可选)
echo ""
echo "7. 检查Docker (可选)..."
check_command docker "Docker" "optional"

# 8. 检查项目依赖
echo ""
echo "8. 检查项目依赖..."

# 检查后端依赖
if [ -d "src/backend" ]; then
    echo "  后端依赖..."
    if [ -f "src/backend/pom.xml" ]; then
        echo -e "${GREEN}  ✓ pom.xml存在${NC}"
        if [ -d "src/backend/.mvn" ] || [ -f "src/backend/pom.xml" ]; then
            echo -e "${BLUE}    提示: 首次运行需要执行 'mvn clean install'${NC}"
        fi
    fi
else
    echo -e "${YELLOW}  ⚠ 后端目录不存在${NC}"
fi

# 检查前端依赖
if [ -d "src/frontend" ]; then
    echo "  前端依赖..."
    if [ -f "src/frontend/package.json" ]; then
        echo -e "${GREEN}  ✓ package.json存在${NC}"
        if [ ! -d "src/frontend/node_modules" ]; then
            echo -e "${BLUE}    提示: 首次运行需要执行 'npm install'${NC}"
        else
            echo -e "${GREEN}  ✓ node_modules存在${NC}"
        fi
    fi
else
    echo -e "${YELLOW}  ⚠ 前端目录不存在${NC}"
fi

# 9. 生成环境检查报告
echo ""
echo "========================================="
echo "  环境检查完成"
echo "========================================="
echo ""
echo "下一步操作:"
echo ""
echo "1. 安装缺失的工具（如需要）"
echo "2. 配置Git用户信息和SSH密钥"
echo "3. 搭建数据库:"
echo "   mysql -u root -p"
echo "   CREATE DATABASE security_teaching_system;"
echo "4. 安装项目依赖:"
echo "   cd src/backend && mvn clean install"
echo "   cd src/frontend && npm install"
echo "5. 配置数据库连接（application.yml）"
echo "6. 启动项目:"
echo "   后端: cd src/backend && mvn spring-boot:run"
echo "   前端: cd src/frontend && npm run dev"
echo ""
echo "详细说明请查看: docs/新开发者入职指南.md"
echo ""



