package space.akko.platform.menu.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单数据传输对象
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "菜单数据传输对象")
public class MenuDTO {

    @Schema(description = "菜单ID", example = "1")
    private Long id;

    @Schema(description = "菜单编码", example = "USER_MANAGEMENT")
    private String menuCode;

    @Schema(description = "菜单名称", example = "用户管理")
    private String menuName;

    @Schema(description = "菜单类型", example = "MENU")
    private String menuType;

    @Schema(description = "父菜单ID", example = "0")
    private Long parentId;

    @Schema(description = "父菜单名称", example = "系统管理")
    private String parentMenuName;

    @Schema(description = "菜单路径", example = "/system/user")
    private String menuPath;

    @Schema(description = "组件路径", example = "system/user/index")
    private String componentPath;

    @Schema(description = "图标", example = "user")
    private String icon;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否可见", example = "true")
    private Boolean isVisible;

    @Schema(description = "是否缓存", example = "true")
    private Boolean isCache;

    @Schema(description = "是否外链", example = "false")
    private Boolean isExternal;

    @Schema(description = "菜单描述", example = "用户管理菜单")
    private String description;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive;

    @Schema(description = "子菜单列表")
    private List<MenuDTO> children;

    @Schema(description = "权限列表")
    private List<String> permissions;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedAt;
}
