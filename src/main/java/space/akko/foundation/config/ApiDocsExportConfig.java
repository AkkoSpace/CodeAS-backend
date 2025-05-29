package space.akko.foundation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * API文档自动导出配置
 * 在应用启动完成后自动导出OpenAPI文档
 *
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApiDocsExportConfig implements ApplicationListener<ApplicationReadyEvent> {

    private final Environment environment;
    private final ObjectMapper objectMapper;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${platform.api-docs.export.enabled:true}")
    private boolean exportEnabled;

    @Value("${platform.api-docs.export.output-dir:./api-docs}")
    private String outputDir;

    @Value("${platform.api-docs.export.formats:json,yaml}")
    private String[] formats;

    @Value("${platform.api-docs.export.include-timestamp:true}")
    private boolean includeTimestamp;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!exportEnabled) {
            log.debug("API文档自动导出已禁用");
            return;
        }

        // 延迟一点时间确保所有端点都已注册
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 等待2秒
                exportApiDocs();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("API文档导出被中断");
            }
        }).start();
    }

    private void exportApiDocs() {
        try {
            log.info("🚀 开始自动导出API文档...");

            // 创建输出目录
            Path outputPath = Paths.get(outputDir);
            Files.createDirectories(outputPath);

            String timestamp = includeTimestamp ? 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) : 
                "latest";

            // 获取API文档数据
            String apiDocsUrl = String.format("http://localhost:%d/api/v3/api-docs", serverPort);
            RestTemplate restTemplate = new RestTemplate();
            
            String jsonContent = restTemplate.getForObject(apiDocsUrl, String.class);
            if (jsonContent == null) {
                log.error("❌ 无法获取API文档内容");
                return;
            }

            // 解析JSON以便格式化
            Object apiDocsObject = objectMapper.readValue(jsonContent, Object.class);

            // 导出不同格式
            for (String format : formats) {
                format = format.trim().toLowerCase();
                switch (format) {
                    case "json":
                        exportJson(outputPath, timestamp, apiDocsObject);
                        break;
                    case "yaml":
                        exportYaml(outputPath, timestamp, apiDocsObject);
                        break;
                    default:
                        log.warn("⚠️  不支持的导出格式: {}", format);
                }
            }

            // 生成摘要信息
            generateSummary(outputPath, timestamp, apiDocsObject);

            log.info("🎉 API文档自动导出完成！输出目录: {}", outputPath.toAbsolutePath());

        } catch (Exception e) {
            log.error("❌ API文档自动导出失败: {}", e.getMessage(), e);
        }
    }

    private void exportJson(Path outputPath, String timestamp, Object apiDocsObject) throws IOException {
        String filename = includeTimestamp ? 
            String.format("api-docs-%s.json", timestamp) : 
            "api-docs-latest.json";
        
        Path jsonFile = outputPath.resolve(filename);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile.toFile(), apiDocsObject);
        
        log.info("✅ JSON格式已导出: {}", jsonFile.getFileName());
    }

    private void exportYaml(Path outputPath, String timestamp, Object apiDocsObject) {
        try {
            // 尝试使用Jackson的YAML支持
            com.fasterxml.jackson.dataformat.yaml.YAMLMapper yamlMapper = 
                new com.fasterxml.jackson.dataformat.yaml.YAMLMapper();
            
            String filename = includeTimestamp ? 
                String.format("api-docs-%s.yaml", timestamp) : 
                "api-docs-latest.yaml";
            
            Path yamlFile = outputPath.resolve(filename);
            yamlMapper.writerWithDefaultPrettyPrinter().writeValue(yamlFile.toFile(), apiDocsObject);
            
            log.info("✅ YAML格式已导出: {}", yamlFile.getFileName());
            
        } catch (Exception e) {
            log.warn("⚠️  YAML导出失败，跳过: {}", e.getMessage());
        }
    }

    private void generateSummary(Path outputPath, String timestamp, Object apiDocsObject) throws IOException {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> apiDocs = (Map<String, Object>) apiDocsObject;
            
            Map<String, Object> summary = new HashMap<>();
            
            // 基本信息
            @SuppressWarnings("unchecked")
            Map<String, Object> info = (Map<String, Object>) apiDocs.get("info");
            if (info != null) {
                summary.put("title", info.get("title"));
                summary.put("version", info.get("version"));
                summary.put("description", info.get("description"));
            }
            
            // 统计信息
            @SuppressWarnings("unchecked")
            Map<String, Object> paths = (Map<String, Object>) apiDocs.get("paths");
            if (paths != null) {
                summary.put("totalPaths", paths.size());
                
                int totalOperations = 0;
                Map<String, Integer> operationsByMethod = new HashMap<>();
                
                for (Object pathObj : paths.values()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> pathMethods = (Map<String, Object>) pathObj;
                    for (String method : pathMethods.keySet()) {
                        if (method.matches("get|post|put|delete|patch|options|head")) {
                            totalOperations++;
                            operationsByMethod.merge(method.toUpperCase(), 1, Integer::sum);
                        }
                    }
                }
                
                summary.put("totalOperations", totalOperations);
                summary.put("operationsByMethod", operationsByMethod);
            }
            
            // 导出时间
            summary.put("exportTime", LocalDateTime.now().toString());
            summary.put("environment", environment.getActiveProfiles());
            
            String filename = includeTimestamp ? 
                String.format("api-summary-%s.json", timestamp) : 
                "api-summary-latest.json";
            
            Path summaryFile = outputPath.resolve(filename);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(summaryFile.toFile(), summary);
            
            log.info("✅ API摘要已生成: {}", summaryFile.getFileName());
            log.info("📊 API统计: {} 个路径, {} 个操作", summary.get("totalPaths"), summary.get("totalOperations"));
            
        } catch (Exception e) {
            log.warn("⚠️  API摘要生成失败: {}", e.getMessage());
        }
    }
}
