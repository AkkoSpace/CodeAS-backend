package space.akko.platform.role.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色视图对象
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "角色视图对象")
public class RoleVO {

    @Schema(description = "角色ID", example = "1")
    private Long id;

    @Schema(description = "角色编码", example = "ADMIN")
    private String roleCode;

    @Schema(description = "角色名称", example = "管理员")
    private String roleName;

    @Schema(description = "角色描述", example = "系统管理员角色")
    private String description;

    @Schema(description = "角色级别", example = "1")
    private Integer roleLevel;

    @Schema(description = "父角色ID", example = "0")
    private Long parentId;

    @Schema(description = "父角色名称", example = "超级管理员")
    private String parentRoleName;

    @Schema(description = "是否系统角色", example = "true")
    private Boolean isSystem;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive;

    @Schema(description = "状态显示名称", example = "正常")
    private String statusName;

    @Schema(description = "子角色列表")
    private List<RoleVO> children;

    @Schema(description = "权限信息列表")
    private List<PermissionInfo> permissions;

    @Schema(description = "用户数量", example = "10")
    private Long userCount;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 权限信息
     */
    @Data
    @Schema(description = "权限信息")
    public static class PermissionInfo {
        @Schema(description = "权限ID", example = "1")
        private Long id;

        @Schema(description = "资源编码", example = "USER_MANAGEMENT")
        private String resourceCode;

        @Schema(description = "资源名称", example = "用户管理")
        private String resourceName;

        @Schema(description = "动作编码", example = "READ")
        private String actionCode;

        @Schema(description = "动作名称", example = "查看")
        private String actionName;

        @Schema(description = "是否授权", example = "true")
        private Boolean isGranted;
    }
}
