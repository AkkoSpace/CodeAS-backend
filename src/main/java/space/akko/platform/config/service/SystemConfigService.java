package space.akko.platform.config.service;

import space.akko.foundation.common.PageResult;
import space.akko.platform.config.model.entity.SystemConfig;
import space.akko.platform.config.model.vo.SystemConfigVO;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 * 
 * @author akko
 * @since 1.0.0
 */
public interface SystemConfigService {

    /**
     * 创建配置
     */
    SystemConfigVO createConfig(Object request);

    /**
     * 更新配置
     */
    SystemConfigVO updateConfig(Long configId, Object request);

    /**
     * 删除配置
     */
    void deleteConfig(Long configId);

    /**
     * 批量删除配置
     */
    void batchDeleteConfigs(List<Long> configIds);

    /**
     * 根据ID获取配置
     */
    SystemConfigVO getConfigById(Long configId);

    /**
     * 根据配置键获取配置
     */
    SystemConfig getConfigByKey(String configKey);

    /**
     * 分页查询配置
     */
    PageResult<SystemConfigVO> getConfigPage(Object request);

    /**
     * 根据分类获取配置列表
     */
    List<SystemConfigVO> getConfigsByCategory(String category);

    /**
     * 获取所有配置（Map格式）
     */
    Map<String, String> getAllConfigsAsMap();

    /**
     * 批量更新配置
     */
    void batchUpdateConfigs(Map<String, String> configs);

    /**
     * 获取配置值（字符串）
     */
    String getConfigValue(String configKey);

    /**
     * 获取配置值（字符串，带默认值）
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 获取配置值（整数）
     */
    Integer getConfigValueAsInt(String configKey);

    /**
     * 获取配置值（整数，带默认值）
     */
    Integer getConfigValueAsInt(String configKey, Integer defaultValue);

    /**
     * 获取配置值（布尔）
     */
    Boolean getConfigValueAsBoolean(String configKey);

    /**
     * 获取配置值（布尔，带默认值）
     */
    Boolean getConfigValueAsBoolean(String configKey, Boolean defaultValue);

    /**
     * 设置配置值
     */
    void setConfigValue(String configKey, String configValue);

    /**
     * 检查配置键是否存在
     */
    boolean existsByConfigKey(String configKey);

    /**
     * 刷新配置缓存
     */
    void refreshConfigCache();

    /**
     * 导出配置
     */
    String exportConfigs(String category);

    /**
     * 导入配置
     */
    void importConfigs(String configData);

    /**
     * 重置配置到默认值
     */
    void resetConfigToDefault(String configKey);

    /**
     * 获取配置历史记录
     */
    List<Object> getConfigHistory(String configKey);
}
