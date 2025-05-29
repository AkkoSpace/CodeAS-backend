package space.akko.platform.permission.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建权限请求
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "创建权限请求")
public class PermissionCreateRequest {

    @Schema(description = "资源编码", example = "USER_MANAGEMENT", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "资源编码不能为空")
    @Size(min = 2, max = 100, message = "资源编码长度必须在2-100个字符之间")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "资源编码只能包含大写字母、数字和下划线，且必须以大写字母开头")
    private String resourceCode;

    @Schema(description = "资源名称", example = "用户管理", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "资源名称不能为空")
    @Size(min = 2, max = 100, message = "资源名称长度必须在2-100个字符之间")
    private String resourceName;

    @Schema(description = "资源类型", example = "API", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "资源类型不能为空")
    @Pattern(regexp = "^(API|MENU|BUTTON|DATA)$", message = "资源类型只能是API、MENU、BUTTON或DATA")
    private String resourceType;

    @Schema(description = "资源URL", example = "/api/users")
    @Size(max = 500, message = "资源URL长度不能超过500个字符")
    private String resourceUrl;

    @Schema(description = "HTTP方法", example = "GET")
    @Pattern(regexp = "^(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS|ALL)$", 
             message = "HTTP方法只能是GET、POST、PUT、DELETE、PATCH、HEAD、OPTIONS或ALL")
    private String httpMethod;

    @Schema(description = "父资源ID", example = "1")
    private Long parentId;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder = 0;

    @Schema(description = "资源描述", example = "用户管理相关接口")
    @Size(max = 500, message = "资源描述长度不能超过500个字符")
    private String description;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive = true;
}
