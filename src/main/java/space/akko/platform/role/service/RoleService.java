package space.akko.platform.role.service;

import space.akko.foundation.common.PageResult;
import space.akko.platform.role.model.dto.RoleDTO;
import space.akko.platform.role.model.entity.RoleDefinition;
import space.akko.platform.role.model.request.RoleCreateRequest;
import space.akko.platform.role.model.request.RoleQueryRequest;
import space.akko.platform.role.model.request.RoleUpdateRequest;
import space.akko.platform.role.model.vo.RoleVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色服务接口
 * 
 * @author akko
 * @since 1.0.0
 */
public interface RoleService {

    /**
     * 创建角色
     */
    RoleVO createRole(RoleCreateRequest request);

    /**
     * 更新角色
     */
    RoleVO updateRole(Long roleId, RoleUpdateRequest request);

    /**
     * 删除角色
     */
    void deleteRole(Long roleId);

    /**
     * 批量删除角色
     */
    void batchDeleteRoles(List<Long> roleIds);

    /**
     * 根据ID获取角色
     */
    RoleVO getRoleById(Long roleId);

    /**
     * 根据角色编码获取角色
     */
    RoleDefinition getRoleByCode(String roleCode);

    /**
     * 分页查询角色
     */
    PageResult<RoleVO> getRolePage(RoleQueryRequest request);

    /**
     * 获取角色树
     */
    List<RoleVO> getRoleTree(Long parentId);

    /**
     * 获取所有角色（扁平列表）
     */
    List<RoleVO> getAllRoles();

    /**
     * 启用/禁用角色
     */
    void updateRoleStatus(Long roleId, Boolean isActive);

    /**
     * 批量更新角色状态
     */
    void batchUpdateRoleStatus(List<Long> roleIds, Boolean isActive);

    /**
     * 分配权限给角色
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 移除角色权限
     */
    void removePermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色权限列表
     */
    List<String> getRolePermissions(Long roleId);

    /**
     * 分配角色给用户
     */
    void assignRolesToUser(Long userId, List<Long> roleIds);

    /**
     * 移除用户角色
     */
    void removeRolesFromUser(Long userId, List<Long> roleIds);

    /**
     * 设置用户角色有效期
     */
    void setUserRoleEffectiveTime(Long userId, Long roleId, 
                                 LocalDateTime effectiveFrom, LocalDateTime effectiveTo);

    /**
     * 获取用户角色列表
     */
    List<RoleDefinition> getUserRoles(Long userId);

    /**
     * 获取角色的用户数量
     */
    Long getRoleUserCount(Long roleId);

    /**
     * 检查角色编码是否存在
     */
    boolean existsByRoleCode(String roleCode);

    /**
     * 检查角色是否可以删除
     */
    boolean canDeleteRole(Long roleId);

    /**
     * 获取角色层级路径
     */
    List<RoleVO> getRoleHierarchyPath(Long roleId);

    /**
     * 移动角色到新的父角色下
     */
    void moveRole(Long roleId, Long newParentId);

    /**
     * 复制角色
     */
    RoleVO copyRole(Long sourceRoleId, String newRoleCode, String newRoleName);

    /**
     * 清理过期的角色关联
     */
    void cleanExpiredRoleMappings();
}
