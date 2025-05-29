package space.akko.platform.audit.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计日志视图对象
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "审计日志视图对象")
public class AuditLogVO {

    @Schema(description = "日志ID", example = "1")
    private Long id;

    @Schema(description = "追踪ID", example = "trace_123456")
    private String traceId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "真实姓名", example = "管理员")
    private String realName;

    @Schema(description = "操作类型", example = "CREATE")
    private String operationType;

    @Schema(description = "操作类型显示名称", example = "创建")
    private String operationTypeName;

    @Schema(description = "操作名称", example = "创建用户")
    private String operationName;

    @Schema(description = "资源类型", example = "USER")
    private String resourceType;

    @Schema(description = "资源类型显示名称", example = "用户")
    private String resourceTypeName;

    @Schema(description = "资源ID", example = "123")
    private String resourceId;

    @Schema(description = "请求方法", example = "POST")
    private String requestMethod;

    @Schema(description = "请求URL", example = "/api/users")
    private String requestUrl;

    @Schema(description = "请求参数", example = "page=1&size=10")
    private String requestParams;

    @Schema(description = "请求体", example = "{\"username\":\"test\"}")
    private String requestBody;

    @Schema(description = "响应体", example = "{\"code\":200}")
    private String responseBody;

    @Schema(description = "IP地址", example = "192.168.1.1")
    private String ipAddress;

    @Schema(description = "IP地址归属地", example = "北京市")
    private String ipLocation;

    @Schema(description = "用户代理", example = "Mozilla/5.0...")
    private String userAgent;

    @Schema(description = "浏览器信息", example = "Chrome 120.0")
    private String browserInfo;

    @Schema(description = "操作系统", example = "Windows 10")
    private String osInfo;

    @Schema(description = "执行时间（毫秒）", example = "150")
    private Long executionTime;

    @Schema(description = "执行时间显示", example = "150ms")
    private String executionTimeDisplay;

    @Schema(description = "是否成功", example = "true")
    private Boolean isSuccess;

    @Schema(description = "状态显示名称", example = "成功")
    private String statusName;

    @Schema(description = "错误信息", example = "参数验证失败")
    private String errorMessage;

    @Schema(description = "操作时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
