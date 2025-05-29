package space.akko.platform.permission.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import space.akko.foundation.common.BaseEntity;

/**
 * 权限资源实体
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "permission_resource", schema = "platform_schema")
public class PermissionResource extends BaseEntity {

    /**
     * 资源编码
     */
    private String resourceCode;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源类型：API-接口，MENU-菜单，BUTTON-按钮，DATA-数据
     */
    private String resourceType;

    /**
     * 资源URL
     */
    private String resourceUrl;

    /**
     * HTTP方法
     */
    private String httpMethod;

    /**
     * 父资源ID
     */
    private Long parentId;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 资源描述
     */
    private String description;

    /**
     * 是否激活
     */
    private Boolean isActive;
}
