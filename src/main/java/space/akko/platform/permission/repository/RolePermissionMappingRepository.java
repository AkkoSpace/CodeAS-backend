package space.akko.platform.permission.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import space.akko.platform.permission.model.entity.RolePermissionMapping;

import java.util.List;

/**
 * 角色权限关联Repository
 * 
 * @author akko
 * @since 1.0.0
 */
@Mapper
public interface RolePermissionMappingRepository extends BaseMapper<RolePermissionMapping> {

    /**
     * 根据角色ID查询权限关联
     */
    List<RolePermissionMapping> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据资源ID查询角色关联
     */
    List<RolePermissionMapping> findByResourceId(@Param("resourceId") Long resourceId);

    /**
     * 根据角色ID和资源ID查询关联
     */
    RolePermissionMapping findByRoleIdAndResourceId(@Param("roleId") Long roleId, @Param("resourceId") Long resourceId);

    /**
     * 根据角色ID和资源ID和动作ID查询关联
     */
    RolePermissionMapping findByRoleIdAndResourceIdAndActionId(@Param("roleId") Long roleId, 
                                                              @Param("resourceId") Long resourceId,
                                                              @Param("actionId") Long actionId);

    /**
     * 检查角色是否拥有权限
     */
    boolean existsByRoleIdAndResourceId(@Param("roleId") Long roleId, @Param("resourceId") Long resourceId);

    /**
     * 检查角色是否拥有特定动作权限
     */
    boolean existsByRoleIdAndResourceIdAndActionId(@Param("roleId") Long roleId, 
                                                  @Param("resourceId") Long resourceId,
                                                  @Param("actionId") Long actionId);

    /**
     * 删除角色的所有权限
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除资源的所有角色关联
     */
    int deleteByResourceId(@Param("resourceId") Long resourceId);

    /**
     * 删除角色的指定权限
     */
    int deleteByRoleIdAndResourceId(@Param("roleId") Long roleId, @Param("resourceId") Long resourceId);

    /**
     * 删除角色的指定动作权限
     */
    int deleteByRoleIdAndResourceIdAndActionId(@Param("roleId") Long roleId, 
                                              @Param("resourceId") Long resourceId,
                                              @Param("actionId") Long actionId);

    /**
     * 批量插入角色权限关联
     */
    int batchInsert(@Param("mappings") List<RolePermissionMapping> mappings);

    /**
     * 批量删除角色权限关联
     */
    int batchDeleteByRoleIdAndResourceIds(@Param("roleId") Long roleId, @Param("resourceIds") List<Long> resourceIds);

    /**
     * 更新权限授权状态
     */
    int updateGrantStatus(@Param("roleId") Long roleId, 
                         @Param("resourceId") Long resourceId,
                         @Param("actionId") Long actionId,
                         @Param("isGranted") Boolean isGranted);

    /**
     * 批量更新权限授权状态
     */
    int batchUpdateGrantStatus(@Param("mappingIds") List<Long> mappingIds, @Param("isGranted") Boolean isGranted);

    /**
     * 根据角色ID查询已授权的资源ID列表
     */
    List<Long> selectGrantedResourceIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色ID查询已授权的权限映射（包含动作）
     */
    List<RolePermissionMapping> selectGrantedMappingsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据资源ID查询拥有权限的角色ID列表
     */
    List<Long> selectRoleIdsByResourceId(@Param("resourceId") Long resourceId);

    /**
     * 检查角色权限映射是否存在
     */
    boolean existsMapping(@Param("roleId") Long roleId, 
                         @Param("resourceId") Long resourceId,
                         @Param("actionId") Long actionId);

    /**
     * 查询角色的权限统计信息
     */
    Long countPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询资源的角色统计信息
     */
    Long countRolesByResourceId(@Param("resourceId") Long resourceId);
}