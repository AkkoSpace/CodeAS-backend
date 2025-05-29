# GitHub Actions 问题修复总览

## 问题概述

通过分析GitHub Actions的运行日志，发现了以下主要问题并进行了修复：

## 修复的问题

### 1. 部署失败 (🚀 Deploy to Staging)

**问题**：
- Maven Assembly Plugin 找不到 `src/main/assembly/assembly.xml` 文件
- Slack 通知配置缺少密钥检查

**修复**：
- 将 Assembly Plugin 配置改为使用内置的 `jar-with-dependencies` 描述符
- 添加了 Slack webhook 密钥存在性检查

**修改文件**：
- `pom.xml`: 更新 Assembly Plugin 配置
- `.github/workflows/deploy.yml`: 添加 Slack 密钥检查

### 2. 安全扫描失败 (🔒 Security Vulnerability Scan)

**问题**：
- OWASP 依赖检查报告文件不存在
- Snyk SARIF 文件不存在
- 权限问题导致上传失败

**修复**：
- 修复 OWASP 依赖检查命令
- 配置 OWASP 插件生成 HTML 和 JSON 格式报告
- 修复 Snyk 扫描配置，添加 SARIF 输出
- 添加文件存在性检查

**修改文件**：
- `pom.xml`: 更新 OWASP 插件配置
- `.github/workflows/code-quality.yml`: 修复扫描命令
- `.github/workflows/ci.yml`: 修复安全扫描命令

### 3. 构建配置优化

**问题**：
- Assembly 插件配置不正确
- 分发包路径错误

**修复**：
- 使用标准的 `jar-with-dependencies` 描述符
- 修正分发包的文件路径匹配

**修改文件**：
- `pom.xml`: Assembly 插件配置
- `.github/workflows/ci.yml`: 分发包路径

### 4. NVD API 403 错误修复 (🔒 Security Vulnerability Scan)

**问题**：
- OWASP dependency-check 插件报错 "NVD Returned Status Code: 403"
- NVD API 现在需要 API 密钥才能访问

**修复**：
- 添加 NVD API 密钥配置
- 配置自动降级策略（无密钥时跳过更新）
- 增加重试机制和超时设置
- 使用本地缓存减少 API 调用

**修改文件**：
- `pom.xml`: 更新 OWASP 插件配置，添加 NVD API 支持
- `.github/workflows/code-quality.yml`: 添加 NVD_API_KEY 环境变量
- `.github/workflows/ci.yml`: 添加 NVD_API_KEY 环境变量
- `docs/troubleshooting/github-actions/owasp-dependency-check.md`: 详细修复指南

### 5. GitHub Container Registry 推送失败 (🐳 Docker Build)

**问题**：
- Docker 镜像推送到 GHCR 时遇到 "403 Forbidden" 错误
- GitHub Actions 缺少 packages 写入权限

**修复**：
- 添加 `packages: write` 权限到 Docker 构建作业
- 改进镜像命名和标签策略
- 添加登录验证步骤
- 优化镜像元数据配置

**修改文件**：
- `.github/workflows/ci.yml`: 添加权限配置和登录验证
- `docs/troubleshooting/github-actions/github-container-registry.md`: 详细修复指南

## 配置说明

### OWASP 依赖检查

现在配置为：
- 生成 HTML 和 JSON 格式报告
- 使用抑制文件 `.github/dependency-check-suppressions.xml`
- CVSS 阈值设为 7
- 支持 NVD API 密钥配置

### Snyk 安全扫描

配置为：
- 生成 SARIF 格式报告
- 严重性阈值设为 high
- 允许失败但继续执行

### Assembly 插件

配置为：
- 使用内置的 `jar-with-dependencies` 描述符
- 设置主类为 `space.akko.backend.BackendApplication`
- 在 package 阶段执行

### Docker 构建

配置为：
- 具有 `packages: write` 权限
- 推送到 GitHub Container Registry
- 支持多种标签策略
- 包含完整的镜像元数据

## 后续建议

1. **配置密钥和权限**：
   - 在 GitHub 仓库设置中添加必要的密钥：
     - `NVD_API_KEY`: NVD API 密钥（从 https://nvd.nist.gov/developers/request-an-api-key 获取）
     - `SLACK_WEBHOOK`: Slack 通知（可选）
     - `SNYK_TOKEN`: Snyk 安全扫描（可选）
   - 配置 GitHub Actions 权限：
     - Settings → Actions → General → Workflow permissions
     - 选择 "Read and write permissions"
     - 勾选 "Allow GitHub Actions to create and approve pull requests"

2. **测试验证**：
   - 推送代码到 dev 分支验证修复效果
   - 检查各个工作流是否正常运行
   - 特别关注：
     - OWASP dependency-check 是否正常工作
     - Docker 镜像是否成功推送到 GHCR
     - 安全扫描报告是否正常生成

3. **监控优化**：
   - 定期检查安全扫描报告
   - 根据实际情况调整 CVSS 阈值和抑制规则
   - 监控 NVD API 使用情况，避免超过限制
   - 检查 GitHub Container Registry 中的镜像

## 文件变更清单

- `pom.xml`: Assembly 和 OWASP 插件配置
- `.github/workflows/ci.yml`: 安全扫描、构建配置和 Docker 权限
- `.github/workflows/code-quality.yml`: 安全扫描配置
- `.github/workflows/deploy.yml`: Slack 通知配置
- `docs/troubleshooting/github-actions/overview.md`: 本文档
- `docs/troubleshooting/github-actions/owasp-dependency-check.md`: OWASP 修复指南
- `docs/troubleshooting/github-actions/github-container-registry.md`: GHCR 推送修复指南

## 相关文档

- [OWASP Dependency Check 修复指南](owasp-dependency-check.md)
- [GitHub Container Registry 修复指南](github-container-registry.md)
- [GitHub 设置指南](../../guides/GITHUB_SETUP.md)
- [部署指南](../../deployment/DEPLOYMENT_GUIDE.md)
