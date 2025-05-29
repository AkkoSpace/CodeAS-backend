package space.akko.platform.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import space.akko.foundation.common.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户基本信息实体
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "user_profile", schema = "platform_schema")
public class UserProfile extends BaseEntity {

    /**
     * 应用唯一标识
     */
    private String asid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 性别：MALE-男，FEMALE-女，OTHER-其他
     */
    private String gender;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 是否激活
     */
    private Boolean isActive;

    /**
     * 邮箱是否验证
     */
    private Boolean isEmailVerified;

    /**
     * 手机是否验证
     */
    private Boolean isPhoneVerified;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginAt;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;
}
