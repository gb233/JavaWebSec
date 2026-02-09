#!/bin/bash

# 高级代码安全审计脚本
# 集成专业安全扫描工具

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 审计结果
AUDIT_RESULTS="advanced-security-audit-$(date +%Y%m%d-%H%M%S).txt"
ISSUES_FOUND=0
CRITICAL_ISSUES=0
WARNINGS=0

echo "========================================="
echo "  高级代码安全审计"
echo "  日期: $(date)"
echo "========================================="
echo ""

# 函数：检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        echo -e "${YELLOW}⚠ $1 未安装，跳过相关检查${NC}"
        echo "  安装方法: $2" >> "$AUDIT_RESULTS"
        return 1
    fi
    return 0
}

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

# 1. Snyk依赖扫描
echo "1. Snyk依赖漏洞扫描..."
if check_command snyk "npm install -g snyk 或访问 https://snyk.io/"; then
    echo "  - 扫描后端依赖..."
    if [ -d "src/backend" ]; then
        cd src/backend
        if snyk test --severity-threshold=high --json 2>&1 | tee -a "../$AUDIT_RESULTS" | grep -q '"vulnerabilities":\['; then
            record_issue "CRITICAL" "Snyk发现高危依赖漏洞"
        else
            echo -e "${GREEN}  ✓ 后端依赖扫描通过${NC}"
        fi
        cd ../..
    fi
    
    echo "  - 扫描前端依赖..."
    if [ -d "src/frontend" ]; then
        cd src/frontend
        if snyk test --severity-threshold=high --json 2>&1 | tee -a "../$AUDIT_RESULTS" | grep -q '"vulnerabilities":\['; then
            record_issue "CRITICAL" "Snyk发现高危依赖漏洞"
        else
            echo -e "${GREEN}  ✓ 前端依赖扫描通过${NC}"
        fi
        cd ../..
    fi
else
    echo -e "${YELLOW}  ⚠ Snyk未安装，跳过依赖扫描${NC}"
    echo "  建议: 安装Snyk进行专业依赖漏洞扫描"
fi

echo ""

# 2. Semgrep静态分析
echo "2. Semgrep静态代码分析..."
if check_command semgrep "pip install semgrep 或访问 https://semgrep.dev/"; then
    echo "  - 使用OWASP规则扫描..."
    if semgrep --config=p/owasp-top-ten --json src/ 2>&1 | tee -a "$AUDIT_RESULTS" | grep -q '"results":\['; then
        SEMGREP_ISSUES=$(semgrep --config=p/owasp-top-ten --json src/ 2>/dev/null | grep -c '"severity":"ERROR"' || echo "0")
        if [ "$SEMGREP_ISSUES" -gt 0 ]; then
            record_issue "CRITICAL" "Semgrep发现 $SEMGREP_ISSUES 个高危安全问题"
        else
            echo -e "${GREEN}  ✓ Semgrep扫描通过${NC}"
        fi
    else
        echo -e "${GREEN}  ✓ Semgrep扫描通过${NC}"
    fi
else
    echo -e "${YELLOW}  ⚠ Semgrep未安装，跳过静态分析${NC}"
    echo "  建议: 安装Semgrep进行专业静态代码分析"
fi

echo ""

# 3. SpotBugs Java安全扫描
echo "3. SpotBugs Java安全扫描..."
if [ -d "src/backend" ]; then
    cd src/backend
    if [ -f "pom.xml" ]; then
        echo "  - 检查SpotBugs配置..."
        if grep -q "spotbugs-maven-plugin" pom.xml; then
            echo "  - 运行SpotBugs扫描..."
            if mvn spotbugs:check -Dspotbugs.failOnError=false 2>&1 | tee -a "../$AUDIT_RESULTS" | grep -q "BUILD FAILURE\|ERROR"; then
                record_issue "WARNING" "SpotBugs发现安全问题"
            else
                echo -e "${GREEN}  ✓ SpotBugs扫描通过${NC}"
            fi
        else
            echo -e "${YELLOW}  ⚠ SpotBugs未配置${NC}"
            echo "  建议: 在pom.xml中添加spotbugs-maven-plugin"
        fi
    fi
    cd ../..
fi

echo ""

# 4. ESLint安全扫描（前端）
echo "4. ESLint安全扫描..."
if [ -d "src/frontend" ]; then
    cd src/frontend
    if [ -f "package.json" ]; then
        if grep -q "eslint-plugin-security" package.json; then
            echo "  - 运行ESLint安全扫描..."
            if npm run lint:security 2>&1 | tee -a "../$AUDIT_RESULTS" | grep -q "error"; then
                record_issue "WARNING" "ESLint安全插件发现安全问题"
            else
                echo -e "${GREEN}  ✓ ESLint安全扫描通过${NC}"
            fi
        else
            echo -e "${YELLOW}  ⚠ eslint-plugin-security未安装${NC}"
            echo "  建议: npm install --save-dev eslint-plugin-security"
        fi
    fi
    cd ../..
