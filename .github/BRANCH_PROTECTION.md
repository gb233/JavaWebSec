# 分支保护规则配置指南

## 🎯 目标

保护主分支，防止意外删除或强制推送，并确保代码质量。

## ⚠️ 当前问题

1. **主分支未保护**：任何人都可以强制推送或删除
2. **Dependabot分支过多**：每周自动创建多个依赖更新分支
3. **缺少状态检查**：合并前没有验证CI/CD是否通过

## 🔧 解决方案

### 方案1: 通过GitHub Web界面配置（推荐）

#### 步骤1: 进入分支保护设置

```
1. 进入仓库 Settings → Branches
2. 点击 "Add rule" 或 "Add branch protection rule"
3. 在 "Branch name pattern" 输入: main
```

#### 步骤2: 配置保护规则

**必需配置：**

✅ **Protect matching branches** - 启用分支保护

✅ **Require a pull request before merging**
   - ✅ Require approvals: 1（至少1个审批）
   - ✅ Dismiss stale pull request approvals when new commits are pushed
   - ✅ Require review from Code Owners（如果有CODEOWNERS文件）

✅ **Require status checks to pass before merging**
   - ✅ Require branches to be up to date before merging
   - 选择必需的状态检查：
     - `CI / Backend Build`
     - `CI / Frontend Lint & Build`
     - `Security Scan / CodeQL Security Analysis (Admin Only)`
     - `Database Schema Verification`（如果相关）

✅ **Require conversation resolution before merging**
   - 确保所有评论都已解决

✅ **Do not allow bypassing the above settings**
   - 即使是管理员也不能绕过

**可选配置：**

⚠️ **Restrict pushes that create files larger than 100 MB**
   - 防止大文件提交

⚠️ **Do not allow force pushes**
   - 禁止强制推送

⚠️ **Do not allow deletions**
   - 禁止删除分支

### 方案2: 使用GitHub API配置（高级）

如果需要通过API或脚本配置，可以使用GitHub CLI：

```bash
# 安装GitHub CLI
brew install gh  # macOS
# 或访问 https://cli.github.com/

# 登录
gh auth login

# 配置分支保护规则
gh api repos/:owner/:repo/branches/main/protection \
  --method PUT \
  --field required_status_checks='{"strict":true,"contexts":["CI / Backend Build","CI / Frontend Lint & Build","Security Scan / CodeQL Security Analysis (Admin Only)"]}' \
  --field enforce_admins=true \
  --field required_pull_request_reviews='{"required_approving_review_count":1,"dismiss_stale_reviews":true}' \
  --field restrictions=null
```

## 📋 推荐配置清单

### 主分支 (main/master)

- [x] Require a pull request before merging
- [x] Require approvals: 1
- [x] Require status checks to pass
- [x] Require branches to be up to date
- [x] Require conversation resolution
- [x] Do not allow force pushes
- [x] Do not allow deletions
- [x] Do not allow bypassing

### 开发分支 (develop)

- [x] Require a pull request before merging
- [x] Require approvals: 1
- [x] Require status checks to pass
- [ ] Do not allow force pushes（可选，开发分支可能需要）
- [ ] Do not allow deletions（可选）

## 🔍 状态检查配置

### 必需的状态检查

根据当前工作流，需要配置以下状态检查：

1. **CI / Backend Build** - 后端构建和测试
2. **CI / Frontend Lint & Build** - 前端构建和测试
3. **Security Scan / CodeQL Security Analysis (Admin Only)** - 代码安全扫描
4. **Database Schema Verification** - 数据库验证（如果相关文件变更）

### 如何获取状态检查名称

1. 推送代码触发工作流
2. 查看工作流执行结果
3. 在分支保护设置中，状态检查名称会自动出现在列表中

## 🧹 Dependabot分支管理

### 问题

Dependabot每周会创建多个分支，导致分支数量过多。

### 解决方案

#### 方案1: 减少PR数量限制

修改 `.github/dependabot.yml`：

```yaml
updates:
  - package-ecosystem: "maven"
    open-pull-requests-limit: 5  # 从10减少到5
  - package-ecosystem: "npm"
    open-pull-requests-limit: 5  # 从10减少到5
```

#### 方案2: 批量更新配置

```yaml
updates:
  - package-ecosystem: "maven"
    open-pull-requests-limit: 3
    # 只创建安全更新PR
    allow:
      - dependency-type: "security"
```

#### 方案3: 定期清理旧分支

创建清理脚本或使用GitHub Actions自动清理已合并的Dependabot分支。

### 推荐配置

```yaml
# 限制每个生态系统的PR数量
open-pull-requests-limit: 3  # 最多3个打开的PR

# 优先处理安全更新
allow:
  - dependency-type: "security"
```

## 📊 分支保护效果

### 配置前

- ❌ 任何人都可以强制推送
- ❌ 任何人都可以删除分支
- ❌ 可以直接推送到主分支
- ❌ 没有代码审查要求
- ❌ 没有CI/CD验证要求

### 配置后

- ✅ 必须通过PR合并
- ✅ 需要至少1个审批
- ✅ 必须通过所有状态检查
- ✅ 禁止强制推送
- ✅ 禁止删除分支
- ✅ 管理员也不能绕过

## 🚨 注意事项

### 1. 首次配置

- 配置后，即使是仓库管理员也需要通过PR合并
- 确保工作流正常运行，否则无法合并代码

### 2. 状态检查名称

- 状态检查名称必须与工作流中的job名称完全匹配
- 区分大小写

### 3. Dependabot权限

- Dependabot需要权限来更新分支
- 确保Dependabot可以推送更新

### 4. 紧急情况

- 如果配置错误导致无法合并，可以临时禁用保护规则
- 但需要管理员权限

## 🔧 故障排查

### 无法合并PR

1. **检查状态检查**：
   - 确保所有必需的状态检查都通过
   - 检查工作流是否正常运行

2. **检查审批**：
   - 确保有足够的审批
   - 检查审批者是否有权限

3. **检查分支状态**：
   - 确保分支是最新的
   - 可能需要更新分支

### Dependabot无法更新分支

1. **检查权限**：
   - 确保Dependabot有写入权限
   - 检查分支保护规则是否过于严格

2. **检查配置**：
   - 确保Dependabot配置正确
   - 检查是否有冲突的规则

## 📝 相关文档

- [GitHub分支保护文档](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches)
- [Dependabot配置文档](https://docs.github.com/en/code-security/dependabot/dependabot-version-updates/configuration-options-for-the-dependabot.yml-file)
- [状态检查文档](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches#require-status-checks-before-merging)

---

**重要提示**：配置分支保护后，所有对主分支的更改都必须通过PR，确保代码质量和安全性。



