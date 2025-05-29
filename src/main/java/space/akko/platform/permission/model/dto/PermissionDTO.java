package space.akko.platform.permission.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限数据传输对象
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "权限数据传输对象")
public class PermissionDTO {

    @Schema(description = "权限ID", example = "1")
    private Long id;

    @Schema(description = "资源编码", example = "USER_MANAGEMENT")
    private String resourceCode;

    @Schema(description = "资源名称", example = "用户管理")
    private String resourceName;

    @Schema(description = "资源类型", example = "API")
    private String resourceType;

    @Schema(description = "资源URL", example = "/api/users")
    private String resourceUrl;

    @Schema(description = "HTTP方法", example = "GET")
    private String httpMethod;

    @Schema(description = "父资源ID", example = "0")
    private Long parentId;

    @Schema(description = "父资源名称", example = "系统管理")
    private String parentResourceName;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "资源描述", example = "用户管理相关接口")
    private String description;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive;

    @Schema(description = "子资源列表")
    private List<PermissionDTO> children;

    @Schema(description = "可用动作列表")
    private List<ActionInfo> actions;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedAt;

    /**
     * 动作信息
     */
    @Data
    @Schema(description = "动作信息")
    public static class ActionInfo {
        @Schema(description = "动作ID", example = "1")
        private Long id;

        @Schema(description = "动作编码", example = "READ")
        private String actionCode;

        @Schema(description = "动作名称", example = "查看")
        private String actionName;

        @Schema(description = "动作描述", example = "查看权限")
        private String description;

        @Schema(description = "是否激活", example = "true")
        private Boolean isActive;
    }
}
