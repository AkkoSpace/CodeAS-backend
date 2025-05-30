# GitHub Actions 工作流规范

## 概述

本文档定义了 CodeAS Backend 项目的 GitHub Actions 工作流规范，结合 GitHub Actions MCP 集成，实现标准化的 CI/CD 流程。

## 工作流结构

### 标准工作流文件

```
.github/
├── workflows/
│   ├── ci.yml                    # 主要CI流程
│   ├── code-quality.yml          # 代码质量检查
│   ├── security-scan.yml         # 安全扫描
│   ├── deploy-dev.yml            # 开发环境部署
│   ├── deploy-prod.yml           # 生产环境部署
│   └── release.yml               # 版本发布
├── actions/                      # 自定义Actions
│   ├── setup-java/
│   ├── cache-dependencies/
│   └── notify-teams/
└── templates/                    # 模板文件
    ├── bug_report.md
    ├── feature_request.md
    └── pull_request_template.md
```

## 工作流命名规范

### 文件命名

- 使用小写字母和连字符
- 描述性名称，体现工作流用途
- 避免使用缩写

```yaml
# 正确
ci.yml
code-quality.yml
deploy-production.yml
security-scan.yml

# 错误
CI.yml
codeQuality.yml
deploy_prod.yml
sec-scan.yml
```

### 工作流名称

```yaml
# 正确 - 清晰描述用途
name: "CI Pipeline"
name: "Code Quality & Security"
name: "Deploy to Production"

# 错误 - 模糊不清
name: "Build"
name: "Test"
name: "Deploy"
```

## 触发器规范

### 标准触发器配置

```yaml
# CI Pipeline - 主要构建流程
on:
  push:
    branches: [ main, dev ]
  pull_request:
    branches: [ main, dev ]
  workflow_dispatch:  # 手动触发

# 部署工作流 - 仅特定分支
on:
  push:
    branches: [ main ]
    tags: [ 'v*' ]
  workflow_dispatch:
    inputs:
      environment:
        description: '部署环境'
        required: true
        default: 'dev'
        type: choice
        options:
        - dev
        - staging
        - prod

# 定时任务 - 安全扫描
on:
  schedule:
    - cron: '0 2 * * 1'  # 每周一凌晨2点
  workflow_dispatch:
```

## 作业结构规范

### 作业命名

使用 emoji + 描述性名称：

```yaml
jobs:
  test-and-quality:
    name: "🧪 Test & Quality Check"
    
  security-scan:
    name: "🔒 Security Scan"
    
  build-application:
    name: "🏗️ Build Application"
    
  docker-build:
    name: "🐳 Docker Build"
    
  deploy-dev:
    name: "🚀 Deploy to Development"
```

### 作业依赖

```yaml
jobs:
  test:
    name: "🧪 Test & Quality Check"
    runs-on: ubuntu-latest
    
  security:
    name: "🔒 Security Scan"
    runs-on: ubuntu-latest
    needs: test  # 依赖测试作业
    
  build:
    name: "🏗️ Build Application"
    runs-on: ubuntu-latest
    needs: [test, security]  # 依赖多个作业
    
  deploy:
    name: "🚀 Deploy"
    runs-on: ubuntu-latest
    needs: build
    if: github.ref == 'refs/heads/main'  # 条件执行
```

## 步骤规范

### 步骤命名

使用 emoji + 动词 + 对象：

```yaml
steps:
  - name: "📥 Checkout code"
    uses: actions/checkout@v4
    
  - name: "☕ Set up JDK 21"
    uses: actions/setup-java@v4
    
  - name: "📦 Cache Maven dependencies"
    uses: actions/cache@v3
    
  - name: "🧹 Clean and compile"
    run: mvn clean compile
    
  - name: "🧪 Run tests"
    run: mvn test
    
  - name: "📊 Generate test report"
    uses: dorny/test-reporter@v1
```

### 常用步骤模板

#### Java项目设置

```yaml
- name: "📥 Checkout code"
  uses: actions/checkout@v4
  with:
    fetch-depth: 0  # 获取完整历史，用于SonarQube分析

- name: "☕ Set up JDK 21"
  uses: actions/setup-java@v4
  with:
    java-version: '21'
    distribution: 'temurin'
    cache: maven

- name: "🔍 Verify Maven version"
  run: mvn --version
```

#### 缓存配置

```yaml
- name: "📦 Cache Maven dependencies"
  uses: actions/cache@v3
  with:
    path: ~/.m2/repository
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
    restore-keys: |
      ${{ runner.os }}-maven-
```

#### 测试和报告

```yaml
- name: "🧪 Run tests"
  run: mvn test -Dmaven.test.failure.ignore=true

- name: "📊 Publish test results"
  uses: dorny/test-reporter@v1
  if: always()
  with:
    name: Maven Tests
    path: target/surefire-reports/*.xml
    reporter: java-junit
```

