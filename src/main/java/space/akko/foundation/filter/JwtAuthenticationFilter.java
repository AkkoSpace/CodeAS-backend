package space.akko.foundation.filter;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import space.akko.foundation.constant.SecurityConstants;
import space.akko.foundation.utils.JwtUtils;
import space.akko.foundation.utils.SecurityUtils;
import space.akko.platform.user.service.UserService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT认证过滤器
 *
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

    private final UserService userService;

    @Value("${platform.security.jwt.secret}")
    private String jwtSecret;

    /**
     * 不需要认证的路径
     */
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
        // 认证相关接口
        "/api/auth/login",
        "/api/auth/refresh",
        "/api/auth/forgot-password",
        "/api/auth/reset-password",
        "/api/auth/verify-email",
        "/api/auth/verify-phone",

        // 用户数据验证接口（注册时需要）
        "/api/users/check/username",
        "/api/users/check/email",
        "/api/users/check/phone",

        // 角色数据验证接口（管理员创建角色时需要）
        "/api/roles/check/code",

        // 公开的系统信息接口
        "/api/system/info",
        "/api/system/time",
        "/api/system/version",

        // 公开的配置信息接口
        "/api/config/info",

        // 健康检查接口（监控需要）
        "/api/health",
        "/api/health/database",
        "/api/health/redis",
        "/api/health/jvm",

        // 验证码相关
        "/api/captcha",

        // 监控端点
        "/actuator/health",
        "/actuator/info",
        "/actuator/metrics",
        "/actuator/prometheus",
        "/actuator",

        // 文档和静态资源
        "/swagger-ui",
        "/api/swagger-ui",
        "/v3/api-docs",
        "/api/v3/api-docs",
        "/webjars",
        "/static",
        "/favicon.ico",
        "/error"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        try {
            // 检查是否需要认证
            if (shouldSkipAuthentication(requestURI, method)) {
                chain.doFilter(request, response);
                return;
            }

            // 获取JWT令牌
            String token = extractToken(httpRequest);
            if (StrUtil.isBlank(token)) {
                sendUnauthorizedResponse(httpResponse, "缺少认证令牌");
                return;
            }

            // 验证JWT令牌
            if (!JwtUtils.validateToken(token, jwtSecret)) {
                sendUnauthorizedResponse(httpResponse, "无效的认证令牌");
                return;
            }

            // 检查令牌类型
            String tokenType = JwtUtils.getTokenTypeFromToken(token, jwtSecret);
            if (!SecurityConstants.ACCESS_TOKEN.equals(tokenType)) {
                sendUnauthorizedResponse(httpResponse, "令牌类型错误");
                return;
            }

            // 获取用户信息并设置到上下文
            Long userId = JwtUtils.getUserIdFromToken(token, jwtSecret);
            String username = JwtUtils.getUsernameFromToken(token, jwtSecret);

            // 获取用户角色和权限
            List<String> roles = userService.getUserRoles(userId);
            List<String> permissions = userService.getUserPermissions(userId);

            // 设置用户上下文
            SecurityUtils.setCurrentUser(userId, username, null, roles, permissions);

            log.debug("用户认证成功 - UserId: {}, Username: {}", userId, username);

            // 继续执行过滤器链
            chain.doFilter(request, response);

        } catch (Exception e) {
            log.error("JWT认证过程中发生错误: {}", e.getMessage(), e);
            sendUnauthorizedResponse(httpResponse, "认证失败");
        } finally {
            // 清理用户上下文
            SecurityUtils.clearCurrentUser();
        }
    }

    /**
     * 检查是否应该跳过认证
     */
    private boolean shouldSkipAuthentication(String requestURI, String method) {
        // OPTIONS请求跳过认证
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // 检查排除路径
        return EXCLUDE_PATHS.stream().anyMatch(requestURI::startsWith);
    }

    /**
     * 从请求中提取JWT令牌
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(SecurityConstants.TOKEN_HEADER);
        return JwtUtils.extractTokenFromHeader(authHeader);
    }

    /**
     * 发送未授权响应
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
            "{\"code\":401,\"message\":\"%s\",\"timestamp\":%d}",
            message, System.currentTimeMillis()));
    }
}
