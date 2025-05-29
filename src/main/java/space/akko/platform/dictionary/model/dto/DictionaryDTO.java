package space.akko.platform.dictionary.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典数据传输对象
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "字典数据传输对象")
public class DictionaryDTO {

    @Schema(description = "字典ID", example = "1")
    private Long id;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类编码", example = "USER_STATUS")
    private String categoryCode;

    @Schema(description = "分类名称", example = "用户状态")
    private String categoryName;

    @Schema(description = "项编码", example = "ACTIVE")
    private String itemCode;

    @Schema(description = "项名称", example = "激活")
    private String itemName;

    @Schema(description = "项值", example = "1")
    private String itemValue;

    @Schema(description = "父项ID", example = "0")
    private Long parentId;

    @Schema(description = "父项名称", example = "状态")
    private String parentItemName;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "项描述", example = "用户激活状态")
    private String description;

    @Schema(description = "扩展数据", example = "{\"color\": \"green\"}")
    private String extraData;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive;

    @Schema(description = "子项列表")
    private List<DictionaryDTO> children;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedAt;
}
