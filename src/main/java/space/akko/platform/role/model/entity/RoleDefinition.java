package space.akko.platform.role.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import space.akko.foundation.common.BaseEntity;

/**
 * 角色定义实体
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "role_definition", schema = "platform_schema")
public class RoleDefinition extends BaseEntity {

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 角色级别
     */
    private Integer roleLevel;

    /**
     * 父角色ID
     */
    private Long parentId;

    /**
     * 是否系统角色
     */
    private Boolean isSystem;

    /**
     * 是否激活
     */
    private Boolean isActive;
}
