# GitHub功能配置指南

本文档介绍如何配置和使用项目中的GitHub功能。

## 🚀 GitHub Actions

### 已配置的工作流

#### 1. CI Pipeline (`.github/workflows/ci.yml`)
**触发条件：**
- 推送到 `main` 或 `dev` 分支
- 创建Pull Request到 `main` 或 `dev` 分支

**功能：**
- ✅ 代码编译和测试
- 📊 测试覆盖率报告
- 🏗️ 应用构建
- 🔒 安全扫描
- 🐳 Docker镜像构建

#### 2. 部署工作流 (`.github/workflows/deploy.yml`)
**触发条件：**
- 推送到 `dev` 分支（自动部署到staging）
- 手动触发（可选择staging或production）

**功能：**
- 🚀 自动部署到测试环境
- 🎯 手动部署到生产环境
- 🏥 健康检查
- 🔄 失败自动回滚
- 📦 创建Release

#### 3. 代码质量检查 (`.github/workflows/code-quality.yml`)
**触发条件：**
- 推送到主要分支
- Pull Request
- 每周定时扫描

**功能：**
- 📊 SonarCloud代码质量分析
- 🔒 OWASP依赖安全扫描
- 📄 许可证合规检查
- ⚡ 性能测试

### 需要配置的Secrets

在GitHub仓库的 `Settings > Secrets and variables > Actions` 中添加：

#### 必需的Secrets
```bash
# SonarCloud配置
SONAR_TOKEN=your_sonar_token

# Snyk安全扫描
SNYK_TOKEN=your_snyk_token

# 部署服务器SSH配置
STAGING_SSH_KEY=your_staging_private_key
STAGING_HOST=your_staging_host
STAGING_USER=your_staging_user

PRODUCTION_SSH_KEY=your_production_private_key
PRODUCTION_HOST=your_production_host
PRODUCTION_USER=your_production_user

# 通知配置
SLACK_WEBHOOK=your_slack_webhook_url
```

#### 环境变量 (Variables)
```bash
# 环境URL
STAGING_URL=https://staging.akko.space
PRODUCTION_URL=https://api.akko.space
```

## 🔄 Dependabot

### 自动依赖更新
Dependabot已配置为：
- **Maven依赖**：每周一检查更新
- **Docker镜像**：每周二检查更新
- **GitHub Actions**：每周三检查更新

### 配置说明
- 自动创建PR进行依赖更新
- 忽略主要版本更新（需手动处理）
- 自动分配给项目维护者
- 使用gitmoji提交格式

## 📋 Issue和PR模板

### Issue模板
1. **🐛 Bug Report** - 用于报告bug
2. **✨ Feature Request** - 用于功能请求

### PR模板
包含完整的变更检查清单，确保：
- 代码质量
- 测试覆盖
- 文档更新
- 部署注意事项

## 🔒 安全功能

### 1. 代码扫描
- **CodeQL**：GitHub原生代码安全分析
- **Snyk**：第三方依赖漏洞扫描
- **OWASP Dependency Check**：开源依赖安全检查

### 2. Secret扫描
- 自动检测提交中的敏感信息
- 防止API密钥、密码等泄露

### 3. 依赖审查
- 自动检查新依赖的安全风险
- 在PR中显示依赖变更影响

## 📦 GitHub Packages

### Docker镜像仓库
配置为自动推送Docker镜像到GitHub Container Registry：
```bash
ghcr.io/akkospace/codeas-backend:latest
ghcr.io/akkospace/codeas-backend:dev
```

### Maven包发布
可配置发布Maven包到GitHub Packages（可选）。

## 🎯 项目管理

### GitHub Projects
建议创建项目看板管理开发进度：

1. **Backlog** - 待开发功能
2. **In Progress** - 开发中
3. **Review** - 代码审查中
4. **Testing** - 测试中
5. **Done** - 已完成

### Labels标签
建议使用的标签：
- `bug` - Bug报告
- `enhancement` - 功能增强
- `documentation` - 文档相关
- `dependencies` - 依赖更新
- `security` - 安全相关
- `performance` - 性能优化
- `needs-triage` - 需要分类

## 📊 监控和报告

### 代码覆盖率
- **Codecov**：代码覆盖率报告
- **SonarCloud**：代码质量指标

### 性能监控
- CI中的性能测试
- 部署后的健康检查

## 🔧 本地开发集成

### 预提交钩子
建议安装pre-commit钩子：
```bash
# 安装pre-commit
pip install pre-commit

# 安装钩子
pre-commit install
```

### IDE集成
- **SonarLint**：实时代码质量检查
- **GitHub Copilot**：AI代码助手

## 📚 最佳实践

### 1. 分支策略
- `main` - 生产环境代码
- `dev` - 开发环境代码
- `feature/*` - 功能分支
- `hotfix/*` - 紧急修复分支

### 2. 提交规范
- 使用gitmoji + 类型前缀
- 清晰的提交信息
- 小而频繁的提交

### 3. PR流程
- 创建功能分支
- 完整的PR描述
- 代码审查
- 自动化测试通过
- 合并到目标分支

### 4. 发布流程
- 从dev分支测试
- 合并到main分支
- 手动触发生产部署
- 创建Release标签

## 🚨 故障排除

### 常见问题

1. **CI失败**
   - 检查测试用例
   - 查看依赖冲突
   - 验证环境配置

2. **部署失败**
   - 检查服务器连接
   - 验证环境变量
   - 查看应用日志

3. **安全扫描误报**
   - 更新抑制配置
   - 升级依赖版本
   - 联系安全团队

### 获取帮助
- 查看GitHub Actions日志
- 检查仓库Issues
- 联系项目维护者
