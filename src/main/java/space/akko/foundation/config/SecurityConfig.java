package space.akko.foundation.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置
 *
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤器链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("配置Spring Security过滤器链");

        http
            // 禁用CSRF保护（REST API不需要）
            .csrf(AbstractHttpConfigurer::disable)

            // 禁用CORS（使用WebConfig中的配置）
            .cors(AbstractHttpConfigurer::disable)

            // 禁用表单登录
            .formLogin(AbstractHttpConfigurer::disable)

            // 禁用HTTP Basic认证
            .httpBasic(AbstractHttpConfigurer::disable)

            // 禁用默认登出
            .logout(AbstractHttpConfigurer::disable)

            // 设置会话管理策略为无状态
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 配置请求授权 - 允许所有请求（由自定义JWT过滤器处理认证）
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
