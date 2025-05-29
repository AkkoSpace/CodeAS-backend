package space.akko.platform.permission.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import space.akko.foundation.common.BaseEntity;

/**
 * 角色权限关联实体
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "role_permission_mapping", schema = "platform_schema")
public class RolePermissionMapping extends BaseEntity {

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 动作ID
     */
    private Long actionId;

    /**
     * 是否授权
     */
    private Boolean isGranted;
}
