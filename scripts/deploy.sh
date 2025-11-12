#!/bin/bash

# ========================================
# 部署脚本 - Java Web安全教学系统
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
    Docker部署脚本
========================================
EOF
    echo -e "${NC}"
}

# 检查环境
check_environment() {
    log_step "检查部署环境..."
    
    # 检查Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker未安装或不在PATH中"
        exit 1
    fi
    
    # 检查Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose未安装或不在PATH中"
        exit 1
    fi
    
    # 检查Docker服务状态
    if ! docker info &> /dev/null; then
        log_error "Docker服务未运行"
        exit 1
    fi
    
    log_info "环境检查通过"
}

# 加载环境变量
load_environment() {
    log_step "加载环境变量..."
    
    # 默认环境变量
    export DEPLOY_ENV="${DEPLOY_ENV:-production}"
    export IMAGE_TAG="${IMAGE_TAG:-security-teaching-system:latest}"
    export MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-$(openssl rand -base64 32)}"
    export MYSQL_PASSWORD="${MYSQL_PASSWORD:-$(openssl rand -base64 16)}"
    export JWT_SECRET="${JWT_SECRET:-$(openssl rand -base64 64)}"
    
    # 加载环境文件
    local env_file=".env.${DEPLOY_ENV}"
    if [ -f "$env_file" ]; then
        log_info "加载环境文件: $env_file"
        set -a
        source "$env_file"
        set +a
    else
        log_warn "环境文件不存在: $env_file"
    fi
    
    log_info "环境变量加载完成"
    log_debug "部署环境: $DEPLOY_ENV"
    log_debug "镜像标签: $IMAGE_TAG"
}

# 创建必要目录
create_directories() {
    log_step "创建必要目录..."
    
    local dirs=(
        "data/mysql"
        "data/logs" 
        "data/uploads"
        "data/backups"
        "data/ssl"
        "config"
    )
    
    for dir in "${dirs[@]}"; do
        if [ ! -d "$dir" ]; then
            mkdir -p "$dir"
            log_debug "创建目录: $dir"
        fi
    done
    
    # 设置目录权限
    chmod 755 data/
    chmod 700 data/mysql
    chmod 755 data/logs
    chmod 755 data/uploads
    chmod 700 data/backups
    
    log_info "目录创建完成"
}

# 生成配置文件
generate_configs() {
    log_step "生成配置文件..."
    
    # 生成Docker Compose环境文件
    cat > .env << EOF
# 自动生成的环境配置文件
# 生成时间: $(date)

# 部署环境
DEPLOY_ENV=${DEPLOY_ENV}
COMPOSE_PROJECT_NAME=security-teaching-system

# 镜像配置
IMAGE_TAG=${IMAGE_TAG}

# 数据库配置
MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
MYSQL_DATABASE=javaweb_security
MYSQL_USER=security_user
MYSQL_PASSWORD=${MYSQL_PASSWORD}

# 应用配置
JWT_SECRET=${JWT_SECRET}
SERVER_PORT=8080
CORS_ALLOWED_ORIGINS=${CORS_ALLOWED_ORIGINS:-*}

# 网络配置
EXTERNAL_PORT=${EXTERNAL_PORT:-8080}
MYSQL_EXTERNAL_PORT=${MYSQL_EXTERNAL_PORT:-3306}

# 日志级别
LOG_LEVEL=${LOG_LEVEL:-INFO}

# 其他配置
TZ=Asia/Shanghai
EOF
    
    log_info "配置文件生成完成"
}

# 拉取镜像
pull_images() {
    log_step "拉取Docker镜像..."
    
    if [ "$BUILD_LOCAL" != "true" ]; then
        # 拉取预构建镜像
        if docker pull "$IMAGE_TAG" 2>/dev/null; then
            log_info "镜像拉取成功: $IMAGE_TAG"
        else
            log_warn "无法拉取镜像，将使用本地构建"
            BUILD_LOCAL=true
        fi
    fi
    
    if [ "$BUILD_LOCAL" = "true" ]; then
        log_info "使用本地构建镜像"
        ./scripts/build.sh --tag "$IMAGE_TAG"
    fi
}

