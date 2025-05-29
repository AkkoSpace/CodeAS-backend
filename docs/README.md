# 📚 文档中心

欢迎来到 CodeAS Backend 的文档中心！这里包含了项目的所有技术文档和指南。

## 📁 文档结构

```
docs/
├── README.md                    # 文档中心首页（本文件）
├── project/                     # 项目文档
│   ├── ROADMAP.md               # 项目发展路线图（主要文档）
│   ├── PROJECT_SUMMARY.md       # 项目技术总结
│   ├── ARCHITECTURE.md          # 系统架构文档
│   └── CHANGELOG.md             # 版本变更记录
├── guides/                      # 开发指南
│   ├── DEVELOPMENT_GUIDE.md     # 开发指南
│   ├── SECURITY_GUIDE.md        # 安全指南
│   ├── FLYWAY_GUIDE.md          # Flyway数据库迁移指南
│   └── ENVIRONMENT_VARIABLES.md # 环境变量配置指南
├── deployment/                  # 部署文档
│   ├── DEPLOYMENT_GUIDE.md      # 部署指南
│   ├── deployment-setup.md      # 部署环境准备清单
│   └── deployment-flow-explanation.md # GitHub Actions 部署流程说明
└── troubleshooting/             # 故障排除和修复指南
    ├── README.md                # 故障排除总览
    └── github-actions/          # GitHub Actions 相关问题
        ├── overview.md          # GitHub Actions 问题总览
        ├── owasp-dependency-check.md    # OWASP 依赖检查问题
        └── github-container-registry.md # GHCR 推送问题
```

## 🎯 快速导航

### 🚀 新手入门
如果您是第一次接触这个项目，建议按以下顺序阅读：

1. **[项目路线图](project/ROADMAP.md)** - 了解项目当前状态和发展计划
2. **[技术总结](project/PROJECT_SUMMARY.md)** - 了解项目技术架构和特性
3. **[系统架构](project/ARCHITECTURE.md)** - 理解系统设计和技术架构
4. **[开发指南](guides/DEVELOPMENT_GUIDE.md)** - 搭建开发环境，开始编码
5. **[在线API文档](http://localhost:26300/swagger-ui.html)** - 查看实时API接口文档

### 👨‍💻 开发人员
- **[开发指南](guides/DEVELOPMENT_GUIDE.md)** - 开发环境、代码规范、测试指南
- **[环境变量配置](guides/ENVIRONMENT_VARIABLES.md)** - 环境变量管理和配置
- **[Flyway指南](guides/FLYWAY_GUIDE.md)** - 数据库迁移管理
- **[系统架构](project/ARCHITECTURE.md)** - 深入了解系统设计
- **[在线API文档](http://localhost:26300/swagger-ui.html)** - 实时API接口文档

### 🚀 运维人员
- **[部署指南](deployment/DEPLOYMENT_GUIDE.md)** - 部署、配置、监控指南
- **[部署环境准备](deployment/deployment-setup.md)** - 服务器和环境准备清单
- **[部署流程说明](deployment/deployment-flow-explanation.md)** - GitHub Actions 部署流程详解
- **[安全指南](guides/SECURITY_GUIDE.md)** - 安全配置和最佳实践

### 🔒 安全管理员
- **[安全指南](guides/SECURITY_GUIDE.md)** - 安全配置、权限管理、应急处理

### 🔧 故障排除
- **[故障排除总览](fixes/README.md)** - 常见问题和解决方案
- **[GitHub Actions 问题](fixes/github-actions/overview.md)** - CI/CD 相关问题修复

## 📖 文档说明

### 项目文档 (project/)
包含项目的整体介绍、架构设计和版本历史：

- **ROADMAP.md**: 项目发展路线图，包括当前状态、功能实现进度、版本规划、长期愿景等（主要文档）
- **PROJECT_SUMMARY.md**: 项目技术总结，包括架构设计、技术栈、快速启动指南等
- **ARCHITECTURE.md**: 详细的系统架构文档，包括分层设计、数据架构、安全架构等
- **CHANGELOG.md**: 版本变更记录，遵循Keep a Changelog格式

### 开发指南 (guides/)
面向开发人员的指南文档：

- **DEVELOPMENT_GUIDE.md**: 开发环境搭建、项目结构、开发规范、测试指南等
- **ENVIRONMENT_VARIABLES.md**: 环境变量配置指南，包括不同环境的配置管理
- **SECURITY_GUIDE.md**: 安全配置指南、最佳实践、应急处理流程等
- **FLYWAY_GUIDE.md**: Flyway数据库迁移工具的使用指南和最佳实践

### 部署文档 (deployment/)
面向运维人员的部署和运维文档：

- **DEPLOYMENT_GUIDE.md**: 详细的部署指南，包括本地部署、Docker部署、生产环境部署等
- **deployment-setup.md**: 部署环境准备清单，包括服务器配置、域名设置等
- **deployment-flow-explanation.md**: GitHub Actions 部署流程详细说明

### 故障排除 (troubleshooting/)
常见问题的解决方案和修复指南：

- **README.md**: 故障排除总览，包含目录结构和使用指南
- **github-actions/**: GitHub Actions 相关问题修复
  - **overview.md**: GitHub Actions 问题总览和修复记录
  - **owasp-dependency-check.md**: OWASP 依赖检查 NVD API 问题修复
  - **github-container-registry.md**: GitHub Container Registry 推送权限问题修复



## 🔗 API文档

项目使用Swagger自动生成API文档，确保文档与代码同步：

- **Swagger UI**: http://localhost:26300/swagger-ui.html - 交互式API文档界面
- **OpenAPI Spec**: http://localhost:26300/v3/api-docs - OpenAPI 3.0规范文件

**为什么使用自动生成的API文档？**
- ✅ **实时同步**：文档与代码自动同步，永远不会过时
- ✅ **交互测试**：可以直接在文档中测试API接口
- ✅ **标准格式**：遵循OpenAPI 3.0标准
- ✅ **零维护成本**：无需手动维护API文档

## 📝 文档维护

### 文档更新原则
1. **及时性**: 代码变更时同步更新相关文档
2. **准确性**: 确保文档内容与实际代码保持一致
3. **完整性**: 重要功能和API都应有相应文档
4. **易读性**: 使用清晰的结构和示例

### 文档格式规范
- 使用Markdown格式编写
- 遵循统一的文档结构和样式
- 包含必要的代码示例和截图
- 使用emoji增强可读性

### 贡献指南
如果您发现文档有错误或需要改进，欢迎：
1. 提交Issue报告问题
2. 提交Pull Request改进文档
3. 联系维护团队

## 📞 获取帮助

如果您在使用过程中遇到问题：

1. **查看文档**: 首先查看相关文档是否有解答
2. **搜索Issue**: 在项目Issue中搜索是否有类似问题
3. **提交Issue**: 如果没有找到解答，请提交新的Issue
4. **联系团队**: 紧急问题可以直接联系开发团队

## 🏷️ 文档版本

当前文档版本对应项目版本：**v0.0.1**

文档最后更新时间：**2025-05-28**

---

**Happy Coding! 🎉**
