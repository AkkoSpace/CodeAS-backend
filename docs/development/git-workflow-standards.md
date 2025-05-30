# Git 工作流规范

## 概述

本文档定义了 CodeAS Backend 项目的标准 Git 工作流程，结合 GitHub Actions MCP 集成，确保代码质量和开发效率。

## 分支策略

### 主要分支

- **`main`** - 生产环境分支，仅包含稳定的发布版本
- **`dev`** - 开发分支，用于集成和测试新功能
- **`feature/*`** - 功能分支，用于开发新功能
- **`fix/*`** - 修复分支，用于修复bug
- **`hotfix/*`** - 热修复分支，用于紧急修复生产问题

### 分支命名规范

```
feature/用户模块-登录功能
feature/权限管理-角色分配
fix/安全扫描-nvd-api-错误
hotfix/数据库连接-超时问题
```

## 提交信息规范

### Gitmoji + 类型 + 范围 + 描述

```
:gitmoji: type(scope): 简短描述

详细描述（可选）
- 变更点1
- 变更点2

相关问题: #123
```

### 提交类型

| 类型 | Gitmoji | 说明 | 示例 |
|------|---------|------|------|
| feat | :sparkles: | 新功能 | `:sparkles: feat(auth): 添加JWT认证功能` |
| fix | :bug: | 修复bug | `:bug: fix(db): 修复数据库连接超时问题` |
| docs | :memo: | 文档更新 | `:memo: docs(api): 更新API文档` |
| style | :art: | 代码格式化 | `:art: style(user): 优化用户模块代码格式` |
| refactor | :recycle: | 重构代码 | `:recycle: refactor(service): 重构用户服务层` |
| perf | :zap: | 性能优化 | `:zap: perf(query): 优化数据库查询性能` |
| test | :white_check_mark: | 测试相关 | `:white_check_mark: test(auth): 添加认证模块单元测试` |
| build | :construction_worker: | 构建系统 | `:construction_worker: build(maven): 升级Maven插件版本` |
| ci | :green_heart: | CI配置 | `:green_heart: ci(actions): 优化GitHub Actions工作流` |
| chore | :wrench: | 其他杂项 | `:wrench: chore(config): 更新配置文件` |

### 范围说明

- **auth** - 认证授权
- **user** - 用户管理
- **role** - 角色权限
- **api** - API接口
- **db** - 数据库
- **config** - 配置
- **ci** - 持续集成
- **docs** - 文档
- **test** - 测试

## 标准工作流程

### 1. 功能开发流程

```bash
# 1. 从dev分支创建功能分支
git checkout dev
git pull origin dev
git checkout -b feature/用户模块-登录功能

# 2. 开发功能
# ... 编写代码 ...

# 3. 提交代码
git add .
git commit -m ":sparkles: feat(auth): 实现用户登录功能

- 添加JWT token生成和验证
- 实现用户密码加密存储
- 添加登录状态检查中间件

相关问题: #123"

# 4. 推送到远程
git push origin feature/用户模块-登录功能

# 5. 创建Pull Request
# 通过GitHub界面或MCP创建PR
```

### 2. Bug修复流程

```bash
# 1. 从dev分支创建修复分支
git checkout dev
git pull origin dev
git checkout -b fix/安全扫描-nvd-api-错误

# 2. 修复问题
# ... 修复代码 ...

# 3. 提交修复
git add .
git commit -m ":bug: fix(ci): 修复OWASP dependency-check NVD API 403错误

- 配置NVD_API_KEY环境变量
- 优化Maven插件配置增加容错性
- 更新相关文档

修复GitHub Actions安全扫描步骤失败问题"

# 4. 推送并创建PR
git push origin fix/安全扫描-nvd-api-错误
```

### 3. 热修复流程

```bash
# 1. 从main分支创建热修复分支
git checkout main
git pull origin main
git checkout -b hotfix/数据库连接-超时问题

# 2. 修复问题
# ... 修复代码 ...

# 3. 提交修复
git add .
git commit -m ":ambulance: hotfix(db): 修复生产环境数据库连接超时

- 增加连接池配置
- 设置合理的超时时间
- 添加连接重试机制"

# 4. 合并到main和dev
git checkout main
git merge hotfix/数据库连接-超时问题
git push origin main

git checkout dev
git merge hotfix/数据库连接-超时问题
git push origin dev
```

## Pull Request 规范

### PR标题格式

```
:gitmoji: type(scope): 简短描述
```

