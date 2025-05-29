# 安全配置指南

## 🚨 紧急情况处理

如果您发现敏感信息已经被提交到Git仓库，请立即执行以下步骤：

### 1. 立即更改所有暴露的密码和密钥
- 数据库密码
- Redis密码  
- JWT密钥
- 任何其他敏感信息

### 2. 检查访问日志
检查相关服务的访问日志，确认是否有异常访问。

### 3. 清理Git历史记录

如果敏感信息已经推送到远程仓库，需要重写Git历史：

```bash
# 方法1：使用git filter-branch（适用于小仓库）
git filter-branch --force --index-filter \
  'git rm --cached --ignore-unmatch src/main/resources/application-dev.yml' \
  --prune-empty --tag-name-filter cat -- --all

# 方法2：使用BFG Repo-Cleaner（推荐，适用于大仓库）
# 1. 下载BFG: https://rtyley.github.io/bfg-repo-cleaner/
# 2. 运行清理命令
java -jar bfg.jar --delete-files application-dev.yml
git reflog expire --expire=now --all && git gc --prune=now --aggressive

# 强制推送到远程仓库（危险操作，需要团队协调）
git push --force --all
git push --force --tags
```

## 📋 安全最佳实践

### 1. 环境变量配置

使用环境变量存储敏感信息：

```yaml
# application-dev.yml
spring:
  datasource:
    password: ${DB_PASSWORD:default_value}
  data:
    redis:
      password: ${REDIS_PASSWORD:default_value}
```

### 2. 本地环境配置

1. 复制 `.env.example` 为 `.env`
2. 在 `.env` 文件中填入真实的配置值
3. 确保 `.env` 文件在 `.gitignore` 中

### 3. 生产环境配置

- 使用容器编排工具的密钥管理（如Kubernetes Secrets）
- 使用云服务商的密钥管理服务（如AWS Secrets Manager）
- 使用专门的配置管理工具（如HashiCorp Vault）

### 4. 代码审查检查清单

提交代码前检查：
- [ ] 没有硬编码的密码
- [ ] 没有API密钥
- [ ] 没有数据库连接字符串
- [ ] 没有私钥文件
- [ ] 配置文件使用环境变量

## 🔧 工具推荐

### Git Hooks
设置pre-commit hook检查敏感信息：

```bash
#!/bin/sh
# .git/hooks/pre-commit
if git diff --cached --name-only | grep -E "\.(yml|yaml|properties|json)$" | xargs grep -l "password\|secret\|key" > /dev/null; then
    echo "警告：检测到可能的敏感信息，请检查配置文件"
    exit 1
fi
```

### 扫描工具
- [git-secrets](https://github.com/awslabs/git-secrets)
- [truffleHog](https://github.com/trufflesecurity/trufflehog)
- [detect-secrets](https://github.com/Yelp/detect-secrets)

## 📞 应急联系

如果发生安全事件，请立即：
1. 通知团队负责人
2. 更改所有相关密码
3. 检查系统访问日志
4. 评估潜在影响范围
