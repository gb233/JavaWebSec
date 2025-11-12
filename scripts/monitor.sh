#!/bin/bash

# ========================================
# 系统监控脚本
# ========================================

set -e

# 配置
MONITOR_INTERVAL="${MONITOR_INTERVAL:-30}"
LOG_FILE="${LOG_FILE:-data/logs/monitor.log}"
ALERT_THRESHOLD_CPU="${ALERT_THRESHOLD_CPU:-80}"
ALERT_THRESHOLD_MEMORY="${ALERT_THRESHOLD_MEMORY:-80}"
ALERT_THRESHOLD_DISK="${ALERT_THRESHOLD_DISK:-80}"

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() {
    local msg="[$(date '+%Y-%m-%d %H:%M:%S')] [INFO] $1"
    echo -e "${GREEN}${msg}${NC}"
    echo "$msg" >> "$LOG_FILE"
}

log_warn() {
    local msg="[$(date '+%Y-%m-%d %H:%M:%S')] [WARN] $1"
    echo -e "${YELLOW}${msg}${NC}"
    echo "$msg" >> "$LOG_FILE"
}

log_error() {
    local msg="[$(date '+%Y-%m-%d %H:%M:%S')] [ERROR] $1"
    echo -e "${RED}${msg}${NC}"
    echo "$msg" >> "$LOG_FILE"
}

# 获取容器状态
get_container_stats() {
    log_info "获取容器状态..."
    
    if ! docker-compose ps | grep -q "Up"; then
        log_error "没有运行中的容器"
        return 1
    fi
    
    # 获取容器统计信息
    docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}\t{{.NetIO}}\t{{.BlockIO}}"
}

