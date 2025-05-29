package space.akko.platform.user.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视图对象
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "用户视图对象")
public class UserVO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "应用唯一标识", example = "user_001")
    private String asid;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phoneNumber;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "昵称", example = "管理员")
    private String nickname;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "性别", example = "MALE")
    private String gender;

    @Schema(description = "性别显示名称", example = "男")
    private String genderName;

    @Schema(description = "出生日期", example = "1990-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive;

    @Schema(description = "状态显示名称", example = "正常")
    private String statusName;

    @Schema(description = "邮箱是否验证", example = "true")
    private Boolean isEmailVerified;

    @Schema(description = "手机是否验证", example = "false")
    private Boolean isPhoneVerified;

    @Schema(description = "最后登录时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginAt;

    @Schema(description = "最后登录IP", example = "192.168.1.1")
    private String lastLoginIp;

    @Schema(description = "用户角色列表")
    private List<RoleInfo> roles;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 角色信息
     */
    @Data
    @Schema(description = "角色信息")
    public static class RoleInfo {
        @Schema(description = "角色ID", example = "1")
        private Long id;

        @Schema(description = "角色编码", example = "ADMIN")
        private String roleCode;

        @Schema(description = "角色名称", example = "管理员")
        private String roleName;
    }
}
