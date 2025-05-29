# 部署环境准备清单

## 🎯 当前状态
- ✅ 代码开发完成
- ✅ GitHub Actions CI/CD 配置完成
- ❌ 服务器未准备
- ❌ 域名未准备
- ❌ 部署环境未配置

## 📋 部署准备步骤

### 1. 服务器准备（必需）

#### 选项A：云服务器（推荐）
- **阿里云 ECS**：适合国内用户
- **腾讯云 CVM**：性价比高
- **AWS EC2**：国际化选择
- **DigitalOcean**：简单易用

**最低配置建议**：
- CPU: 2核
- 内存: 4GB
- 存储: 40GB SSD
- 带宽: 5Mbps

#### 选项B：本地开发（测试用）
- 使用 Docker Desktop
- 配置端口映射
- 使用 ngrok 内网穿透

### 2. 域名准备（可选）

#### 购买域名
- **国内**：阿里云、腾讯云、华为云
- **国外**：Cloudflare、Namecheap、GoDaddy

#### DNS 配置
```
A记录：
staging.yourdomain.com -> 服务器IP
api.yourdomain.com -> 服务器IP

或使用子路径：
yourdomain.com/staging
yourdomain.com/api
```

### 3. 服务器环境配置

#### 安装必需软件
```bash
# Docker & Docker Compose
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 创建应用目录
sudo mkdir -p /opt/codeas
sudo chown $USER:$USER /opt/codeas
```

#### 配置 SSH 密钥
```bash
# 生成密钥对
ssh-keygen -t rsa -b 4096 -C "github-actions"

# 将公钥添加到服务器
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
```

### 4. GitHub 配置

#### Secrets 配置
在 GitHub 仓库设置中添加：

```
# 服务器配置
STAGING_HOST=your-staging-server-ip
STAGING_USER=your-username
STAGING_SSH_KEY=your-private-key-content

PRODUCTION_HOST=your-production-server-ip
PRODUCTION_USER=your-username
PRODUCTION_SSH_KEY=your-private-key-content

# 可选配置
SLACK_WEBHOOK=your-slack-webhook-url
SNYK_TOKEN=your-snyk-token
```

#### Variables 配置
```
STAGING_URL=http://your-staging-server:26300
PRODUCTION_URL=http://your-production-server:26300
```

### 5. 应用配置文件

#### 创建 docker-compose.staging.yml
```yaml
version: '3.8'
services:
  backend:
    image: codeas-backend:staging
    ports:
      - "26300:26300"
    environment:
      - SPRING_PROFILES_ACTIVE=staging
    depends_on:
      - postgres
      - redis
  
  postgres:
    image: postgres:17.5-alpine
    environment:
      POSTGRES_DB: codeas
      POSTGRES_USER: codeas
      POSTGRES_PASSWORD: your-password
    volumes:
      - postgres_data:/var/lib/postgresql/data
  
  redis:
    image: redis:7-alpine
    
volumes:
  postgres_data:
```

## 🚀 快速开始（本地测试）

如果你想先在本地测试部署流程：

1. **启用本地部署**：
   ```bash
   # 修改 .github/workflows/deploy.yml
   # 将服务器部署步骤改为本地 Docker 运行
   ```

2. **使用 Docker Compose**：
   ```bash
   docker-compose up -d
   ```

3. **验证部署**：
   ```bash
   curl http://localhost:26300/actuator/health
   ```

## 📝 下一步行动

1. **立即可做**：
   - 在本地测试 Docker 部署
   - 完善应用配置
   - 编写部署脚本

2. **需要准备**：
   - 购买云服务器
   - 配置域名（可选）
   - 设置 GitHub Secrets

3. **部署就绪后**：
   - 取消注释 deploy.yml 中的自动触发
   - 测试完整的 CI/CD 流程
   - 监控应用运行状态

## 💡 建议

- **开发阶段**：专注于功能开发，使用本地环境
- **测试阶段**：准备一台便宜的云服务器用于测试
- **生产阶段**：配置正式的生产环境和域名
