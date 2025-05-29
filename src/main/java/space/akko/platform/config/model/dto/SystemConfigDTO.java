package space.akko.platform.config.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置数据传输对象
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "系统配置数据传输对象")
public class SystemConfigDTO {

    @Schema(description = "配置ID", example = "1")
    private Long id;

    @Schema(description = "配置键", example = "system.title")
    private String configKey;

    @Schema(description = "配置值", example = "后端平台")
    private String configValue;

    @Schema(description = "配置类型", example = "STRING")
    private String configType;

    @Schema(description = "配置分类", example = "SYSTEM")
    private String category;

    @Schema(description = "配置描述", example = "系统标题")
    private String description;

    @Schema(description = "是否加密", example = "false")
    private Boolean isEncrypted;

    @Schema(description = "是否只读", example = "false")
    private Boolean isReadonly;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedAt;
}
