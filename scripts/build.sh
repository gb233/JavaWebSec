#!/bin/bash

# ========================================
# 构建脚本 - Java Web安全教学系统
# ========================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 日志函数
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

log_step() {
    echo -e "${PURPLE}[STEP]${NC} $1"
}

# 显示横幅
show_banner() {
    echo -e "${CYAN}"
    cat << "EOF"
========================================
    Java Web安全教学系统
    Docker构建脚本
========================================
EOF
    echo -e "${NC}"
}

# 检查依赖
check_dependencies() {
    log_step "检查构建依赖..."
    
    # 检查Java版本
    if ! command -v java &> /dev/null; then
        log_error "Java未安装，请先安装Java 17+"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        log_error "Java版本过低，需要Java 17+，当前版本：$JAVA_VERSION"
        exit 1
    fi
    log_info "Java版本检查通过: $(java -version 2>&1 | head -n 1)"
    
    # 检查Node.js
    if ! command -v node &> /dev/null; then
        log_error "Node.js未安装，请先安装Node.js 18+"
        exit 1
    fi
    log_info "Node.js版本检查通过: $(node --version)"
    
    # 检查Maven
    if ! command -v mvn &> /dev/null && [ ! -f "src/backend/mvnw" ]; then
        log_error "Maven未安装，请先安装Maven 3.6+或使用Maven Wrapper"
        exit 1
    fi
    log_info "Maven检查通过"
    
    local deps=("docker" "docker-compose" "git")
    local missing_deps=()
    
    for dep in "${deps[@]}"; do
        if ! command -v "$dep" &> /dev/null; then
            missing_deps+=("$dep")
        fi
    done
    
    if [ ${#missing_deps[@]} -ne 0 ]; then
        log_warn "缺少以下可选依赖: ${missing_deps[*]}"
        log_info "这些依赖是可选的，但建议安装"
    fi
    
    log_info "所有依赖检查通过"
}

# 清理构建环境
clean_build_env() {
    log_step "清理构建环境..."
    
    # 停止现有容器
    docker-compose down 2>/dev/null || true
    
    # 清理悬挂镜像
    docker image prune -f || true
    
    # 清理构建缓存
    docker builder prune -f || true
    
    log_info "构建环境清理完成"
}

# 构建前端
build_frontend() {
    log_step "构建前端项目..."
    
    if [ ! -d "src/frontend" ]; then
        log_error "前端项目目录不存在: src/frontend"
        return 1
    fi
    
    cd src/frontend
    
    # 检查package.json
    if [ ! -f "package.json" ]; then
        log_error "未找到package.json文件"
        return 1
    fi
    
    # 清理旧的构建产物和缓存（确保每次构建都是全新的）
    log_info "清理旧的构建产物和缓存..."
    rm -rf dist
    rm -rf .vite
    rm -rf node_modules/.vite 2>/dev/null || true
    
    # 安装依赖
    log_info "安装前端依赖..."
    if command -v yarn &> /dev/null; then
        if [ -f "yarn.lock" ]; then
            yarn install --frozen-lockfile
        else
            log_warn "yarn.lock不存在，使用yarn install"
            yarn install
        fi
    else
        if [ -f "package-lock.json" ]; then
            npm ci
        else
            log_warn "package-lock.json不存在，使用npm install"
            npm install
        fi
    fi
    
    # 构建项目
    log_info "构建前端项目..."
    if command -v yarn &> /dev/null; then
        yarn build
    else
        npm run build
    fi
    
    cd ../..
    
    if [ -d "src/frontend/dist" ] && [ -n "$(ls -A src/frontend/dist 2>/dev/null)" ]; then
        log_info "前端构建成功"
    else
        log_error "前端构建失败：dist目录不存在或为空"
        return 1
    fi
}

# 构建后端
build_backend() {
    log_step "构建后端项目..."
    
    if [ ! -d "src/backend" ]; then
        log_error "后端项目目录不存在: src/backend"
        return 1
    fi
    
    cd src/backend
    
    # 检查pom.xml
    if [ ! -f "pom.xml" ]; then
        log_error "未找到pom.xml文件"
        return 1
    fi
    
    # 验证前端dist目录存在
    if [ ! -d "../frontend/dist" ] || [ -z "$(ls -A ../frontend/dist 2>/dev/null)" ]; then
        log_error "前端dist目录不存在或为空，无法构建后端"
        return 1
    fi
    
    # 确定Maven命令
    if [ -f "./mvnw" ]; then
        MVN_CMD="./mvnw"
    elif command -v mvn &> /dev/null; then
        MVN_CMD="mvn"
    else
        log_error "Maven未安装，请先安装Maven"
        return 1
    fi
    
    # 清理并构建
    log_info "清理后端项目..."
    $MVN_CMD clean
    
    log_info "构建后端项目..."
    $MVN_CMD package -DskipTests
    
    cd ../..
    
    if [ -f "src/backend/target/security-teaching-system.jar" ]; then
        log_info "后端构建成功"
    else
        log_error "后端构建失败"
        return 1
    fi
}

# 构建Docker镜像
build_docker_image() {
    log_step "构建Docker镜像..."
    
    local image_tag="${IMAGE_TAG:-security-teaching-system:latest}"
    local build_args="${BUILD_ARGS:-}"
    
    log_info "构建镜像标签: $image_tag"
    
    # 构建镜像
    docker build \
        $build_args \
        -t "$image_tag" \
        -f Dockerfile \
        .
    
    if [ $? -eq 0 ]; then
        log_info "Docker镜像构建成功: $image_tag"
    else
        log_error "Docker镜像构建失败"
        return 1
    fi
}

# 运行测试
run_tests() {
    log_step "运行测试..."
    
    # 后端测试
    if [ "$SKIP_TESTS" != "true" ]; then
        log_info "运行后端测试..."
        cd src/backend
        ./mvnw test || mvn test
        cd ../..
        
        # 前端测试
        log_info "运行前端测试..."
        cd src/frontend
        if command -v yarn &> /dev/null; then
            yarn test:unit
        else
            npm run test:unit
        fi
        cd ../..
    else
        log_warn "跳过测试阶段"
    fi
}

# 创建数据目录
create_data_dirs() {
    log_step "创建数据目录..."
    
    local dirs=("data/mysql" "data/logs" "data/uploads" "data/backups")
    
    for dir in "${dirs[@]}"; do
        mkdir -p "$dir"
        log_debug "创建目录: $dir"
    done
    
    log_info "数据目录创建完成"
}

# 验证构建结果
verify_build() {
    log_step "验证构建结果..."
    
    # 检查Docker镜像
    local image_tag="${IMAGE_TAG:-security-teaching-system:latest}"
    if docker images | grep -q "${image_tag%:*}"; then
        log_info "Docker镜像验证成功"
    else
        log_error "Docker镜像验证失败"
        return 1
    fi
    
    # 检查文件
    local files=("src/frontend/dist/index.html" "src/backend/target/security-teaching-system.jar")
    for file in "${files[@]}"; do
        if [ -f "$file" ]; then
            log_debug "文件存在: $file"
        else
            log_error "文件缺失: $file"
            return 1
        fi
    done
    
    log_info "构建结果验证通过"
}

# 显示构建信息
show_build_info() {
    log_step "构建信息汇总..."
    
    echo -e "${CYAN}========================================${NC}"
    echo -e "${CYAN}构建完成信息${NC}"
    echo -e "${CYAN}========================================${NC}"
    echo -e "镜像标签: ${IMAGE_TAG:-security-teaching-system:latest}"
    echo -e "构建时间: $(date)"
    echo -e "Git提交: $(git rev-parse --short HEAD 2>/dev/null || echo 'N/A')"
    echo -e "构建环境: $(uname -s)"
    echo -e "Docker版本: $(docker --version)"
    echo -e "${CYAN}========================================${NC}"
    
    log_info "使用以下命令启动应用:"
    echo -e "${GREEN}docker-compose up -d${NC}"
    echo -e "或者:"
    echo -e "${GREEN}docker run -d -p 8080:8080 ${IMAGE_TAG:-security-teaching-system:latest}${NC}"
}

# 主函数
main() {
    # 解析命令行参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            --clean)
                CLEAN_BUILD=true
                shift
                ;;
            --skip-tests)
                SKIP_TESTS=true
                shift
                ;;
            --tag)
                IMAGE_TAG="$2"
                shift 2
                ;;
            --build-args)
                BUILD_ARGS="$2"
                shift 2
                ;;
            --help)
                echo "用法: $0 [选项]"
                echo "选项:"
                echo "  --clean        清理构建环境"
                echo "  --skip-tests   跳过测试"
                echo "  --tag TAG      设置镜像标签"
                echo "  --build-args   Docker构建参数"
                echo "  --help         显示帮助"
                exit 0
                ;;
            *)
                log_error "未知参数: $1"
                exit 1
                ;;
        esac
    done
    
    # 记录开始时间
    local start_time=$(date +%s)
    
    # 显示横幅
    show_banner
    
    # 执行构建步骤
    check_dependencies
    
    if [ "$CLEAN_BUILD" = "true" ]; then
        clean_build_env
    fi
    
    create_data_dirs
    build_frontend
    build_backend
    
    if [ "$SKIP_TESTS" != "true" ]; then
        run_tests
    fi
    
    build_docker_image
    verify_build
    
    # 计算构建时间
    local end_time=$(date +%s)
    local build_time=$((end_time - start_time))
    
    show_build_info
    
    log_info "构建完成! 总耗时: ${build_time}秒"
}

# 错误处理
error_handler() {
    log_error "构建过程中发生错误，退出码: $?"
    exit 1
}

trap error_handler ERR

# 检查是否为直接执行
if [ "${BASH_SOURCE[0]}" = "${0}" ]; then
    main "$@"
fi

