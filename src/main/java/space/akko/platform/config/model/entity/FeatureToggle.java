package space.akko.platform.config.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import space.akko.foundation.common.BaseEntity;

/**
 * 功能开关实体
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "config_feature_toggle", schema = "foundation_schema")
public class FeatureToggle extends BaseEntity {

    /**
     * 功能键
     */
    private String featureKey;

    /**
     * 功能名称
     */
    private String featureName;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 功能描述
     */
    private String description;

    /**
     * 适用环境：ALL-所有，DEV-开发，TEST-测试，PROD-生产
     */
    private String environment;
}
