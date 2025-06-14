# CodeAS Backend 系统架构文档

## 🏗️ 整体架构

### 分层架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│                   (Controller + API)                        │
├─────────────────────────────────────────────────────────────┤
│                     Business Layer                          │
│                  (Service + Domain)                         │
├─────────────────────────────────────────────────────────────┤
│                   Persistence Layer                         │
│                  (Repository + DAO)                         │
├─────────────────────────────────────────────────────────────┤
│                  Infrastructure Layer                       │
│              (Cache + MQ + External APIs)                   │
└─────────────────────────────────────────────────────────────┘
```

### 模块化架构

```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│   Business      │  │   Integration   │  │   Extension     │
│   Module        │  │   Module        │  │   Module        │
│                 │  │                 │  │                 │
│ • 业务逻辑      │  │ • 第三方集成    │  │ • 扩展功能      │
│ • 业务实体      │  │ • 外部API       │  │ • 插件系统      │
│ • 业务规则      │  │ • 消息队列      │  │ • 自定义组件    │
└─────────────────┘  └─────────────────┘  └─────────────────┘
         │                     │                     │
         └─────────────────────┼─────────────────────┘
                               │
┌─────────────────────────────────────────────────────────────┐
│                    Platform Module                          │
│                                                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │    User     │ │    Role     │ │ Permission  │           │
│  │ Management  │ │ Management  │ │ Management  │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
│                                                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │    Menu     │ │   Config    │ │   Audit     │           │
│  │ Management  │ │ Management  │ │ Management  │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                               │
┌─────────────────────────────────────────────────────────────┐
│                   Foundation Module                         │
│                                                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   Common    │ │    Cache    │ │  Security   │           │
│  │  Components │ │  Management │ │  Framework  │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
│                                                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ Exception   │ │    Utils    │ │    Config   │           │
│  │  Handling   │ │  Libraries  │ │ Management  │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
```

## 🗄️ 数据架构

### 数据库Schema设计

```
┌─────────────────────────────────────────────────────────────┐
│                      PostgreSQL                             │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │ foundation_     │  │ platform_       │                  │
│  │ schema          │  │ schema          │                  │
│  ├─────────────────┤  ├─────────────────┤                  │
│  │ • config_*      │  │ • user_*        │                  │
│  │ • audit_*       │  │ • role_*        │                  │
│  │ • cache_*       │  │ • permission_*  │                  │
│  │ • dictionary_*  │  │ • menu_*        │                  │
│  └─────────────────┘  └─────────────────┘                  │
│                                                             │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │ business_       │  │ integration_    │                  │
│  │ schema          │  │ schema          │                  │
│  ├─────────────────┤  ├─────────────────┤                  │
│  │ • 业务表        │  │ • 集成表        │                  │
│  │ （预留扩展）    │  │ （预留扩展）    │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

### 缓存架构

```
┌─────────────────────────────────────────────────────────────┐
│                    Multi-Level Cache                        │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐                                        │
│  │   L1 Cache      │  ← Caffeine (本地缓存)                │
│  │   (Local)       │    • 高性能访问                        │
│  │                 │    • 内存限制                          │
│  └─────────────────┘    • 进程级别                          │
│           │                                                 │
│           ▼                                                 │
│  ┌─────────────────┐                                        │
│  │   L2 Cache      │  ← Redis (分布式缓存)                 │
│  │ (Distributed)   │    • 集群共享                          │
│  │                 │    • 持久化                            │
│  └─────────────────┘    • 高可用                            │
└─────────────────────────────────────────────────────────────┘
```

#### 多级缓存实现

系统实现了智能的多级缓存策略：

1. **L1缓存（本地）**：基于Caffeine的高性能本地缓存
   - 快速访问，纳秒级响应
   - 内存限制，自动淘汰
   - 进程级别，不跨实例

2. **L2缓存（分布式）**：基于Redis的分布式缓存
   - 集群共享，数据一致性
   - 持久化存储，高可用性
   - 网络访问，毫秒级响应

3. **缓存策略**：
   - 读取：L1 → L2 → 数据源
   - 写入：同时写入L1和L2
   - 失效：同时清除L1和L2
   - 回写：L2命中时自动回写L1

## 🔐 安全架构