## 环境变量规范

### 变量命名

```yaml
env:
  # 全局环境变量 - 大写下划线
  JAVA_VERSION: '21'
  MAVEN_OPTS: '-Xmx1024m'
  
  # 应用配置 - 前缀标识
  APP_PROFILE: 'ci'
  DB_URL: ${{ secrets.DATABASE_URL }}
  
  # 工具配置
  SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  DOCKER_REGISTRY: ghcr.io
```

### 密钥管理

```yaml
# 在作业级别使用密钥
jobs:
  security-scan:
    runs-on: ubuntu-latest
    env:
      NVD_API_KEY: ${{ secrets.NVD_API_KEY }}
      SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
    steps:
      - name: "🔍 Run security scan"
        run: mvn dependency-check:check
```

## 错误处理规范

### 失败处理

```yaml
- name: "🧪 Run tests"
  run: mvn test
  continue-on-error: false  # 默认值，测试失败则停止

- name: "📊 Upload test results"
  uses: actions/upload-artifact@v3
  if: always()  # 即使前面步骤失败也执行
  with:
    name: test-results
    path: target/surefire-reports/

- name: "🔍 Security scan"
  run: mvn dependency-check:check
  continue-on-error: true  # 安全扫描失败不阻止流程
```

### 超时设置

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    timeout-minutes: 30  # 作业超时
    steps:
      - name: "🏗️ Build application"
        run: mvn package
        timeout-minutes: 15  # 步骤超时
