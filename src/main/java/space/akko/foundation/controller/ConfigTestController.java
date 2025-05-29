package space.akko.foundation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置测试控制器 - 用于验证环境变量加载
 * 
 * @author akko
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigTestController {

    private final Environment environment;

    @Value("${SERVER_PORT:未设置}")
    private String serverPort;

    @Value("${DB_HOST:未设置}")
    private String dbHost;

    @Value("${REDIS_HOST:未设置}")
    private String redisHost;

    /**
     * 获取当前环境配置信息（不包含敏感信息）
     */
    @GetMapping("/info")
    public Map<String, Object> getConfigInfo() {
        Map<String, Object> config = new HashMap<>();
        
        // 基本信息
        config.put("activeProfiles", environment.getActiveProfiles());
        config.put("defaultProfiles", environment.getDefaultProfiles());
        
        // 非敏感配置信息
        config.put("serverPort", serverPort);
        config.put("dbHost", dbHost);
        config.put("redisHost", redisHost);
        
        // 检查是否从环境变量加载
        config.put("envVarLoaded", !serverPort.equals("未设置"));
        
        return config;
    }

    /**
     * 检查特定环境变量是否存在
     */
    @GetMapping("/check")
    public Map<String, Object> checkEnvironmentVariables() {
        Map<String, Object> result = new HashMap<>();
        
        String[] requiredVars = {
            "SERVER_PORT", "DB_HOST", "DB_PORT", "DB_USERNAME", 
            "REDIS_HOST", "REDIS_PORT", "JWT_SECRET"
        };
        
        Map<String, Boolean> varStatus = new HashMap<>();
        int loadedCount = 0;
        
        for (String var : requiredVars) {
            String value = environment.getProperty(var);
            boolean exists = value != null && !value.trim().isEmpty();
            varStatus.put(var, exists);
            if (exists) {
                loadedCount++;
            }
        }
        
        result.put("requiredVariables", varStatus);
        result.put("loadedCount", loadedCount);
        result.put("totalRequired", requiredVars.length);
        result.put("allLoaded", loadedCount == requiredVars.length);
        
        return result;
    }
}
