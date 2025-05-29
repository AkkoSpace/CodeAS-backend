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

### 修复
- 无

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
- Spring Boot 3.2.1
- PostgreSQL 15-alpine
- MyBatis Plus 3.5.5
- Flyway 11.8.3
- Redis 7-alpine
- Caffeine 3.1.8
- Spring Security
- JWT (JJWT 0.12.3)
- SpringDoc OpenAPI 2.3.0
- Hutool 5.8.38
- Commons Lang3 3.14.0
- PostgreSQL Driver 42.7.1
- Lombok
- Maven