# 数据库备份
backup_database() {
    log_step "备份现有数据库..."
    
    if docker-compose ps | grep -q mysql; then
        local backup_file="data/backups/backup_$(date +%Y%m%d_%H%M%S).sql"
        
        log_info "创建数据库备份: $backup_file"
        
        docker-compose exec -T mysql-dev mysqldump \
            -u root -p"${MYSQL_ROOT_PASSWORD}" \
            --single-transaction \
            --routines \
            --triggers \
            "${MYSQL_DATABASE}" > "$backup_file"
        
        if [ $? -eq 0 ]; then
            log_info "数据库备份成功"
        else
            log_warn "数据库备份失败"
        fi
    else
        log_info "没有运行中的数据库，跳过备份"
    fi
}

# 停止现有服务
stop_services() {
    log_step "停止现有服务..."
    
    if [ -f "docker-compose.yml" ]; then
        docker-compose down --remove-orphans || true
        log_info "现有服务已停止"
    else
        log_warn "未找到docker-compose.yml文件"
    fi
}

# 启动服务
start_services() {
    log_step "启动服务..."
    
    # 启动服务
    docker-compose up -d
    
    if [ $? -eq 0 ]; then
        log_info "服务启动成功"
    else
        log_error "服务启动失败"
        return 1
    fi
}

# 等待服务就绪
wait_for_services() {
    log_step "等待服务就绪..."
    
    local max_attempts=60
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f http://localhost:${EXTERNAL_PORT:-8080}/actuator/health &> /dev/null; then
            log_info "应用服务已就绪"
            return 0
        fi
        
        log_debug "等待应用启动... ($attempt/$max_attempts)"
        sleep 5
        attempt=$((attempt + 1))
    done
    
    log_error "应用启动超时"
    return 1
}

