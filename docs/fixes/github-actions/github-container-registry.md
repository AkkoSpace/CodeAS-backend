# GitHub Container Registry 推送失败修复指南

## 问题描述

GitHub Actions 在推送 Docker 镜像到 GitHub Container Registry (GHCR) 时遇到 403 Forbidden 错误：

```
ERROR: failed to push ghcr.io/akkospace/codeas-backend:dev: unexpected status from POST request to https://ghcr.io/v2/akkospace/codeas-backend/blobs/uploads/: 403 Forbidden
```

## 问题原因

1. **权限不足**：GitHub Actions 缺少 `packages: write` 权限
2. **仓库设置**：私有仓库可能需要额外的包权限配置
3. **命名问题**：镜像名称可能不符合 GHCR 规范

## 解决方案

### 1. 修复 GitHub Actions 权限

已在 `.github/workflows/ci.yml` 中添加必要权限：

```yaml
docker:
  name: 🐳 Docker Build
  runs-on: ubuntu-latest
  needs: build
  if: github.event_name == 'push'
  permissions:
    contents: read
    packages: write  # 添加包写入权限
```

### 2. 配置仓库包权限

#### 方法 A：通过 GitHub 网页界面

1. 进入 GitHub 仓库页面
2. 点击 "Settings" 标签
3. 在左侧菜单中找到 "Actions" → "General"
4. 滚动到 "Workflow permissions" 部分
5. 选择 "Read and write permissions"
6. 勾选 "Allow GitHub Actions to create and approve pull requests"
7. 点击 "Save"

#### 方法 B：通过包设置

1. 进入 GitHub 个人/组织主页
2. 点击 "Packages" 标签
3. 找到 `codeas-backend` 包（如果已存在）
4. 点击包名进入包详情页
5. 点击 "Package settings"
6. 在 "Manage Actions access" 部分
7. 确保仓库有 "Write" 权限

### 3. 验证镜像命名

确保镜像名称符合 GHCR 规范：
- 格式：`ghcr.io/OWNER/IMAGE_NAME:TAG`
- 示例：`ghcr.io/akkospace/codeas-backend:dev`

### 4. 手动测试推送

可以在本地测试推送权限：

```bash
# 1. 创建个人访问令牌
# 在 GitHub Settings > Developer settings > Personal access tokens
# 创建具有 write:packages 权限的令牌

# 2. 本地登录
echo $GITHUB_TOKEN | docker login ghcr.io -u USERNAME --password-stdin

# 3. 构建并推送测试镜像
docker build -t ghcr.io/akkospace/codeas-backend:test .
docker push ghcr.io/akkospace/codeas-backend:test
```

## 配置说明

### 当前配置

已更新的 CI 工作流包含：

1. **权限配置**：
   ```yaml
   permissions:
     contents: read
     packages: write
   ```

2. **登录验证**：
   ```yaml
   - name: 🔍 Verify Docker login
     run: |
       echo "Logged in as: ${{ github.actor }}"
       echo "Repository: ${{ github.repository }}"
       echo "Registry: ghcr.io"
   ```

3. **镜像元数据**：
   ```yaml
   images: ghcr.io/${{ github.repository_owner }}/codeas-backend
   labels: |
     org.opencontainers.image.title=CodeAS Backend
     org.opencontainers.image.description=Backend service for CodeAS platform
     org.opencontainers.image.vendor=AkkoSpace
   ```

### 镜像标签策略

- `main` 分支 → `latest` 标签
- `dev` 分支 → `dev` 标签
- Pull Request → `pr-{number}` 标签
- 提交 SHA → `{branch}-{sha}` 标签

## 故障排除

### 如果仍然遇到 403 错误

1. **检查仓库权限**：
   - 确保 Actions 有 "Read and write permissions"
   - 检查包的访问权限设置

2. **验证令牌权限**：
   - `GITHUB_TOKEN` 应该自动具有必要权限
   - 如果使用自定义 PAT，确保有 `write:packages` 权限

3. **检查仓库可见性**：
   - 私有仓库可能需要额外配置
   - 考虑临时设为公开仓库测试

4. **清理缓存**：
   ```bash
   # 在 Actions 中清理 Docker 缓存
   docker system prune -af
   ```

### 常见错误和解决方案

| 错误 | 原因 | 解决方案 |
|------|------|----------|
| 403 Forbidden | 权限不足 | 添加 `packages: write` 权限 |
| 401 Unauthorized | 登录失败 | 检查 GITHUB_TOKEN 配置 |
| 404 Not Found | 镜像名称错误 | 验证镜像名称格式 |
| 429 Too Many Requests | 请求过多 | 等待或减少并发推送 |

## 验证修复

推送代码到 `dev` 分支后，检查：

1. **GitHub Actions 日志**：
   - 查看 Docker 构建步骤是否成功
   - 确认登录和推送步骤无错误

2. **GitHub Packages**：
   - 在仓库或个人主页查看 Packages
   - 确认镜像已成功推送

3. **镜像拉取测试**：
   ```bash
   docker pull ghcr.io/akkospace/codeas-backend:dev
   ```

## 相关链接

- [GitHub Container Registry 文档](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry)
- [GitHub Actions 权限](https://docs.github.com/en/actions/using-jobs/assigning-permissions-to-jobs)
- [Docker Build Push Action](https://github.com/docker/build-push-action)
- [GitHub Actions 问题总览](overview.md)
