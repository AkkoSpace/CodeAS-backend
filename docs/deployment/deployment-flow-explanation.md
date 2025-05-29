# GitHub Actions 部署流程说明

## 🔄 完整的部署流程

```
1. 你推送代码到 GitHub
   ↓
2. GitHub Actions 自动触发
   ↓  
3. GitHub Actions 执行：
   - 下载代码
   - 编译打包
   - 运行测试
   - 构建 Docker 镜像
   ↓
4. GitHub Actions 连接到你的服务器
   ↓
5. 将应用部署到你的服务器
   ↓
6. 你的网站在你的服务器上运行
```

## 🌐 网址指向说明

### 你的域名应该指向：
- **你的服务器 IP**，不是 GitHub！

```
你的域名: api.yourdomain.com
DNS A记录: 指向你的服务器IP (如: 123.456.789.0)
```

### GitHub Actions 中的网址用途：
1. **告诉 GitHub Actions 去哪里检查部署结果**
2. **在 GitHub 界面显示部署环境链接**
3. **自动化健康检查**

## 📋 实际配置示例

假设你有：
- 域名: `yourdomain.com`
- 服务器IP: `123.456.789.0`

### DNS 配置（在域名服务商）：
```
A记录: api.yourdomain.com → 123.456.789.0
A记录: staging.yourdomain.com → 123.456.789.0
```

### GitHub Variables 配置：
```
STAGING_URL=https://staging.yourdomain.com
PRODUCTION_URL=https://api.yourdomain.com
```

### GitHub Secrets 配置：
```
STAGING_HOST=123.456.789.0
PRODUCTION_HOST=123.456.789.0
STAGING_USER=your-server-username
PRODUCTION_USER=your-server-username
```

## 🔧 GitHub Actions 工作流程

### 部署步骤：
1. **构建应用** (在 GitHub 服务器上)
2. **连接你的服务器** (通过 SSH)
3. **上传应用** (到你的服务器)
4. **启动应用** (在你的服务器上)
5. **健康检查** (访问你的网站确认部署成功)

### 关键理解：
- GitHub Actions = 自动化工具
- 你的服务器 = 应用运行的地方
- 你的域名 = 指向你的服务器

## 🏗️ 服务器架构示例

```
你的服务器 (123.456.789.0)
├── Docker 容器
│   ├── CodeAS Backend (端口 26300)
│   ├── PostgreSQL (端口 5432)
│   └── Redis (端口 6379)
├── Nginx (反向代理)
│   ├── staging.yourdomain.com → localhost:26300
│   └── api.yourdomain.com → localhost:26300
```

## 🚀 部署后的访问流程

```
用户访问 api.yourdomain.com
↓
DNS 解析到你的服务器 IP
↓
Nginx 接收请求
↓
转发到 Docker 容器中的应用
↓
应用响应用户请求
```

## ❌ 常见误解

### 错误理解：
- ❌ 域名指向 GitHub
- ❌ GitHub Actions 托管网站
- ❌ 在 GitHub 上运行应用

### 正确理解：
- ✅ 域名指向你的服务器
- ✅ GitHub Actions 自动化部署
- ✅ 应用在你的服务器上运行

## 💡 如果你已经有网站

如果你已经有运行的网站和服务器：

1. **确认服务器信息**：
   - 服务器 IP 地址
   - SSH 登录用户名
   - SSH 密钥

2. **配置 GitHub Secrets**：
   ```
   PRODUCTION_HOST=你的服务器IP
   PRODUCTION_USER=你的SSH用户名
   PRODUCTION_SSH_KEY=你的SSH私钥
   ```

3. **配置 GitHub Variables**：
   ```
   PRODUCTION_URL=https://你的域名
   ```

4. **在服务器上准备部署目录**：
   ```bash
   mkdir -p /opt/codeas
   # 安装 Docker 和 Docker Compose
   ```

这样 GitHub Actions 就会自动将代码部署到你现有的服务器上！

## 相关文档

- [部署环境准备清单](deployment-setup.md)
- [部署指南](DEPLOYMENT_GUIDE.md)
- [GitHub Actions 问题修复](../fixes/github-actions/overview.md)
