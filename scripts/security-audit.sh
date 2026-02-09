#!/bin/bash

# 安全审计脚本
# 用于自动化执行安全审计检查

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 审计结果
AUDIT_RESULTS="security-audit-results-$(date +%Y%m%d-%H%M%S).txt"
ISSUES_FOUND=0
CRITICAL_ISSUES=0
WARNINGS=0

echo "========================================="
echo "  安全审计开始"
echo "  日期: $(date)"
echo "========================================="
echo ""

# 函数：记录问题
record_issue() {
    local severity=$1
    local message=$2
    echo "[$severity] $message" >> "$AUDIT_RESULTS"
    ISSUES_FOUND=$((ISSUES_FOUND + 1))
    if [ "$severity" = "CRITICAL" ]; then
        CRITICAL_ISSUES=$((CRITICAL_ISSUES + 1))
    elif [ "$severity" = "WARNING" ]; then
        WARNINGS=$((WARNINGS + 1))
    fi
}

# 函数：检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        echo -e "${YELLOW}警告: $1 未安装，跳过相关检查${NC}"
        return 1
    fi
    return 0
}

# 1. 后端依赖审计
echo "1. 后端依赖审计..."
if [ -d "src/backend" ]; then
    cd src/backend
    
    # 检查Maven是否可用
    if check_command mvn; then
        echo "  - 检查Maven依赖漏洞..."
        if mvn org.owasp:dependency-check-maven:check -DskipTests 2>&1 | tee -a "../$AUDIT_RESULTS"; then
            echo -e "${GREEN}  ✓ Maven依赖检查完成${NC}"
        else
            record_issue "WARNING" "Maven依赖检查失败或发现漏洞"
        fi
        
        echo "  - 检查过时的依赖..."
        mvn versions:display-dependency-updates 2>&1 | tee -a "../$AUDIT_RESULTS" || true
    fi
    
    cd ../..
else
    record_issue "WARNING" "后端目录不存在"
fi

echo ""

# 2. 前端依赖审计
echo "2. 前端依赖审计..."
if [ -d "src/frontend" ]; then
    cd src/frontend
    
    # 检查npm是否可用
    if check_command npm; then
        echo "  - 检查npm依赖漏洞..."
        if npm audit --audit-level=moderate 2>&1 | tee -a "../$AUDIT_RESULTS"; then
            echo -e "${GREEN}  ✓ npm依赖检查完成${NC}"
        else
            record_issue "WARNING" "npm依赖检查发现漏洞"
        fi
    fi
    
    cd ../..
else
    record_issue "WARNING" "前端目录不存在"
fi

echo ""

# 3. 硬编码密钥检查
echo "3. 硬编码密钥检查..."
echo "  - 扫描代码中的硬编码密钥..."

# 检查常见的硬编码密钥模式
PATTERNS=(
    "password\s*=\s*[\"'][^\"']+[\"']"
    "secret\s*=\s*[\"'][^\"']+[\"']"
    "api[_-]?key\s*=\s*[\"'][^\"']+[\"']"
    "token\s*=\s*[\"'][^\"']+[\"']"
    "private[_-]?key\s*=\s*[\"'][^\"']+[\"']"
)

FOUND_SECRETS=0
for pattern in "${PATTERNS[@]}"; do
    # 排除测试文件和配置文件中的示例
    if grep -r -E "$pattern" --include="*.java" --include="*.ts" --include="*.vue" \
        --exclude-dir=node_modules --exclude-dir=target --exclude-dir=.git \
        --exclude="*Test.java" --exclude="*test.ts" 2>/dev/null | grep -v "example\|test\|TODO\|FIXME" > /dev/null; then
        FOUND_SECRETS=$((FOUND_SECRETS + 1))
        record_issue "CRITICAL" "发现可能的硬编码密钥: $pattern"
    fi
done

if [ $FOUND_SECRETS -eq 0 ]; then
    echo -e "${GREEN}  ✓ 未发现硬编码密钥${NC}"
else
    echo -e "${RED}  ✗ 发现 $FOUND_SECRETS 个可能的硬编码密钥${NC}"
fi

echo ""

