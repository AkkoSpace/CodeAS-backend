# 故障排除和修复指南

本目录包含项目开发和部署过程中遇到的常见问题及其解决方案。

## 📁 目录结构

```
troubleshooting/
├── README.md                           # 本文档
├── github-actions/                     # GitHub Actions 相关问题
│   ├── overview.md                     # GitHub Actions 问题总览
│   ├── owasp-dependency-check.md       # OWASP 依赖检查问题
│   └── github-container-registry.md    # GHCR 推送问题
├── build/                              # 构建相关问题
├── deployment/                         # 部署相关问题
├── database/                           # 数据库相关问题
└── security/                           # 安全相关问题
```

## 🔧 GitHub Actions 问题

### [GitHub Actions 问题总览](github-actions/overview.md)
- 部署失败修复
- 安全扫描失败修复
- 构建配置优化
- NVD API 403 错误修复
- GitHub Container Registry 推送失败修复

### [OWASP Dependency Check 修复](github-actions/owasp-dependency-check.md)
- NVD API 密钥配置
- 403 错误解决方案
- 本地开发配置
- 故障排除指南

### [GitHub Container Registry 修复](github-actions/github-container-registry.md)
- 权限配置问题
- 镜像推送失败
- 仓库设置指南
- 验证和测试方法

## 🏗️ 构建问题

*待添加*

## 🚀 部署问题

*待添加*

## 🗄️ 数据库问题

*待添加*

## 🔒 安全问题

*待添加*

## 📝 如何使用本指南

1. **查找问题**：根据错误信息或问题类型找到对应的文档
2. **按步骤操作**：每个修复指南都包含详细的步骤说明
3. **验证修复**：按照文档中的验证步骤确认问题已解决
4. **反馈问题**：如果遇到文档中未涵盖的问题，请创建 Issue

## 🤝 贡献指南

如果您遇到了新的问题并找到了解决方案，欢迎贡献到本指南：

1. 在对应的目录下创建新的 Markdown 文件
2. 使用统一的文档格式：
   - 问题描述
   - 问题原因
   - 解决方案
   - 验证步骤
   - 相关链接
3. 更新本 README 文件的目录
4. 提交 Pull Request

## 📋 文档模板

```markdown
# 问题标题

## 问题描述
详细描述遇到的问题和错误信息

## 问题原因
分析问题的根本原因

## 解决方案
### 方案 1：推荐方案
详细的解决步骤

### 方案 2：备选方案
备选的解决方法

## 验证修复
如何验证问题已经解决

## 相关链接
- 相关文档链接
- 参考资料
```

## 🔗 相关文档

- [开发指南](../guides/DEVELOPMENT_GUIDE.md)
- [部署指南](../deployment/DEPLOYMENT_GUIDE.md)
- [GitHub 设置指南](../guides/GITHUB_SETUP.md)
- [安全指南](../guides/SECURITY_GUIDE.md)
