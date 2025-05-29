package space.akko.foundation.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import space.akko.foundation.annotation.OperationLog;
import space.akko.foundation.utils.SecurityUtils;
import space.akko.foundation.utils.TraceUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 操作日志切面
 *
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    @Around("@annotation(space.akko.foundation.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);

        // 获取请求信息
        HttpServletRequest request = getCurrentRequest();

        // 构建日志信息
        OperationLogInfo logInfo = buildLogInfo(joinPoint, operationLog, request, startTime);

        Object result = null;
        Exception exception = null;

        try {
            // 执行目标方法
            result = joinPoint.proceed();
            logInfo.setIsSuccess(true);

            // 记录响应体
            if (operationLog.includeResponseBody() && result != null) {
                logInfo.setResponseBody(JSONUtil.toJsonStr(result));
            }

        } catch (Exception e) {
            exception = e;
            logInfo.setIsSuccess(false);
            logInfo.setErrorMessage(e.getMessage());
            throw e;
        } finally {
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            logInfo.setExecutionTime(executionTime);
            logInfo.setOperationTime(LocalDateTime.now());

            // 记录日志
            if (operationLog.async()) {
                recordLogAsync(logInfo);
            } else {
                recordLog(logInfo);
            }
        }

        return result;
    }

    /**
     * 构建日志信息
     */
    private OperationLogInfo buildLogInfo(ProceedingJoinPoint joinPoint, OperationLog operationLog,
                                        HttpServletRequest request, long startTime) {
        OperationLogInfo logInfo = new OperationLogInfo();

        // 设置追踪ID
        logInfo.setTraceId(TraceUtils.getTraceId());

        // 设置用户信息
        try {
            SecurityUtils.UserContext userContext = SecurityUtils.getCurrentUser();
            if (userContext != null) {
                logInfo.setUserId(userContext.getUserId());
                logInfo.setUsername(userContext.getUsername());
            }
        } catch (Exception e) {
            log.debug("获取当前用户信息失败: {}", e.getMessage());
        }

        // 设置操作信息
        logInfo.setOperationType(StrUtil.isNotBlank(operationLog.operationType()) ?
                                operationLog.operationType() : "UNKNOWN");
        logInfo.setOperationName(StrUtil.isNotBlank(operationLog.operationName()) ?
                                operationLog.operationName() : joinPoint.getSignature().getName());
        logInfo.setResourceType(operationLog.resourceType());

        // 设置请求信息
        if (request != null) {
            logInfo.setRequestMethod(request.getMethod());
            logInfo.setRequestUrl(request.getRequestURL().toString());
            logInfo.setIpAddress(SecurityUtils.getClientIpAddress());
            logInfo.setUserAgent(SecurityUtils.getUserAgent());

            // 记录请求参数
            if (operationLog.includeRequestParams()) {
                logInfo.setRequestParams(getRequestParams(request));
            }
        }

        // 记录请求体
        if (operationLog.includeRequestBody()) {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                // 过滤敏感参数
                Object[] filteredArgs = filterSensitiveArgs(args);
                logInfo.setRequestBody(JSONUtil.toJsonStr(filteredArgs));
            }
        }

        return logInfo;
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(HttpServletRequest request) {
        try {
            return request.getParameterMap().entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + Arrays.toString(entry.getValue()))
                    .collect(Collectors.joining("&"));
        } catch (Exception e) {
            log.warn("获取请求参数失败: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 过滤敏感参数
     */
    private Object[] filterSensitiveArgs(Object[] args) {
        // TODO: 实现敏感参数过滤逻辑，如密码等
        return args;
    }

    /**
     * 获取当前请求
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 异步记录日志
     */
    @Async
    public void recordLogAsync(OperationLogInfo logInfo) {
        recordLog(logInfo);
    }

    /**
     * 记录日志
     */
    private void recordLog(OperationLogInfo logInfo) {
        try {
            // 转换为实体对象并保存到数据库
            space.akko.platform.audit.model.entity.AuditOperationLog auditLog =
                new space.akko.platform.audit.model.entity.AuditOperationLog();

            auditLog.setTraceId(logInfo.getTraceId());
            auditLog.setUserId(logInfo.getUserId());
            auditLog.setUsername(logInfo.getUsername());
            auditLog.setOperationType(logInfo.getOperationType());
            auditLog.setOperationName(logInfo.getOperationName());
            auditLog.setResourceType(logInfo.getResourceType());
            auditLog.setResourceId(logInfo.getResourceId());
            auditLog.setRequestMethod(logInfo.getRequestMethod());
            auditLog.setRequestUrl(logInfo.getRequestUrl());
            auditLog.setRequestParams(logInfo.getRequestParams());
            auditLog.setRequestBody(logInfo.getRequestBody());
            auditLog.setResponseBody(logInfo.getResponseBody());
            auditLog.setIpAddress(logInfo.getIpAddress());
            auditLog.setUserAgent(logInfo.getUserAgent());
            auditLog.setExecutionTime(logInfo.getExecutionTime());
            auditLog.setIsSuccess(logInfo.getIsSuccess());
            auditLog.setErrorMessage(logInfo.getErrorMessage());
            auditLog.setOperationTime(logInfo.getOperationTime());

            // 通过Spring上下文获取AuditLogService并保存
            try {
                space.akko.platform.audit.service.AuditLogService auditLogService =
                    space.akko.foundation.utils.ApplicationContextHolder.getApplicationContext()
                        .getBean(space.akko.platform.audit.service.AuditLogService.class);
                auditLogService.recordOperationLog(auditLog);
            } catch (Exception e) {
                // 如果服务不可用，则只记录到日志文件
                log.info("操作日志: {}", JSONUtil.toJsonStr(logInfo));
            }
        } catch (Exception e) {
            log.error("记录操作日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 操作日志信息
     */
    public static class OperationLogInfo {
        private String traceId;
        private Long userId;
        private String username;
        private String operationType;
        private String operationName;
        private String resourceType;
        private String resourceId;
        private String requestMethod;
        private String requestUrl;
        private String requestParams;
        private String requestBody;
        private String responseBody;
        private String ipAddress;
        private String userAgent;
        private Long executionTime;
        private Boolean isSuccess;
        private String errorMessage;
        private LocalDateTime operationTime;

        // Getters and Setters
        public String getTraceId() { return traceId; }
        public void setTraceId(String traceId) { this.traceId = traceId; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getOperationType() { return operationType; }
        public void setOperationType(String operationType) { this.operationType = operationType; }

        public String getOperationName() { return operationName; }
        public void setOperationName(String operationName) { this.operationName = operationName; }

        public String getResourceType() { return resourceType; }
        public void setResourceType(String resourceType) { this.resourceType = resourceType; }

        public String getResourceId() { return resourceId; }
        public void setResourceId(String resourceId) { this.resourceId = resourceId; }

        public String getRequestMethod() { return requestMethod; }
        public void setRequestMethod(String requestMethod) { this.requestMethod = requestMethod; }

        public String getRequestUrl() { return requestUrl; }
        public void setRequestUrl(String requestUrl) { this.requestUrl = requestUrl; }

        public String getRequestParams() { return requestParams; }
        public void setRequestParams(String requestParams) { this.requestParams = requestParams; }

        public String getRequestBody() { return requestBody; }
        public void setRequestBody(String requestBody) { this.requestBody = requestBody; }

        public String getResponseBody() { return responseBody; }
        public void setResponseBody(String responseBody) { this.responseBody = responseBody; }

        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

        public Long getExecutionTime() { return executionTime; }
        public void setExecutionTime(Long executionTime) { this.executionTime = executionTime; }

        public Boolean getIsSuccess() { return isSuccess; }
        public void setIsSuccess(Boolean isSuccess) { this.isSuccess = isSuccess; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        public LocalDateTime getOperationTime() { return operationTime; }
        public void setOperationTime(LocalDateTime operationTime) { this.operationTime = operationTime; }
    }
}
