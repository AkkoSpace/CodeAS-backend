# 部署指南

## 🚀 部署方式概览

本项目支持多种部署方式：
- **本地开发部署**：适用于开发和测试
- **Docker容器部署**：适用于快速部署和容器化环境
- **生产环境部署**：适用于生产环境的标准部署

## 🏠 本地开发部署

### 环境准备

1. **安装Java 21+**
```bash
# 检查Java版本
java -version
```

2. **安装Maven 3.8+**
```bash
# 检查Maven版本
mvn -version
```

3. **安装PostgreSQL 15+**
```bash
# Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib

# CentOS/RHEL
sudo yum install postgresql-server postgresql-contrib

# macOS
brew install postgresql
```

4. **安装Redis 7+**
```bash
# Ubuntu/Debian
sudo apt-get install redis-server

# CentOS/RHEL
sudo yum install redis

# macOS
brew install redis
```

### 配置和启动

1. **配置环境变量**
```bash
# 复制环境变量模板
cp .env.example .env

# 编辑配置文件
vim .env
```

2. **启动数据库服务**
```bash
# 启动PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql

# 启动Redis
sudo systemctl start redis
sudo systemctl enable redis
```

3. **创建数据库**
```sql
-- 连接PostgreSQL
sudo -u postgres psql

-- 创建数据库和用户
CREATE DATABASE codeas;
CREATE USER codeas WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE codeas TO codeas;
```

4. **运行应用**
```bash
# 使用Maven运行
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/backend-1.0.0-SNAPSHOT.jar
```

## 🐳 Docker容器部署

### 使用Docker Compose（推荐）

1. **启动所有服务**
```bash
# 进入docker目录
cd docker

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f backend
```

2. **停止服务**
```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v
```

### 单独使用Docker

1. **构建应用镜像**
```bash
# 构建JAR包
mvn clean package -DskipTests

# 构建Docker镜像
docker build -t backend:latest .
```

2. **启动依赖服务**
```bash
# 启动PostgreSQL
docker run -d \
  --name postgres \
  -e POSTGRES_DB=codeas \
  -e POSTGRES_USER=codeas \
  -e POSTGRES_PASSWORD=your_password \
  -p 5432:5432 \
  postgres:15

# 启动Redis
docker run -d \
  --name redis \
  -p 6379:6379 \
  redis:7-alpine
```

3. **启动应用**
```bash
docker run -d \
  --name backend \
  --link postgres:postgres \
  --link redis:redis \
  -e DB_HOST=postgres \
  -e DB_PASSWORD=your_password \
  -e REDIS_HOST=redis \
  -p 8080:8080 \
  backend:latest
```

## 🏭 生产环境部署

### 使用Assembly分发包

1. **构建分发包**
```bash
# 构建完整的分发包
mvn clean package assembly:single

# 分发包位置
ls target/backend-*-distribution.tar.gz
```

2. **部署到服务器**
```bash
# 上传分发包到服务器
scp target/backend-*-distribution.tar.gz user@server:/opt/

# 在服务器上解压
cd /opt
tar -xzf backend-*-distribution.tar.gz
cd backend-*
```

3. **环境检查**
```bash
# 检查运行环境
./sbin/env-check.sh
```

4. **配置应用**
```bash
# 编辑配置文件
vim conf/application-prod.yml

# 或使用环境变量
export DB_PASSWORD=your_production_password
export REDIS_PASSWORD=your_production_password
export JWT_SECRET=your_production_jwt_secret
```

5. **启动应用**
```bash
# 启动应用
./bin/start.sh

# 检查状态
./bin/status.sh

# 查看日志
tail -f logs/backend.log
```

6. **管理应用**
```bash
# 停止应用
./bin/stop.sh

# 重启应用
./bin/restart.sh

# 优雅停止
./bin/shutdown.sh
```

### 使用Systemd服务

1. **创建服务文件**
```bash
sudo vim /etc/systemd/system/backend.service
```

