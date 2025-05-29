package space.akko.platform.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 更新用户请求
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "更新用户请求")
public class UserUpdateRequest {

    @Schema(description = "邮箱", example = "admin@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;

    @Schema(description = "真实姓名", example = "张三")
    @Size(max = 100, message = "真实姓名长度不能超过100个字符")
    private String realName;

    @Schema(description = "昵称", example = "管理员")
    @Size(max = 100, message = "昵称长度不能超过100个字符")
    private String nickname;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    private String avatarUrl;

    @Schema(description = "性别", example = "MALE", allowableValues = {"MALE", "FEMALE", "OTHER"})
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "性别只能是MALE、FEMALE或OTHER")
    private String gender;

    @Schema(description = "出生日期", example = "1990-01-01")
    private LocalDate birthDate;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive;

    @Schema(description = "角色ID列表", example = "[1, 2]")
    private List<Long> roleIds;
}
