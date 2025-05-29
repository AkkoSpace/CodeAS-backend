package space.akko.platform.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户查询请求
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "用户查询请求")
public class UserQueryRequest {

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phoneNumber;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "性别", example = "MALE", allowableValues = {"MALE", "FEMALE", "OTHER"})
    private String gender;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive;

    @Schema(description = "邮箱是否验证", example = "true")
    private Boolean isEmailVerified;

    @Schema(description = "手机是否验证", example = "false")
    private Boolean isPhoneVerified;

    @Schema(description = "角色ID", example = "1")
    private Long roleId;

    @Schema(description = "创建时间开始", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAtStart;

    @Schema(description = "创建时间结束", example = "2024-12-31T23:59:59")
    private LocalDateTime createdAtEnd;

    @Schema(description = "关键词搜索（用户名、邮箱、真实姓名）", example = "admin")
    private String keyword;

    @Schema(description = "页码", example = "1")
    private Long current = 1L;

    @Schema(description = "每页大小", example = "10")
    private Long size = 10L;

    @Schema(description = "排序字段", example = "created_at")
    private String sortField = "created_at";

    @Schema(description = "排序方向", example = "desc", allowableValues = {"asc", "desc"})
    private String sortOrder = "desc";
}