### 认证授权流程

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Client    │    │   Gateway   │    │  Backend    │
│             │    │             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
        │                   │                   │
        │ 1. Login Request  │                   │
        ├──────────────────►│                   │
        │                   │ 2. Authenticate   │
        │                   ├──────────────────►│
        │                   │                   │
        │                   │ 3. JWT Token      │
        │                   │◄──────────────────┤
        │ 4. JWT Token      │                   │
        │◄──────────────────┤                   │
        │                   │                   │
        │ 5. API Request    │                   │
        │   + JWT Token     │                   │
        ├──────────────────►│                   │
        │                   │ 6. Validate Token │
        │                   ├──────────────────►│
        │                   │                   │
        │                   │ 7. Response       │
        │                   │◄──────────────────┤
        │ 8. Response       │                   │
        │◄──────────────────┤                   │
```

### 权限控制模型

```
┌─────────────────────────────────────────────────────────────┐
│                    RBAC Model                               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────┐    ┌─────────┐    ┌─────────────┐             │
│  │  User   │────│  Role   │────│ Permission  │             │
│  │         │    │         │    │             │             │
│  └─────────┘    └─────────┘    └─────────────┘             │
│       │              │               │                     │
│       │              │               │                     │
│  ┌─────────┐    ┌─────────┐    ┌─────────────┐             │
│  │ Profile │    │Hierarchy│    │  Resource   │             │
│  │         │    │         │    │             │             │
│  └─────────┘    └─────────┘    └─────────────┘             │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## 🔄 请求处理流程

### HTTP请求生命周期

```
┌─────────────┐
│   Client    │
└─────────────┘
        │
        ▼
┌─────────────┐
│   Filter    │ ← TraceFilter (链路追踪)
│   Chain     │ ← JwtAuthenticationFilter (JWT验证)
└─────────────┘
        │
        ▼
┌─────────────┐
│ Controller  │ ← @RestController
│             │ ← @RequestMapping
└─────────────┘
        │
        ▼
┌─────────────┐
│   Service   │ ← @Service
│             │ ← @Transactional
└─────────────┘
        │
        ▼
┌─────────────┐
│   Mapper    │ ← MyBatis Plus
│             │ ← @Mapper
└─────────────┘
        │
        ▼
┌─────────────┐
│  Database   │ ← PostgreSQL
│             │ ← Connection Pool
└─────────────┘
```

### AOP切面处理

```
┌─────────────────────────────────────────────────────────────┐
│                    AOP Aspects                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │ Permission      │  │ Operation       │                  │
│  │ Aspect          │  │ Log Aspect      │                  │
│  │                 │  │                 │                  │
│  │ @RequireRole    │  │ @OperationLog   │                  │
│  │ @RequirePermission│ │ 记录操作日志    │                  │
│  └─────────────────┘  └─────────────────┘                  │
│                                                             │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │ Cache           │  │ Exception       │                  │
│  │ Aspect          │  │ Handler         │                  │
│  │                 │  │                 │                  │
│  │ @Cacheable      │  │ @ExceptionHandler│                 │
│  │ @CacheEvict     │  │ 全局异常处理    │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

## 📊 监控架构

### 应用监控

```
┌─────────────────────────────────────────────────────────────┐
│                  Monitoring Stack                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   Actuator      │  │    Metrics      │                  │
│  │   Endpoints     │  │   Collection    │                  │
│  │                 │  │                 │                  │
│  │ • /health       │  │ • JVM Metrics   │                  │
│  │ • /info         │  │ • DB Metrics    │                  │
│  │ • /metrics      │  │ • Cache Metrics │                  │
│  └─────────────────┘  └─────────────────┘                  │
│                                                             │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │     Logging     │  │    Alerting     │                  │
│  │    System       │  │    System       │                  │
│  │                 │  │                 │                  │
│  │ • Logback       │  │ • Health Check  │                  │
│  │ • File Rotation │  │ • Error Alert   │                  │
│  │ • Log Levels    │  │ • Performance   │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 部署架构

### 容器化部署

