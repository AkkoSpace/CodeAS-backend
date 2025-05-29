# 环境变量配置指南

## 概述

本项目支持通过 `.env` 文件来管理环境变量，可以根据不同的环境（开发、测试、生产）自动加载对应的配置文件。

## 文件加载优先级

系统会按照以下优先级加载 `.env` 文件（后加载的会覆盖先加载的）：

1. `.env` - 默认配置文件
2. `.env.{profile}` - 环境特定配置文件（如 `.env.dev`、`.env.prod`）
3. `.env.local` - 本地通用配置文件
4. `.env.{profile}.local` - 环境特定的本地配置文件（如 `.env.dev.local`、`.env.prod.local`）

## 环境配置

### 开发环境 (dev)

当 Spring 激活 `dev` profile 时，系统会尝试加载：
- `.env`
- `.env.dev`
- `.env.local`
- `.env.dev.local` ⭐ **最高优先级**

### 生产环境 (prod)

当 Spring 激活 `prod` profile 时，系统会尝试加载：
- `.env`
- `.env.prod`
- `.env.local`
- `.env.prod.local` ⭐ **最高优先级**

## 快速开始

### 1. 创建开发环境配置

```bash
# 复制开发环境模板
cp .env.dev.local.example .env.dev.local

# 编辑配置文件
vim .env.dev.local
```

### 2. 创建生产环境配置

```bash
# 复制生产环境模板
cp .env.prod.local.example .env.prod.local

# 编辑配置文件
vim .env.prod.local
```

### 3. 配置示例

`.env.dev.local` 文件内容示例：
```properties
# 数据库配置
DB_HOST=localhost
DB_PORT=5432
DB_USERNAME=dev_user
DB_PASSWORD=dev_password

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=dev_redis_pass

# JWT密钥
JWT_SECRET=dev_jwt_secret_key
```

## 支持的环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `SERVER_PORT` | 服务器端口 | `26300` |
| `DB_HOST` | 数据库主机 | `localhost` |
| `DB_PORT` | 数据库端口 | `5432` |
| `DB_USERNAME` | 数据库用户名 | `codeas` |
| `DB_PASSWORD` | 数据库密码 | `codeas123` |
| `REDIS_HOST` | Redis主机 | `localhost` |
| `REDIS_PORT` | Redis端口 | `6379` |
| `REDIS_PASSWORD` | Redis密码 | 空 |
| `JWT_SECRET` | JWT密钥 | 默认密钥 |
| `ADMIN_USERNAME` | 管理员用户名 | `admin` |
| `ADMIN_PASSWORD` | 管理员密码 | `admin123` |

## 安全注意事项

1. **不要提交敏感配置**：`.env.*.local` 文件已被添加到 `.gitignore`，不会被提交到版本控制
2. **使用强密码**：生产环境请使用强随机密码
3. **定期轮换密钥**：定期更换JWT密钥和数据库密码
4. **权限控制**：确保配置文件只有必要的用户可以访问

## 故障排除

### 配置未生效

1. 检查文件名是否正确
2. 检查文件是否在项目根目录
3. 检查Spring激活的profile是否正确
4. 查看启动日志中的环境变量加载信息

### 查看加载的配置

启动应用时，日志会显示：
```
INFO  - 当前激活的环境: dev
INFO  - 成功加载.env文件: .env.dev.local
INFO  - 成功加载环境变量，共12个配置项
```

在DEBUG级别下，还会显示具体的配置项（敏感信息会被隐藏）。

## 最佳实践

1. **使用模板文件**：为每个环境提供 `.example` 模板文件
2. **文档化配置**：在README中说明必需的环境变量
3. **验证配置**：启动时验证关键配置项是否存在
4. **环境隔离**：不同环境使用不同的配置值
5. **备份配置**：重要环境的配置文件要有备份
