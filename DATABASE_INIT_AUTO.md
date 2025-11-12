# 数据库自动初始化说明

## ✅ 自动初始化流程

Docker Compose启动时会**自动一次性导入所有数据**，无需手动操作。

### 初始化顺序

MySQL容器首次启动时，会自动按文件名顺序执行以下SQL脚本：

1. **`01-init-db.sql`** - 创建所有表结构
2. **`02-a01-questions.sql`** - 导入A01题目（100题）
3. **`03-a02-questions.sql`** - 导入A02题目（100题）
4. **`04-a03-questions.sql`** - 导入A03题目（100题）
5. **`05-a04-questions.sql`** - 导入A04题目（100题）
6. **`06-a05-questions.sql`** - 导入A05题目（100题）
7. **`07-a06-questions.sql`** - 导入A06题目（100题）
8. **`08-a07-questions.sql`** - 导入A07题目（100题）
9. **`09-a08-questions.sql`** - 导入A08题目（100题）
10. **`10-a09-questions.sql`** - 导入A09题目（100题）
11. **`11-a10-questions.sql`** - 导入A10题目（100题）
12. **`12-challenge-scenarios.sql`** - 导入挑战场景数据

**总计：1000题自动导入**

## 🚀 使用方法

### 标准流程（推荐）

```bash
# 1. 启动服务（自动初始化）
docker-compose up -d

# 2. 等待初始化完成（约2-3分钟）
docker-compose logs -f mysql

# 3. 验证数据导入
docker-compose exec mysql mysql -u security_user -psecurity_pass security_teaching_system -e "SELECT COUNT(*) as total_questions FROM vulnerability_questions;"
# 应该显示：total_questions = 1000
```

### 验证各分类题目数量

```bash
docker-compose exec mysql mysql -u security_user -psecurity_pass security_teaching_system -e "SELECT category_code, COUNT(*) as count FROM vulnerability_questions GROUP BY category_code ORDER BY category_code;"
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

## ⚠️ 故障排查

### 如果题目数量不对

1. **检查MySQL初始化日志**：
   ```bash
   docker-compose logs mysql | grep -i error
   ```

2. **检查SQL脚本是否存在**：
   ```bash
   ls -la scripts/a*_complete_questions.sql
   ```
   应该看到10个文件（a01-a10）

3. **重新初始化**（会删除现有数据）：
   ```bash
   docker-compose down -v
   docker-compose up -d
   ```

### 如果自动初始化失败

只有在自动初始化失败时，才需要手动执行：

```bash
# 1. 进入MySQL容器
docker-compose exec mysql bash

# 2. 手动执行SQL脚本（按顺序）
mysql -u security_user -psecurity_pass security_teaching_system < /docker-entrypoint-initdb.d/01-init-db.sql
mysql -u security_user -psecurity_pass security_teaching_system < /docker-entrypoint-initdb.d/02-a01-questions.sql
# ... 依次执行其他脚本

# 3. 检查执行结果
mysql -u security_user -psecurity_pass security_teaching_system -e "SELECT COUNT(*) FROM vulnerability_questions;"
```

## 📋 配置说明

### docker-compose.yml配置

所有SQL脚本都在 `docker-compose.yml` 中配置为自动挂载：

```yaml
volumes:
  - mysql_data:/var/lib/mysql
  # 数据库初始化脚本（按顺序执行）
  - ./scripts/init-db.sql:/docker-entrypoint-initdb.d/01-init-db.sql:ro
  # A01-A10题目数据（每个分类100题，共1000题）
  - ./scripts/a01_complete_questions.sql:/docker-entrypoint-initdb.d/02-a01-questions.sql:ro
  - ./scripts/a02_complete_questions.sql:/docker-entrypoint-initdb.d/03-a02-questions.sql:ro
  # ... 其他分类
  - ./scripts/challenge-scenarios-init.sql:/docker-entrypoint-initdb.d/12-challenge-scenarios.sql:ro
```

### MySQL自动执行机制

MySQL容器启动时，会自动执行 `/docker-entrypoint-initdb.d/` 目录下的所有 `.sql` 文件，按文件名顺序执行。

## ✅ 优势

1. **完全自动化**：无需手动执行任何SQL脚本
2. **一次性导入**：所有1000题自动导入
3. **顺序保证**：通过文件名前缀确保执行顺序
4. **错误可见**：所有错误都会记录在MySQL容器日志中
5. **易于排查**：如果失败，可以查看日志快速定位问题

## 📝 注意事项

1. **首次启动**：自动初始化只在MySQL容器首次启动时执行
2. **数据持久化**：数据存储在Docker卷中，重启不会重新初始化
3. **重新初始化**：需要删除数据卷（`docker-compose down -v`）才会重新初始化
4. **执行时间**：1000题导入可能需要2-3分钟，请耐心等待
5. **日志监控**：建议使用 `docker-compose logs -f mysql` 监控初始化过程