```
┌─────────────────────────────────────────────────────────────┐
│                   Docker Environment                        │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   Application   │  │   PostgreSQL    │                  │
│  │   Container     │  │   Container     │                  │
│  │                 │  │                 │                  │
│  │ • Spring Boot   │  │ • Database      │                  │
│  │ • Java 21       │  │ • Persistent    │                  │
│  │ • Port 8080     │  │ • Port 5432     │                  │
│  └─────────────────┘  └─────────────────┘                  │
│                                                             │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │     Redis       │  │     Nginx       │                  │
│  │   Container     │  │   Container     │                  │
│  │                 │  │                 │                  │
│  │ • Cache         │  │ • Reverse Proxy │                  │
│  │ • Session       │  │ • Load Balancer │                  │
│  │ • Port 6379     │  │ • Port 80/443   │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

### 生产环境架构

```
┌─────────────────────────────────────────────────────────────┐
│                 Production Environment                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │  Load Balancer  │  │   Application   │                  │
│  │    (Nginx)      │  │   Cluster       │                  │
│  │                 │  │                 │                  │
│  │ • SSL/TLS       │  │ • Multiple      │                  │
│  │ • Rate Limiting │  │   Instances     │                  │
│  │ • Health Check  │  │ • Auto Scaling  │                  │
│  └─────────────────┘  └─────────────────┘                  │
│                                                             │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   Database      │  │     Cache       │                  │
│  │   Cluster       │  │    Cluster      │                  │
│  │                 │  │                 │                  │
│  │ • Master/Slave  │  │ • Redis Cluster │                  │
│  │ • Backup        │  │ • High Availability│               │
│  │ • Monitoring    │  │ • Persistence   │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

## 🔧 技术选型

### 核心技术栈

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 应用框架 | Spring Boot | 3.5.0 | 主框架 |
| 编程语言 | Java | 21 | LTS版本 |
| 数据库 | PostgreSQL | 15-alpine | 主数据库 |
| 缓存 | Redis | 7-alpine | 分布式缓存 |
| 本地缓存 | Caffeine | 3.1.8 | 高性能本地缓存 |
| ORM框架 | MyBatis Plus | 3.5.12 | 数据访问层 |
| 数据库迁移 | Flyway | 11.9.0 | 版本控制 |
| 安全框架 | Spring Security | 6.x | 安全认证 |
| JWT | JJWT | 0.12.6 | 令牌处理 |
| API文档 | SpringDoc | 2.8.8 | OpenAPI 3.0 |
| 工具库 | Hutool | 5.8.38 | 工具集合 |
| 工具库 | Commons Lang3 | 3.17.0 | 工具集合 |
| 数据库驱动 | PostgreSQL Driver | 42.7.2 | 数据库连接 |
| 环境变量管理 | Dotenv Java | 3.2.0 | 配置管理 |
| 代码简化 | Lombok | 1.18.30 | 注解处理器 |
| 构建工具 | Maven | 3.8+ | 项目管理 |
| 容器化 | Docker | 20+ | 容器部署 |

### 设计模式应用

1. **分层架构模式**：清晰的职责分离
2. **依赖注入模式**：Spring IoC容器
3. **AOP模式**：横切关注点处理
4. **模板方法模式**：统一的CRUD操作
5. **策略模式**：多种缓存策略
6. **观察者模式**：事件驱动机制
7. **建造者模式**：复杂对象构建
8. **单例模式**：Spring Bean管理

## 📈 性能优化

### 数据库优化

1. **连接池配置**：HikariCP优化
2. **索引策略**：合理的索引设计
3. **查询优化**：SQL性能调优
4. **分页优化**：MyBatis Plus分页

### 缓存优化

1. **多级缓存**：L1+L2缓存策略
2. **缓存预热**：应用启动时预加载
3. **缓存更新**：合理的失效策略
4. **缓存监控**：命中率统计

### JVM优化

1. **内存配置**：堆内存大小调优
2. **GC策略**：垃圾回收器选择
3. **JIT编译**：热点代码优化
4. **监控工具**：JVM性能监控

## 🔮 扩展性设计

### 水平扩展

1. **无状态设计**：支持多实例部署
2. **负载均衡**：请求分发策略
3. **数据库分片**：水平分库分表
4. **缓存集群**：Redis集群模式

### 垂直扩展

1. **模块化设计**：功能模块独立
2. **插件机制**：动态功能扩展
3. **配置中心**：集中配置管理
4. **服务治理**：微服务架构准备

### 可维护性

1. **代码规范**：统一的编码标准
2. **文档完善**：详细的技术文档
3. **测试覆盖**：完整的测试体系
4. **监控告警**：全面的运维监控
