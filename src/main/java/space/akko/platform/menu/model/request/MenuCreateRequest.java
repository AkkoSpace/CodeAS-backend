package space.akko.platform.menu.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建菜单请求
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "创建菜单请求")
public class MenuCreateRequest {

    @Schema(description = "菜单编码", example = "USER_MANAGEMENT", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "菜单编码不能为空")
    @Size(min = 2, max = 100, message = "菜单编码长度必须在2-100个字符之间")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "菜单编码只能包含大写字母、数字和下划线，且必须以大写字母开头")
    private String menuCode;

    @Schema(description = "菜单名称", example = "用户管理", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 2, max = 100, message = "菜单名称长度必须在2-100个字符之间")
    private String menuName;

    @Schema(description = "菜单类型", example = "MENU", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "菜单类型不能为空")
    @Pattern(regexp = "^(DIRECTORY|MENU|BUTTON)$", message = "菜单类型只能是DIRECTORY、MENU或BUTTON")
    private String menuType;

    @Schema(description = "父菜单ID", example = "1")
    private Long parentId;

    @Schema(description = "菜单路径", example = "/system/user")
    @Size(max = 200, message = "菜单路径长度不能超过200个字符")
    private String menuPath;

    @Schema(description = "组件路径", example = "system/user/index")
    @Size(max = 200, message = "组件路径长度不能超过200个字符")
    private String componentPath;

    @Schema(description = "图标", example = "user")
    @Size(max = 100, message = "图标长度不能超过100个字符")
    private String icon;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder = 0;

    @Schema(description = "是否可见", example = "true")
    private Boolean isVisible = true;

    @Schema(description = "是否缓存", example = "true")
    private Boolean isCache = false;

    @Schema(description = "是否外链", example = "false")
    private Boolean isExternal = false;

    @Schema(description = "菜单描述", example = "用户管理菜单")
    @Size(max = 500, message = "菜单描述长度不能超过500个字符")
    private String description;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive = true;

    @Schema(description = "权限ID列表", example = "[1, 2, 3]")
    private List<Long> permissionIds;
}
