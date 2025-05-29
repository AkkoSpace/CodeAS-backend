package space.akko.platform.config.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import space.akko.foundation.common.BaseEntity;

/**
 * 系统配置实体
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "config_system", schema = "foundation_schema")
public class SystemConfig extends BaseEntity {

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置类型：STRING-字符串，NUMBER-数字，BOOLEAN-布尔，JSON-JSON
     */
    private String configType;

    /**
     * 配置分类
     */
    private String category;

    /**
     * 配置描述
     */
    private String description;

    /**
     * 是否加密
     */
    private Boolean isEncrypted;

    /**
     * 是否只读
     */
    private Boolean isReadonly;
}
