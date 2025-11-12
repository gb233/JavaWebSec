# 本地环境数据库初始化指南

## 📋 概述

本文档说明如何在Windows、Linux、Mac本地环境中初始化数据库，适用于不使用Docker的情况。

## 🚀 快速开始

### 方式一：使用自动化脚本（推荐）

#### Linux / Mac

```bash
# 1. 进入项目目录
cd javaweb安全教学系统

# 2. 运行初始化脚本
./scripts/init-all-questions.sh

# 3. 按提示输入数据库密码（如果需要）
```

#### Windows

```cmd
REM 1. 进入项目目录
cd javaweb安全教学系统

REM 2. 运行初始化脚本
scripts\init-all-questions.bat

REM 3. 按提示输入数据库密码（如果需要）
```

### 方式二：手动执行SQL脚本

如果自动化脚本无法使用，可以手动执行：

#### Linux / Mac

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE security_teaching_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 导入表结构
mysql -u root -p security_teaching_system < scripts/init-db.sql

# 3. 导入所有题目数据（按顺序）
mysql -u root -p security_teaching_system < scripts/a01_complete_questions.sql
mysql -u root -p security_teaching_system < scripts/a02_complete_questions.sql
mysql -u root -p security_teaching_system < scripts/a03_complete_questions.sql
mysql -u root -p security_teaching_system < scripts/a04_complete_questions.sql
mysql -u root -p security_teaching_system < scripts/a05_complete_questions.sql
mysql -u root -p security_teaching_system < scripts/a06_complete_questions.sql
mysql -u root -p security_teaching_system < scripts/a07_complete_questions.sql
mysql -u root -p security_teaching_system < scripts/a08_complete_questions.sql
mysql -u root -p security_teaching_system < scripts/a09_complete_questions.sql
mysql -u root -p security_teaching_system < scripts/a10_complete_questions.sql

# 4. 导入挑战场景数据
mysql -u root -p security_teaching_system < scripts/challenge-scenarios-init.sql
```

#### Windows

```cmd
REM 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE security_teaching_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

REM 2. 导入表结构
mysql -u root -p security_teaching_system < scripts\init-db.sql

REM 3. 导入所有题目数据（按顺序）
mysql -u root -p security_teaching_system < scripts\a01_complete_questions.sql
mysql -u root -p security_teaching_system < scripts\a02_complete_questions.sql
mysql -u root -p security_teaching_system < scripts\a03_complete_questions.sql
mysql -u root -p security_teaching_system < scripts\a04_complete_questions.sql
mysql -u root -p security_teaching_system < scripts\a05_complete_questions.sql
mysql -u root -p security_teaching_system < scripts\a06_complete_questions.sql
mysql -u root -p security_teaching_system < scripts\a07_complete_questions.sql
mysql -u root -p security_teaching_system < scripts\a08_complete_questions.sql
mysql -u root -p security_teaching_system < scripts\a09_complete_questions.sql
mysql -u root -p security_teaching_system < scripts\a10_complete_questions.sql

REM 4. 导入挑战场景数据
mysql -u root -p security_teaching_system < scripts\challenge-scenarios-init.sql
```

## ⚙️ 配置说明

### 环境变量配置（可选）

可以通过环境变量自定义数据库配置：

#### Linux / Mac

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_USER=root
export DB_PASSWORD=your_password
export DB_NAME=security_teaching_system

./scripts/init-all-questions.sh
```

#### Windows

```cmd
set DB_HOST=localhost
set DB_PORT=3306
set DB_USER=root
set DB_PASSWORD=your_password
set DB_NAME=security_teaching_system

scripts\init-all-questions.bat
```

### 默认配置

如果不设置环境变量，脚本使用以下默认值：

- **DB_HOST**: localhost
- **DB_PORT**: 3306
- **DB_USER**: root
- **DB_PASSWORD**: root（会提示输入）
- **DB_NAME**: security_teaching_system

