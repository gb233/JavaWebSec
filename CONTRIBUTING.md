# 贡献指南

感谢您对Java Web安全教学系统的关注！我们欢迎各种形式的贡献，包括但不限于：

- 🐛 报告Bug
- 💡 提出新功能建议
- 📝 改进文档
- 🔧 提交代码修复
- 🎨 优化用户界面
- 🌍 翻译文档

## 🚀 快速开始

### 1. Fork 项目
点击项目页面右上角的 "Fork" 按钮，将项目复制到您的GitHub账户。

### 2. 克隆项目
```bash
git clone https://github.com/your-username/javaweb-security-teaching-system.git
cd javaweb-security-teaching-system
```

### 3. 添加上游仓库
```bash
git remote add upstream https://github.com/original-owner/javaweb-security-teaching-system.git
```

### 4. 创建开发分支
```bash
git checkout -b feature/your-feature-name
```

## 🔧 开发环境搭建

### 环境要求
- Java 17+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+
- Docker (可选)

### 本地开发
```bash
# 启动MySQL
docker run -d --name mysql-dev \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=security_teaching_system \
  -p 3306:3306 \
  mysql:8.0

# 启动后端
cd src/backend
mvn spring-boot:run

# 启动前端
cd src/frontend
npm install
npm run dev
```

## 📝 贡献流程

### 1. 提交Issue
在提交代码之前，请先创建一个Issue来描述您要解决的问题或新功能。

**Bug报告模板：**
```markdown
## Bug描述
简要描述Bug的情况

## 重现步骤
1. 进入页面...
2. 点击按钮...
3. 出现错误...

## 预期行为
描述您期望的正确行为

## 实际行为
描述实际发生的情况

## 环境信息
- 操作系统：
- 浏览器：
- 版本：
```

**功能建议模板：**
```markdown
## 功能描述
简要描述您希望添加的功能

## 使用场景
描述这个功能的使用场景和价值

## 实现建议
如果有实现建议，请详细描述

## 相关Issue
关联相关的Issue
```

### 2. 开发代码
```bash
# 创建功能分支
git checkout -b feature/your-feature-name

# 进行开发...

# 提交代码
git add .
git commit -m "feat: 添加新功能描述"
```

### 3. 代码规范
请遵循以下代码规范：

**Java代码规范：**
- 使用Google Java Style Guide
- 类名使用PascalCase
- 方法名使用camelCase
- 常量使用UPPER_SNAKE_CASE
- 添加适当的注释

**Vue/TypeScript代码规范：**
- 使用ESLint和Prettier
- 组件名使用PascalCase
- 文件名使用kebab-case
- 添加类型注解

**提交信息规范：**
```
<type>(<scope>): <subject>

<body>

<footer>
```

类型说明：
- `feat`: 新功能
- `fix`: Bug修复
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

### 4. 测试代码
```bash
# 后端测试
cd src/backend
mvn test

# 前端测试
cd src/frontend
npm test

# 集成测试
npm run test:e2e
```

### 5. 提交Pull Request
```bash
# 推送分支
git push origin feature/your-feature-name

# 在GitHub上创建Pull Request
```

**Pull Request模板：**
```markdown
## 变更描述
简要描述本次变更的内容

## 变更类型
- [ ] Bug修复
- [ ] 新功能
- [ ] 文档更新
- [ ] 代码重构
- [ ] 性能优化
- [ ] 其他

## 测试说明
描述如何测试这些变更

## 相关Issue
关联的Issue编号

## 截图（如有UI变更）
上传相关截图
```

## 🎯 贡献类型

### 🐛 Bug修复
1. 在Issue列表中查找标记为"bug"的Issue
2. 确认Bug可以重现
3. 创建修复分支
4. 编写修复代码和测试
5. 提交Pull Request

### 💡 新功能开发
1. 在Issue列表中查找标记为"enhancement"的Issue
2. 与维护者讨论实现方案
3. 创建功能分支
4. 实现功能并添加测试
5. 更新相关文档
6. 提交Pull Request

### 📝 文档改进
1. 识别需要改进的文档
2. 创建文档分支
3. 改进文档内容
4. 检查语法和格式
5. 提交Pull Request

### 🎨 UI/UX改进
1. 识别UI/UX问题
2. 设计改进方案
3. 实现UI变更
4. 测试不同设备和浏览器
5. 提交Pull Request

## 🔍 代码审查

### 审查标准
- 代码符合项目规范
- 功能实现正确
- 测试覆盖充分
- 文档更新完整
- 性能影响评估

### 审查流程
1. 自动检查（CI/CD）
2. 维护者审查
3. 社区反馈
4. 合并或要求修改

## 🏆 贡献者认可

### 贡献者类型
- **核心维护者**: 长期参与项目维护
- **活跃贡献者**: 定期提交代码
- **文档贡献者**: 主要贡献文档
- **社区贡献者**: 帮助回答问题

### 认可方式
- 在README中列出贡献者
- 在发布说明中提及贡献
- 颁发贡献者徽章
- 邀请参与项目决策

## 📞 获取帮助

### 联系方式
- 📖 查看文档：[项目文档](docs/)
- 💬 讨论交流：[GitHub Discussions](https://github.com/your-username/javaweb-security-teaching-system/discussions)
- 🐛 报告问题：[GitHub Issues](https://github.com/your-username/javaweb-security-teaching-system/issues)
- 📧 联系维护者：maintainer@javaweb-security.com

### 社区资源
- 📚 学习资源：[安全学习指南](docs/user-guide/SECURITY_LEARNING_GUIDE.md)
- 🛠️ 开发工具：[开发环境搭建](docs/deployment/DEVELOPMENT_SETUP.md)
- 🧪 测试指南：[测试最佳实践](docs/development/TESTING_GUIDE.md)

## 📋 贡献清单

在提交贡献之前，请确认：

- [ ] 我已经阅读并理解了贡献指南
- [ ] 我的代码遵循了项目的代码规范
- [ ] 我已经添加了必要的测试
- [ ] 我已经更新了相关文档
- [ ] 我的提交信息遵循了规范格式
- [ ] 我已经检查了代码没有引入新的问题

感谢您的贡献！🎉
