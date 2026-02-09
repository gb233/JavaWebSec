# GitHub Actions 工作流管理

## 📋 当前启用的工作流

### ✅ 必需工作流（会自动触发）

| 工作流文件 | 触发条件 | 用途 | 资源消耗 |
|-----------|---------|------|---------|
| `ci.yml` | push/PR | 前后端构建和测试 | 中等 |
| `security-scan.yml` | push/PR/定时 | 安全扫描（仅管理员可见） | 高 |
| `database-schema-verify.yml` | SQL/Entity变更 | 数据库结构验证 | 中等 |
| `docker-build.yml` | push/PR/标签 | Docker镜像构建 | 高 |
| `deploy-vps.yml` | CI 成功后 / 手动 | 部署到 VPS 演示环境 | 低 |

### ⚠️ 已禁用工作流（不会触发）

| 工作流文件 | 位置 | 原因 |
|-----------|------|------|
| `security-scan.yml.disabled` | `disabled/` | 已被管理员可见版本替代 |
| `security-scan-private.yml.disabled` | `disabled/` | 重复，已被管理员可见版本替代 |
| `ci-enhanced.yml.disabled` | `disabled/` | 功能重复，使用简化版 `ci.yml` |

## 🎯 工作流触发规则

### 1. CI 工作流 (`ci.yml`)
```yaml
触发条件:
  - push 到 main/master/develop
  - 所有 PR
功能:
  - 后端单元测试和构建
  - 前端 lint 检查和构建
```

### 2. 安全扫描工作流 (`security-scan.yml`)
```yaml
触发条件:
  - push 到 main/master/develop
  - PR 到 main/master
  - 每天凌晨2点（定时）
  - 手动触发
功能:
  - CodeQL 代码安全扫描
  - 后端依赖漏洞扫描
  - 前端依赖漏洞扫描
  - 密钥泄露扫描
权限:
  - 详细结果仅管理员可见
  - 普通成员只能看到摘要
```

### 3. 数据库验证工作流 (`database-schema-verify.yml`)
```yaml
触发条件:
  - SQL 脚本变更
  - Entity 类变更
  - 手动触发
功能:
  - 验证数据库结构与代码一致性
  - 生成差异报告
```

### 4. Docker 构建工作流 (`docker-build.yml`)
```yaml
触发条件:
  - push 到 main/master
  - 版本标签 (v*)
  - PR 到 main/master（不推送）
  - 手动触发
功能:
  - 构建后端镜像
  - 构建前端镜像
  - 构建全栈镜像
```

### 5. VPS 部署工作流 (`deploy-vps.yml`)
```yaml
触发条件:
  - CI 工作流完成后且分支为 main/master
  - 手动触发 (workflow_dispatch)
功能:
  - 当前仅支持 Docker 方式，要求 VPS 已安装 Docker、已 clone 本仓库到 DEPLOY_PATH
  - 可选先停止现有容器（小内存 VPS 建议开启）：STOP_BEFORE_START=true 时先执行 docker compose down，再 git pull 与 up -d --build，避免双进程导致宕机
  - 使用 docker-compose.prod.yml 更新演示环境
  - 可选：对 DEMO_URL 做健康检查
从零环境:
  - 首次部署可在 VPS 上执行 scripts/ubuntu-docker-deploy.sh，或手动安装 Docker 后 clone 仓库再执行 docker compose -f docker-compose.prod.yml up -d
所需 Secrets:
  - VPS_HOST: VPS 主机名或 IP
  - VPS_USER: SSH 登录用户名
  - SSH_PRIVATE_KEY: 能登录 VPS 的私钥全文
  - DEPLOY_PATH:（可选）项目在 VPS 上的路径，默认 javaweb-security
  - VPS_PORT:（可选）SSH 端口，默认 22
  - DEMO_URL:（可选）演示站健康检查 URL，如 http://javasec.icu:8080
  - STOP_BEFORE_START:（可选）部署前是否先停止现有容器，默认 true，小内存 VPS 建议保持 true
```

## ⚠️ 重要说明

### 避免重复触发

**已解决的问题：**
- ✅ 移除了重复的安全扫描工作流（3个 → 1个）
- ✅ 移除了重复的CI工作流（2个 → 1个）
- ✅ 所有备用工作流已禁用

**当前状态：**
- ✅ 每次提交只会触发必要的4个工作流
- ✅ 没有重复执行
- ✅ 资源使用优化

### 资源消耗估算

| 工作流 | 平均执行时间 | 资源消耗 | 频率 |
|-------|------------|---------|------|
| CI | 5-10分钟 | 中等 | 每次提交 |
| 安全扫描 | 15-30分钟 | 高 | 每次提交+定时 |
| 数据库验证 | 3-5分钟 | 低 | 相关文件变更 |
| Docker构建 | 10-20分钟 | 高 | main分支+标签 |

**总估算：**
- 每次提交到 main：约 30-60 分钟总执行时间
- 每次 PR：约 20-40 分钟总执行时间

## 🔧 如何启用/禁用工作流

### 禁用工作流

```bash
# 方法1: 移动到 disabled 目录
mv .github/workflows/workflow-name.yml .github/workflows/disabled/workflow-name.yml.disabled

# 方法2: 重命名添加 .disabled 后缀
mv .github/workflows/workflow-name.yml .github/workflows/workflow-name.yml.disabled
```

### 启用工作流

```bash
# 从 disabled 目录移回
mv .github/workflows/disabled/workflow-name.yml.disabled .github/workflows/workflow-name.yml
```

## 📊 工作流状态监控

### 查看工作流执行情况

1. **GitHub Actions 标签页**
   - 查看所有工作流的执行状态
   - 查看执行日志和结果

2. **工作流摘要**
   - 每个工作流执行后会生成摘要
   - 包含关键信息和状态

### 优化建议

1. **减少触发频率**
   - 数据库验证：只在相关文件变更时触发 ✅
   - Docker构建：只在 main 分支触发 ✅

2. **使用缓存**
   - Maven 依赖缓存 ✅
   - npm 依赖缓存 ✅
   - Docker 层缓存 ✅

3. **并行执行**
   - 前后端测试并行 ✅
   - 多个安全扫描工具并行 ✅

## 🚨 故障排查

### 工作流未触发

1. 检查文件是否在 `.github/workflows/` 目录
2. 检查文件名是否为 `.yml` 或 `.yaml`
3. 检查触发条件是否匹配
4. 检查是否有语法错误

### 工作流重复触发

1. 检查是否有重复的工作流文件
2. 检查 `disabled/` 目录中的文件是否已禁用
3. 检查工作流名称是否唯一

### 资源消耗过高

1. 检查是否有重复的工作流
2. 优化工作流执行时间
3. 使用缓存减少重复工作
4. 考虑使用条件执行

## 📝 更新日志

### 2024-12-XX: 工作流清理
- ✅ 禁用重复的安全扫描工作流
- ✅ 禁用重复的CI工作流
- ✅ 统一使用管理员可见的安全扫描
- ✅ 创建工作流管理文档

---

**提示**：如需启用已禁用的工作流，请先评估是否会导致重复触发和资源浪费。