## ✅ 验证数据导入

### 检查题目总数

```sql
SELECT COUNT(*) as total_questions FROM vulnerability_questions;
```

应该显示：`total_questions = 1000`

### 检查各分类题目数量

```sql
SELECT category_code, COUNT(*) as count 
FROM vulnerability_questions 
GROUP BY category_code 
ORDER BY category_code;
```

应该显示：
```
+--------------+-------+
| category_code| count |
+--------------+-------+
| A01          |   100 |
| A02          |   100 |
| A03          |   100 |
| A04          |   100 |
| A05          |   100 |
| A06          |   100 |
| A07          |   100 |
| A08          |   100 |
| A09          |   100 |
| A10          |   100 |
+--------------+-------+
```

## 🐛 故障排查

### 问题1：找不到mysql命令

**Linux / Mac**：
```bash
# 检查MySQL是否安装
which mysql

# 如果未安装，使用包管理器安装
# Ubuntu/Debian:
sudo apt-get install mysql-client

# CentOS/RHEL:
sudo yum install mysql

# macOS:
brew install mysql-client
```

**Windows**：
- 确保MySQL已安装
- 将MySQL的bin目录添加到PATH环境变量
- 例如：`C:\Program Files\MySQL\MySQL Server 8.0\bin`

### 问题2：数据库连接失败

1. **检查MySQL服务是否运行**：
   ```bash
   # Linux
   sudo systemctl status mysql
   
   # Mac
   brew services list | grep mysql
   
   # Windows
   # 在服务管理器中检查MySQL服务
   ```

2. **检查数据库配置**：
   - 确认主机、端口、用户名、密码正确
   - 确认MySQL允许本地连接

3. **测试连接**：
   ```bash
   mysql -h localhost -P 3306 -u root -p
   ```

### 问题3：字符编码问题

确保数据库和表使用utf8mb4字符集：

```sql
-- 检查数据库字符集
SHOW CREATE DATABASE security_teaching_system;

-- 如果不对，重新创建
DROP DATABASE IF EXISTS security_teaching_system;
CREATE DATABASE security_teaching_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 问题4：题目数量不对

1. **检查导入日志**：查看脚本输出的错误信息
2. **检查SQL文件是否存在**：
   ```bash
   ls scripts/a*_complete_questions.sql
   ```
3. **重新导入**：删除数据库后重新执行初始化脚本

## 📝 执行顺序说明

**重要**：必须按以下顺序执行：

1. **init-db.sql** - 创建所有表结构（必须先执行）
2. **a01_complete_questions.sql** - A01题目（100题）
3. **a02_complete_questions.sql** - A02题目（100题）
4. **a03_complete_questions.sql** - A03题目（100题）
5. **a04_complete_questions.sql** - A04题目（100题）
6. **a05_complete_questions.sql** - A05题目（100题）
7. **a06_complete_questions.sql** - A06题目（100题）
8. **a07_complete_questions.sql** - A07题目（100题）
9. **a08_complete_questions.sql** - A08题目（100题）
10. **a09_complete_questions.sql** - A09题目（100题）
11. **a10_complete_questions.sql** - A10题目（100题）
12. **challenge-scenarios-init.sql** - 挑战场景数据

## 🔄 重新初始化

如果需要重新初始化数据库：

```bash
# 1. 删除现有数据库
mysql -u root -p -e "DROP DATABASE IF EXISTS security_teaching_system;"

# 2. 重新运行初始化脚本
./scripts/init-all-questions.sh  # Linux/Mac
# 或
scripts\init-all-questions.bat   # Windows
```

## 📚 相关文档

- [Docker环境自动初始化](DATABASE_INIT_AUTO.md) - Docker环境的自动初始化说明
- [数据库初始化脚本说明](../scripts/README.md) - SQL脚本详细说明
- [快速开始指南](../../QUICK_START.md) - 项目快速开始指南

