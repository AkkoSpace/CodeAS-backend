package space.akko.platform.role.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建角色请求
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "创建角色请求")
public class RoleCreateRequest {

    @Schema(description = "角色编码", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色编码不能为空")
    @Size(min = 2, max = 50, message = "角色编码长度必须在2-50个字符之间")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "角色编码只能包含大写字母、数字和下划线，且必须以大写字母开头")
    private String roleCode;

    @Schema(description = "角色名称", example = "管理员", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 100, message = "角色名称长度必须在2-100个字符之间")
    private String roleName;

    @Schema(description = "角色描述", example = "系统管理员角色")
    @Size(max = 500, message = "角色描述长度不能超过500个字符")
    private String description;

    @Schema(description = "角色级别", example = "1")
    private Integer roleLevel = 0;

    @Schema(description = "父角色ID", example = "1")
    private Long parentId;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive = true;

    @Schema(description = "权限ID列表", example = "[1, 2, 3]")
    private List<Long> permissionIds;
}
