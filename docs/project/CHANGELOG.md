# 更新日志

本文档记录了项目的所有重要变更。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
并且本项目遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [未发布]

### 新增
- 无

### 变更
- 📚 移除手动维护的API文档，改为使用Swagger自动生成
- 📚 优化文档结构，强调在线API文档的重要性
- :arrow_up: 升级 Spring Boot 从 3.2.1 到 3.5.0
- :arrow_up: 升级 PostgreSQL Driver 从 42.7.2 到 42.7.6
- :arrow_up: 升级 MyBatis Plus 从 3.5.5 到 3.5.12
- :arrow_up: 升级 Flyway 从 11.8.3 到 11.9.0
- :arrow_up: 升级 Caffeine 从 3.1.8 到 3.2.0
- :arrow_up: 升级 Lombok 从 1.18.30 到 1.18.38
- :arrow_up: 升级 JWT (JJWT) 从 0.12.3 到 0.12.6
- :arrow_up: 升级 SpringDoc OpenAPI 从 2.3.0 到 2.8.8
- :arrow_up: 升级 Commons Lang3 从 3.14.0 到 3.17.0
- :arrow_up: 升级 Dotenv Java 从 3.0.0 到 3.2.0
- :arrow_up: 升级 JaCoCo 从 0.8.11 到 0.8.13
- :arrow_up: 升级 License Maven Plugin 从 4.3 到 5.0.0
- :arrow_up: 升级 Maven Assembly Plugin 从 3.6.0 到 3.7.1
- :arrow_up: 升级 SonarQube Scanner 从 3.10.0.2594 到 3.11.0.3922

### 修复
- :bug: 修复 PostgreSQL Driver 安全漏洞（升级到 42.7.6）
- :bug: 修复 MyBatis Plus 分页查询相关问题
- :bug: 修复 JWT 令牌处理的多个问题
- :bug: 修复 SpringDoc OpenAPI Schema 生成问题
- :bug: 修复 Caffeine 缓存性能和并发问题
- :bug: 修复 Lombok 代码生成的多个边缘情况

### 移除
- 📚 删除 `docs/api/` 目录及手动维护的API文档

## [0.0.1] - 2025-05-28

### 新增
- :sparkles: 初始化项目结构
- :sparkles: 添加Spring Boot 3.2.1基础框架
- :sparkles: 集成PostgreSQL数据库支持
- :sparkles: 集成MyBatis Plus ORM框架
- :sparkles: 集成Flyway数据库迁移工具
- :sparkles: 集成Redis缓存支持
- :sparkles: 集成Caffeine本地缓存
- :sparkles: 集成Spring Security安全框架
- :sparkles: 集成JWT认证机制
- :sparkles: 集成SpringDoc OpenAPI文档
- :sparkles: 添加用户管理模块基础结构
- :sparkles: 添加Docker容器化支持
- :sparkles: 添加多环境配置支持
- :sparkles: 添加日志配置
- :sparkles: 添加数据库分层架构（foundation/platform/business/integration）

### 技术栈
- Java 21
- Spring Boot 3.5.0
- PostgreSQL 15-alpine
- MyBatis Plus 3.5.12
- Flyway 11.9.0
- Redis 7-alpine
- Caffeine 3.2.0
- Spring Security
- JWT (JJWT 0.12.6)
- SpringDoc OpenAPI 2.8.8
- Hutool 5.8.38
- Commons Lang3 3.17.0
- PostgreSQL Driver 42.7.6
- Dotenv Java 3.2.0
- Lombok 1.18.38
- Maven
