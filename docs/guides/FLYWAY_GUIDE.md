# Flyway 数据库迁移指南

## 📋 概述

Flyway 是一个数据库版本管理工具，用于管理数据库 schema 的变更。本项目使用 Flyway 来：

1. **版本控制数据库结构**
2. **自动化数据库迁移**
3. **确保不同环境的数据库一致性**

## 🔧 配置说明

### 为什么 pom.xml 中有数据库配置？

Flyway Maven Plugin 需要数据库连接信息来执行迁移命令。但是，我们**不应该**在 `pom.xml` 中硬编码这些信息。

### 正确的配置方式

#### 1. 环境变量配置

在 `.env` 文件中配置：
```bash
# Flyway数据库迁移配置（数据库名称固定为 codeas）
FLYWAY_URL=jdbc:postgresql://localhost:5432/codeas
FLYWAY_USER=your_db_username
FLYWAY_PASSWORD=your_db_password
```

#### 2. Maven 属性配置

在 `pom.xml` 中使用占位符：
```xml
<configuration>
    <url>${flyway.url}</url>
    <user>${flyway.user}</user>
    <password>${flyway.password}</password>
</configuration>
```

#### 3. 运行时传递参数

```bash
# 方式1：通过系统属性（数据库名称固定为 codeas）
mvn flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/codeas \
                   -Dflyway.user=your_db_username \
                   -Dflyway.password=your_db_password

# 方式2：通过环境变量
export FLYWAY_URL=jdbc:postgresql://localhost:5432/codeas
export FLYWAY_USER=your_db_username
export FLYWAY_PASSWORD=your_db_password
mvn flyway:migrate
```

## 🚀 常用命令

### 基本迁移命令

```bash
# 执行数据库迁移
mvn flyway:migrate

# 查看迁移状态
mvn flyway:info

# 验证迁移脚本
mvn flyway:validate

# 清理数据库（仅开发环境）
mvn flyway:clean
```

### 带参数的命令

```bash
# 指定特定数据库
mvn flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/test_db

# 指定特定用户
mvn flyway:migrate -Dflyway.user=test_user -Dflyway.password=test_pass

# 迁移到特定版本
mvn flyway:migrate -Dflyway.target=1.2.0
```

## 📁 迁移脚本管理

### 脚本命名规范

```
src/main/resources/db/migration/
├── V1.0.0__create_schemas.sql
├── V1.1.0__create_foundation_tables.sql
├── V1.2.0__create_platform_tables.sql
├── V1.3.0__init_data.sql
└── V2.0.0__add_new_features.sql
```

### 命名规则

- **V**：版本迁移脚本前缀
- **1.0.0**：版本号（语义化版本）
- **__**：双下划线分隔符
- **create_schemas**：描述性名称
- **.sql**：文件扩展名

## 🔒 安全最佳实践

### 1. 不要在代码中硬编码密码

❌ **错误做法**：
```xml
<configuration>
    <url>jdbc:postgresql://localhost:5432/mydb</url>
    <user>myuser</user>
    <password>mypassword</password>
</configuration>
```

✅ **正确做法**：
```xml
<configuration>
    <url>${flyway.url}</url>
    <user>${flyway.user}</user>
    <password>${flyway.password}</password>
</configuration>
```

### 2. 使用不同环境的配置

```bash
# 开发环境
export FLYWAY_URL=jdbc:postgresql://localhost:5432/codeas_dev

# 测试环境
export FLYWAY_URL=jdbc:postgresql://test-server:5432/codeas_test

# 生产环境（通过CI/CD系统配置）
export FLYWAY_URL=jdbc:postgresql://prod-server:5432/codeas_prod
```

### 3. 限制数据库权限

为 Flyway 创建专门的数据库用户，只授予必要的权限：

```sql
-- 创建Flyway专用用户
CREATE USER flyway_user WITH PASSWORD 'secure_password';

-- 授予必要权限
GRANT CONNECT ON DATABASE codeas TO flyway_user;
GRANT USAGE ON SCHEMA foundation_schema TO flyway_user;
GRANT CREATE ON SCHEMA foundation_schema TO flyway_user;
-- 根据需要授予其他权限
```

## 🛠️ 开发工作流

### 1. 创建新的迁移脚本

```bash
# 1. 创建新的迁移文件
touch src/main/resources/db/migration/V1.4.0__add_user_preferences.sql

# 2. 编写SQL脚本
# 3. 验证脚本
mvn flyway:validate

# 4. 执行迁移
mvn flyway:migrate

# 5. 检查状态
mvn flyway:info
```

### 2. 团队协作

```bash
# 拉取最新代码后，同步数据库
git pull
mvn flyway:migrate

# 提交新的迁移脚本
git add src/main/resources/db/migration/V1.4.0__add_user_preferences.sql
git commit -m "feat: add user preferences table"
```

## 🚨 注意事项

### 1. 迁移脚本的不可变性

- ✅ 已执行的迁移脚本**不能修改**
- ✅ 如需修改，创建新的迁移脚本
- ❌ 不要删除已执行的迁移脚本

### 2. 生产环境注意事项

- 🔒 **备份数据库**：迁移前务必备份
- 🔍 **测试迁移**：先在测试环境验证
- ⏰ **维护窗口**：在低峰期执行迁移
- 📊 **监控性能**：大表迁移可能耗时较长

### 3. 回滚策略

Flyway 不支持自动回滚，需要手动处理：

```bash
# 创建回滚脚本
touch src/main/resources/db/migration/V1.4.1__rollback_user_preferences.sql

# 在脚本中编写回滚逻辑
DROP TABLE IF EXISTS user_preferences;
```

## 📚 更多资源

- [Flyway 官方文档](https://flywaydb.org/documentation/)
- [Maven Plugin 文档](https://flywaydb.org/documentation/usage/maven/)
- [最佳实践指南](https://flywaydb.org/documentation/concepts/migrations#best-practices)