```

## 制品管理规范

### 构建制品

```yaml
- name: "📤 Upload build artifacts"
  uses: actions/upload-artifact@v3
  with:
    name: build-artifacts-${{ github.run_number }}
    path: |
      target/*.jar
      target/classes/
    retention-days: 30

- name: "📥 Download build artifacts"
  uses: actions/download-artifact@v3
  with:
    name: build-artifacts-${{ github.run_number }}
    path: ./artifacts
```

### 测试报告

```yaml
- name: "📊 Upload test reports"
  uses: actions/upload-artifact@v3
  if: always()
  with:
    name: test-reports-${{ github.run_number }}
    path: |
      target/surefire-reports/
      target/site/jacoco/
      target/dependency-check-report.html
```

## Docker 构建规范

### 多阶段构建

```yaml
- name: "🐳 Set up Docker Buildx"
  uses: docker/setup-buildx-action@v3

- name: "🔑 Log in to Container Registry"
  uses: docker/login-action@v3
  with:
    registry: ghcr.io
    username: ${{ github.actor }}
    password: ${{ secrets.GITHUB_TOKEN }}

- name: "🏷️ Extract metadata"
  id: meta
  uses: docker/metadata-action@v5
  with:
    images: ghcr.io/${{ github.repository }}
    tags: |
      type=ref,event=branch
      type=ref,event=pr
      type=semver,pattern={{version}}
      type=sha,prefix={{branch}}-

- name: "🏗️ Build and push Docker image"
  uses: docker/build-push-action@v5
  with:
    context: .
    platforms: linux/amd64,linux/arm64
    push: true
    tags: ${{ steps.meta.outputs.tags }}
    labels: ${{ steps.meta.outputs.labels }}
    cache-from: type=gha
    cache-to: type=gha,mode=max
```

## 通知规范

### 失败通知

```yaml
- name: "📢 Notify on failure"
  if: failure()
  uses: 8398a7/action-slack@v3
  with:
    status: failure
    channel: '#ci-alerts'
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
    fields: repo,message,commit,author,action,eventName,ref,workflow
```

### 成功通知

```yaml
- name: "🎉 Notify on success"
  if: success() && github.ref == 'refs/heads/main'
  uses: 8398a7/action-slack@v3
  with:
    status: success
    channel: '#deployments'
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

## MCP 集成规范

### 工作流监控

使用 GitHub Actions MCP 进行实时监控：

```bash
# 列出所有工作流
list_workflows_Github_Action --owner AkkoSpace --repo CodeAS-backend

# 查看最近的运行
list_workflow_runs_Github_Action --owner AkkoSpace --repo CodeAS-backend --perPage 5

# 获取特定运行的详情
get_workflow_run_Github_Action --owner AkkoSpace --repo CodeAS-backend --runId 123456

# 查看作业详情
get_workflow_run_jobs_Github_Action --owner AkkoSpace --repo CodeAS-backend --runId 123456
```

### 自动化脚本

```yaml
# 在工作流中集成MCP监控
- name: "📊 Monitor workflow status"
  if: always()
  run: |
    echo "Workflow Status: ${{ job.status }}"
    echo "Run ID: ${{ github.run_id }}"
    echo "Run Number: ${{ github.run_number }}"
```

## 性能优化规范

### 并行执行

```yaml
jobs:
  test:
    strategy:
      matrix:
        java-version: [17, 21]
        os: [ubuntu-latest, windows-latest]
    runs-on: ${{ matrix.os }}
    steps:
      - name: "☕ Set up JDK ${{ matrix.java-version }}"
        uses: actions/setup-java@v4
        with:
          java-version: ${{ matrix.java-version }}
```

### 缓存优化

```yaml
- name: "📦 Cache Maven dependencies"
  uses: actions/cache@v3
  with:
    path: |
      ~/.m2/repository
      ~/.sonar/cache
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
    restore-keys: |
      ${{ runner.os }}-maven-
```

### 条件执行

```yaml
- name: "🚀 Deploy to production"
  if: |
    github.ref == 'refs/heads/main' &&
    github.event_name == 'push' &&
    !contains(github.event.head_commit.message, '[skip deploy]')
  run: ./deploy.sh production
```

## 安全规范

### 权限最小化

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write
      security-events: write
```

### 密钥使用

```yaml
# 正确 - 使用GitHub Secrets
env:
  DATABASE_URL: ${{ secrets.DATABASE_URL }}
  API_KEY: ${{ secrets.API_KEY }}

# 错误 - 硬编码敏感信息
env:
  DATABASE_URL: "jdbc:postgresql://localhost:5432/mydb"
  API_KEY: "sk-1234567890abcdef"
```

## 版本管理规范

### 工作流版本

```yaml
# 使用特定版本，确保稳定性
- uses: actions/checkout@v4
- uses: actions/setup-java@v4
- uses: docker/build-push-action@v5

# 避免使用latest或主分支
- uses: actions/checkout@main  # 不推荐
- uses: actions/setup-java@latest  # 不推荐
```

### 自定义Action版本

```yaml
# 引用自定义Action
- name: "🔧 Setup custom environment"
  uses: ./.github/actions/setup-environment
  with:
    environment: ${{ inputs.environment }}
```

## 文档规范

### 工作流文档

每个工作流文件应包含详细注释：

```yaml
# CI Pipeline
# 
# 此工作流在以下情况下触发：
# - 推送到 main 或 dev 分支
# - 创建针对 main 或 dev 分支的 Pull Request
# - 手动触发
#
# 执行步骤：
# 1. 代码检出和环境设置
# 2. 编译和测试
# 3. 安全扫描
# 4. 构建应用
# 5. Docker镜像构建
#
# 依赖的Secrets：
# - NVD_API_KEY: OWASP dependency check API密钥
# - SONAR_TOKEN: SonarQube分析令牌
# - GITHUB_TOKEN: GitHub访问令牌（自动提供）

name: "CI Pipeline"
```

### README更新

在项目README中说明工作流：

```markdown
## CI/CD 流程

### 工作流说明

- **CI Pipeline** - 主要构建和测试流程
- **Code Quality & Security** - 代码质量和安全检查
- **Deploy to Development** - 开发环境自动部署
- **Deploy to Production** - 生产环境部署（需要审批）

### 状态徽章

![CI](https://github.com/AkkoSpace/CodeAS-backend/workflows/CI%20Pipeline/badge.svg)
![Security](https://github.com/AkkoSpace/CodeAS-backend/workflows/Security%20Scan/badge.svg)
```

## 故障排除规范

### 常见问题处理

```yaml
# 网络问题重试
- name: "📦 Install dependencies"
  run: mvn dependency:resolve
  timeout-minutes: 10
  continue-on-error: true

- name: "🔄 Retry on failure"
  if: failure()
  run: mvn dependency:resolve
  timeout-minutes: 10
```

### 调试信息

```yaml
- name: "🔍 Debug information"
  if: runner.debug == '1'
  run: |
    echo "Runner OS: ${{ runner.os }}"
    echo "Java Version: $JAVA_VERSION"
    echo "Maven Version: $(mvn --version)"
    echo "Environment: ${{ github.event_name }}"
    env
```

## 最佳实践

### 工作流设计原则

1. **快速反馈** - 最重要的检查放在前面
2. **并行执行** - 独立的作业并行运行
3. **失败快速** - 遇到错误立即停止
4. **资源节约** - 合理使用缓存和制品
5. **安全第一** - 最小权限原则

### 监控和维护

1. **定期检查** - 每月检查工作流性能
2. **版本更新** - 及时更新Action版本
3. **清理制品** - 定期清理过期制品
4. **优化缓存** - 监控缓存命中率

### 团队协作

1. **统一标准** - 所有工作流遵循相同规范
2. **代码审查** - 工作流变更需要审查
3. **文档更新** - 及时更新相关文档
4. **知识分享** - 定期分享最佳实践
