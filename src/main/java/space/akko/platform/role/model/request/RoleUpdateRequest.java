package space.akko.platform.role.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 更新角色请求
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "更新角色请求")
public class RoleUpdateRequest {

    @Schema(description = "角色名称", example = "管理员")
    @Size(min = 2, max = 100, message = "角色名称长度必须在2-100个字符之间")
    private String roleName;

    @Schema(description = "角色描述", example = "系统管理员角色")
    @Size(max = 500, message = "角色描述长度不能超过500个字符")
    private String description;

    @Schema(description = "角色级别", example = "1")
    private Integer roleLevel;

    @Schema(description = "父角色ID", example = "1")
    private Long parentId;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive;

    @Schema(description = "权限ID列表", example = "[1, 2, 3]")
    private List<Long> permissionIds;
}
