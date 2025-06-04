# CodeAS Backend - CodeAS 后端项目

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 项目简介

CodeAS Backend 是 akko.space 网站的后端服务项目，基于 Spring Boot 3.x 构建，采用混合分层架构设计，提供用户管理、角色管理、操作审计等核心功能，为 CodeAS 平台提供完整的后端支撑。部分高级功能模块正在持续开发完善中。

## 核心特性

### 🏗️ 架构设计
- **混合分层架构**：Foundation（基础层）→ Platform（平台层）→ Modules（业务层）
- **多Schema数据库设计**：foundation_schema、platform_schema、business_schema、integration_schema
- **模块化设计**：高内聚、低耦合的模块结构

### 🔐 安全认证
- **JWT认证**：基于JWT的无状态认证机制
- **角色权限控制**：基于RBAC的权限管理框架
- **会话管理**：Redis会话存储，支持单点登录
- **密码安全**：BCrypt加密，密码强度验证

### 💾 数据存储
- **PostgreSQL**：主数据库，支持多Schema
- **智能多级缓存**：L1本地缓存（Caffeine）+ L2分布式缓存（Redis）
- **MyBatis Plus**：优雅的ORM框架
- **Flyway**：数据库版本管理

### 📊 监控运维
- **操作审计**：基础的操作日志记录
- **健康检查**：Spring Boot Actuator
- **链路追踪**：请求追踪和日志关联
- **性能监控**：JVM监控、缓存监控
- **API文档**：自动导出OpenAPI文档（JSON/YAML格式）

### 🚀 部署方案
- **Docker支持**：完整的容器化方案
- **Assembly打包**：生产环境部署包
- **脚本管理**：启动、停止、重启、状态检查
- **环境隔离**：dev、test、prod环境配置

## 技术栈

| 技术 | 版本     | 说明 |
|------|--------|------|
| Java | 21     | 编程语言 |
| Spring Boot | 3.5.0  | 应用框架 |
| PostgreSQL | 15+    | 主数据库 |
| Redis | 7+     | 缓存数据库 |
| MyBatis Plus | 3.5.12 | ORM框架 |
| Flyway | 11.9.0 | 数据库迁移 |
| Caffeine | 3.1.8  | 本地缓存 |
| JWT | 0.12.6 | 认证令牌 |
| SpringDoc | 2.8.8  | API文档 |
| Dotenv Java | 3.2.0  | 环境变量管理 |
| Docker | -      | 容器化 |

## 快速开始

### 环境要求

- Java 21+
- Maven 3.8+
- PostgreSQL 15+
- Redis 7+
- Docker & Docker Compose（可选）

### 本地开发

1. **克隆项目**
```bash
git clone <repository-url>
cd backend
```

2. **配置数据库**
```bash
# 创建数据库（数据库名称固定为 codeas）
createdb -U postgres codeas
```

**配置环境变量**：
```bash
# 复制环境变量模板
cp .env.example .env
```

然后编辑 `.env` 文件，配置实际的连接信息：
```properties
# 应用配置
SERVER_PORT=26300

# 数据库配置（数据库名称固定为 codeas）
DB_HOST=localhost
DB_PORT=5432
DB_USERNAME=your_actual_username
DB_PASSWORD=your_actual_password

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password

# JWT密钥
JWT_SECRET=your_jwt_secret
```

3. **启动Redis**
```bash
redis-server
```

4. **初始化数据库**
```bash
# 执行数据库迁移（创建表结构和初始数据）
mvn flyway:migrate
```

5. **运行应用**
```bash
mvn spring-boot:run
```

6. **访问应用**
- 应用地址：http://localhost:26300
- API文档：http://localhost:26300/api/swagger-ui.html
- 健康检查：http://localhost:26300/actuator/health
- 自动导出的API文档：`./docs/api/generated/` 目录（应用启动后自动生成）

### Docker部署

