package space.akko.platform.permission.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import space.akko.foundation.common.BaseEntity;

/**
 * 权限动作实体
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "permission_action", schema = "platform_schema")
public class PermissionAction extends BaseEntity {

    /**
     * 动作编码
     */
    private String actionCode;

    /**
     * 动作名称
     */
    private String actionName;

    /**
     * 动作描述
     */
    private String description;

    /**
     * 是否激活
     */
    private Boolean isActive;
}
