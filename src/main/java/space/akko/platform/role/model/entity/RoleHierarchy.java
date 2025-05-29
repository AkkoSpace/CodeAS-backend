package space.akko.platform.role.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import space.akko.foundation.common.BaseEntity;

/**
 * 角色层级关系实体
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "role_hierarchy", schema = "platform_schema")
public class RoleHierarchy extends BaseEntity {

    /**
     * 父角色ID
     */
    private Long parentRoleId;

    /**
     * 子角色ID
     */
    private Long childRoleId;

    /**
     * 层级级别
     */
    private Integer hierarchyLevel;
}
