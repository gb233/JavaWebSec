# Java Web 安全教学系统

[![License: Custom](https://img.shields.io/badge/License-Custom-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.x-green.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-4FC08D.svg)](https://vuejs.org/)
[![Docker](https://img.shields.io/badge/Docker-Supported-2496ED.svg)](https://www.docker.com/)

> 🌐 **在线演示地址**: [http://javasec.icu:8080](http://javasec.icu:8080)  
> 💡 **免费注册使用**，无需任何限制，立即体验完整的Web安全学习功能！

面向安全初学者、Java 开发者及高校教学场景的漏洞教学平台，覆盖 **OWASP Top 10** 与扩展攻击面，支持"漏洞演示 → 修复对比 → 知识测试 → 挑战模式 → 学习档案 → 学习笔记"的完整学习闭环。

## 🎯 系统价值

### 为学习者提供
- ✅ **零门槛上手**：无需复杂环境配置，在线即可学习
- ✅ **实战化教学**：真实漏洞演示，对比安全与不安全代码
- ✅ **系统化学习**：从理论到实践，从测试到挑战的完整路径
- ✅ **可视化理解**：流程图、时序图、代码流程图直观展示攻击原理
- ✅ **个性化追踪**：学习进度、徽章成就、错题本全面记录

### 为教育者提供
- ✅ **开箱即用**：完整的教学内容和题库，可直接用于课程
- ✅ **灵活部署**：支持Docker、JAR、源码多种部署方式
- ✅ **数据统计**：学生学习情况、测试成绩、挑战完成度一目了然
- ✅ **可扩展性**：基于策略模式设计，易于添加新的漏洞类型

## 🏗️ 系统架构

### 技术架构

```
┌─────────────────────────────────────────────────────────┐
│                    用户界面层                            │
│  Vue 3 + TypeScript + Element Plus + Pinia + ECharts   │
└────────────────────┬────────────────────────────────────┘
                    │
┌───────────────────▼────────────────────────────────────┐
│                    API网关层                            │
│              Spring Boot REST API                       │
└───────────────────┬────────────────────────────────────┘
                    │
┌───────────────────▼────────────────────────────────────┐
│                  业务逻辑层                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ 漏洞知识中心  │  │  知识测试    │  │  挑战模式    │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
│  ┌──────────────┐  ┌──────────────┐    
│  │ 学习档案     │  │  用户管理    │  │
│  └──────────────┘  └──────────────┘    
└───────────────────┬────────────────────────────────────┘
                    │
┌───────────────────▼────────────────────────────────────┐
│                  数据访问层                              │
│  Spring Data JPA + MyBatis + HikariCP                  │
└───────────────────┬────────────────────────────────────┘
                    │
┌───────────────────▼────────────────────────────────────┐
│                  数据存储层                              │
│              MySQL 8.0 + 文件存储                        │
└─────────────────────────────────────────────────────────┘
```

## 📦 核心模块

### 1. 漏洞知识中心

覆盖 **OWASP Top 10 (2021)** 所有漏洞类型：

- **A01:2021 - 失效的访问控制** (Broken Access Control)
  - CWE-639：通过用户控制键绕过授权 (Authorization Bypass Through User-Controlled Key) - 水平越权
  - CWE-285：授权不当 (Improper Authorization) - 垂直越权
  - CWE-639：通过用户控制键绕过授权 (Authorization Bypass Through User-Controlled Key) - 直接对象引用（IDOR）
  - CWE-284：访问控制不当 (Improper Access Control) - 上下文越权
  - CWE-284：访问控制不当 (Improper Access Control) - 功能级越权
  - CWE-22：路径名限制不当导致目录遍历 (Improper Limitation of a Pathname to a Restricted Directory) - 路径遍历

- **A02:2021 - 加密失败** (Cryptographic Failures)
  - CWE-327：使用已破解或存在风险的加密算法 (Use of a Broken or Risky Cryptographic Algorithm) - 弱哈希算法
  - CWE-327：使用已破解或存在风险的加密算法 (Use of a Broken or Risky Cryptographic Algorithm) - 弱加密算法
  - CWE-321：使用硬编码的加密密钥 (Use of Hard-coded Cryptographic Key) - 密钥管理不当
  - CWE-259：硬编码密钥 (Hard-coded Cryptographic Key)
  - CWE-331：熵不足 (Insufficient Entropy)

- **A03:2021 - 注入** (Injection)
  - CWE-89：SQL注入 (SQL Injection)
  - CWE-77：命令注入 (Command Injection)
  - CWE-943：NoSQL注入 (NoSQL Injection)
  - CWE-90：LDAP注入 (LDAP Injection)
  - CWE-643：XPath注入 (XPath Injection)
  - CWE-611：XML外部实体引用限制不当 (Improper Restriction of XML External Entity Reference)

- **A04:2021 - 不安全设计** (Insecure Design)
  - CWE-840：业务逻辑错误 (Business Logic Errors)
  - CWE-209：通过错误消息暴露信息 (Information Exposure Through an Error Message)
  - CWE-367：检查时间到使用时间竞态条件 (Time-of-check Time-of-use (TOCTOU) Race Condition)

- **A05:2021 - 安全配置错误** (Security Misconfiguration)
  - CWE-521：弱密码要求 (Weak Password Requirements) - 默认凭据
  - CWE-489：活动调试代码 (Active Debug Code) - 调试模式
  - CWE-693：保护机制失败 (Protection Mechanism Failure) - 缺少安全头
  - CWE-548：通过目录列表泄露信息 (Information Exposure Through Directory Listing) - 目录列表
  - CWE-209：通过错误消息暴露信息 (Information Exposure Through an Error Message) - 错误泄露

- **A06:2021 - 易受攻击和过时的组件** (Vulnerable and Outdated Components)
  - CWE-1104：使用未维护的第三方组件 (Use of Unmaintained Third Party Components) - 过时组件
  - CWE-1104：使用未维护的第三方组件 (Use of Unmaintained Third Party Components) - 已知漏洞组件
  - CWE-1104：使用未维护的第三方组件 (Use of Unmaintained Third Party Components) - 依赖漏洞
  - CWE-502：反序列化不可信数据 (Deserialization of Untrusted Data) - Log4Shell
  - CWE-94：代码注入 (Code Injection) - Struts2 RCE

- **A07:2021 - 身份识别和认证失败** (Identification and Authentication Failures)
  - CWE-521：弱密码要求 (Weak Password Requirements) - 弱密码
  - CWE-613：会话过期不足 (Insufficient Session Expiration) - 会话劫持
  - CWE-799：交互频率控制不当 (Improper Control of Interaction Frequency) - 凭据填充
  - CWE-287：身份认证不当 (Improper Authentication) - MFA绕过

- **A08:2021 - 软件和数据完整性失效** (Software and Data Integrity Failures)
  - CWE-502：反序列化不可信数据 (Deserialization of Untrusted Data) - 不安全反序列化
  - CWE-494：下载代码时缺少完整性检查 (Download of Code Without Integrity Check) - 软件更新完整性
  - CWE-345：数据真实性验证不足 (Insufficient Verification of Data Authenticity) - 数据完整性
  - CWE-506：嵌入恶意代码 (Embedded Malicious Code) - CI/CD破坏
  - CWE-829：从未受信任的控制域包含功能 (Inclusion of Functionality from Untrusted Control Sphere) - 供应链

- **A09:2021 - 安全日志和监控失败** (Security Logging and Monitoring Failures)
  - CWE-778：日志记录不足 (Insufficient Logging) - 日志记录不足
  - CWE-532：日志文件中插入敏感信息 (Insertion of Sensitive Information into Log File) - 日志篡改
  - CWE-551：行为顺序不正确 (Incorrect Behavior Order) - 监控不足

- **A10:2021 - 服务端请求伪造** (Server-Side Request Forgery)
  - CWE-918：服务端请求伪造 (Server-Side Request Forgery (SSRF))

**每个漏洞类型包含：**
- 📖 **理论知识**：漏洞定义、危害场景、实际案例
- 🔍 **详细讲解**：漏洞类型详解、攻击场景分析、技术原理
- 🎯 **攻击演示**：上百种交互式漏洞演示，真实复现攻击过程，提供实际请求、攻击载荷、攻击结果、漏洞原因等详细内容
- 🛡️ **防护演示**：对应每种攻击载荷提供相应的安全防护演示，提供请求示例、攻击载荷、防护结果、防护机制分析等详细内容
- 🛡️ **修复建议**：8种修复维度
  1. 💻 **代码层面修复**：核心修复，提供代码示例和检查清单
  2. ⚙️ **配置层面修复**：重要配置，提供配置示例和实施步骤
  3. 🏗️ **架构设计修复**：长期规划，提供架构示例和实施步骤
  4. 📋 **SDL流程修复**：流程优化，提供SDL流程步骤和检查清单
  5. 🚀 **运维部署修复**：运维实践，提供部署指南和推荐工具
  6. 📊 **监控审计修复**：监控配置，提供监控配置和推荐工具
  7. 🚨 **应急响应修复**：应急处理，提供应急响应步骤
  8. 📚 **培训教育修复**：人员培训，提供培训内容和参考资源

### 2. 交互式漏洞演示

- **双模式切换**：`vulnerable`（漏洞模式）和 `secure`（安全模式）实时对比
- **真实攻击复现**：支持多种攻击载荷，真实模拟攻击场景
- **代码对比**：不安全代码 vs 安全代码并排展示
- **流程图可视化**：
  - 时序图：展示攻击流程的时间顺序
  - 攻击流程图：展示攻击步骤和路径
  - 代码流程图：展示代码执行流程
  - 防护流程图：展示安全防护机制

### 3. 知识测试系统

- **三种测试模式**：
  - 📝 练习模式：不限时间，实时反馈，适合学习
  - ⏱️ 考试模式：限时答题，模拟真实考试环境
  - 🎯 挑战模式：高难度题目，检验真实水平

- **题库覆盖**：
  - OWASP Top 10 所有漏洞类型
  - 单选、多选、判断题等多种题型
  - 每道题目包含详细解析和知识点说明

- **测试功能**：
  - 实时答题反馈
  - 错题本自动收集
  - 测试成绩统计
  - 历史记录查询

### 4. 挑战模式

- **综合场景挑战**：
   - 开发不同的多种漏洞组合进行深度利用的场景，根据多种攻击步骤指引完成对应挑战

### 5. 学习档案与徽章系统

- **学习进度追踪**：
  - 漏洞学习完成度
  - 测试完成情况
  - 挑战完成进度
  - 连续学习天数

- **徽章成就系统**：
  - 🏆 时间类徽章：连续学习、累计学习时长
  - 📚 学习类徽章：完成漏洞学习、通过测试
  - 🎯 挑战类徽章：完成挑战、获得高分
  - 📊 进度可视化：徽章收集进度、成就展示

- **个人数据管理**：
  - 学习笔记：记录学习心得
  - 我的收藏：收藏对应漏洞


### 6. 用户认证与权限管理

- **认证功能**：
  - 用户注册/登录
  - JWT Token认证
  - 验证码防护（防暴力破解）
  - 会话管理


## 👥 适用人群

### 🎓 安全初学者
- **学习目标**：从零开始学习Web安全基础知识
- **推荐路径**：漏洞知识中心 → 知识测试 → 挑战模式
- **价值**：系统化学习OWASP Top 10，建立安全知识体系

### 💻 Java开发者
- **学习目标**：了解常见安全漏洞，编写安全代码
- **推荐路径**：漏洞演示 → 代码对比 → 修复建议
- **价值**：通过真实代码对比，学习安全编程实践

### 🏫 高校教师
- **使用场景**：Web安全课程教学、实验环境搭建
- **推荐路径**：使用系统作为教学平台，学生在线学习
- **价值**：完整的教学内容和题库，开箱即用的教学工具

### 🔒 安全工程师
- **学习目标**：提升漏洞挖掘和防护能力
- **推荐路径**：挑战模式 → 攻击日志分析 → 漏洞复现
- **价值**：实战化训练，提升安全技能

## 🚀 快速开始

### 方式一：在线体验（推荐）

直接访问 [http://javasec.icu:8080](http://javasec.icu:8080)，免费注册账号即可开始学习！

### 方式二：Docker部署

```bash
# 1. 克隆项目
git clone <repository-url>
cd javaweb安全教学系统

# 2. 启动服务（前后端分离，数据库自动初始化）
docker-compose -f docker-compose.prod.yml up -d

# 3. 访问应用
# 前端: http://localhost:80
# 后端API: http://localhost:8080
```

### 方式三：本地开发

#### 前后端分离运行（推荐开发环境）

```bash
# 1. 初始化数据库
./scripts/init-database.sh  # Linux/Mac

# 2. 启动后端（端口8080）
cd src/backend && mvn spring-boot:run

# 3. 启动前端（端口3000）
cd src/frontend && npm install && npm run dev

# 访问：http://localhost:3000
```

#### JAR一体化运行（推荐生产环境）

```bash
# 1. 构建包含前后端的JAR包
./scripts/build-jar.sh

# 2. 运行JAR包（统一端口8080）
java -jar src/backend/target/security-teaching-system.jar

# 访问：http://localhost:8080
```

## 🧱 技术栈

| 层级 | 技术 | 说明 |
| --- | --- | --- |
| **前端** | Vue 3 · Vite · TypeScript · Element Plus · Pinia · ECharts | 现代化单页应用，响应式设计，组件化开发 |
| **后端** | Spring Boot 2.7.x · Java 17 · Spring Security · JPA · MyBatis | RESTful API，JWT认证，AOP日志记录 |
| **数据库** | MySQL 8.0 | 关系型数据库，支持事务和复杂查询 |
| **部署** | Docker · Docker Compose | 容器化部署，支持前后端分离和单体部署 |
| **流程图** | Mermaid · PlantUML | 可视化攻击流程、时序图、代码流程图 |

## 📚 核心特性

### ✨ 功能特性

- ✅ **完整的OWASP Top 10覆盖**：所有漏洞类型都有详细的理论、演示和修复建议
- ✅ **交互式漏洞演示**：真实复现攻击过程，对比安全与不安全实现
- ✅ **可视化流程图**：时序图、攻击流程图、代码流程图直观展示
- ✅ **系统化知识测试**：多种测试模式，完整题库，详细解析
- ✅ **实战化挑战场景**：真实业务场景，Flag提交，积分排名
- ✅ **个性化学习追踪**：学习进度、徽章成就、错题本
- ✅ **完整的攻击日志**：记录所有攻击请求，支持分析和导出
- ✅ **灵活权限管理**：角色权限分离，支持多角色管理

### 🎨 用户体验

- ✅ **现代化UI设计**：基于Element Plus，界面美观易用
- ✅ **响应式布局**：支持PC、平板、手机多端访问
- ✅ **实时反馈**：操作即时反馈，提升交互体验
- ✅ **引导式学习**：新手引导，清晰的学习路径
- ✅ **数据可视化**：图表展示学习进度和统计数据



## 🤝 贡献

欢迎提交Issue和Pull Request！

## 📄 许可证

本项目采用自定义许可证，详见 [LICENSE](LICENSE) 文件。

## 🌟 致谢

感谢所有为Web安全教育事业做出贡献的开发者！

---

<div align="center">

**🎓 让Web安全学习更简单、更系统、更实战！**

</div>

---

## 📝 开发日志

### 2025-11-20 20:34 - CI Lint错误修复

#### 会话目的
修复Git推送后CI构建失败的问题，解决前端代码的ESLint和TypeScript类型检查错误。

#### 完成的主要任务
1. **修复未使用的导入和变量**：
   - 移除 `src/frontend/src/layouts/index.vue` 中未使用的图标导入（Setting, Monitor, Collection）
   - 移除 `src/frontend/src/i18n/index.ts` 中未使用的 `getBrowserLanguage` 函数
   - 移除 `src/frontend/src/composables/useTestMode.ts` 中未使用的 `computed` 导入
   - 移除 `src/frontend/src/components/PlaceholderView.vue` 中未使用的 `Tools` 导入和 `props` 变量
   - 移除 `src/frontend/src/components/LanguageSwitch.vue` 中未使用的 `computed` 导入

2. **修复代码格式问题**：
   - 修复 `src/frontend/src/api/vulnerabilityProgress.ts` 文件末尾多余的空行

3. **修复TypeScript类型安全问题**：
   - 为 `src/frontend/src/api/collectionApi.ts` 添加完整的类型定义：
     - `Collection` 接口：收藏夹实体类型
     - `CreateCollectionData` 接口：创建收藏夹请求数据类型
     - `UpdateCollectionData` 接口：更新收藏夹请求数据类型
     - `CollectionPageParams` 接口：分页查询参数类型
     - 将所有 `any` 类型替换为具体类型
   
   - 为 `src/frontend/src/api/collectionTagApi.ts` 添加类型定义：
     - `CollectionTag` 接口：收藏标签实体类型
     - `CreateTagData` 接口：创建标签请求数据类型
     - `UpdateTagData` 接口：更新标签请求数据类型
     - 修复 `batchCreateTags` 方法的参数类型
   
   - 为 `src/frontend/src/api/badgeNotificationApi.ts` 添加类型定义：
     - `BadgeStats` 接口：徽章统计数据类型
   
   - 修复 `src/frontend/src/api/a01.ts` 中的 `any` 类型：
     - 将 `A01Response` 接口中的 `data?: any` 改为 `data?: Record<string, unknown>`
   
   - 修复 `src/frontend/src/composables/useWebSocket.ts` 中的类型：
     - 将 `WebSocketMessage` 接口中的 `data: any` 改为 `data: Record<string, unknown>`
     - 将所有相关的 `any` 类型替换为 `Record<string, unknown>`

#### 关键决策和解决方案
1. **类型安全优先**：将所有 `any` 类型替换为具体的接口类型或 `Record<string, unknown>`，提高代码的类型安全性
2. **代码清理**：移除所有未使用的导入和变量，保持代码整洁
3. **接口定义规范**：为API接口创建完整的TypeScript类型定义，确保前后端类型一致性

#### 使用的技术栈
- TypeScript：类型检查和类型定义
- ESLint：代码质量检查
- Vue 3 Composition API：前端框架

#### 修改的文件列表
1. `src/frontend/src/layouts/index.vue` - 移除未使用的图标导入
2. `src/frontend/src/i18n/index.ts` - 移除未使用的函数
3. `src/frontend/src/composables/useTestMode.ts` - 移除未使用的导入
4. `src/frontend/src/components/PlaceholderView.vue` - 移除未使用的导入和变量
5. `src/frontend/src/components/LanguageSwitch.vue` - 移除未使用的导入
6. `src/frontend/src/api/vulnerabilityProgress.ts` - 修复文件末尾空行
7. `src/frontend/src/api/collectionApi.ts` - 添加完整的类型定义，修复所有 `any` 类型
8. `src/frontend/src/api/collectionTagApi.ts` - 添加类型定义，修复所有 `any` 类型
9. `src/frontend/src/api/badgeNotificationApi.ts` - 添加类型定义，修复 `any` 类型
10. `src/frontend/src/api/a01.ts` - 修复 `any` 类型
11. `src/frontend/src/composables/useWebSocket.ts` - 修复所有 `any` 类型

#### 文件的修改内容
- **类型定义增强**：为所有API接口添加了完整的TypeScript类型定义，包括请求参数类型、响应数据类型和实体类型
- **代码清理**：移除了所有未使用的导入、变量和函数，减少代码冗余
- **类型安全**：将所有 `any` 类型替换为具体类型或 `Record<string, unknown>`，提高代码的类型安全性
- **格式规范**：修复了文件末尾多余空行等格式问题

#### 验证结果
- ✅ 所有ESLint错误已修复
- ✅ 所有TypeScript类型检查错误已修复
- ✅ CI构建应该能够通过

