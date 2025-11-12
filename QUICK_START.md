# 🚀 快速开始指南

## 方式一：Docker一键启动（推荐）

```bash
# 1. 克隆项目
git clone <repository-url>
cd javaweb安全教学系统

# 2. 复制环境变量文件（可选，使用默认值）
cp env.example .env

# 3. 启动服务（数据库自动初始化）
docker-compose up -d

# 4. 等待服务启动（约2-3分钟）
# 查看日志：docker-compose logs -f

# 5. 访问应用
# 前端: http://localhost:8080
# API文档: http://localhost:8080/swagger-ui.html
# 健康检查: http://localhost:8080/actuator/health
```

### Docker环境要求

- Docker 20.10+
- Docker Compose 2.0+
- 4GB+ 内存
- 20GB+ 磁盘空间

## 方式二：本地编译启动（Windows/Linux/Mac）

### 1. 环境要求

- **Java**: JDK 17+
- **Maven**: 3.6+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **内存**: 4GB+

### 2. 数据库初始化

#### Linux / Mac

```bash
# 运行自动化初始化脚本
./scripts/init-all-questions.sh

# 脚本会自动：
# - 测试数据库连接
# - 创建数据库（如果不存在）
# - 导入所有表结构和1000题数据
# - 验证导入结果
```

#### Windows

```cmd
REM 运行自动化初始化脚本
scripts\init-all-questions.bat

REM 脚本会自动：
REM - 测试数据库连接
REM - 创建数据库（如果不存在）
REM - 导入所有表结构和1000题数据
REM - 验证导入结果
```

**配置数据库连接**（可选）：
```bash
# Linux/Mac
export DB_HOST=localhost
export DB_PORT=3306
export DB_USER=root
export DB_PASSWORD=your_password
export DB_NAME=security_teaching_system

# Windows
set DB_HOST=localhost
set DB_PORT=3306
set DB_USER=root
set DB_PASSWORD=your_password
set DB_NAME=security_teaching_system
```

详细说明请查看 [本地环境数据库初始化指南](docs/deployment/DATABASE_INIT_LOCAL.md)

### 3. 启动后端服务

```bash
cd src/backend

# 安装依赖（首次运行）
mvn clean install -DskipTests

# 启动服务
mvn spring-boot:run

# 或使用IDE直接运行主类：
# com.javaweb.security.SecurityTeachingSystemApplication
```

后端服务启动后：
- API地址: http://localhost:8080
- API文档: http://localhost:8080/swagger-ui.html
- 健康检查: http://localhost:8080/actuator/health

### 4. 启动前端服务

```bash
cd src/frontend

# 安装依赖（首次运行）
npm install

# 启动开发服务器（推荐，开发时使用）
npm run dev
```

前端服务启动后：
- **开发地址**: http://localhost:3000
  - 支持热重载、源码映射等开发特性
  - 适合日常开发和调试

**⚠️ 注意：4173端口（preview）不能用于生产**
- `npm run preview` 只是预览构建后的静态文件，**不连接后端API**
- 不能进行实际的登录、数据操作等功能
- 仅用于测试构建结果，不能正常使用系统功能

### 5. 验证数据导入

```bash
# 检查题目总数（应该显示1000题）
mysql -u root -p security_teaching_system -e "SELECT COUNT(*) as total_questions FROM vulnerability_questions;"

# 检查各分类题目数量（每个分类应该显示100题）
mysql -u root -p security_teaching_system -e "SELECT category_code, COUNT(*) as count FROM vulnerability_questions GROUP BY category_code ORDER BY category_code;"
```

## ⚙️ 配置说明

### 默认配置

项目使用以下默认配置，无需修改即可启动：

- **数据库**：
  - 数据库名：`security_teaching_system`
  - 用户名：`security_user`
  - 密码：`security_pass`
  - 端口：`3306`

- **应用**：
  - 端口：`8080`
  - JWT密钥：`change-me-in-production`

### 自定义配置

如需自定义配置，编辑 `.env` 文件：

```bash
# 复制示例文件
cp env.example .env

# 编辑配置
vim .env  # 或使用其他编辑器
```

## 🔍 验证安装

### 1. 检查服务状态

```bash
docker-compose ps
```

应该看到两个服务：
- `security-teaching-mysql` - 数据库服务
- `security-teaching-app` - 应用服务

### 2. 检查数据库初始化

```bash
# 进入MySQL容器
docker-compose exec mysql mysql -u security_user -psecurity_pass security_teaching_system

# 检查表数量
SHOW TABLES;

# 检查题目数量（应该显示1000题，A01-A10各100题）
SELECT COUNT(*) as total_questions FROM vulnerability_questions;
SELECT category_code, COUNT(*) as count FROM vulnerability_questions GROUP BY category_code ORDER BY category_code;

# 如果题目数量不对，检查MySQL初始化日志
# docker-compose logs mysql | grep -i error

# 退出
exit
```

### 3. 检查应用健康

```bash
# 健康检查
curl http://localhost:8080/actuator/health

# 应该返回：{"status":"UP"}
```

## 🐛 常见问题

### 问题1：端口被占用

**错误**：`Bind for 0.0.0.0:8080 failed: port is already allocated`

**解决**：
```bash
# 修改.env文件中的端口
APP_PORT=8081

# 或停止占用端口的服务
```

### 问题2：数据库初始化失败

**错误**：数据库连接失败或表不存在

**解决**：
```bash
# 1. 删除旧数据卷
docker-compose down -v

# 2. 重新启动
docker-compose up -d

# 3. 查看MySQL日志
docker-compose logs mysql
```

### 问题3：内存不足

**错误**：容器启动失败或OOM

**解决**：
```bash
# 修改.env文件，减少JVM内存
JAVA_OPTS=-Xms256m -Xmx512m
```

### 问题4：构建失败

**错误**：Docker构建超时或失败

**解决**：
```bash
# 1. 清理Docker缓存
docker system prune -a

# 2. 使用国内镜像源（可选）
# 配置Docker镜像加速器

# 3. 重新构建
docker-compose build --no-cache
```

## 📚 更多文档

- [Docker部署指南](docs/deployment/DOCKER.md)
- [数据库初始化指南](docs/DATABASE_INITIALIZATION.md)
- [配置分析文档](docs/deployment/CONFIGURATION_ANALYSIS.md)

## 💡 开发环境

如需开发环境，使用：

```bash
docker-compose -f docker-compose.dev.yml up -d
```

开发环境特点：
- 前后端分离运行
- 支持热重载
- 支持远程调试（后端端口5005）

