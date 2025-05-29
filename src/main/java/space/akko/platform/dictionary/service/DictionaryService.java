package space.akko.platform.dictionary.service;

import space.akko.foundation.common.PageResult;
import space.akko.platform.dictionary.model.dto.DictionaryDTO;
import space.akko.platform.dictionary.model.entity.DictionaryCategory;
import space.akko.platform.dictionary.model.entity.DictionaryItem;

import java.util.List;
import java.util.Map;

/**
 * 字典服务接口
 * 
 * @author akko
 * @since 1.0.0
 */
public interface DictionaryService {

    // ========== 字典分类管理 ==========

    /**
     * 创建字典分类
     */
    Object createCategory(Object request);

    /**
     * 更新字典分类
     */
    Object updateCategory(Long categoryId, Object request);

    /**
     * 删除字典分类
     */
    void deleteCategory(Long categoryId);

    /**
     * 根据ID获取字典分类
     */
    Object getCategoryById(Long categoryId);

    /**
     * 根据编码获取字典分类
     */
    DictionaryCategory getCategoryByCode(String categoryCode);

    /**
     * 获取字典分类树
     */
    List<Object> getCategoryTree(Long parentId);

    /**
     * 获取所有字典分类
     */
    List<Object> getAllCategories();

    // ========== 字典项管理 ==========

    /**
     * 创建字典项
     */
    Object createItem(Object request);

    /**
     * 更新字典项
     */
    Object updateItem(Long itemId, Object request);

    /**
     * 删除字典项
     */
    void deleteItem(Long itemId);

    /**
     * 批量删除字典项
     */
    void batchDeleteItems(List<Long> itemIds);

    /**
     * 根据ID获取字典项
     */
    Object getItemById(Long itemId);

    /**
     * 分页查询字典项
     */
    PageResult<Object> getItemPage(Object request);

    /**
     * 根据分类获取字典项列表
     */
    List<Object> getItemsByCategory(String categoryCode);

    /**
     * 根据分类获取字典项树
     */
    List<Object> getItemTreeByCategory(String categoryCode);

    /**
     * 启用/禁用字典项
     */
    void updateItemStatus(Long itemId, Boolean isActive);

    /**
     * 批量更新字典项状态
     */
    void batchUpdateItemStatus(List<Long> itemIds, Boolean isActive);

    // ========== 字典查询 ==========

    /**
     * 根据分类编码和项编码获取字典项
     */
    DictionaryItem getItemByCategoryAndCode(String categoryCode, String itemCode);

    /**
     * 根据分类编码和项值获取字典项
     */
    DictionaryItem getItemByCategoryAndValue(String categoryCode, String itemValue);

    /**
     * 获取字典项的显示名称
     */
    String getItemName(String categoryCode, String itemCode);

    /**
     * 获取字典项的值
     */
    String getItemValue(String categoryCode, String itemCode);

    /**
     * 检查字典项是否存在
     */
    boolean existsItem(String categoryCode, String itemCode);

    // ========== 字典缓存 ==========

    /**
     * 刷新字典缓存
     */
    void refreshDictionaryCache();

    /**
     * 刷新指定分类的字典缓存
     */
    void refreshCategoryCache(String categoryCode);

    /**
     * 获取所有字典数据（缓存）
     */
    Map<String, List<Object>> getAllDictionariesFromCache();

    /**
     * 根据分类获取字典数据（缓存）
     */
    List<Object> getDictionaryFromCache(String categoryCode);

    // ========== 字典工具 ==========

    /**
     * 导出字典数据
     */
    String exportDictionaries(List<String> categoryCodes);

    /**
     * 导入字典数据
     */
    void importDictionaries(String dictionaryData);

    /**
     * 复制字典分类
     */
    Object copyCategory(Long sourceCategoryId, String newCategoryCode, String newCategoryName);

    /**
     * 移动字典项到新分类
     */
    void moveItemsToCategory(List<Long> itemIds, Long newCategoryId);

    /**
     * 获取字典统计信息
     */
    Map<String, Object> getDictionaryStatistics();
}
