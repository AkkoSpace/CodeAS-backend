# GitHub Actions 问题修复说明

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

## 配置说明

### OWASP 依赖检查

现在配置为：
- 生成 HTML 和 JSON 格式报告
- 使用抑制文件 `.github/dependency-check-suppressions.xml`
- CVSS 阈值设为 7

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

## 后续建议

1. **配置密钥**：
   - 在 GitHub 仓库设置中添加必要的密钥（如 SLACK_WEBHOOK、SNYK_TOKEN 等）

2. **测试验证**：
   - 推送代码到 dev 分支验证修复效果
   - 检查各个工作流是否正常运行

3. **监控优化**：
   - 定期检查安全扫描报告
   - 根据实际情况调整 CVSS 阈值和抑制规则

## 文件变更清单

- `pom.xml`: Assembly 和 OWASP 插件配置
- `.github/workflows/ci.yml`: 安全扫描和构建配置
- `.github/workflows/code-quality.yml`: 安全扫描配置
- `.github/workflows/deploy.yml`: Slack 通知配置
- `docs/github-actions-fixes.md`: 本文档
