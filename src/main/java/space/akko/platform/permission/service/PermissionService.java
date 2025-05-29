package space.akko.platform.permission.service;

import space.akko.foundation.common.PageResult;
import space.akko.platform.permission.model.dto.PermissionDTO;
import space.akko.platform.permission.model.entity.PermissionResource;
import space.akko.platform.permission.model.request.PermissionCreateRequest;
import space.akko.platform.permission.model.vo.PermissionVO;

import java.util.List;

/**
 * 权限服务接口
 * 
 * @author akko
 * @since 1.0.0
 */
public interface PermissionService {

    /**
     * 创建权限
     */
    PermissionVO createPermission(PermissionCreateRequest request);

    /**
     * 更新权限
     */
    PermissionVO updatePermission(Long permissionId, PermissionCreateRequest request);

    /**
     * 删除权限
     */
    void deletePermission(Long permissionId);

    /**
     * 批量删除权限
     */
    void batchDeletePermissions(List<Long> permissionIds);

    /**
     * 根据ID获取权限
     */
    PermissionVO getPermissionById(Long permissionId);

    /**
     * 根据资源编码获取权限
     */
    PermissionResource getPermissionByCode(String resourceCode);

    /**
     * 分页查询权限
     */
    PageResult<PermissionVO> getPermissionPage(Object request);

    /**
     * 获取权限树
     */
    List<PermissionVO> getPermissionTree(Long parentId);

    /**
     * 获取所有权限（扁平列表）
     */
    List<PermissionVO> getAllPermissions();

    /**
     * 启用/禁用权限
     */
    void updatePermissionStatus(Long permissionId, Boolean isActive);

    /**
     * 批量更新权限状态
     */
    void batchUpdatePermissionStatus(List<Long> permissionIds, Boolean isActive);

    /**
     * 根据角色ID获取权限列表
     */
    List<PermissionResource> getPermissionsByRoleId(Long roleId);

    /**
     * 根据用户ID获取权限列表
     */
    List<PermissionResource> getPermissionsByUserId(Long userId);

    /**
     * 检查资源编码是否存在
     */
    boolean existsByResourceCode(String resourceCode);

    /**
     * 检查权限是否可以删除
     */
    boolean canDeletePermission(Long permissionId);

    /**
     * 获取权限层级路径
     */
    List<PermissionVO> getPermissionHierarchyPath(Long permissionId);

    /**
     * 移动权限到新的父权限下
     */
    void movePermission(Long permissionId, Long newParentId);

    /**
     * 根据资源类型获取权限
     */
    List<PermissionResource> getPermissionsByType(String resourceType);

    /**
     * 根据URL和方法获取权限
     */
    PermissionResource getPermissionByUrlAndMethod(String resourceUrl, String httpMethod);

    /**
     * 同步API权限（扫描Controller注解自动创建权限）
     */
    void syncApiPermissions();
}
