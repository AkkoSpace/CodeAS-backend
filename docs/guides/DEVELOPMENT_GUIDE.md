# 开发指南

## 🚀 快速开始

### 环境要求

- **Java**: 21+
- **Maven**: 3.8+
- **PostgreSQL**: 15+
- **Redis**: 7+
- **IDE**: IntelliJ IDEA 推荐

### 本地开发环境搭建

1. **克隆项目**
```bash
git clone <repository-url>
cd CodeAS-backend
```

2. **配置环境变量**
```bash
# 复制环境变量模板
cp .env.example .env

# 编辑 .env 文件，填入真实的配置值
# DB_PASSWORD=your_actual_db_password
# REDIS_PASSWORD=your_actual_redis_password
# JWT_SECRET=your_actual_jwt_secret
```

3. **启动依赖服务**
```bash
# 使用Docker启动PostgreSQL和Redis
docker-compose -f docker/docker-compose.yml up -d postgres redis
```

4. **初始化数据库**
```bash
# 设置Flyway环境变量（或在.env文件中配置）
# 注意：数据库名称固定为 codeas
export FLYWAY_URL=jdbc:postgresql://localhost:5432/codeas
export FLYWAY_USER=your_db_username
export FLYWAY_PASSWORD=your_db_password

# 执行数据库迁移
mvn flyway:migrate
```

5. **运行应用**
```bash
# 方式1：使用Maven
mvn spring-boot:run

# 方式2：打包后运行
mvn clean package
java -jar target/backend-1.0.0-SNAPSHOT.jar
```

## 📁 项目结构说明

### 分层架构

```
src/main/java/space/akko/
├── foundation/          # 基础层 - 通用功能
│   ├── common/         # 通用类（响应格式、分页等）
│   ├── config/         # 配置类
│   ├── constant/       # 常量定义
│   ├── exception/      # 异常处理
│   ├── utils/          # 工具类
│   ├── annotation/     # 自定义注解
│   ├── aspect/         # 切面编程
│   ├── filter/         # 过滤器
│   ├── cache/          # 缓存管理
│   └── controller/     # 基础控制器
├── platform/           # 平台层 - 平台功能
│   ├── user/          # 用户管理
│   ├── role/          # 角色管理
│   ├── permission/    # 权限管理
│   ├── menu/          # 菜单管理
│   ├── config/        # 系统配置
│   ├── audit/         # 操作审计
│   └── dictionary/    # 数据字典
└── business/          # 业务层 - 业务功能（预留）
```

### 模块结构

每个功能模块通常包含以下结构：
```
module/
├── controller/        # 控制器层
├── service/          # 服务层
│   └── impl/         # 服务实现
├── mapper/           # 数据访问层
├── entity/           # 实体类
├── dto/              # 数据传输对象
│   ├── request/      # 请求DTO
│   └── response/     # 响应DTO
└── enums/            # 枚举类
```

## 🛠️ 开发规范

### 代码规范

1. **命名规范**
   - 类名：大驼峰命名法（PascalCase）
   - 方法名和变量名：小驼峰命名法（camelCase）
   - 常量：全大写，下划线分隔（UPPER_SNAKE_CASE）
   - 包名：全小写，点分隔

2. **注释规范**
   - 类和接口必须有JavaDoc注释
   - 公共方法必须有JavaDoc注释
   - 复杂逻辑必须有行内注释

3. **异常处理**
   - 使用统一的异常处理机制
   - 业务异常继承`BusinessException`
   - 安全异常继承`SecurityException`

### 数据库规范

1. **表命名**
   - 使用下划线分隔的小写字母
   - 按模块分组：`user_profile`、`role_definition`等

2. **字段命名**
   - 使用下划线分隔的小写字母
   - 布尔字段以`is_`开头
   - 时间字段以`_time`或`_at`结尾

3. **Schema设计**
   - `foundation_schema`：基础功能表
   - `platform_schema`：平台功能表
   - `business_schema`：业务功能表
   - `integration_schema`：集成功能表

