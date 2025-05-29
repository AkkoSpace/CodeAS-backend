package space.akko.platform.menu.service;

import space.akko.foundation.common.PageResult;
import space.akko.platform.menu.model.dto.MenuDTO;
import space.akko.platform.menu.model.entity.MenuItem;
import space.akko.platform.menu.model.request.MenuCreateRequest;
import space.akko.platform.menu.model.vo.MenuVO;

import java.util.List;

/**
 * 菜单服务接口
 * 
 * @author akko
 * @since 1.0.0
 */
public interface MenuService {

    /**
     * 创建菜单
     */
    MenuVO createMenu(MenuCreateRequest request);

    /**
     * 更新菜单
     */
    MenuVO updateMenu(Long menuId, MenuCreateRequest request);

    /**
     * 删除菜单
     */
    void deleteMenu(Long menuId);

    /**
     * 批量删除菜单
     */
    void batchDeleteMenus(List<Long> menuIds);

    /**
     * 根据ID获取菜单
     */
    MenuVO getMenuById(Long menuId);

    /**
     * 根据菜单编码获取菜单
     */
    MenuItem getMenuByCode(String menuCode);

    /**
     * 分页查询菜单
     */
    PageResult<MenuVO> getMenuPage(Object request);

    /**
     * 获取菜单树
     */
    List<MenuVO> getMenuTree(Long parentId);

    /**
     * 获取所有菜单（扁平列表）
     */
    List<MenuVO> getAllMenus();

    /**
     * 启用/禁用菜单
     */
    void updateMenuStatus(Long menuId, Boolean isActive);

    /**
     * 批量更新菜单状态
     */
    void batchUpdateMenuStatus(List<Long> menuIds, Boolean isActive);

    /**
     * 分配权限给菜单
     */
    void assignPermissions(Long menuId, List<Long> permissionIds);

    /**
     * 移除菜单权限
     */
    void removePermissions(Long menuId, List<Long> permissionIds);

    /**
     * 获取菜单权限列表
     */
    List<String> getMenuPermissions(Long menuId);

    /**
     * 根据用户ID获取菜单树
     */
    List<MenuVO> getUserMenuTree(Long userId);

    /**
     * 根据角色ID获取菜单列表
     */
    List<MenuItem> getMenusByRoleId(Long roleId);

    /**
     * 检查菜单编码是否存在
     */
    boolean existsByMenuCode(String menuCode);

    /**
     * 检查菜单是否可以删除
     */
    boolean canDeleteMenu(Long menuId);

    /**
     * 获取菜单层级路径
     */
    List<MenuVO> getMenuHierarchyPath(Long menuId);

    /**
     * 移动菜单到新的父菜单下
     */
    void moveMenu(Long menuId, Long newParentId);

    /**
     * 复制菜单
     */
    MenuVO copyMenu(Long sourceMenuId, String newMenuCode, String newMenuName);

    /**
     * 根据菜单类型获取菜单
     */
    List<MenuItem> getMenusByType(String menuType);

    /**
     * 生成前端路由配置
     */
    List<Object> generateRoutes(Long userId);
}
