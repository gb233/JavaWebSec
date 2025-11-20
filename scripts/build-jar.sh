#!/bin/bash

# =====================================================
# Java Web安全教学系统 - 一键打包脚本（前后端集成）
# =====================================================
# 用途：构建前后端集成的单个 JAR 包
# 使用方法：./scripts/build-jar.sh
# =====================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 项目根目录
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
FRONTEND_DIR="${PROJECT_ROOT}/src/frontend"
BACKEND_DIR="${PROJECT_ROOT}/src/backend"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Java Web安全教学系统 - 一键打包脚本${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 检查 Java
echo -e "${BLUE}[0/4] 检查 Java 环境...${NC}"
if ! command -v java &> /dev/null; then
    echo -e "${RED}✗ Java 未安装，请先安装 Java 17+${NC}"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo -e "${RED}✗ Java版本过低，需要Java 17+，当前版本：$JAVA_VERSION${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Java 版本: $(java -version 2>&1 | head -n 1)${NC}"
echo ""

# 检查 Node.js
echo -e "${BLUE}[1/4] 检查 Node.js 环境...${NC}"
if ! command -v node &> /dev/null; then
    echo -e "${RED}✗ Node.js 未安装，请先安装 Node.js${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Node.js 版本: $(node --version)${NC}"

# 检查 Maven
echo -e "${BLUE}[2/4] 检查 Maven 环境...${NC}"
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}✗ Maven 未安装，请先安装 Maven${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Maven 版本: $(mvn --version | head -1)${NC}"
echo ""

# 构建前端
echo -e "${BLUE}[3/4] 构建前端...${NC}"
cd "${FRONTEND_DIR}"

# 清理旧的构建产物和缓存（确保每次构建都是全新的）
echo "清理旧的构建产物和缓存..."
rm -rf dist
rm -rf .vite
rm -rf node_modules/.vite 2>/dev/null || true

if [ ! -d "node_modules" ]; then
    echo "安装前端依赖..."
    npm install
fi
echo "构建前端项目..."
npm run build
if [ ! -d "dist" ] || [ -z "$(ls -A dist 2>/dev/null)" ]; then
    echo -e "${RED}✗ 前端构建失败：dist 目录不存在或为空${NC}"
    exit 1
fi
echo -e "${GREEN}✓ 前端构建完成${NC}"
echo ""

# 验证前端dist目录在Maven构建前存在
if [ ! -d "${FRONTEND_DIR}/dist" ] || [ -z "$(ls -A ${FRONTEND_DIR}/dist 2>/dev/null)" ]; then
    echo -e "${RED}✗ 前端dist目录不存在或为空，无法继续构建${NC}"
    exit 1
fi

# 构建后端（会自动复制前端 dist 到 static）
echo -e "${BLUE}[4/4] 构建后端 JAR（集成前端）...${NC}"
cd "${BACKEND_DIR}"
echo "清理旧的构建..."
mvn clean
echo "打包 JAR（包含前端静态文件）..."
mvn package -DskipTests

# 检查 JAR 文件
JAR_FILE="${BACKEND_DIR}/target/security-teaching-system.jar"
if [ ! -f "${JAR_FILE}" ]; then
    echo -e "${RED}✗ JAR 文件不存在${NC}"
    exit 1
fi

JAR_SIZE=$(ls -lh "${JAR_FILE}" | awk '{print $5}')
echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}打包完成！${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "JAR 文件位置: ${JAR_FILE}"
echo -e "文件大小: ${JAR_SIZE}"
echo ""
echo -e "${BLUE}运行命令:${NC}"
echo -e "  java -jar ${JAR_FILE}"
echo ""
echo -e "${BLUE}测试命令:${NC}"
echo -e "  java -jar ${JAR_FILE} > app.log 2>&1 &"
echo -e "  curl http://localhost:8080/actuator/health"
echo ""