# 检查应用健康状态
check_app_health() {
    log_info "检查应用健康状态..."
    
    local health_url="http://localhost:8080/actuator/health"
    local response=$(curl -s --max-time 10 "$health_url" 2>/dev/null || echo "")
    
    if [ -z "$response" ]; then
        log_error "无法访问健康检查端点"
        return 1
    fi
    
    local status=$(echo "$response" | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
    
    if [ "$status" = "UP" ]; then
        log_info "应用状态: $status"
    else
        log_error "应用状态异常: $status"
        return 1
    fi
}

# 检查数据库状态
check_database_health() {
    log_info "检查数据库状态..."
    
    if docker-compose exec -T security-teaching-app mysql \
        -h localhost -u security_user -psecurity_pass \
        -e "SELECT 1" &> /dev/null; then
        log_info "数据库连接正常"
    else
        log_error "数据库连接失败"
        return 1
    fi
    
    # 检查数据库大小
    local db_size=$(docker-compose exec -T security-teaching-app mysql \
        -h localhost -u security_user -psecurity_pass \
        -e "SELECT ROUND(SUM(data_length + index_length) / 1024 / 1024, 1) AS 'DB Size in MB' FROM information_schema.tables WHERE table_schema='javaweb_security';" \
        2>/dev/null | tail -n 1)
    
    if [ ! -z "$db_size" ]; then
        log_info "数据库大小: ${db_size} MB"
    fi
}

# 检查系统资源
check_system_resources() {
    log_info "检查系统资源..."
    
    # CPU使用率
    local cpu_usage=$(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | cut -d'%' -f1)
    cpu_usage=${cpu_usage%%.*}  # 取整数部分
    
    if [ "$cpu_usage" -gt "$ALERT_THRESHOLD_CPU" ]; then
        log_error "CPU使用率过高: ${cpu_usage}%"
    else
        log_info "CPU使用率: ${cpu_usage}%"
    fi
    
    # 内存使用率
    local mem_info=$(free | grep Mem)
    local total=$(echo $mem_info | awk '{print $2}')
    local used=$(echo $mem_info | awk '{print $3}')
    local mem_usage=$((used * 100 / total))
    
    if [ "$mem_usage" -gt "$ALERT_THRESHOLD_MEMORY" ]; then
        log_error "内存使用率过高: ${mem_usage}%"
    else
        log_info "内存使用率: ${mem_usage}%"
    fi
    
    # 磁盘使用率
    local disk_usage=$(df / | awk 'NR==2 {print $5}' | sed 's/%//')
    
    if [ "$disk_usage" -gt "$ALERT_THRESHOLD_DISK" ]; then
        log_error "磁盘使用率过高: ${disk_usage}%"
    else
        log_info "磁盘使用率: ${disk_usage}%"
    fi
}

# 检查日志错误
check_error_logs() {
    log_info "检查错误日志..."
    
    local error_count=$(docker-compose logs --since="5m" 2>&1 | grep -i "error\|exception\|fail" | wc -l)
    
    if [ "$error_count" -gt 10 ]; then
        log_warn "最近5分钟内发现 $error_count 个错误日志"
    else
        log_info "最近5分钟内错误日志数量: $error_count"
    fi
}

# 性能指标收集
collect_metrics() {
    log_info "收集性能指标..."
    
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    local metrics_file="data/logs/metrics.csv"
    
    # 创建CSV头部（如果文件不存在）
    if [ ! -f "$metrics_file" ]; then
        echo "timestamp,cpu_usage,memory_usage,disk_usage,app_status,db_status" > "$metrics_file"
    fi
    
    # 获取指标数据
    local cpu_usage=$(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | cut -d'%' -f1 | cut -d'.' -f1)
    local mem_usage=$(free | grep Mem | awk '{printf "%.1f", $3/$2 * 100.0}')
    local disk_usage=$(df / | awk 'NR==2 {print $5}' | sed 's/%//')
    
    # 检查应用状态
    local app_status="DOWN"
    if curl -s --max-time 5 "http://localhost:8080/actuator/health" | grep -q '"status":"UP"'; then
        app_status="UP"
    fi
    
    # 检查数据库状态
    local db_status="DOWN"
    if docker-compose exec -T security-teaching-app mysql -h localhost -u security_user -psecurity_pass -e "SELECT 1" &> /dev/null; then
        db_status="UP"
    fi
    
    # 写入CSV
    echo "$timestamp,$cpu_usage,$mem_usage,$disk_usage,$app_status,$db_status" >> "$metrics_file"
    
    log_info "性能指标已记录"
}

# 清理旧日志
cleanup_logs() {
    log_info "清理旧日志文件..."
    
    # 清理超过30天的日志文件
    find data/logs/ -name "*.log" -mtime +30 -delete 2>/dev/null || true
    
    # 清理超过7天的容器日志
    docker system prune -f --volumes --filter "until=168h" 2>/dev/null || true
    
    log_info "日志清理完成"
}

# 生成监控报告
generate_report() {
    log_info "生成监控报告..."
    
    local report_file="data/logs/monitor_report_$(date +%Y%m%d).html"
    
    cat > "$report_file" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>系统监控报告 - $(date +%Y-%m-%d)</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .header { background: #f0f0f0; padding: 10px; border-radius: 5px; }
        .section { margin: 20px 0; }
        .metric { display: inline-block; margin: 10px; padding: 10px; border: 1px solid #ccc; border-radius: 5px; }
        .error { color: red; }
        .warning { color: orange; }
        .success { color: green; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Java Web安全教学系统 - 监控报告</h1>
        <p>生成时间: $(date)</p>
    </div>
    
    <div class="section">
        <h2>系统状态</h2>
        <div class="metric">
            <strong>应用状态:</strong> 
            <span class="$(curl -s --max-time 5 "http://localhost:8080/actuator/health" | grep -q '"status":"UP"' && echo 'success' || echo 'error')">
                $(curl -s --max-time 5 "http://localhost:8080/actuator/health" | grep -q '"status":"UP"' && echo 'UP' || echo 'DOWN')
            </span>
        </div>
        
        <div class="metric">
            <strong>数据库状态:</strong>
            <span class="$(docker-compose exec -T security-teaching-app mysql -h localhost -u security_user -psecurity_pass -e "SELECT 1" &> /dev/null && echo 'success' || echo 'error')">
                $(docker-compose exec -T security-teaching-app mysql -h localhost -u security_user -psecurity_pass -e "SELECT 1" &> /dev/null && echo 'UP' || echo 'DOWN')
            </span>
        </div>
    </div>
    
    <div class="section">
        <h2>资源使用</h2>
        <div class="metric">
            <strong>CPU使用率:</strong> $(top -bn1 | grep "Cpu(s)" | awk '{print $2}')
        </div>
        
        <div class="metric">
            <strong>内存使用率:</strong> $(free | grep Mem | awk '{printf "%.1f%%", $3/$2 * 100.0}')
        </div>
        
        <div class="metric">
            <strong>磁盘使用率:</strong> $(df / | awk 'NR==2 {print $5}')
        </div>
    </div>
    
    <div class="section">
        <h2>容器状态</h2>
        <pre>$(docker-compose ps)</pre>
    </div>
</body>
</html>
EOF
    
    log_info "监控报告已生成: $report_file"
}

# 持续监控模式
continuous_monitor() {
    log_info "启动持续监控模式，间隔: ${MONITOR_INTERVAL}秒"
    
    while true; do
        echo "========================================="
        log_info "开始监控检查 - $(date)"
        
        get_container_stats
        check_app_health
        check_database_health
        check_system_resources
        check_error_logs
        collect_metrics
        
        # 每小时清理一次日志
        if [ $(($(date +%M))) -eq 0 ]; then
            cleanup_logs
        fi
        
        # 每天生成一次报告
        if [ "$(date +%H:%M)" = "00:00" ]; then
            generate_report
        fi
        
        log_info "监控检查完成，等待下次检查..."
        sleep "$MONITOR_INTERVAL"
    done
}

# 主函数
main() {
    # 创建日志目录
    mkdir -p "$(dirname "$LOG_FILE")"
    
    case "${1:-continuous}" in
        "continuous")
            continuous_monitor
            ;;
        "once")
            get_container_stats
            check_app_health
            check_database_health
            check_system_resources
            check_error_logs
            collect_metrics
            ;;
        "report")
            generate_report
            ;;
        "cleanup")
            cleanup_logs
            ;;
        *)
            echo "用法: $0 [continuous|once|report|cleanup]"
            echo "  continuous - 持续监控模式（默认）"
            echo "  once      - 单次检查"
            echo "  report    - 生成报告"
            echo "  cleanup   - 清理日志"
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"

