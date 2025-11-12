-- 学习笔记系统初始数据

-- 插入系统标签
INSERT INTO note_tags (tag_name, tag_description, usage_count, color, is_system) VALUES
('SQL注入', 'SQL注入相关学习笔记', 0, '#dc3545', true),
('XSS', '跨站脚本攻击相关学习笔记', 0, '#fd7e14', true),
('CSRF', '跨站请求伪造相关学习笔记', 0, '#ffc107', true),
('越权访问', '越权访问相关学习笔记', 0, '#6f42c1', true),
('加密失败', '加密失败相关学习笔记', 0, '#20c997', true),
('安全配置', '安全配置相关学习笔记', 0, '#0dcaf0', true),
('组件漏洞', '组件漏洞相关学习笔记', 0, '#198754', true),
('学习心得', '学习心得和总结', 0, '#0d6efd', true),
('实验记录', '实验过程和结果记录', 0, '#6c757d', true),
('安全工具', '安全工具使用笔记', 0, '#e83e8c', true),
('漏洞分析', '漏洞分析相关笔记', 0, '#fd7e14', true),
('防护措施', '安全防护措施笔记', 0, '#198754', true),
('代码审计', '代码审计相关笔记', 0, '#6f42c1', true),
('渗透测试', '渗透测试相关笔记', 0, '#dc3545', true),
('安全开发', '安全开发相关笔记', 0, '#0dcaf0', true);

-- 插入示例学习笔记（为testuser3用户）
INSERT INTO learning_notes (
    user_id, title, content, summary, note_type, vulnerability_code, 
    tags, is_public, is_pinned, word_count, reading_time
) VALUES 
(
    64, -- testuser3的用户ID
    'SQL注入攻击原理与防护',
    '# SQL注入攻击原理与防护

## 什么是SQL注入

SQL注入是一种代码注入技术，攻击者通过在应用程序的输入字段中插入恶意的SQL代码，来操纵后端数据库。

## 攻击原理

1. **输入验证不足**: 应用程序没有对用户输入进行充分的验证和过滤
2. **动态SQL拼接**: 直接拼接用户输入到SQL语句中
3. **权限过大**: 数据库用户权限过大

## 常见攻击类型

### 1. 联合查询注入
```sql
SELECT * FROM users WHERE id = 1 UNION SELECT username, password FROM admin_users
```

### 2. 布尔盲注
```sql
SELECT * FROM users WHERE id = 1 AND (SELECT COUNT(*) FROM information_schema.tables) > 0
```

### 3. 时间盲注
```sql
SELECT * FROM users WHERE id = 1; WAITFOR DELAY \'00:00:05\'
```

## 防护措施

### 1. 参数化查询
```java
String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setString(1, username);
stmt.setString(2, password);
```

### 2. 输入验证
- 白名单验证
- 长度限制
- 特殊字符过滤

### 3. 最小权限原则
- 数据库用户权限最小化
- 使用只读账户进行查询

## 实验记录

### 测试环境
- 目标: http://localhost:8080/demo/sql-injection
- 工具: Burp Suite, SQLMap
- 时间: 2025-10-04

### 测试结果
1. 发现注入点: /search?keyword=test
2. 成功获取数据库信息
3. 提取用户表数据

## 总结

SQL注入是最常见也最危险的Web安全漏洞之一。通过本次学习，我深入理解了SQL注入的攻击原理和防护方法。在实际开发中，一定要使用参数化查询，避免直接拼接SQL语句。',
    '深入理解SQL注入攻击原理，学习防护措施和实验验证',
    'SHARED',
    'A03',
    JSON_ARRAY('SQL注入', '安全', '学习心得', '实验记录'),
    true,
    true,
    1200,
    5
),
(
    64,
    'XSS跨站脚本攻击学习笔记',
    '# XSS跨站脚本攻击学习笔记

## XSS攻击概述

XSS（Cross-Site Scripting）是一种常见的Web安全漏洞，攻击者通过在目标网站上注入恶意脚本，在用户浏览器中执行。

## XSS类型

### 1. 反射型XSS
- 攻击脚本通过URL参数传递
- 服务器直接返回包含脚本的页面
- 需要用户主动点击恶意链接

### 2. 存储型XSS
- 恶意脚本存储在服务器数据库中
- 每次访问页面都会执行
- 危害最大，影响所有用户

### 3. DOM型XSS
- 完全在客户端执行
- 不经过服务器处理
- 通过修改DOM结构实现攻击

## 攻击示例

### 反射型XSS
```html
<script>alert(\'XSS\')</script>
```

### 存储型XSS
```html
<img src="x" onerror="alert(\'XSS\')">
```

## 防护措施

### 1. 输入过滤
- HTML实体编码
- 特殊字符转义
- 白名单验证

### 2. 输出编码
```java
String encoded = StringEscapeUtils.escapeHtml4(userInput);
```

### 3. CSP策略
```html
Content-Security-Policy: default-src \'self\'
```

## 学习心得

XSS攻击虽然看似简单，但防护起来需要考虑很多细节。在实际开发中，一定要对所有用户输入进行适当的编码和过滤。',
    '学习XSS攻击原理，掌握防护方法和最佳实践',
    'PERSONAL',
    'A03',
    JSON_ARRAY('XSS', '安全', '学习心得'),
    false,
    false,
    800,
    3
),
(
    64,
    '越权访问漏洞分析',
    '# 越权访问漏洞分析

## 漏洞概述

越权访问是指用户能够访问超出其权限范围的数据或功能。

## 常见类型

### 1. 水平越权
- 访问同级别其他用户的资源
- 如：查看他人订单、个人信息

### 2. 垂直越权
- 获取更高级别的权限
- 如：普通用户获得管理员权限

## 攻击场景

### 场景1: IDOR漏洞
```
GET /api/user/123/profile  # 正常访问自己的资料
GET /api/user/456/profile  # 越权访问他人资料
```

### 场景2: 功能越权
```
POST /api/admin/delete-user  # 普通用户执行管理员功能
```

## 防护措施

### 1. 权限验证
```java
@PreAuthorize("hasRole(\'USER\') and @userService.isOwner(#userId, authentication.name)")
public UserProfile getUserProfile(@PathVariable Long userId) {
    // 业务逻辑
}
```

### 2. 资源级权限控制
- 每个资源都有明确的权限要求
- 验证用户是否有权限访问特定资源

### 3. 最小权限原则
- 用户只能访问必要的资源
- 定期审查权限分配

## 实验验证

通过修改URL参数成功访问了其他用户的敏感信息，验证了越权访问漏洞的存在。

## 总结

越权访问是Web应用中非常严重的安全问题，需要在设计和开发阶段就充分考虑权限控制机制。',
    '分析越权访问漏洞类型，学习防护措施和权限控制',
    'SHARED',
    'A01',
    JSON_ARRAY('越权访问', '安全', '漏洞分析', '防护措施'),
    true,
    false,
    900,
    4
);
