package space.akko.platform.config.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置视图对象
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "系统配置视图对象")
public class SystemConfigVO {

    @Schema(description = "配置ID", example = "1")
    private Long id;

    @Schema(description = "配置键", example = "system.title")
    private String configKey;

    @Schema(description = "配置值", example = "后端平台")
    private String configValue;

    @Schema(description = "配置类型", example = "STRING")
    private String configType;

    @Schema(description = "配置类型显示名称", example = "字符串")
    private String configTypeName;

    @Schema(description = "配置分类", example = "SYSTEM")
    private String category;

    @Schema(description = "配置分类显示名称", example = "系统配置")
    private String categoryName;

    @Schema(description = "配置描述", example = "系统标题")
    private String description;

    @Schema(description = "是否加密", example = "false")
    private Boolean isEncrypted;

    @Schema(description = "是否只读", example = "false")
    private Boolean isReadonly;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2024-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
