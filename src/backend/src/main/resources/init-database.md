# 数据库初始化说明

## Spring Boot自动初始化

本项目使用Spring Boot的标准SQL初始化机制，应用启动时会自动执行：

1. **schema.sql** - 创建所有表结构
2. **data.sql** - 插入所有初始数据（包括1000道题目）

## 配置

在 `application.yml` 中已配置：

```yaml
spring:
  sql:
    init:
      mode: always  # 总是执行初始化脚本
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql
      continue-on-error: false  # 遇到错误时停止
```

## 文件说明

- **schema.sql**: 包含所有表结构的CREATE TABLE语句
- **data.sql**: 包含所有初始数据的INSERT语句，包括：
  - 基础配置数据（漏洞分类、测试模式等）
  - 题目数据（A01-A10，共1000题）

## 注意事项

1. **首次启动**: 确保数据库已创建且为空
2. **数据重置**: 删除数据库后重新创建即可重置
3. **开发环境**: 建议使用 `ddl-auto: update` 模式，避免每次重启都重新创建表
4. **生产环境**: 使用 `ddl-auto: validate` 模式，只验证表结构

## 手动初始化（可选）

如果不想使用Spring Boot自动初始化，可以手动执行：

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE security_teaching_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 执行初始化脚本
mysql -u root -p security_teaching_system < scripts/init-db.sql

# 3. 导入题目数据
mysql -u root -p security_teaching_system < scripts/all-questions.sql
```

## 验证

启动应用后，检查数据库：

```sql
-- 检查题目数量（应该显示1000）
SELECT category_code, COUNT(*) as count 
FROM vulnerability_questions 
GROUP BY category_code;

-- 检查漏洞分类（应该显示10个）
SELECT COUNT(*) FROM vulnerability_categories;
```














