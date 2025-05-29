package space.akko.platform.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import space.akko.foundation.common.BaseEntity;

import java.time.LocalDateTime;

/**
 * 用户凭证实体
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "user_credential", schema = "platform_schema")
public class UserCredential extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 凭证类型：PASSWORD-密码，OAUTH-第三方登录，API_KEY-API密钥
     */
    private String credentialType;

    /**
     * 凭证值
     */
    private String credentialValue;

    /**
     * 盐值
     */
    private String salt;

    /**
     * 是否激活
     */
    private Boolean isActive;

    /**
     * 过期时间
     */
    private LocalDateTime expiresAt;
}
