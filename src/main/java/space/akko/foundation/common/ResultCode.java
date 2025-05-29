package space.akko.foundation.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 * 
 * @author akko
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "资源冲突"),
    VALIDATION_ERROR(422, "参数验证失败"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    // 服务器错误 5xx
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    BAD_GATEWAY(502, "网关错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    GATEWAY_TIMEOUT(504, "网关超时"),

    // 业务错误 1xxx
    BUSINESS_ERROR(1000, "业务处理失败"),
    
    // 用户相关错误 11xx
    USER_NOT_FOUND(1101, "用户不存在"),
    USER_ALREADY_EXISTS(1102, "用户已存在"),
    USER_DISABLED(1103, "用户已被禁用"),
    USER_LOCKED(1104, "用户已被锁定"),
    INVALID_CREDENTIALS(1105, "用户名或密码错误"),
    PASSWORD_EXPIRED(1106, "密码已过期"),
    ACCOUNT_EXPIRED(1107, "账户已过期"),
    
    // 认证相关错误 12xx
    TOKEN_INVALID(1201, "令牌无效"),
    TOKEN_EXPIRED(1202, "令牌已过期"),
    TOKEN_REVOKED(1203, "令牌已被撤销"),
    LOGIN_FAILED(1204, "登录失败"),
    LOGOUT_FAILED(1205, "登出失败"),
    SESSION_EXPIRED(1206, "会话已过期"),
    
    // 权限相关错误 13xx
    PERMISSION_DENIED(1301, "权限不足"),
    ROLE_NOT_FOUND(1302, "角色不存在"),
    PERMISSION_NOT_FOUND(1303, "权限不存在"),
    ROLE_ALREADY_EXISTS(1304, "角色已存在"),
    
    // 数据相关错误 14xx
    DATA_NOT_FOUND(1401, "数据不存在"),
    DATA_ALREADY_EXISTS(1402, "数据已存在"),
    DATA_INTEGRITY_VIOLATION(1403, "数据完整性约束违反"),
    OPTIMISTIC_LOCK_FAILURE(1404, "数据已被其他用户修改"),
    
    // 文件相关错误 15xx
    FILE_NOT_FOUND(1501, "文件不存在"),
    FILE_UPLOAD_FAILED(1502, "文件上传失败"),
    FILE_TYPE_NOT_ALLOWED(1503, "文件类型不允许"),
    FILE_SIZE_EXCEEDED(1504, "文件大小超出限制"),
    
    // 配置相关错误 16xx
    CONFIG_NOT_FOUND(1601, "配置不存在"),
    CONFIG_READONLY(1602, "配置为只读"),
    FEATURE_DISABLED(1603, "功能已禁用"),
    
    // 缓存相关错误 17xx
    CACHE_ERROR(1701, "缓存操作失败"),
    CACHE_KEY_NOT_FOUND(1702, "缓存键不存在"),
    
    // 外部服务错误 18xx
    EXTERNAL_SERVICE_ERROR(1801, "外部服务调用失败"),
    EXTERNAL_SERVICE_TIMEOUT(1802, "外部服务调用超时"),
    
    // 系统相关错误 19xx
    SYSTEM_BUSY(1901, "系统繁忙，请稍后重试"),
    SYSTEM_MAINTENANCE(1902, "系统维护中"),
    RATE_LIMIT_EXCEEDED(1903, "访问频率超出限制");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态消息
     */
    private final String message;
}