### API设计规范

1. **RESTful设计**
   - GET：查询操作
   - POST：创建操作
   - PUT：更新操作
   - DELETE：删除操作

2. **URL设计**
   - 使用复数名词：`/api/users`
   - 层级关系：`/api/users/{id}/roles`
   - 查询参数：`/api/users?page=1&size=10`

3. **响应格式**
   - 统一使用`Result<T>`包装响应
   - 分页查询使用`PageResult<T>`
   - 错误响应包含错误码和错误信息

## 🔧 开发工具

### IDE配置

**IntelliJ IDEA推荐插件：**
- Lombok Plugin
- MyBatis Log Plugin
- RestfulTool
- Rainbow Brackets
- GitToolBox

**代码格式化：**
- 使用Google Java代码风格
- 导入项目根目录的`google-java-format.xml`

### 数据库工具

**推荐工具：**
- DBeaver（免费）
- DataGrip（JetBrains）
- pgAdmin（PostgreSQL专用）

### API测试

**推荐工具：**
- **Swagger UI（内置，推荐）**：http://localhost:26300/swagger-ui.html
  - 实时同步的API文档
  - 可直接测试API接口
  - 支持认证和参数配置
- Postman - 专业的API测试工具
- Insomnia - 轻量级API客户端
- IntelliJ IDEA HTTP Client - IDE集成工具

## 🧪 测试指南

### 单元测试

1. **测试框架**
   - JUnit 5
   - Mockito
   - Spring Boot Test

2. **测试规范**
   - 测试类命名：`XxxTest`
   - 测试方法命名：`should_xxx_when_xxx`
   - 使用`@MockBean`模拟依赖

3. **测试示例**
```java
@SpringBootTest
class UserServiceTest {

    @MockBean
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Test
    void should_create_user_when_valid_input() {
        // given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");

        // when
        Result<UserResponse> result = userService.createUser(request);

        // then
        assertThat(result.isSuccess()).isTrue();
    }
}
```

### 集成测试

1. **使用H2内存数据库**
2. **测试完整的业务流程**
3. **验证数据库状态变化**

## 📦 构建和部署

### Maven配置

**常用命令：**
```bash
# 编译
mvn compile

# 测试
mvn test

# 打包
mvn package

# 跳过测试打包
mvn package -DskipTests

# 清理并打包
mvn clean package

# 生成分发包
mvn clean package assembly:single
```

### Docker部署

**本地开发：**
```bash
# 启动依赖服务
docker-compose -f docker/docker-compose.yml up -d postgres redis

# 停止服务
docker-compose -f docker/docker-compose.yml down
```

**生产部署：**
```bash
# 构建镜像
docker build -t backend:latest .

# 运行容器
docker run -d -p 26300:26300 backend:latest
```

## 🐛 调试技巧

### 日志调试

1. **日志级别配置**
```yaml
logging:
  level:
    space.akko: DEBUG
    org.springframework.web: DEBUG
```

2. **使用MDC追踪请求**
```java
MDC.put("traceId", TraceUtils.getTraceId());
log.info("Processing request: {}", request);
```

### 性能调试

1. **使用Spring Boot Actuator**
   - 健康检查：`/actuator/health`
   - 指标信息：`/actuator/metrics`
   - 线程信息：`/actuator/threaddump`

2. **数据库性能**
   - 开启SQL日志
   - 使用`EXPLAIN`分析查询计划
   - 监控慢查询

## 📚 学习资源

### 官方文档
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [MyBatis Plus](https://baomidou.com/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

### 推荐书籍
- 《Spring Boot实战》
- 《Java并发编程实战》
- 《高性能MySQL》

### 在线资源
- [Spring Boot Guides](https://spring.io/guides)
- [Baeldung](https://www.baeldung.com/)
- [Java Code Geeks](https://www.javacodegeeks.com/)
