package space.akko.foundation.utils;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import space.akko.foundation.constant.SecurityConstants;

import java.util.List;

/**
 * 安全工具类
 * 
 * @author akko
 * @since 1.0.0
 */
@Slf4j
public final class SecurityUtils {

    private SecurityUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 当前用户信息线程变量
     */
    private static final ThreadLocal<UserContext> USER_CONTEXT = new ThreadLocal<>();

    /**
     * 用户上下文
     */
    public static class UserContext {
        private Long userId;
        private String username;
        private String asid;
        private List<String> roles;
        private List<String> permissions;

        public UserContext(Long userId, String username, String asid, 
                          List<String> roles, List<String> permissions) {
            this.userId = userId;
            this.username = username;
            this.asid = asid;
            this.roles = roles;
            this.permissions = permissions;
        }

        // Getters
        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getAsid() { return asid; }
        public List<String> getRoles() { return roles; }
        public List<String> getPermissions() { return permissions; }
    }

    /**
     * 设置当前用户信息
     */
    public static void setCurrentUser(Long userId, String username, String asid,
                                    List<String> roles, List<String> permissions) {
        USER_CONTEXT.set(new UserContext(userId, username, asid, roles, permissions));
    }

    /**
     * 获取当前用户信息
     */
    public static UserContext getCurrentUser() {
        return USER_CONTEXT.get();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        UserContext context = getCurrentUser();
        return context != null ? context.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        UserContext context = getCurrentUser();
        return context != null ? context.getUsername() : null;
    }

    /**
     * 获取当前用户ASID
     */
    public static String getCurrentUserAsid() {
        UserContext context = getCurrentUser();
        return context != null ? context.getAsid() : null;
    }

    /**
     * 获取当前用户角色
     */
    public static List<String> getCurrentUserRoles() {
        UserContext context = getCurrentUser();
        return context != null ? context.getRoles() : List.of();
    }

    /**
     * 获取当前用户权限
     */
    public static List<String> getCurrentUserPermissions() {
        UserContext context = getCurrentUser();
        return context != null ? context.getPermissions() : List.of();
    }

    /**
     * 检查当前用户是否有指定角色
     */
    public static boolean hasRole(String role) {
        List<String> roles = getCurrentUserRoles();
        return roles.contains(role);
    }

    /**
     * 检查当前用户是否有任意一个指定角色
     */
    public static boolean hasAnyRole(String... roles) {
        List<String> userRoles = getCurrentUserRoles();
        for (String role : roles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前用户是否有指定权限
     */
    public static boolean hasPermission(String permission) {
        List<String> permissions = getCurrentUserPermissions();
        return permissions.contains(permission);
    }

    /**
     * 检查当前用户是否有任意一个指定权限
     */
    public static boolean hasAnyPermission(String... permissions) {
        List<String> userPermissions = getCurrentUserPermissions();
        for (String permission : permissions) {
            if (userPermissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前用户是否为超级管理员
     */
    public static boolean isSuperAdmin() {
        return hasRole(SecurityConstants.ROLE_PREFIX + "SUPER_ADMIN");
    }

    /**
     * 检查当前用户是否为系统管理员
     */
    public static boolean isSystemAdmin() {
        return hasRole(SecurityConstants.ROLE_PREFIX + "SYSTEM_ADMIN");
    }

    /**
     * 清除当前用户信息
     */
    public static void clearCurrentUser() {
        USER_CONTEXT.remove();
    }

    /**
     * 获取客户端IP地址
     */
    public static String getClientIpAddress() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return "unknown";
        }

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StrUtil.isNotBlank(xForwardedFor) && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (StrUtil.isNotBlank(xRealIp) && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        String proxyClientIp = request.getHeader("Proxy-Client-IP");
        if (StrUtil.isNotBlank(proxyClientIp) && !"unknown".equalsIgnoreCase(proxyClientIp)) {
            return proxyClientIp;
        }

        String wlProxyClientIp = request.getHeader("WL-Proxy-Client-IP");
        if (StrUtil.isNotBlank(wlProxyClientIp) && !"unknown".equalsIgnoreCase(wlProxyClientIp)) {
            return wlProxyClientIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * 获取用户代理
     */
    public static String getUserAgent() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? request.getHeader("User-Agent") : "unknown";
    }

    /**
     * 获取当前请求
     */
    public static HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest();
        } catch (Exception e) {
            log.debug("获取当前请求失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从请求头中获取令牌
     */
    public static String getTokenFromRequest() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }

        String authHeader = request.getHeader(SecurityConstants.TOKEN_HEADER);
        return JwtUtils.extractTokenFromHeader(authHeader);
    }

    /**
     * 检查是否为匿名用户
     */
    public static boolean isAnonymous() {
        return getCurrentUser() == null;
    }

    /**
     * 检查是否为认证用户
     */
    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }
}
