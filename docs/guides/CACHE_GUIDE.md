# 缓存使用指南

## 📋 概述

CodeAS Backend 实现了智能的多级缓存系统，结合本地缓存（Caffeine）和分布式缓存（Redis）的优势，提供高性能的数据访问能力。

## 🏗️ 缓存架构

### 多级缓存设计

```
┌─────────────────────────────────────────────────────────────┐
│                    应用请求                                 │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                L1 缓存 (Caffeine)                           │
│  • 纳秒级访问速度                                          │
│  • 进程内存存储                                            │
│  • 自动过期淘汰                                            │
└─────────────────────┬───────────────────────────────────────┘
                      │ 未命中
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                L2 缓存 (Redis)                              │
│  • 毫秒级访问速度                                          │
│  • 分布式共享存储                                          │
│  • 持久化支持                                              │
└─────────────────────┬───────────────────────────────────────┘
                      │ 未命中
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                   数据源                                    │
│  • 数据库查询                                              │
│  • 外部API调用                                             │
│  • 计算密集操作                                            │
└─────────────────────────────────────────────────────────────┘
```

### 缓存策略

1. **读取策略**：L1 → L2 → 数据源
2. **写入策略**：同时写入L1和L2
3. **失效策略**：同时清除L1和L2
4. **回写策略**：L2命中时自动回写L1

## ⚙️ 配置说明

### 基础配置

```yaml
platform:
  cache:
    l1:
      enabled: true              # 启用L1缓存
      maximum-size: 1000         # 最大缓存条目数
      expire-after-write: 5m     # 写入后过期时间
    l2:
      enabled: true              # 启用L2缓存
      default-ttl: 30m           # 默认生存时间
```

### 环境变量配置

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `PLATFORM_CACHE_L1_ENABLED` | L1缓存开关 | `true` |
| `PLATFORM_CACHE_L1_MAXIMUM_SIZE` | L1缓存最大条目数 | `1000` |
| `PLATFORM_CACHE_L1_EXPIRE_AFTER_WRITE` | L1缓存写入后过期时间 | `5m` |
| `PLATFORM_CACHE_L2_ENABLED` | L2缓存开关 | `true` |
| `PLATFORM_CACHE_L2_DEFAULT_TTL` | L2缓存默认TTL | `30m` |

## 🔧 使用方法

### 1. 注解方式（推荐）

```java
@Service
public class UserService {

    @Cacheable(value = "users", key = "#userId")
    public User getUserById(Long userId) {
        // 数据库查询逻辑
        return userMapper.selectById(userId);
    }

    @CacheEvict(value = "users", key = "#user.id")
    public void updateUser(User user) {
        // 更新用户信息
        userMapper.updateById(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void clearAllUsers() {
        // 清除所有用户缓存
    }
}
```

### 2. 编程方式

```java
@Service
public class UserService {

    @Autowired
    private CacheManager cacheManager;

    public User getUserById(Long userId) {
        Cache cache = cacheManager.getCache("users");
        
        // 尝试从缓存获取
        User user = cache.get(userId, User.class);
        if (user != null) {
            return user;
        }
        
        // 从数据库查询
        user = userMapper.selectById(userId);
        
        // 写入缓存
        if (user != null) {
            cache.put(userId, user);
        }
        
        return user;
    }
}
```

### 3. 自定义缓存名称

```java
@Component
public class CacheNames {
    public static final String USERS = "users";
    public static final String ROLES = "roles";
    public static final String PERMISSIONS = "permissions";
    public static final String MENUS = "menus";
    public static final String CONFIGS = "configs";
}

@Cacheable(value = CacheNames.USERS, key = "#userId")
public User getUserById(Long userId) {
    return userMapper.selectById(userId);
}
```

## 📊 性能优化

### 缓存命中率监控

```java
@Component
public class CacheMetrics {

    @EventListener
    public void handleCacheHit(CacheHitEvent event) {
        log.info("缓存命中: cache={}, key={}", 
                event.getCacheName(), event.getKey());
    }

    @EventListener
    public void handleCacheMiss(CacheMissEvent event) {
        log.info("缓存未命中: cache={}, key={}", 
                event.getCacheName(), event.getKey());
    }
}
```

### 缓存预热

```java
@Component
public class CacheWarmup {

    @EventListener(ApplicationReadyEvent.class)
    public void warmupCache() {
        log.info("开始缓存预热...");
        
        // 预加载热点数据
        loadHotUsers();
        loadSystemConfigs();
        loadMenus();
        
        log.info("缓存预热完成");
    }
}
```

## 🚨 最佳实践

### 1. 缓存键设计

```java
// ✅ 好的做法
@Cacheable(value = "users", key = "'user:' + #userId")
@Cacheable(value = "users", key = "'user:profile:' + #userId")

// ❌ 避免的做法
@Cacheable(value = "users", key = "#userId") // 可能与其他缓存冲突
```

### 2. 缓存过期时间

```java
// 根据数据特性设置合适的过期时间
@Cacheable(value = "users", key = "#userId")
@CacheTTL(30, TimeUnit.MINUTES)  // 用户信息：30分钟
public User getUserById(Long userId) { ... }

@Cacheable(value = "configs", key = "#configKey")
@CacheTTL(1, TimeUnit.HOURS)     // 系统配置：1小时
public String getConfig(String configKey) { ... }
```

### 3. 缓存更新策略

```java
@Service
@Transactional
public class UserService {

    // 更新时清除缓存
    @CacheEvict(value = "users", key = "#user.id")
    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    // 删除时清除相关缓存
    @CacheEvict(value = {"users", "user-roles"}, key = "#userId")
    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
        userRoleMapper.deleteByUserId(userId);
    }
}
```

### 4. 避免缓存穿透

```java
@Cacheable(value = "users", key = "#userId", unless = "#result == null")
public User getUserById(Long userId) {
    User user = userMapper.selectById(userId);
    // 返回null时不缓存，避免缓存穿透
    return user;
}
```

## 🔍 监控和调试

### 1. 缓存统计

```java
@RestController
@RequestMapping("/api/admin/cache")
public class CacheController {

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/stats")
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache instanceof MultiLevelCache) {
                MultiLevelCache mlCache = (MultiLevelCache) cache;
                stats.put(cacheName, mlCache.getStatistics());
            }
        });
        
        return stats;
    }
}
```

### 2. 缓存清理

```java
@RestController
@RequestMapping("/api/admin/cache")
public class CacheController {

    @PostMapping("/clear/{cacheName}")
    public Result<Void> clearCache(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            return Result.success();
        }
        return Result.error("缓存不存在");
    }

    @PostMapping("/clear/all")
    public Result<Void> clearAllCache() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
        return Result.success();
    }
}
```

## ⚠️ 注意事项

1. **数据一致性**：缓存更新要与数据库操作保持一致
2. **内存使用**：合理设置L1缓存大小，避免内存溢出
3. **网络延迟**：L2缓存依赖Redis，注意网络延迟影响
4. **序列化**：确保缓存对象可序列化
5. **事务处理**：缓存操作要考虑事务回滚的影响

## 🔧 故障排除

### 常见问题

1. **缓存不生效**
   - 检查方法是否为public
   - 检查是否通过Spring代理调用
   - 检查缓存配置是否正确

2. **内存泄漏**
   - 检查L1缓存大小设置
   - 检查缓存过期时间配置
   - 监控内存使用情况

3. **性能问题**
   - 检查缓存命中率
   - 优化缓存键设计
   - 调整缓存过期策略
