package space.akko.platform.audit.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计日志查询请求
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "审计日志查询请求")
public class AuditLogQueryRequest {

    @Schema(description = "追踪ID", example = "trace_123456")
    private String traceId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "操作类型", example = "CREATE")
    private String operationType;

    @Schema(description = "资源类型", example = "USER")
    private String resourceType;

    @Schema(description = "资源ID", example = "123")
    private String resourceId;

    @Schema(description = "请求方法", example = "POST")
    private String requestMethod;

    @Schema(description = "请求URL", example = "/api/users")
    private String requestUrl;

    @Schema(description = "IP地址", example = "192.168.1.1")
    private String ipAddress;

    @Schema(description = "是否成功", example = "true")
    private Boolean isSuccess;

    @Schema(description = "操作时间开始", example = "2024-01-01T00:00:00")
    private LocalDateTime operationTimeStart;

    @Schema(description = "操作时间结束", example = "2024-12-31T23:59:59")
    private LocalDateTime operationTimeEnd;

    @Schema(description = "执行时间最小值（毫秒）", example = "100")
    private Long executionTimeMin;

    @Schema(description = "执行时间最大值（毫秒）", example = "5000")
    private Long executionTimeMax;

    @Schema(description = "关键词搜索（操作名称、资源ID）", example = "用户")
    private String keyword;

    @Schema(description = "页码", example = "1")
    private Long current = 1L;

    @Schema(description = "每页大小", example = "10")
    private Long size = 10L;

    @Schema(description = "排序字段", example = "operation_time")
    private String sortField = "operation_time";

    @Schema(description = "排序方向", example = "desc", allowableValues = {"asc", "desc"})
    private String sortOrder = "desc";
}