# 验证部署
verify_deployment() {
    log_step "验证部署..."
    
    # 检查容器状态
    local containers=$(docker-compose ps -q)
    if [ -z "$containers" ]; then
        log_error "没有运行的容器"
        return 1
    fi
    
    # 检查应用健康状态
    local health_response=$(curl -s http://localhost:${EXTERNAL_PORT:-8080}/actuator/health)
    if echo "$health_response" | grep -q '"status":"UP"'; then
        log_info "应用健康检查通过"
    else
        log_error "应用健康检查失败"
        log_debug "健康检查响应: $health_response"
        return 1
    fi
    
    # 检查数据库连接
    if docker-compose exec -T security-teaching-app mysql \
        -h localhost -u "${MYSQL_USER}" -p"${MYSQL_PASSWORD}" \
        -e "SELECT 1" &> /dev/null; then
        log_info "数据库连接正常"
    else
        log_error "数据库连接失败"
        return 1
    fi
    
    log_info "部署验证通过"
}

# 显示部署信息
show_deployment_info() {
    log_step "部署信息汇总..."
    
    echo -e "${CYAN}========================================${NC}"
    echo -e "${CYAN}部署完成信息${NC}"
    echo -e "${CYAN}========================================${NC}"
    echo -e "部署环境: ${DEPLOY_ENV}"
    echo -e "镜像版本: ${IMAGE_TAG}"
    echo -e "应用地址: http://localhost:${EXTERNAL_PORT:-8080}"
    echo -e "API文档: http://localhost:${EXTERNAL_PORT:-8080}/swagger-ui.html"
    echo -e "健康检查: http://localhost:${EXTERNAL_PORT:-8080}/actuator/health"
    echo -e "部署时间: $(date)"
    echo -e "${CYAN}========================================${NC}"
    
    log_info "常用命令:"
    echo -e "${GREEN}查看日志: docker-compose logs -f${NC}"
    echo -e "${GREEN}重启服务: docker-compose restart${NC}"
    echo -e "${GREEN}停止服务: docker-compose down${NC}"
    echo -e "${GREEN}查看状态: docker-compose ps${NC}"
}

# 创建管理脚本
create_management_scripts() {
    log_step "创建管理脚本..."
    
    # 创建启动脚本
    cat > start.sh << 'EOF'
#!/bin/bash
echo "启动Java Web安全教学系统..."
docker-compose up -d
echo "服务启动完成"
EOF
    
    # 创建停止脚本
    cat > stop.sh << 'EOF'
#!/bin/bash
echo "停止Java Web安全教学系统..."
docker-compose down
echo "服务停止完成"
EOF
    
    # 创建重启脚本
    cat > restart.sh << 'EOF'
#!/bin/bash
echo "重启Java Web安全教学系统..."
docker-compose restart
echo "服务重启完成"
EOF
    
    # 创建日志查看脚本
    cat > logs.sh << 'EOF'
#!/bin/bash
echo "查看系统日志..."
docker-compose logs -f
EOF
    
    # 设置执行权限
    chmod +x start.sh stop.sh restart.sh logs.sh
    
    log_info "管理脚本创建完成"
}

# 滚动更新
rolling_update() {
    log_step "执行滚动更新..."
    
    # 拉取新镜像
    docker-compose pull
    
    # 重新创建并启动容器
    docker-compose up -d --force-recreate
    
    # 清理旧镜像
    docker image prune -f
    
    log_info "滚动更新完成"
}

# 回滚部署
rollback_deployment() {
    log_step "回滚部署..."
    
    local backup_tag="${ROLLBACK_TAG:-security-teaching-system:previous}"
    
    log_info "回滚到镜像: $backup_tag"
    
    # 停止当前服务
    docker-compose down
    
    # 更新镜像标签
    export IMAGE_TAG="$backup_tag"
    
    # 重新启动服务
    docker-compose up -d
    
    log_info "回滚完成"
}

# 主函数
main() {
    # 解析命令行参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            --env)
                DEPLOY_ENV="$2"
                shift 2
                ;;
            --tag)
                IMAGE_TAG="$2"
                shift 2
                ;;
            --build-local)
                BUILD_LOCAL=true
                shift
                ;;
            --skip-backup)
                SKIP_BACKUP=true
                shift
                ;;
            --rolling-update)
                ROLLING_UPDATE=true
                shift
                ;;
            --rollback)
                ROLLBACK=true
                ROLLBACK_TAG="$2"
                shift 2
                ;;
            --help)
                echo "用法: $0 [选项]"
                echo "选项:"
                echo "  --env ENV           设置部署环境 (default: production)"
                echo "  --tag TAG           设置镜像标签"
                echo "  --build-local       使用本地构建"
                echo "  --skip-backup       跳过数据库备份"
                echo "  --rolling-update    执行滚动更新"
                echo "  --rollback TAG      回滚到指定标签"
                echo "  --help              显示帮助"
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
    
    # 执行部署步骤
    check_environment
    load_environment
    create_directories
    generate_configs
    
    # 根据参数执行不同操作
    if [ "$ROLLBACK" = "true" ]; then
        rollback_deployment
    elif [ "$ROLLING_UPDATE" = "true" ]; then
        rolling_update
    else
        # 正常部署流程
        if [ "$SKIP_BACKUP" != "true" ]; then
            backup_database
        fi
        
        stop_services
        pull_images
        start_services
        wait_for_services
        verify_deployment
        create_management_scripts
    fi
    
    # 计算部署时间
    local end_time=$(date +%s)
    local deploy_time=$((end_time - start_time))
    
    show_deployment_info
    
    log_info "部署完成! 总耗时: ${deploy_time}秒"
}

# 错误处理
error_handler() {
    log_error "部署过程中发生错误，退出码: $?"
    
    # 显示容器状态
    echo -e "\n${YELLOW}容器状态:${NC}"
    docker-compose ps || true
    
    # 显示日志
    echo -e "\n${YELLOW}最近的日志:${NC}"
    docker-compose logs --tail=20 || true
    
    exit 1
}

trap error_handler ERR

# 检查是否为直接执行
if [ "${BASH_SOURCE[0]}" = "${0}" ]; then
    main "$@"
fi