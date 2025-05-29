package space.akko.foundation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.akko.foundation.common.Result;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 
 * @author akko
 * @since 1.0.0
 */
@Tag(name = "健康检查", description = "系统健康检查接口")
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final DataSource dataSource;
    private final RedisTemplate<String, Object> redisTemplate;

    @Operation(summary = "系统健康检查", description = "检查系统各组件健康状态")
    @GetMapping
    public Result<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("application", "Backend Platform");
        healthInfo.put("version", "1.0.0");

        // 检查数据库
        healthInfo.put("database", checkDatabase());

        // 检查Redis
        healthInfo.put("redis", checkRedis());

        // 检查JVM
        healthInfo.put("jvm", checkJvm());

        return Result.success(healthInfo);
    }

    @Operation(summary = "数据库健康检查", description = "检查数据库连接状态")
    @GetMapping("/database")
    public Result<Map<String, Object>> databaseHealth() {
        return Result.success(checkDatabase());
    }

    @Operation(summary = "Redis健康检查", description = "检查Redis连接状态")
    @GetMapping("/redis")
    public Result<Map<String, Object>> redisHealth() {
        return Result.success(checkRedis());
    }

    @Operation(summary = "JVM健康检查", description = "检查JVM状态")
    @GetMapping("/jvm")
    public Result<Map<String, Object>> jvmHealth() {
        return Result.success(checkJvm());
    }

    /**
     * 检查数据库健康状态
     */
    private Map<String, Object> checkDatabase() {
        Map<String, Object> dbInfo = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            dbInfo.put("status", "UP");
            dbInfo.put("database", connection.getMetaData().getDatabaseProductName());
            dbInfo.put("version", connection.getMetaData().getDatabaseProductVersion());
            dbInfo.put("url", connection.getMetaData().getURL());
        } catch (Exception e) {
            dbInfo.put("status", "DOWN");
            dbInfo.put("error", e.getMessage());
        }
        return dbInfo;
    }

    /**
     * 检查Redis健康状态
     */
    private Map<String, Object> checkRedis() {
        Map<String, Object> redisInfo = new HashMap<>();
        try {
            String pong = redisTemplate.getConnectionFactory().getConnection().ping();
            redisInfo.put("status", "UP");
            redisInfo.put("ping", pong);
        } catch (Exception e) {
            redisInfo.put("status", "DOWN");
            redisInfo.put("error", e.getMessage());
        }
        return redisInfo;
    }

    /**
     * 检查JVM健康状态
     */
    private Map<String, Object> checkJvm() {
        Map<String, Object> jvmInfo = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        jvmInfo.put("status", "UP");
        jvmInfo.put("maxMemory", formatBytes(maxMemory));
        jvmInfo.put("totalMemory", formatBytes(totalMemory));
        jvmInfo.put("usedMemory", formatBytes(usedMemory));
        jvmInfo.put("freeMemory", formatBytes(freeMemory));
        jvmInfo.put("memoryUsage", String.format("%.2f%%", (double) usedMemory / maxMemory * 100));
        jvmInfo.put("processors", runtime.availableProcessors());
        jvmInfo.put("javaVersion", System.getProperty("java.version"));
        jvmInfo.put("javaVendor", System.getProperty("java.vendor"));
        
        return jvmInfo;
    }

    /**
     * 格式化字节数
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