1. **使用Docker Compose**
```bash
cd docker
docker-compose up -d
```

2. **查看服务状态**
```bash
docker-compose ps
docker-compose logs -f backend
```

### 生产部署

1. **打包应用**
```bash
mvn clean package assembly:single
```

2. **解压部署包**
```bash
cd target
tar -xzf backend-1.0.0-SNAPSHOT-distribution.tar.gz
cd backend-1.0.0-SNAPSHOT
```

3. **环境检查**
```bash
./sbin/env-check.sh
```

4. **启动应用**
```bash
./bin/start.sh
```

5. **检查状态**
```bash
./bin/status.sh
```

## 项目结构

```
backend/
├── src/main/
│   ├── java/space/akko/
│   │   ├── foundation/          # 基础层
│   │   │   ├── common/         # 通用类
│   │   │   ├── config/         # 配置类
│   │   │   ├── constant/       # 常量
│   │   │   ├── exception/      # 异常处理
│   │   │   ├── utils/          # 工具类
│   │   │   ├── annotation/     # 注解
│   │   │   ├── aspect/         # 切面
│   │   │   ├── filter/         # 过滤器
│   │   │   └── cache/          # 缓存
│   │   ├── platform/           # 平台层
│   │   │   ├── user/           # 用户管理
│   │   │   ├── role/           # 角色管理
│   │   │   ├── permission/     # 权限管理
│   │   │   ├── menu/           # 菜单管理
│   │   │   ├── config/         # 系统配置
│   │   │   ├── audit/          # 操作审计
│   │   │   └── dictionary/     # 数据字典
│   │   ├── modules/            # 业务层（预留）
│   │   └── BackendApplication.java
│   ├── resources/
│   │   ├── db/migration/       # 数据库迁移脚本
│   │   ├── mapper/             # MyBatis映射文件
│   │   └── application*.yml    # 配置文件
│   └── assembly/               # 打包配置
├── docker/                     # Docker配置
├── sql/                        # SQL脚本
└── doc/                        # 项目文档
```

## 默认账号

- **用户名**：admin
- **密码**：admin123
- **角色**：超级管理员

## 健康检查

```bash
# 应用健康状态
curl http://localhost:26300/actuator/health

# 应用信息
curl http://localhost:26300/actuator/info

# 指标信息
curl http://localhost:26300/actuator/metrics
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 作者：akko
- 邮箱：akko@akko.space
- 网站：https://akko.space
- 项目地址：https://github.com/akko/CodeAS-backend

## 📚 文档

### 项目文档
- [项目路线图](docs/project/ROADMAP.md) - 项目当前状态、功能进度和发展规划（主要文档）
- [技术总结](docs/project/PROJECT_SUMMARY.md) - 项目技术架构和特性总结
- [系统架构](docs/project/ARCHITECTURE.md) - 详细的架构设计文档
- [更新日志](docs/project/CHANGELOG.md) - 版本变更记录

### 开发指南
- [开发指南](docs/guides/DEVELOPMENT_GUIDE.md) - 开发环境搭建和规范
- [缓存使用指南](docs/guides/CACHE_GUIDE.md) - 多级缓存系统使用说明
- [安全指南](docs/guides/SECURITY_GUIDE.md) - 安全配置和最佳实践
- [Flyway指南](docs/guides/FLYWAY_GUIDE.md) - 数据库迁移管理

### 部署运维
- [部署指南](docs/deployment/DEPLOYMENT_GUIDE.md) - 详细的部署说明

### API文档
- [API参考文档](docs/api/API_REFERENCE.md) - 详细的API使用说明和示例
- [在线API文档](http://localhost:26300/api/swagger-ui.html) - Swagger UI交互式文档
- [自动导出文档](docs/api/generated/) - 应用启动时自动导出的OpenAPI规范文档

## 更新日志

查看 [CHANGELOG.md](docs/project/CHANGELOG.md) 了解详细的版本变更记录。
