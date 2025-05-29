package space.akko.platform.menu.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单视图对象
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "菜单视图对象")
public class MenuVO {

    @Schema(description = "菜单ID", example = "1")
    private Long id;

    @Schema(description = "菜单编码", example = "USER_MANAGEMENT")
    private String menuCode;

    @Schema(description = "菜单名称", example = "用户管理")
    private String menuName;

    @Schema(description = "菜单类型", example = "MENU")
    private String menuType;

    @Schema(description = "菜单类型显示名称", example = "菜单")
    private String menuTypeName;

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

    @Schema(description = "状态显示名称", example = "正常")
    private String statusName;

    @Schema(description = "子菜单列表")
    private List<MenuVO> children;

    @Schema(description = "权限信息列表")
    private List<PermissionInfo> permissions;

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

        @Schema(description = "资源编码", example = "USER_LIST")
        private String resourceCode;

        @Schema(description = "资源名称", example = "用户列表")
        private String resourceName;

        @Schema(description = "资源类型", example = "API")
        private String resourceType;
    }

    /**
     * 前端路由元信息
     */
    @Data
    @Schema(description = "路由元信息")
    public static class Meta {
        @Schema(description = "菜单标题", example = "用户管理")
        private String title;

        @Schema(description = "菜单图标", example = "user")
        private String icon;

        @Schema(description = "是否隐藏", example = "false")
        private Boolean hidden;

        @Schema(description = "是否缓存", example = "true")
        private Boolean keepAlive;

        @Schema(description = "是否固定标签", example = "false")
        private Boolean affix;
    }
}