# 4. 环境变量检查
echo "4. 环境变量检查..."
if [ -f ".env.example" ] || [ -f "src/backend/src/main/resources/application.yml" ]; then
    echo "  - 检查环境变量配置..."
    
    # 检查是否有硬编码的敏感配置
    if grep -r "password.*root\|secret.*123\|key.*test" \
        --include="*.yml" --include="*.yaml" --include="*.properties" \
        --exclude-dir=target --exclude-dir=.git 2>/dev/null | grep -v "example\|test" > /dev/null; then
        record_issue "CRITICAL" "发现可能的硬编码敏感配置"
    else
        echo -e "${GREEN}  ✓ 未发现硬编码敏感配置${NC}"
    fi
else
    record_issue "WARNING" "未找到环境变量配置文件"
fi

echo ""

# 5. SQL注入检查
echo "5. SQL注入检查..."
echo "  - 检查SQL注入风险..."

# 检查Java代码中的SQL拼接
if grep -r "String.*sql.*\+" --include="*.java" \
    --exclude-dir=target --exclude-dir=.git 2>/dev/null | grep -v "//.*安全\|//.*Safe" > /dev/null; then
    record_issue "CRITICAL" "发现可能的SQL注入风险（字符串拼接）"
else
    echo -e "${GREEN}  ✓ 未发现明显的SQL注入风险${NC}"
fi

echo ""

# 6. XSS检查
echo "6. XSS检查..."
echo "  - 检查XSS风险..."

# 检查Vue代码中的v-html使用
if grep -r "v-html" --include="*.vue" \
    --exclude-dir=node_modules --exclude-dir=.git 2>/dev/null | grep -v "//.*安全\|//.*Safe" > /dev/null; then
    record_issue "WARNING" "发现v-html使用，需要确认已转义"
else
    echo -e "${GREEN}  ✓ 未发现明显的XSS风险${NC}"
fi

echo ""

# 7. 文件权限检查
echo "7. 文件权限检查..."
echo "  - 检查敏感文件权限..."

# 检查配置文件权限
SENSITIVE_FILES=(
    ".env"
    "application.yml"
    "application.properties"
)

for file in "${SENSITIVE_FILES[@]}"; do
    if [ -f "$file" ]; then
        PERMS=$(stat -f "%A" "$file" 2>/dev/null || stat -c "%a" "$file" 2>/dev/null || echo "unknown")
        if [ "$PERMS" != "600" ] && [ "$PERMS" != "400" ]; then
            record_issue "WARNING" "敏感文件 $file 权限可能过于宽松: $PERMS"
        fi
    fi
done

echo ""

# 8. Docker安全检查
echo "8. Docker安全检查..."
if [ -f "Dockerfile" ] || [ -f "Dockerfile.backend" ] || [ -f "Dockerfile.frontend" ]; then
    echo "  - 检查Docker配置..."
    
    # 检查是否以root用户运行
    if grep -r "USER root" Dockerfile* 2>/dev/null | grep -v "#.*安全\|#.*Safe" > /dev/null; then
        record_issue "WARNING" "Dockerfile中使用root用户，建议使用非root用户"
    fi
    
    # 检查是否有硬编码的密钥
    if grep -r "ENV.*PASSWORD\|ENV.*SECRET\|ENV.*KEY" Dockerfile* 2>/dev/null | grep -v "example\|test" > /dev/null; then
        record_issue "CRITICAL" "Dockerfile中可能有硬编码的敏感信息"
    fi
else
    echo "  - 未找到Dockerfile，跳过Docker检查"
fi

echo ""

# 生成审计报告
echo "========================================="
echo "  安全审计完成"
echo "========================================="
echo ""
echo "审计结果:"
echo "  - 总问题数: $ISSUES_FOUND"
echo "  - 严重问题: $CRITICAL_ISSUES"
echo "  - 警告: $WARNINGS"
echo ""
echo "详细报告已保存到: $AUDIT_RESULTS"
echo ""

# 根据问题严重程度返回退出码
if [ $CRITICAL_ISSUES -gt 0 ]; then
    echo -e "${RED}发现严重安全问题，请立即修复！${NC}"
    exit 1
elif [ $ISSUES_FOUND -gt 0 ]; then
    echo -e "${YELLOW}发现一些问题，建议修复${NC}"
    exit 0
else
    echo -e "${GREEN}未发现安全问题${NC}"
    exit 0
fi



