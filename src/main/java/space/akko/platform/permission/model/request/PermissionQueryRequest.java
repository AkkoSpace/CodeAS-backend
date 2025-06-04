package space.akko.platform.permission.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限查询请求
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "权限查询请求")
public class PermissionQueryRequest {

    @Schema(description = "资源编码", example = "USER_MANAGEMENT")
    private String resourceCode;

    @Schema(description = "资源名称", example = "用户管理")
    private String resourceName;

    @Schema(description = "资源类型", example = "API", allowableValues = {"API", "MENU", "BUTTON", "DATA"})
    private String resourceType;

    @Schema(description = "资源URL", example = "/api/users")
    private String resourceUrl;

    @Schema(description = "HTTP方法", example = "GET", allowableValues = {"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS", "ALL"})
    private String httpMethod;

    @Schema(description = "父资源ID", example = "1")
    private Long parentId;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive;

    @Schema(description = "创建时间开始", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAtStart;

    @Schema(description = "创建时间结束", example = "2024-12-31T23:59:59")
    private LocalDateTime createdAtEnd;

    @Schema(description = "关键词搜索（资源编码、资源名称、描述）", example = "用户")
    private String keyword;

    @Schema(description = "是否包含子权限", example = "false")
    private Boolean includeChildren = false;

    @Schema(description = "页码", example = "1")
    private Long current = 1L;

    @Schema(description = "每页大小", example = "10")
    private Long size = 10L;

    @Schema(description = "排序字段", example = "sort_order")
    private String sortField = "sort_order";

    @Schema(description = "排序方向", example = "asc", allowableValues = {"asc", "desc"})
    private String sortOrder = "asc";
}