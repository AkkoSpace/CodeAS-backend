package space.akko.platform.role.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import space.akko.foundation.common.BaseEntity;

import java.time.LocalDateTime;

/**
 * 用户角色关联实体
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "user_role_mapping", schema = "platform_schema")
public class UserRoleMapping extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 生效开始时间
     */
    private LocalDateTime effectiveFrom;

    /**
     * 生效结束时间
     */
    private LocalDateTime effectiveTo;

    /**
     * 是否激活
     */
    private Boolean isActive;
}
