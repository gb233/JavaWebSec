# Docker 部署指南

Java Web安全教学系统的Docker容器化部署说明。

## 🚀 快速开始

### 1. 环境要求

- Docker 20.10+
- Docker Compose 2.0+
- 4GB+ 内存
- 20GB+ 磁盘空间

### 2. 一键部署

```bash
# 克隆项目
git clone <repository-url>
cd javaweb安全教学系统

# 构建并启动
./scripts/build.sh
./scripts/deploy.sh
```

### 3. 访问应用

- 应用地址: http://localhost:8080
- API文档: http://localhost:8080/swagger-ui.html
- 健康检查: http://localhost:8080/actuator/health

## 📋 详细说明

### 构建选项

```bash
# 基础构建
./scripts/build.sh

# 清理构建
./scripts/build.sh --clean

# 跳过测试
./scripts/build.sh --skip-tests

# 自定义标签
./scripts/build.sh --tag my-tag:1.0.0
```

### 部署选项

```bash
# 生产环境部署
./scripts/deploy.sh --env production

# 使用本地构建
./scripts/deploy.sh --build-local

# 跳过数据备份
./scripts/deploy.sh --skip-backup

# 滚动更新
./scripts/deploy.sh --rolling-update

# 回滚部署
./scripts/deploy.sh --rollback previous-tag
```

### 管理命令

```bash
# 启动服务
./start.sh
# 或者
docker-compose up -d

# 停止服务
./stop.sh
# 或者
docker-compose down

# 重启服务
./restart.sh
# 或者
docker-compose restart

# 查看日志
./logs.sh
# 或者
docker-compose logs -f

# 查看状态
docker-compose ps
```

## 🔧 配置说明

### 环境变量

创建 `.env` 文件来自定义配置：

```bash
# 数据库配置
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_PASSWORD=your_user_password

# 应用配置
JWT_SECRET=your_jwt_secret
CORS_ALLOWED_ORIGINS=http://localhost:3000

# 端口配置
EXTERNAL_PORT=8080
MYSQL_EXTERNAL_PORT=3306
```

### 数据持久化

数据存储在以下目录：

```
data/
├── mysql/          # MySQL数据文件
├── logs/           # 应用日志
├── uploads/        # 上传文件
└── backups/        # 数据库备份
```

### 日志管理

日志文件位置：

- 应用日志: `data/logs/application.log`
- 错误日志: `data/logs/error.log`
- SQL日志: `data/logs/sql.log`
- 安全日志: `data/logs/security.log`

## 📊 监控和健康检查

### 健康检查

```bash
# 完整健康检查
./scripts/health-check.sh

# 单项检查
./scripts/health-check.sh http
./scripts/health-check.sh database
./scripts/health-check.sh disk
./scripts/health-check.sh memory
```

### 系统监控

```bash
# 持续监控
./scripts/monitor.sh continuous

# 单次检查
./scripts/monitor.sh once

# 生成报告
./scripts/monitor.sh report

# 清理日志
./scripts/monitor.sh cleanup
```

### 性能指标

访问 `/actuator/metrics` 端点查看详细指标：

- JVM内存使用
- 数据库连接池状态
- HTTP请求统计
- 自定义业务指标

## 🛠️ 开发环境

### 开发模式部署

```bash
# 使用开发配置
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# 或者使用开发专用Dockerfile
docker build -f Dockerfile.dev -t security-teaching:dev .
docker run -d -p 8080:8080 -p 5005:5005 security-teaching:dev
```

### 调试配置

开发模式启用了远程调试：

- 调试端口: 5005
- JVM参数: `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005`

## 🔐 安全配置

### 生产环境安全

1. **更改默认密码**：
   ```bash
   export MYSQL_ROOT_PASSWORD=$(openssl rand -base64 32)
   export MYSQL_PASSWORD=$(openssl rand -base64 16)
   export JWT_SECRET=$(openssl rand -base64 64)
   ```

2. **限制网络访问**：
   ```yaml
   # docker-compose.yml
   ports:
     - "127.0.0.1:8080:8080"  # 仅本地访问
   ```

3. **启用HTTPS**：
   ```bash
   # 使用反向代理（Nginx/Traefik）
   # 配置SSL证书
   ```

### 数据备份

```bash
# 手动备份
docker-compose exec security-teaching-app mysqldump \
  -u root -p"$MYSQL_ROOT_PASSWORD" \
  --single-transaction \
  javaweb_security > backup_$(date +%Y%m%d).sql

# 自动备份（定时任务）
0 2 * * * /path/to/scripts/backup.sh
```

## 🐛 故障排除

### 常见问题

1. **容器启动失败**：
   ```bash
   # 查看详细日志
   docker-compose logs security-teaching-app
   
   # 检查资源使用
   docker stats
   ```

2. **数据库连接失败**：
   ```bash
   # 检查MySQL状态
   docker-compose exec security-teaching-app mysql -h localhost -u security_user -p
   
   # 重置数据库
   docker-compose down -v
   docker-compose up -d
   ```

3. **端口冲突**：
   ```bash
   # 修改端口配置
   export EXTERNAL_PORT=8081
   docker-compose up -d
   ```

4. **内存不足**：
   ```bash
   # 调整JVM内存
   export JAVA_OPTS="-Xms256m -Xmx512m"
   docker-compose restart
   ```

### 日志分析

```bash
# 查看错误日志
grep -i error data/logs/application.log

# 查看最近的异常
docker-compose logs --since="1h" | grep -i exception

# 分析访问模式
grep "GET\|POST" data/logs/application.log | tail -100
```

## 📈 性能优化

### 资源限制

```yaml
# docker-compose.yml
services:
  security-teaching-app:
    deploy:
      resources:
        limits:
          memory: 1G
          cpus: '1.0'
        reservations:
          memory: 512M
          cpus: '0.5'
```

### 数据库优化

```bash
# 优化MySQL配置
# 编辑 docker/mysql.cnf
[mysqld]
innodb_buffer_pool_size = 256M
query_cache_size = 32M
max_connections = 100
```

### 应用优化

```bash
# JVM调优
export JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+PrintGC"

# 连接池优化
# 编辑 application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
```

## 📞 技术支持

如遇问题，请提供以下信息：

1. 系统环境信息
2. 错误日志
3. 容器状态
4. 资源使用情况

```bash
# 收集诊断信息
./scripts/health-check.sh > diagnosis.log
docker-compose ps >> diagnosis.log
docker stats --no-stream >> diagnosis.log
```

