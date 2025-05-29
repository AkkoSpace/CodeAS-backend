# CodeAS Backend 技术总结

## 🎯 项目概述

CodeAS Backend 是 akko.space 网站的后端服务项目，基于 Spring Boot 3.x 构建，采用混合分层架构设计，提供用户管理、角色权限、操作审计等核心功能，为 CodeAS 平台提供完整的后端支撑。

> 📊 **项目状态**：查看详细的功能实现状态和开发计划，请参考 **[项目路线图](ROADMAP.md)**

## 🏗️ 系统架构

### 分层架构设计
```
┌─────────────────────────────────────┐
│           Business Layer            │  业务层（可扩展）
├─────────────────────────────────────┤
│           Platform Layer            │  平台层（用户、角色、权限等）
├─────────────────────────────────────┤
│          Foundation Layer           │  基础层（通用功能）
└─────────────────────────────────────┘
```

### 数据库Schema设计
```
┌─────────────────┐  ┌─────────────────┐
│ foundation_     │  │ platform_       │
│ schema          │  │ schema          │
├─────────────────┤  ├─────────────────┤
│ • config_*      │  │ • user_*        │
│ • audit_*       │  │ • role_*        │
│ • cache_*       │  │ • permission_*  │
└─────────────────┘  │ • menu_*        │
                     │ • dictionary_*  │
┌─────────────────┐  └─────────────────┘
│ business_       │  ┌─────────────────┐
│ schema          │  │ integration_    │
├─────────────────┤  │ schema          │
│ • 业务表         │  ├─────────────────┤
│ （预留）        │  │ • 集成表         │
│                │  │ （预留）         │
└─────────────────┘  └─────────────────┘
```

## 🚀 快速启动

### 1. 环境准备
```bash
# 检查Java版本（需要21+）
java -version

# 检查Maven版本
mvn -version

# 启动PostgreSQL和Redis
docker-compose -f docker/docker-compose.yml up -d postgres redis
```

### 2. 构建和运行
```bash
# 构建项目
mvn clean package

# 运行应用
java -jar target/backend-1.0.0-SNAPSHOT.jar

# 或使用Maven运行
mvn spring-boot:run
```

### 3. 访问应用
- 应用地址：http://localhost:26300
- API文档：http://localhost:26300/swagger-ui.html
- 健康检查：http://localhost:26300/actuator/health

### 4. 默认账号
- 用户名：admin
- 密码：admin123

## 📊 技术栈总览

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 21 | 编程语言 |
| Spring Boot | 3.2.1 | 应用框架 |
| PostgreSQL | 15-alpine | 主数据库 |
| Redis | 7-alpine | 缓存数据库 |
| MyBatis Plus | 3.5.5 | ORM框架 |
| Flyway | 11.8.3 | 数据库迁移 |
| Caffeine | 3.1.8 | 本地缓存 |
| JWT (JJWT) | 0.12.3 | 认证令牌 |
| SpringDoc | 2.3.0 | API文档 |
| Lombok | - | 代码简化 |
| Hutool | 5.8.38 | 工具库 |
| Commons Lang3 | 3.14.0 | 工具库 |
| PostgreSQL Driver | 42.7.1 | 数据库驱动 |

## 📁 项目结构

```
backend/
├── src/main/java/space/akko/
│   ├── foundation/              # 基础层
│   │   ├── common/             # 通用类
│   │   ├── config/             # 配置类
│   │   ├── constant/           # 常量
│   │   ├── exception/          # 异常处理
│   │   ├── utils/              # 工具类
│   │   ├── annotation/         # 注解
│   │   ├── aspect/             # 切面
│   │   ├── filter/             # 过滤器
│   │   ├── cache/              # 缓存
│   │   └── controller/         # 基础控制器
│   ├── platform/               # 平台层
│   │   ├── user/               # 用户管理
│   │   ├── role/               # 角色管理
│   │   ├── permission/         # 权限管理
│   │   ├── menu/               # 菜单管理
│   │   ├── config/             # 系统配置
│   │   ├── audit/              # 操作审计
│   │   └── dictionary/         # 数据字典
│   └── BackendApplication.java # 启动类
├── src/main/resources/
│   ├── db/migration/           # 数据库迁移脚本
│   ├── mapper/                 # MyBatis映射文件
│   └── application*.yml        # 配置文件
├── src/test/                   # 测试代码
├── docker/                     # Docker配置
├── sql/                        # SQL脚本
└── deploy.sh                   # 部署脚本
```

## 🔗 相关文档

### 项目文档
- **[项目路线图](ROADMAP.md)** - 查看项目当前状态、功能实现进度和未来发展计划
- **[系统架构](ARCHITECTURE.md)** - 详细的架构设计文档和技术选型说明
- **[更新日志](CHANGELOG.md)** - 版本变更记录和发布历史

### 开发指南
- **[开发指南](../guides/DEVELOPMENT_GUIDE.md)** - 开发环境搭建和开发规范
- **[缓存使用指南](../guides/CACHE_GUIDE.md)** - 多级缓存系统使用说明
- **[安全指南](../guides/SECURITY_GUIDE.md)** - 安全配置和最佳实践
- **[Flyway指南](../guides/FLYWAY_GUIDE.md)** - 数据库迁移管理

### API文档
- **[API参考](../api/API_REFERENCE.md)** - RESTful API接口文档
- **[环境变量](../ENVIRONMENT_VARIABLES.md)** - 环境变量配置说明

## 🏆 项目亮点

1. **企业级架构**：采用主流的分层架构和设计模式
2. **高性能**：智能多级缓存（L1+L2）、连接池优化、JVM调优
3. **高安全性**：JWT认证、权限控制、密码加密
4. **高可用性**：健康检查、优雅关闭、错误处理
5. **易部署**：Docker支持、脚本管理、环境隔离
6. **易扩展**：模块化设计、清晰的分层、完善的基础设施
7. **代码质量**：统一规范、完整注释、单元测试
8. **文档完善**：自动生成API文档、部署文档、开发指南
9. **智能缓存**：L1本地缓存+L2分布式缓存，自动回写和失效
10. **自动化文档**：启动时自动导出OpenAPI文档，支持多格式

## 📝 项目定位

CodeAS Backend 是 **akko.space 网站的核心后端服务**，具有：

- **现代化技术栈**：基于 Spring Boot 3.x + Java 21 + PostgreSQL
- **清晰的代码结构**：分层架构设计，职责分离明确
- **完善的基础设施**：认证授权、缓存、监控、部署等一应俱全
- **良好的扩展性**：模块化设计，易于定制和扩展
- **生产级部署**：Docker 容器化，自动化脚本，监控集成

**为 CodeAS 平台提供稳定可靠的后端支撑**，支持平台的各种业务功能需求。

> 💡 **提示**：项目的详细功能状态、开发进度和未来规划请查看 **[项目路线图](ROADMAP.md)**