### PR描述模板

```markdown
## 变更类型
- [ ] 新功能 (feature)
- [ ] Bug修复 (fix)
- [ ] 文档更新 (docs)
- [ ] 代码重构 (refactor)
- [ ] 性能优化 (perf)
- [ ] 测试相关 (test)
- [ ] 构建相关 (build)
- [ ] CI相关 (ci)
- [ ] 其他 (chore)

## 变更描述
简要描述本次变更的内容和目的。

## 变更详情
- 变更点1
- 变更点2
- 变更点3

## 测试说明
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 手动测试完成
- [ ] CI/CD流程正常

## 相关问题
关闭 #123

## 检查清单
- [ ] 代码符合项目规范
- [ ] 已添加必要的测试
- [ ] 文档已更新
- [ ] CI检查通过
```

## GitHub Actions 集成

### 自动化检查

每次提交和PR都会触发以下检查：

1. **代码质量检查**
   - 编译检查
   - 代码格式检查
   - 静态代码分析

2. **安全扫描**
   - 依赖漏洞扫描
   - 代码安全检查

3. **测试执行**
   - 单元测试
   - 集成测试

4. **构建验证**
   - 应用构建
   - Docker镜像构建

### MCP监控

使用GitHub Actions MCP实时监控：

```bash
# 检查工作流状态
list_workflow_runs_Github_Action

# 查看具体作业详情
get_workflow_run_jobs_Github_Action

# 监控失败原因
get_workflow_run_Github_Action
```

## 版本管理

### 语义化版本

遵循 [Semantic Versioning](https://semver.org/) 规范：

- **MAJOR.MINOR.PATCH** (如: 1.2.3)
- **MAJOR**: 不兼容的API变更
- **MINOR**: 向后兼容的功能新增
- **PATCH**: 向后兼容的问题修复

### 发布流程

```bash
# 1. 更新版本号
mvn versions:set -DnewVersion=1.2.0

# 2. 提交版本变更
git add .
git commit -m ":bookmark: release: 发布版本 v1.2.0"

# 3. 创建标签
git tag -a v1.2.0 -m "Release version 1.2.0"

# 4. 推送标签
git push origin v1.2.0
```

## 代码审查规范

### 审查要点

1. **功能正确性**
   - 功能是否按需求实现
   - 边界条件处理
   - 错误处理机制

2. **代码质量**
   - 代码可读性
   - 命名规范
   - 注释完整性

3. **性能考虑**
   - 算法效率
   - 资源使用
   - 数据库查询优化

4. **安全性**
   - 输入验证
   - 权限检查
   - 敏感信息处理

### 审查流程

1. **自动检查** - CI/CD自动执行
2. **同行审查** - 至少一人审查
3. **测试验证** - 功能测试通过
4. **合并批准** - 维护者最终批准

## 最佳实践

### 提交频率

- 小而频繁的提交
- 每个提交只包含一个逻辑变更
- 避免大型单体提交

### 分支管理

- 及时删除已合并的分支
- 定期同步dev分支
- 保持分支命名一致性

### 冲突解决

```bash
# 1. 更新目标分支
git checkout dev
git pull origin dev

# 2. 切换到功能分支
git checkout feature/your-feature

# 3. 变基解决冲突
git rebase dev

# 4. 解决冲突后继续
git add .
git rebase --continue

# 5. 强制推送更新
git push origin feature/your-feature --force-with-lease
```

## 工具配置

### Git配置

```bash
# 设置用户信息
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"

# 设置默认编辑器
git config --global core.editor "code --wait"

# 设置默认分支
git config --global init.defaultBranch main

# 启用颜色输出
git config --global color.ui auto
```

### Git Hooks

项目根目录下的 `.githooks/` 目录包含预配置的钩子：

- **pre-commit** - 提交前检查
- **commit-msg** - 提交信息验证
- **pre-push** - 推送前检查

## 故障排除

### 常见问题

1. **提交信息格式错误**
   ```bash
   git commit --amend -m "正确的提交信息"
   ```

2. **推送被拒绝**
   ```bash
   git pull --rebase origin dev
   git push origin feature/your-feature
   ```

3. **CI检查失败**
   - 查看GitHub Actions日志
   - 使用MCP工具监控状态
   - 修复问题后重新推送

## 相关文档

- [开发环境搭建](../guides/development-setup.md)
- [代码规范](./code-standards.md)
- [测试指南](./testing-guide.md)
- [部署流程](../deployment/README.md)
