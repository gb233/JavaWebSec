#!/bin/bash

# ========================================
# Ubuntu 22.04 LTS Docker 一键部署脚本
# Java Web 安全教学系统
# ========================================

set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
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

# 检查是否为 root 用户
check_root() {
    if [ "$EUID" -eq 0 ]; then 
        log_error "请不要使用 root 用户运行此脚本"
        exit 1
    fi
}

# 检查系统版本
check_system() {
    log_info "检查系统版本..."
    if [ ! -f /etc/os-release ]; then
        log_error "无法检测系统版本"
        exit 1
    fi
    
    . /etc/os-release
    if [ "$ID" != "ubuntu" ] || [ "$VERSION_ID" != "22.04" ]; then
        log_warn "检测到系统: $ID $VERSION_ID"
        log_warn "此脚本针对 Ubuntu 22.04 LTS 优化，其他版本可能存在问题"
        read -p "是否继续? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    else
        log_info "系统版本: Ubuntu 22.04 LTS ✓"
    fi
}

# 安装 Docker
install_docker() {
    log_info "检查 Docker 安装状态..."
    if command -v docker &> /dev/null; then
        log_info "Docker 已安装: $(docker --version)"
        return
    fi
    
    log_info "安装 Docker..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    rm get-docker.sh
    
    # 将当前用户添加到 docker 组
    sudo usermod -aG docker $USER
    log_info "已将用户 $USER 添加到 docker 组"
    log_warn "需要重新登录或执行 'newgrp docker' 才能使用 docker 命令"
}

# 安装 Docker Compose
install_docker_compose() {
    log_info "检查 Docker Compose 安装状态..."
    if command -v docker-compose &> /dev/null; then
        log_info "Docker Compose 已安装: $(docker-compose --version)"
        return
    fi
    
    log_info "安装 Docker Compose..."
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    
    log_info "Docker Compose 安装完成"
}

# 检查项目文件
check_project() {
    log_info "检查项目文件..."
    
    if [ ! -f "docker-compose.prod.yml" ]; then
        log_error "未找到 docker-compose.prod.yml 文件"
        log_error "请确保在项目根目录运行此脚本"
        exit 1
    fi
    
    if [ ! -f "Dockerfile.backend" ] && [ ! -f "Dockerfile" ]; then
        log_error "未找到 Dockerfile 文件"
        exit 1
    fi
    
    log_info "项目文件检查通过 ✓"
}

# 创建环境变量文件
create_env_file() {
    log_info "检查 .env 文件..."
    
    if [ -f ".env" ]; then
        log_warn ".env 文件已存在"
        read -p "是否覆盖现有 .env 文件? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log_info "使用现有 .env 文件"
            return
        fi
    fi
    
    log_info "创建 .env 文件..."
    
    # 生成随机密码
    MYSQL_ROOT_PASS=$(openssl rand -base64 24 | tr -d "=+/" | cut -c1-25)
    MYSQL_USER_PASS=$(openssl rand -base64 24 | tr -d "=+/" | cut -c1-25)
    JWT_SECRET=$(openssl rand -base64 64 | tr -d "=+/" | cut -c1-64)
    
    cat > .env << EOF
# ========================================
# Java Web 安全教学系统 - 环境变量配置
# ========================================
# 自动生成时间: $(date '+%Y-%m-%d %H:%M:%S')

# 数据库配置
MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASS}
MYSQL_DATABASE=security_teaching_system
MYSQL_USER=security_user
MYSQL_PASSWORD=${MYSQL_USER_PASS}
MYSQL_PORT=3306

# 应用配置
JWT_SECRET=${JWT_SECRET}
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# 端口配置
BACKEND_PORT=8080
FRONTEND_PORT=80

# CORS配置（根据实际域名修改）
CORS_ALLOWED_ORIGINS=http://localhost:80,http://localhost:3000

# Java配置
JAVA_OPTS=-Xms512m -Xmx1024m -Djava.security.egd=file:/dev/./urandom
EOF
    
    log_info ".env 文件已创建"
    log_warn "请妥善保管 .env 文件中的密码和密钥！"
    log_info "MySQL Root 密码: ${MYSQL_ROOT_PASS}"
    log_info "MySQL User 密码: ${MYSQL_USER_PASS}"
    log_info "JWT Secret: ${JWT_SECRET}"
}

