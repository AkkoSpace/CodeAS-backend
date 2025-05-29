# API 参考文档

本文档提供 CodeAS Backend API 的详细使用说明。

## 📋 概述

CodeAS Backend 提供 RESTful API 接口，支持用户管理、角色管理、系统配置等功能。

### 基础信息

- **Base URL**: `http://localhost:26300/api`
- **API版本**: v1.0.0
- **认证方式**: JWT Bearer Token
- **数据格式**: JSON

## 🔐 认证

### 获取访问令牌

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600,
    "tokenType": "Bearer"
  },
  "timestamp": 1640995200000
}
```

### 使用访问令牌

在需要认证的请求中添加 Authorization 头：

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 📚 API 分组

### 🔐 认证相关 (`/api/auth`)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/login` | 用户登录 | ❌ |
| POST | `/refresh` | 刷新令牌 | ❌ |
| POST | `/logout` | 用户登出 | ✅ |

### 👥 用户管理 (`/api/users`)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/` | 获取用户列表 | ✅ |
| POST | `/` | 创建用户 | ✅ |
| GET | `/{userId}` | 获取用户详情 | ✅ |
| PUT | `/{userId}` | 更新用户 | ✅ |
| DELETE | `/{userId}` | 删除用户 | ✅ |
| GET | `/current` | 获取当前用户信息 | ✅ |
| GET | `/check/username` | 检查用户名可用性 | ❌ |
| GET | `/check/email` | 检查邮箱可用性 | ❌ |
| GET | `/check/phone` | 检查手机号可用性 | ❌ |

### 🎭 角色管理 (`/api/roles`)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/` | 获取角色列表 | ✅ |
| POST | `/` | 创建角色 | ✅ |
| GET | `/{roleId}` | 获取角色详情 | ✅ |
| PUT | `/{roleId}` | 更新角色 | ✅ |
| DELETE | `/{roleId}` | 删除角色 | ✅ |
| GET | `/all` | 获取所有角色 | ✅ |
| GET | `/tree` | 获取角色树结构 | ✅ |
| GET | `/check/code` | 检查角色编码可用性 | ❌ |

### ⚙️ 系统管理 (`/api/system`)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/info` | 获取系统信息 | ❌ |
| GET | `/time` | 获取服务器时间 | ❌ |
| GET | `/version` | 获取版本信息 | ❌ |

### 🏥 健康检查 (`/api/health`)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/` | 总体健康状态 | ❌ |
| GET | `/database` | 数据库健康状态 | ❌ |
| GET | `/redis` | Redis健康状态 | ❌ |
| GET | `/jvm` | JVM健康状态 | ❌ |

### 📋 审计日志 (`/api/audit`)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/logs` | 获取审计日志列表 | ✅ |
| GET | `/logs/{logId}` | 获取审计日志详情 | ✅ |

### 📁 文件管理 (`/api/files`)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/upload` | 上传文件 | ✅ |
| GET | `/{fileId}` | 下载文件 | ✅ |
| DELETE | `/{fileId}` | 删除文件 | ✅ |

## 📊 响应格式

### 统一响应结构

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1640995200000,
  "traceId": "abc123def456"
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未认证或认证失败 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 🔗 相关链接

- [在线API文档](http://localhost:26300/api/swagger-ui.html) - Swagger UI交互式文档
- [自动导出文档](generated/) - 应用启动时自动生成的OpenAPI文档
- [项目文档](../) - 完整项目文档

## 📝 使用示例

### 完整的用户管理流程

1. **登录获取令牌**
```bash
curl -X POST http://localhost:26300/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

2. **获取用户列表**
```bash
curl -X GET http://localhost:26300/api/users \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

3. **创建新用户**
```bash
curl -X POST http://localhost:26300/api/users \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","email":"user@example.com","password":"password123"}'
```

更多详细的API使用示例，请参考 [Swagger UI 文档](http://localhost:26300/api/swagger-ui.html)。
