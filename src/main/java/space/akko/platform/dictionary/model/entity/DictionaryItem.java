package space.akko.platform.dictionary.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import space.akko.foundation.common.BaseEntity;

/**
 * 字典项实体
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "dictionary_item", schema = "platform_schema")
public class DictionaryItem extends BaseEntity {

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 项编码
     */
    private String itemCode;

    /**
     * 项名称
     */
    private String itemName;

    /**
     * 项值
     */
    private String itemValue;

    /**
     * 父项ID
     */
    private Long parentId;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 项描述
     */
    private String description;

    /**
     * 扩展数据（JSON格式）
     */
    private String extraData;

    /**
     * 是否激活
     */
    private Boolean isActive;
}
