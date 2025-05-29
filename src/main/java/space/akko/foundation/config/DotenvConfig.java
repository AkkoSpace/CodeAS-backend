package space.akko.foundation.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Dotenv配置 - 根据环境自动加载对应的.env文件
 * 
 * @author akko
 * @since 1.0.0
 */
@Slf4j
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        // 获取当前激活的profile，默认为dev
        String[] activeProfiles = environment.getActiveProfiles();
        String profile = activeProfiles.length > 0 ? activeProfiles[0] : "dev";
        
        log.info("当前激活的环境: {}", profile);
        
        // 根据环境加载对应的.env文件
        loadDotenvFile(environment, profile);
    }
    
    private void loadDotenvFile(ConfigurableEnvironment environment, String profile) {
        // 定义可能的.env文件路径（按优先级排序）
        String[] dotenvFiles = {
            ".env." + profile + ".local",  // 最高优先级：环境特定的本地文件
            ".env.local",                  // 本地文件
            ".env." + profile,             // 环境特定文件
            ".env"                         // 默认文件
        };
        
        Map<String, String> envVars = new HashMap<>();
        
        // 按优先级加载.env文件（后加载的会覆盖先加载的）
        for (String dotenvFile : dotenvFiles) {
            loadSingleDotenvFile(dotenvFile, envVars);
        }
        
        if (!envVars.isEmpty()) {
            // 将环境变量添加到Spring环境中
            MapPropertySource propertySource = new MapPropertySource("dotenv", envVars);
            environment.getPropertySources().addFirst(propertySource);
            
            log.info("成功加载环境变量，共{}个配置项", envVars.size());
            
            // 在DEBUG级别下显示加载的配置项（不显示敏感信息）
            if (log.isDebugEnabled()) {
                envVars.forEach((key, value) -> {
                    String displayValue = isSensitiveKey(key) ? "***" : value;
                    log.debug("环境变量: {}={}", key, displayValue);
                });
            }
        } else {
            log.warn("未找到任何.env文件，将使用默认配置");
        }
    }
    
    private void loadSingleDotenvFile(String filename, Map<String, String> envVars) {
        File dotenvFile = new File(filename);
        
        if (!dotenvFile.exists()) {
            log.debug(".env文件不存在: {}", filename);
            return;
        }
        
        try {
            Dotenv dotenv = Dotenv.configure()
                    .filename(filename)
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
            
            // 将dotenv中的变量添加到envVars中
            dotenv.entries().forEach(entry -> {
                envVars.put(entry.getKey(), entry.getValue());
            });
            
            log.info("成功加载.env文件: {}", filename);
            
        } catch (DotenvException e) {
            log.warn("加载.env文件失败: {} - {}", filename, e.getMessage());
        }
    }
    
    /**
     * 判断是否为敏感配置项
     */
    private boolean isSensitiveKey(String key) {
        String lowerKey = key.toLowerCase();
        return lowerKey.contains("password") || 
               lowerKey.contains("secret") || 
               lowerKey.contains("token") ||
               lowerKey.contains("key");
    }
}