# 启动服务
start_services() {
    log_info "启动 Docker 服务..."
    
    # 检查 Docker 服务是否运行
    if ! sudo systemctl is-active --quiet docker; then
        log_info "启动 Docker 服务..."
        sudo systemctl start docker
    fi
    
    # 使用 newgrp 临时切换到 docker 组（如果当前会话还没有权限）
    if ! docker ps &> /dev/null; then
        log_warn "当前会话没有 Docker 权限，尝试切换到 docker 组..."
        log_warn "如果仍然失败，请重新登录或执行: newgrp docker"
    fi
    
    log_info "构建并启动容器（这可能需要几分钟）..."
    docker-compose -f docker-compose.prod.yml up -d --build
    
    log_info "等待服务启动..."
    sleep 10
    
    # 检查服务状态
    log_info "检查服务状态..."
    docker-compose -f docker-compose.prod.yml ps
}

# 验证部署
verify_deployment() {
    log_info "验证部署..."
    
    # 等待服务完全启动
    log_info "等待服务完全启动（60秒）..."
    sleep 60
    
    # 检查健康状态
    log_info "检查后端健康状态..."
    if curl -f -s http://localhost:8080/actuator/health > /dev/null; then
        HEALTH=$(curl -s http://localhost:8080/actuator/health)
        log_info "后端健康状态: $HEALTH"
    else
        log_error "后端健康检查失败"
        log_info "查看日志: docker-compose -f docker-compose.prod.yml logs backend"
        return 1
    fi
    
    # 检查前端
    log_info "检查前端状态..."
    if curl -f -s http://localhost:80 > /dev/null; then
        log_info "前端服务正常 ✓"
    else
        log_warn "前端服务可能还未完全启动"
    fi
    
    # 检查数据库
    log_info "检查数据库连接..."
    if docker exec security-teaching-mysql mysqladmin ping -h localhost --silent; then
        log_info "数据库连接正常 ✓"
    else
        log_warn "数据库可能还未完全启动"
    fi
}

# 显示部署信息
show_deployment_info() {
    echo ""
    echo "=========================================="
    echo "  部署完成！"
    echo "=========================================="
    echo ""
    echo "访问地址:"
    echo "  前端: http://localhost:80"
    echo "  后端API: http://localhost:8080"
    echo "  健康检查: http://localhost:8080/actuator/health"
    echo ""
    echo "常用命令:"
    echo "  查看日志: docker-compose -f docker-compose.prod.yml logs -f"
    echo "  停止服务: docker-compose -f docker-compose.prod.yml down"
    echo "  重启服务: docker-compose -f docker-compose.prod.yml restart"
    echo "  查看状态: docker-compose -f docker-compose.prod.yml ps"
    echo ""
    echo "重要提示:"
    echo "  - 请妥善保管 .env 文件中的密码和密钥"
    echo "  - 生产环境请修改 CORS_ALLOWED_ORIGINS"
    echo "  - 建议配置防火墙和 HTTPS"
    echo ""
}

# 主函数
main() {
    echo "=========================================="
    echo "  Ubuntu 22.04 LTS Docker 部署脚本"
    echo "  Java Web 安全教学系统"
    echo "=========================================="
    echo ""
    
    check_root
    check_system
    
    log_info "更新系统包..."
    sudo apt update -qq
    
    log_info "安装必要工具..."
    sudo apt install -y curl wget git openssl > /dev/null 2>&1
    
    install_docker
    install_docker_compose
    
    # 如果 Docker 未在当前会话生效，提示用户
    if ! docker ps &> /dev/null; then
        log_warn "Docker 权限可能还未生效"
        log_warn "如果后续步骤失败，请执行: newgrp docker"
        log_warn "然后重新运行此脚本"
        echo ""
        read -p "是否继续? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi
    
    check_project
    create_env_file
    
    read -p "是否现在启动服务? (Y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Nn]$ ]]; then
        log_info "已跳过服务启动"
        log_info "稍后可以运行: docker-compose -f docker-compose.prod.yml up -d --build"
        exit 0
    fi
    
    start_services
    verify_deployment
    show_deployment_info
}

# 运行主函数
main









