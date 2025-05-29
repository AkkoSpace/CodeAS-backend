package space.akko.platform.role.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色查询请求
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "角色查询请求")
public class RoleQueryRequest {

    @Schema(description = "角色编码", example = "ADMIN")
    private String roleCode;

    @Schema(description = "角色名称", example = "管理员")
    private String roleName;

    @Schema(description = "角色级别", example = "1")
    private Integer roleLevel;

    @Schema(description = "父角色ID", example = "1")
    private Long parentId;

    @Schema(description = "是否系统角色", example = "true")
    private Boolean isSystem;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive;

    @Schema(description = "创建时间开始", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAtStart;

    @Schema(description = "创建时间结束", example = "2024-12-31T23:59:59")
    private LocalDateTime createdAtEnd;

    @Schema(description = "关键词搜索（角色编码、角色名称）", example = "admin")
    private String keyword;

    @Schema(description = "是否包含子角色", example = "false")
    private Boolean includeChildren = false;

    @Schema(description = "页码", example = "1")
    private Long current = 1L;

    @Schema(description = "每页大小", example = "10")
    private Long size = 10L;

    @Schema(description = "排序字段", example = "role_level")
    private String sortField = "role_level";

    @Schema(description = "排序方向", example = "asc", allowableValues = {"asc", "desc"})
    private String sortOrder = "asc";
}