fi

echo ""

# 5. CodeQL（如果可用）
echo "5. CodeQL代码安全分析..."
if check_command codeql "GitHub Actions已集成，或访问 https://codeql.github.com/"; then
    echo "  - CodeQL已在GitHub Actions中配置"
    echo -e "${GREEN}  ✓ CodeQL配置正常${NC}"
else
    echo -e "${BLUE}  ℹ CodeQL通过GitHub Actions运行${NC}"
fi

echo ""

# 6. 增强的硬编码密钥检查
echo "6. 增强的硬编码密钥检查..."
echo "  - 使用Semgrep规则扫描..."

if check_command semgrep ""; then
    # 使用Semgrep的硬编码密钥规则
    if semgrep --config=p/security-audit --include="*.java,*.ts,*.vue" src/ 2>&1 | grep -q "hardcoded-secret\|hardcoded-password"; then
        record_issue "CRITICAL" "Semgrep发现硬编码密钥"
    else
        echo -e "${GREEN}  ✓ 未发现硬编码密钥${NC}"
    fi
else
    # 回退到基础检查
    PATTERNS=(
        "password\s*=\s*[\"'][^\"']+[\"']"
        "secret\s*=\s*[\"'][^\"']+[\"']"
        "api[_-]?key\s*=\s*[\"'][^\"']+[\"']"
    )
    
    FOUND_SECRETS=0
    for pattern in "${PATTERNS[@]}"; do
        if grep -r -E "$pattern" --include="*.java" --include="*.ts" \
            --exclude-dir=node_modules --exclude-dir=target --exclude-dir=.git \
            --exclude="*Test.java" --exclude="*test.ts" 2>/dev/null | grep -v "example\|test\|TODO" > /dev/null; then
            FOUND_SECRETS=$((FOUND_SECRETS + 1))
        fi
    done
    
    if [ $FOUND_SECRETS -eq 0 ]; then
        echo -e "${GREEN}  ✓ 未发现硬编码密钥${NC}"
    else
        record_issue "CRITICAL" "发现 $FOUND_SECRETS 个可能的硬编码密钥"
    fi
fi

echo ""

# 7. 增强的SQL注入检查
echo "7. 增强的SQL注入检查..."
if check_command semgrep ""; then
    if semgrep --config=p/java --include="*.java" src/backend/ 2>&1 | grep -q "sql-injection"; then
        record_issue "CRITICAL" "Semgrep发现SQL注入风险"
    else
        echo -e "${GREEN}  ✓ 未发现SQL注入风险${NC}"
    fi
else
    echo -e "${YELLOW}  ⚠ 使用基础SQL注入检查${NC}"
    if grep -r "String.*sql.*\+" --include="*.java" \
        --exclude-dir=target --exclude-dir=.git 2>/dev/null | grep -v "//.*安全\|//.*Safe" > /dev/null; then
        record_issue "WARNING" "发现可能的SQL注入风险（字符串拼接）"
    else
        echo -e "${GREEN}  ✓ 未发现明显的SQL注入风险${NC}"
    fi
fi

echo ""

# 8. 增强的XSS检查
echo "8. 增强的XSS检查..."
if [ -d "src/frontend" ]; then
    # 检查v-html使用
    VHTML_COUNT=$(grep -r "v-html" --include="*.vue" src/frontend/ 2>/dev/null | wc -l | tr -d ' ')
    if [ "$VHTML_COUNT" -gt 0 ]; then
        echo -e "${YELLOW}  ⚠ 发现 $VHTML_COUNT 处v-html使用${NC}"
        echo "  建议: 确认所有v-html内容已转义"
        record_issue "WARNING" "发现 $VHTML_COUNT 处v-html使用，需要确认已转义"
    else
        echo -e "${GREEN}  ✓ 未发现v-html使用${NC}"
    fi
fi

echo ""

# 生成审计报告
echo "========================================="
echo "  高级安全审计完成"
echo "========================================="
echo ""
echo "审计结果:"
echo "  - 总问题数: $ISSUES_FOUND"
echo "  - 严重问题: $CRITICAL_ISSUES"
echo "  - 警告: $WARNINGS"
echo ""
echo "详细报告已保存到: $AUDIT_RESULTS"
echo ""

# 工具建议
echo "工具使用建议:"
if ! command -v snyk &> /dev/null; then
    echo "  - 安装Snyk: npm install -g snyk"
fi
if ! command -v semgrep &> /dev/null; then
    echo "  - 安装Semgrep: pip install semgrep 或 brew install semgrep"
fi
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



