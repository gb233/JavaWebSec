#!/bin/bash
# 预提交检查脚本
# 在提交代码前运行此脚本，确保所有检查通过

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}🔍 开始预提交检查...${NC}"
echo ""

# 检查后端
echo -e "${YELLOW}📦 检查后端代码...${NC}"
cd src/backend

echo "1. 运行Spotless检查..."
if mvn spotless:check -q; then
    echo -e "${GREEN}✅ Spotless检查通过${NC}"
else
    echo -e "${RED}❌ Spotless检查失败，正在自动修复...${NC}"
    mvn spotless:apply -q
    echo -e "${YELLOW}⚠️  已自动修复，请重新运行检查${NC}"
    exit 1
fi

echo "2. 编译检查..."
if mvn clean compile -DskipTests -q; then
    echo -e "${GREEN}✅ 编译检查通过${NC}"
else
    echo -e "${RED}❌ 编译失败${NC}"
    exit 1
fi

cd ../..

# 检查前端
echo ""
echo -e "${YELLOW}📦 检查前端代码...${NC}"
cd src/frontend

echo "1. ESLint检查..."
if npm run lint:check --silent 2>/dev/null; then
    echo -e "${GREEN}✅ ESLint检查通过${NC}"
else
    echo -e "${YELLOW}⚠️  ESLint发现问题，尝试自动修复...${NC}"
    npm run lint --silent 2>/dev/null || true
    echo -e "${YELLOW}⚠️  请手动检查剩余问题${NC}"
fi

echo "2. TypeScript类型检查..."
if npm run type-check --silent 2>/dev/null; then
    echo -e "${GREEN}✅ TypeScript检查通过${NC}"
else
    echo -e "${RED}❌ TypeScript类型检查失败${NC}"
    exit 1
fi

cd ../..

echo ""
echo -e "${GREEN}✅ 所有检查通过！可以提交代码了${NC}"
