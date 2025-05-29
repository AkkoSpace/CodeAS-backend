package space.akko.foundation.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import space.akko.foundation.annotation.RequirePermission;
import space.akko.foundation.annotation.RequireRole;
import space.akko.foundation.common.ResultCode;
import space.akko.foundation.exception.SecurityException;
import space.akko.foundation.utils.SecurityUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 权限验证切面
 * 
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(1) // 确保在操作日志切面之前执行
public class PermissionAspect {

    /**
     * 权限验证
     */
    @Around("@annotation(space.akko.foundation.annotation.RequirePermission) || " +
            "@within(space.akko.foundation.annotation.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前用户
        SecurityUtils.UserContext userContext = SecurityUtils.getCurrentUser();
        if (userContext == null) {
            throw new SecurityException(ResultCode.UNAUTHORIZED, "用户未登录");
        }

        // 获取方法上的权限注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission methodAnnotation = method.getAnnotation(RequirePermission.class);
        
        // 如果方法上没有注解，检查类上的注解
        RequirePermission classAnnotation = null;
        if (methodAnnotation == null) {
            classAnnotation = method.getDeclaringClass().getAnnotation(RequirePermission.class);
        }
        
        RequirePermission annotation = methodAnnotation != null ? methodAnnotation : classAnnotation;
        
        if (annotation != null) {
            String[] requiredPermissions = annotation.value();
            RequirePermission.Mode mode = annotation.mode();
            
            if (requiredPermissions.length > 0) {
                List<String> userPermissions = userContext.getPermissions();
                
                boolean hasPermission = switch (mode) {
                    case ALL -> Arrays.stream(requiredPermissions)
                            .allMatch(userPermissions::contains);
                    case ANY -> Arrays.stream(requiredPermissions)
                            .anyMatch(userPermissions::contains);
                };
                
                if (!hasPermission) {
                    log.warn("用户 {} 权限不足，需要权限: {}, 拥有权限: {}", 
                            userContext.getUsername(), 
                            Arrays.toString(requiredPermissions), 
                            userPermissions);
                    throw new SecurityException(ResultCode.PERMISSION_DENIED, "权限不足");
                }
            }
        }
        
        return joinPoint.proceed();
    }

    /**
     * 角色验证
     */
    @Around("@annotation(space.akko.foundation.annotation.RequireRole) || " +
            "@within(space.akko.foundation.annotation.RequireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前用户
        SecurityUtils.UserContext userContext = SecurityUtils.getCurrentUser();
        if (userContext == null) {
            throw new SecurityException(ResultCode.UNAUTHORIZED, "用户未登录");
        }

        // 获取方法上的角色注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireRole methodAnnotation = method.getAnnotation(RequireRole.class);
        
        // 如果方法上没有注解，检查类上的注解
        RequireRole classAnnotation = null;
        if (methodAnnotation == null) {
            classAnnotation = method.getDeclaringClass().getAnnotation(RequireRole.class);
        }
        
        RequireRole annotation = methodAnnotation != null ? methodAnnotation : classAnnotation;
        
        if (annotation != null) {
            String[] requiredRoles = annotation.value();
            RequireRole.Mode mode = annotation.mode();
            
            if (requiredRoles.length > 0) {
                List<String> userRoles = userContext.getRoles();
                
                boolean hasRole = switch (mode) {
                    case ALL -> Arrays.stream(requiredRoles)
                            .allMatch(userRoles::contains);
                    case ANY -> Arrays.stream(requiredRoles)
                            .anyMatch(userRoles::contains);
                };
                
                if (!hasRole) {
                    log.warn("用户 {} 角色不足，需要角色: {}, 拥有角色: {}", 
                            userContext.getUsername(), 
                            Arrays.toString(requiredRoles), 
                            userRoles);
                    throw new SecurityException(ResultCode.PERMISSION_DENIED, "角色权限不足");
                }
            }
        }
        
        return joinPoint.proceed();
    }
}
