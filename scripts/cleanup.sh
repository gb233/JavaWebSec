#!/bin/bash

# ==========================================
# 项目清理脚本
# 用于清理开发环境中的临时文件和构建产物
# ==========================================

set -e

echo "🧹 开始清理项目文件..."

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 清理函数
clean_file() {
    local pattern=$1
    local description=$2
    
    echo -e "${YELLOW}清理 ${description}...${NC}"
    find . -name "$pattern" -type f -not -path "./.git/*" -not -path "./node_modules/*" -delete 2>/dev/null || true
    echo -e "${GREEN}✓ ${description} 清理完成${NC}"
}

clean_dir() {
    local dir=$1
    local description=$2
    
    if [ -d "$dir" ]; then
        echo -e "${YELLOW}清理 ${description}...${NC}"
        rm -rf "$dir"
        echo -e "${GREEN}✓ ${description} 清理完成${NC}"
    fi
}

# 1. 清理构建产物
echo ""
echo "=========================================="
echo "1. 清理构建产物"
echo "=========================================="
clean_dir "src/backend/target" "后端构建产物"
clean_dir "src/frontend/dist" "前端构建产物"
clean_dir "src/frontend/node_modules" "Node.js依赖包"

# 2. 清理日志文件
echo ""
echo "=========================================="
echo "2. 清理日志文件"
echo "=========================================="
clean_file "*.log" "日志文件"
clean_file "*.log.*" "压缩日志文件"
clean_dir "src/backend/logs" "后端日志目录"
clean_dir "src/backend/LOG_PATH_IS_UNDEFINED" "未定义路径日志目录"

# 3. 清理临时文件
echo ""
echo "=========================================="
echo "3. 清理临时文件"
echo "=========================================="
clean_file ".DS_Store" "macOS系统文件"
clean_file "*.tmp" "临时文件"
clean_file "*.bak" "备份文件"
clean_file "*.old" "旧文件"
clean_file "*.swp" "Vim交换文件"
clean_file "*.swo" "Vim交换文件"
clean_file "*~" "备份文件"

# 4. 清理IDE配置文件（可选）
echo ""
echo "=========================================="
echo "4. 清理IDE配置文件（可选）"
echo "=========================================="
read -p "是否清理IDE配置文件？(y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    clean_dir ".idea" "IntelliJ IDEA配置"
    clean_dir ".vscode" "VS Code配置"
    clean_file "*.iml" "IntelliJ模块文件"
    clean_file "*.ipr" "IntelliJ项目文件"
    clean_file "*.iws" "IntelliJ工作区文件"
fi

# 5. 清理TypeScript缓存
echo ""
echo "=========================================="
echo "5. 清理TypeScript缓存"
echo "=========================================="
clean_file "*.tsbuildinfo" "TypeScript构建信息"

# 6. 清理测试覆盖率报告
echo ""
echo "=========================================="
echo "6. 清理测试覆盖率报告"
echo "=========================================="
clean_dir "coverage" "测试覆盖率报告"
clean_dir ".nyc_output" "NYC测试覆盖率输出"

# 7. 清理缓存目录
echo ""
echo "=========================================="
echo "7. 清理缓存目录"
echo "=========================================="
clean_dir ".cache" "缓存目录"
clean_dir ".parcel-cache" "Parcel缓存"
clean_dir ".eslintcache" "ESLint缓存"
clean_dir ".stylelintcache" "Stylelint缓存"
clean_dir "src/frontend/.vite" "Vite缓存"

# 8. 检查敏感文件
echo ""
echo "=========================================="
echo "8. 检查敏感文件"
echo "=========================================="
echo -e "${YELLOW}检查敏感文件...${NC}"

SENSITIVE_FILES=(
    ".env"
    ".env.local"
    ".env.production"
    ".env.staging"
)

for file in "${SENSITIVE_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo -e "${RED}⚠ 警告: 发现敏感文件 $file${NC}"
        echo -e "${YELLOW}  请确认是否需要提交此文件${NC}"
    fi
done

# 9. 检查隐私目录
echo ""
echo "=========================================="
echo "9. 检查隐私目录"
echo "=========================================="
if [ -d ".private" ]; then
    echo -e "${RED}⚠ 警告: 发现隐私目录 .private${NC}"
    echo -e "${YELLOW}  请确认此目录已在.gitignore中${NC}"
fi

# 10. 统计清理结果
echo ""
echo "=========================================="
echo "10. 清理完成"
echo "=========================================="
echo -e "${GREEN}✓ 项目清理完成！${NC}"
echo ""
echo "清理后的项目大小："
du -sh . 2>/dev/null | awk '{print "总大小: " $1}'
echo ""
echo "建议在推送前执行以下命令检查："
echo "  git status"
echo "  git status --ignored"
echo ""




