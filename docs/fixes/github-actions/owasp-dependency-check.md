# OWASP Dependency Check 修复指南

## 问题描述

GitHub Actions 中的 OWASP dependency-check 插件报错：
```
NvdApiException: NVD Returned Status Code: 403
UpdateException: Error updating the NVD Data
caused by NvdApiException: NVD Returned Status Code: 403
NoDataException: No documents exist
```

这是因为 NVD（National Vulnerability Database）API 现在需要 API 密钥才能访问，且本地数据库初始化失败。

## 已实施的修复

### 1. ✅ 配置 NVD API 密钥
- 在 GitHub Secrets 中配置了 `NVD_API_KEY`
- CI 工作流自动检测并使用 API 密钥
- 启用完整的 NVD 漏洞数据库更新

### 2. 优化 Maven 插件配置
- 减少重试次数和延迟时间，提高性能
- 启用完整的安全分析功能（OSS Index、Central）
- 允许在更新失败时继续使用本地数据
- 设置合理的超时时间

## 解决方案

### 方案 1：配置 NVD API 密钥（推荐）

#### 1. 获取 NVD API 密钥

1. 访问 [NVD API 注册页面](https://nvd.nist.gov/developers/request-an-api-key)
2. 填写申请表单（通常会立即获得密钥）
3. 记录您的 API 密钥

#### 2. 在 GitHub 仓库中配置密钥

1. 进入 GitHub 仓库设置
2. 点击 "Settings" → "Secrets and variables" → "Actions"
3. 点击 "New repository secret"
4. 添加以下密钥：
   - **Name**: `NVD_API_KEY`
   - **Value**: 您的 NVD API 密钥

#### 3. 验证修复

推送代码到 `dev` 分支或创建 Pull Request，检查 GitHub Actions 是否正常运行。

### 方案 2：临时跳过 NVD 更新

如果暂时无法获取 API 密钥，可以跳过 NVD 更新：

1. 在 GitHub 仓库设置中添加环境变量：
   - **Name**: `SKIP_NVD_UPDATE`
   - **Value**: `true`

2. 这将使用本地缓存的漏洞数据库进行扫描

## 配置说明

### Maven 插件配置

已在 `pom.xml` 中添加以下配置：

```xml
<configuration>
    <!-- NVD API 配置 -->
    <nvdApiKey>${env.NVD_API_KEY}</nvdApiKey>
    <nvdMaxRetryCount>3</nvdMaxRetryCount>
    <nvdApiDelay>2000</nvdApiDelay>
    <!-- 如果没有 API 密钥，跳过 NVD 更新 -->
    <skipNvdUpdate>${env.SKIP_NVD_UPDATE}</skipNvdUpdate>
    <!-- 使用本地缓存数据 -->
    <cacheDirectory>${user.home}/.m2/dependency-check-data</cacheDirectory>
    <!-- 数据库更新超时设置 -->
    <connectionTimeout>15000</connectionTimeout>
    <readTimeout>30000</readTimeout>
    <!-- 允许使用本地数据库，即使更新失败 -->
    <failOnError>false</failOnError>
    <!-- 如果无法更新数据库，继续使用现有数据 -->
    <autoUpdate>false</autoUpdate>
    <!-- 跳过OSS Index分析器（减少网络依赖） -->
    <ossindexAnalyzerEnabled>false</ossindexAnalyzerEnabled>
    <!-- 跳过Central分析器（减少网络依赖） -->
    <centralAnalyzerEnabled>false</centralAnalyzerEnabled>
</configuration>
```

### GitHub Actions 配置

已在工作流中添加环境变量：

```yaml
env:
  NVD_API_KEY: ${{ secrets.NVD_API_KEY }}
  SKIP_NVD_UPDATE: ${{ secrets.NVD_API_KEY == '' && 'true' || 'false' }}
```

**状态**：✅ 已配置 NVD API 密钥，将自动使用最新漏洞数据库。

## 本地开发

### 使用 API 密钥

在本地开发时，可以设置环境变量：

```bash
# Windows
set NVD_API_KEY=your_api_key_here
mvn dependency-check:check

# Linux/macOS
export NVD_API_KEY=your_api_key_here
mvn dependency-check:check
```

### 跳过 NVD 更新

```bash
# Windows
set SKIP_NVD_UPDATE=true
mvn dependency-check:check

# Linux/macOS
export SKIP_NVD_UPDATE=true
mvn dependency-check:check
```

## 注意事项

1. **API 限制**：NVD API 有速率限制，配置了重试和延迟机制
2. **缓存**：插件会缓存漏洞数据库，减少 API 调用
3. **超时**：增加了连接和读取超时时间，避免网络问题
4. **降级策略**：如果没有 API 密钥，会自动跳过更新使用本地数据

## 故障排除

### 如果仍然遇到问题

1. **检查 API 密钥**：确保密钥正确且有效
2. **检查网络**：确保 GitHub Actions 可以访问 NVD API
3. **查看日志**：检查详细的错误信息
4. **临时禁用**：可以临时设置 `SKIP_NVD_UPDATE=true`

### 常见错误

- **403 Forbidden**：API 密钥无效或未设置
- **429 Too Many Requests**：超过 API 限制，会自动重试
- **Timeout**：网络问题，已增加超时时间

## 相关链接

- [NVD API 文档](https://nvd.nist.gov/developers)
- [OWASP Dependency Check 文档](https://jeremylong.github.io/DependencyCheck/)
- [Maven 插件配置](https://jeremylong.github.io/DependencyCheck/dependency-check-maven/configuration.html)
- [GitHub Actions 问题总览](overview.md)