```ini
[Unit]
Description=Backend Application
After=network.target

[Service]
Type=forking
User=backend
Group=backend
WorkingDirectory=/opt/backend
ExecStart=/opt/backend/bin/start.sh
ExecStop=/opt/backend/bin/stop.sh
ExecReload=/opt/backend/bin/restart.sh
PIDFile=/opt/backend/logs/backend.pid
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

2. **启用和启动服务**
```bash
# 重载systemd配置
sudo systemctl daemon-reload

# 启用服务
sudo systemctl enable backend

# 启动服务
sudo systemctl start backend

# 查看状态
sudo systemctl status backend
```

## 🔧 配置管理

### 环境变量配置

**必需的环境变量：**
```bash
# 数据库配置（数据库名称固定为 codeas）
DB_HOST=your_db_host_here
DB_PORT=5432
DB_USERNAME=your_db_username_here
DB_PASSWORD=your_db_password_here

# Redis配置
REDIS_HOST=your_redis_host_here
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password_here

# JWT配置
JWT_SECRET=your_jwt_secret_here

# 应用配置
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=26300
```

### 配置文件优先级

1. 环境变量（最高优先级）
2. `application-{profile}.yml`
3. `application.yml`（最低优先级）

### 生产环境配置建议

```yaml
# application-prod.yml
spring:
  datasource:
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  data:
    redis:
      timeout: 5000ms
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5

logging:
  level:
    root: INFO
    space.akko: INFO
  file:
    name: logs/backend.log
    max-size: 100MB
    max-history: 30

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

## 📊 监控和运维

### 健康检查

```bash
# 应用健康状态
curl http://localhost:26300/actuator/health

# 详细健康信息
curl http://localhost:26300/actuator/health/details
```

### 日志管理

**日志文件位置：**
- 应用日志：`logs/backend.log`
- 错误日志：`logs/backend-error.log`
- 审计日志：`logs/backend-audit.log`

**日志轮转配置：**
```xml
<!-- logback-spring.xml -->
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/backend.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>logs/backend.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
        <maxFileSize>100MB</maxFileSize>
        <maxHistory>30</maxHistory>
        <totalSizeCap>10GB</totalSizeCap>
    </rollingPolicy>
</appender>
```

### 性能监控

**JVM监控：**
```bash
# 查看JVM信息
curl http://localhost:26300/actuator/metrics/jvm.memory.used

# 查看线程信息
curl http://localhost:26300/actuator/threaddump
```

**数据库监控：**
```bash
# 查看连接池状态
curl http://localhost:26300/actuator/metrics/hikaricp.connections
```

## 🔒 安全配置

### 防火墙配置

```bash
# 开放应用端口
sudo ufw allow 26300/tcp

# 限制数据库端口访问
sudo ufw allow from 10.0.0.0/8 to any port 5432
sudo ufw allow from 172.16.0.0/12 to any port 5432
sudo ufw allow from 192.168.0.0/16 to any port 5432
```

### SSL/TLS配置

```yaml
# application-prod.yml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: your_keystore_password
    key-store-type: PKCS12
    key-alias: backend
  port: 26443
```

### 反向代理配置

**Nginx配置示例：**
```nginx
upstream backend {
    server 127.0.0.1:26300;
}

server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;

    location / {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 🚨 故障排除

### 常见问题

1. **应用启动失败**
   - 检查Java版本
   - 检查端口占用
   - 查看启动日志

2. **数据库连接失败**
   - 检查数据库服务状态
   - 验证连接参数
   - 检查网络连通性

3. **Redis连接失败**
   - 检查Redis服务状态
   - 验证密码配置
   - 检查防火墙设置

### 日志分析

```bash
# 查看错误日志
grep ERROR logs/backend.log

# 查看最近的日志
tail -f logs/backend.log

# 搜索特定错误
grep -i "exception" logs/backend.log
```

### 性能问题排查

```bash
# 查看系统资源使用
top
htop

# 查看JVM堆内存使用
jstat -gc <pid>

# 生成堆转储
jmap -dump:format=b,file=heap.hprof <pid>
```
