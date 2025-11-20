#!/bin/bash

# Java Web安全教学系统 - 发布构建脚本
# 用于构建发布版本的JAR包和Docker镜像

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查环境
check_environment() {
    log_info "检查构建环境..."
    
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
    
    # 检查Maven
    if ! command -v mvn &> /dev/null; then
        log_error "Maven未安装，请先安装Maven 3.6+"
        exit 1
    fi
    
    # 检查Node.js
    if ! command -v node &> /dev/null; then
        log_error "Node.js未安装，请先安装Node.js 18+"
        exit 1
    fi
    
    # 检查Docker（可选）
    if command -v docker &> /dev/null; then
        DOCKER_AVAILABLE=true
        log_info "Docker可用，将构建Docker镜像"
    else
        DOCKER_AVAILABLE=false
        log_warning "Docker不可用，跳过Docker镜像构建"
    fi
    
    log_success "环境检查完成"
}

# 清理构建目录
clean_build() {
    log_info "清理构建目录..."
    
    # 清理Maven构建
    if [ -d "src/backend/target" ]; then
        rm -rf src/backend/target
    fi
    
    # 清理前端构建
    if [ -d "src/frontend/dist" ]; then
        rm -rf src/frontend/dist
    fi
    
    # 清理发布目录
    if [ -d "release" ]; then
        rm -rf release
    fi
    
    mkdir -p release
    
    log_success "构建目录清理完成"
}

# 构建后端
build_backend() {
    log_info "构建后端应用..."
    
    cd src/backend
    
    # 验证前端dist目录存在
    if [ ! -d "../frontend/dist" ] || [ -z "$(ls -A ../frontend/dist 2>/dev/null)" ]; then
        log_error "前端dist目录不存在或为空，无法构建后端"
        return 1
    fi
    
    # 清理、测试、打包
    log_info "清理后端项目..."
    mvn clean
    
    # 运行测试
    log_info "运行后端测试..."
    mvn test -DskipTests=false
    
    # 构建JAR包
    log_info "构建JAR包..."
    mvn package -DskipTests -Pprod
    
    # 复制JAR包到发布目录（明确指定JAR文件名）
    JAR_FILE="target/security-teaching-system.jar"
    if [ ! -f "$JAR_FILE" ]; then
        log_error "JAR文件不存在: $JAR_FILE"
        return 1
    fi
    cp "$JAR_FILE" ../../release/security-teaching-system.jar
    
    cd ../..
    
    log_success "后端构建完成"
}

# 构建前端
build_frontend() {
    log_info "构建前端应用..."
    
    cd src/frontend
    
    # 清理旧的构建产物和缓存（确保每次构建都是全新的）
    log_info "清理旧的构建产物和缓存..."
    rm -rf dist
    rm -rf .vite
    rm -rf node_modules/.vite 2>/dev/null || true
    
    # 安装依赖（构建需要开发依赖，不能使用--only=production）
    log_info "安装前端依赖..."
    if [ -f "package-lock.json" ]; then
        npm ci
    else
        log_warning "package-lock.json不存在，使用npm install"
        npm install
    fi
    
    # 构建前端
    log_info "构建前端应用..."
    npm run build
    
    # 验证构建结果
    if [ ! -d "dist" ] || [ -z "$(ls -A dist 2>/dev/null)" ]; then
        log_error "前端构建失败：dist目录不存在或为空"
        return 1
    fi
    
    # 复制构建文件到发布目录
    cp -r dist ../../release/frontend
    
    cd ../..
    
    log_success "前端构建完成"
}

# 构建Docker镜像
build_docker() {
    if [ "$DOCKER_AVAILABLE" = false ]; then
        log_warning "跳过Docker镜像构建"
        return
    fi
    
    log_info "构建Docker镜像..."
    
    # 构建镜像
    docker build -t javaweb-security-teaching-system:latest .
    
    # 保存镜像
    docker save javaweb-security-teaching-system:latest | gzip > release/security-teaching-system.tar.gz
    
    log_success "Docker镜像构建完成"
}

