package space.akko.foundation.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI配置
 *
 * @author akko
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:backend}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                    new Server().url("http://localhost:26300").description("开发环境"),
                    new Server().url("https://api.example.com").description("生产环境")
                ))
                .components(new Components()
                    .addSecuritySchemes("Bearer", securityScheme()))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"));
    }

    /**
     * API信息
     */
    private Info apiInfo() {
        return new Info()
                .title("CodeAS Backend API")
                .description("CodeAS 平台后端接口文档")
                .version("0.0.1")
                .contact(new Contact()
                    .name("akko")
                    .email("akko@akko.space")
                    .url("https://akko.space"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT"));
    }

    /**
     * 安全方案
     */
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("请在请求头中添加JWT令牌，格式：Bearer {token}");
    }
}
