package space.akko.foundation.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
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
            Map<String, Object> properties = new HashMap<>(envVars);
            MapPropertySource propertySource = new MapPropertySource("dotenv", properties);
            environment.getPropertySources().addFirst(propertySource);

            log.info("成功加载.env配置，共{}个配置项", envVars.size());

            // 在DEBUG级别下显示加载的配置项（不显示敏感信息）
            if (log.isDebugEnabled()) {
                envVars.forEach((key, value) -> {
                    String displayValue = isSensitiveKey(key) ? "***" : value;
                    log.debug(".env配置: {}={}", key, displayValue);
                });
            }
        } else {
            log.warn("未找到任何.env文件，将使用默认配置");
        }
    }

    private void loadSingleDotenvFile(String filename, Map<String, String> envVars) {
        Path filePath = Paths.get(filename);

        if (!Files.exists(filePath)) {
            log.debug(".env文件不存在: {}", filename);
            return;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);

            for (String line : lines) {
                line = line.trim();
                // 跳过空行和注释行
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // 解析键值对
                int equalIndex = line.indexOf('=');
                if (equalIndex > 0) {
                    String key = line.substring(0, equalIndex).trim();
                    String value = line.substring(equalIndex + 1).trim();

                    // 移除值两端的引号
                    if ((value.startsWith("\"") && value.endsWith("\"")) ||
                        (value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }

                    envVars.put(key, value);
                }
            }

            log.info("成功加载.env文件: {}", filename);

        } catch (IOException e) {
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