# 创建发布包
create_release_package() {
    log_info "创建发布包..."
    
    # 创建版本信息
    VERSION=$(date +%Y%m%d_%H%M%S)
    echo "version=$VERSION" > release/version.txt
    echo "build_date=$(date)" >> release/version.txt
    echo "git_commit=$(git rev-parse HEAD 2>/dev/null || echo 'unknown')" >> release/version.txt
    
    # 复制配置文件
    cp env.example release/
    cp docker-compose.yml release/
    cp Dockerfile release/
    
    # 复制部署脚本
    cp scripts/deploy.sh release/
    cp scripts/health-check.sh release/
    chmod +x release/*.sh
    
    # 复制文档
    cp -r docs release/
    cp README.md release/
    cp LICENSE release/
    cp CONTRIBUTING.md release/
    
    # 创建启动脚本
    cat > release/start.sh << 'EOF'
#!/bin/bash
# Java Web安全教学系统启动脚本

echo "启动Java Web安全教学系统..."

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误：Java未安装，请先安装Java 17+"
    exit 1
fi

# 检查配置文件
if [ ! -f "application.yml" ]; then
    echo "警告：未找到application.yml配置文件，使用默认配置"
fi

# 启动应用
java -jar security-teaching-system.jar

echo "系统启动完成，访问 http://localhost:8080"
EOF
    chmod +x release/start.sh
    
    # 创建Docker启动脚本
    cat > release/docker-start.sh << 'EOF'
#!/bin/bash
# Docker方式启动脚本

echo "使用Docker启动Java Web安全教学系统..."

# 检查Docker
if ! command -v docker &> /dev/null; then
    echo "错误：Docker未安装，请先安装Docker"
    exit 1
fi

# 检查docker-compose
if ! command -v docker-compose &> /dev/null; then
    echo "错误：docker-compose未安装，请先安装docker-compose"
    exit 1
fi

# 启动服务
docker-compose up -d

echo "系统启动完成，访问 http://localhost:8080"
echo "查看日志：docker-compose logs -f"
EOF
    chmod +x release/docker-start.sh
    
    # 创建压缩包
    log_info "创建发布压缩包..."
    tar -czf "javaweb-security-teaching-system-${VERSION}.tar.gz" -C release .
    
    log_success "发布包创建完成：javaweb-security-teaching-system-${VERSION}.tar.gz"
}

# 验证构建结果
verify_build() {
    log_info "验证构建结果..."
    
    # 检查JAR包
    if [ -f "release/security-teaching-system.jar" ]; then
        log_success "JAR包构建成功"
    else
        log_error "JAR包构建失败"
        exit 1
    fi
    
    # 检查前端文件
    if [ -d "release/frontend" ]; then
        log_success "前端构建成功"
    else
        log_error "前端构建失败"
        exit 1
    fi
    
    # 检查Docker镜像
    if [ "$DOCKER_AVAILABLE" = true ] && [ -f "release/security-teaching-system.tar.gz" ]; then
        log_success "Docker镜像构建成功"
    fi
    
    log_success "构建验证完成"
}

# 显示构建信息
show_build_info() {
    log_info "构建信息："
    echo "  版本：$(cat release/version.txt | grep version | cut -d'=' -f2)"
    echo "  构建时间：$(cat release/version.txt | grep build_date | cut -d'=' -f2)"
    echo "  Git提交：$(cat release/version.txt | grep git_commit | cut -d'=' -f2)"
    echo ""
    log_info "发布文件："
    echo "  JAR包：release/security-teaching-system.jar"
    echo "  前端：release/frontend/"
    echo "  文档：release/docs/"
    echo "  脚本：release/start.sh, release/docker-start.sh"
    if [ "$DOCKER_AVAILABLE" = true ]; then
        echo "  Docker镜像：release/security-teaching-system.tar.gz"
    fi
    echo ""
    log_info "使用方法："
    echo "  1. JAR包方式：java -jar security-teaching-system.jar"
    echo "  2. Docker方式：./docker-start.sh"
    echo "  3. 源码方式：参考README.md"
}

# 主函数
main() {
    log_info "开始构建Java Web安全教学系统发布版本..."
    
    check_environment
    clean_build
    # 修复：先构建前端，再构建后端（后端需要前端dist目录）
    build_frontend
    build_backend
    build_docker
    create_release_package
    verify_build
    show_build_info
    
    log_success "构建完成！"
}

# 执行主函数
main "$@"
