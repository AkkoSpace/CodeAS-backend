package space.akko.platform.permission.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import space.akko.platform.permission.model.dto.PermissionDTO;
import space.akko.platform.permission.model.entity.PermissionResource;

import java.util.List;

/**
 * 权限Repository
 * 
 * @author akko
 * @since 1.0.0
 */
@Mapper
public interface PermissionRepository extends BaseMapper<PermissionResource> {

    /**
     * 根据资源编码查找权限
     */
    PermissionResource findByResourceCode(@Param("resourceCode") String resourceCode);

    /**
     * 检查资源编码是否存在
     */
    boolean existsByResourceCode(@Param("resourceCode") String resourceCode);

    /**
     * 分页查询权限
     */
    IPage<PermissionDTO> selectPermissionPage(Page<PermissionDTO> page, @Param("query") Object query);

    /**
     * 根据权限ID查询权限详情
     */
    PermissionDTO selectPermissionDetailById(@Param("permissionId") Long permissionId);

    /**
     * 查询权限树（层级结构）
     */
    List<PermissionDTO> selectPermissionTree(@Param("parentId") Long parentId);

    /**
     * 根据角色ID查询权限列表
     */
    List<PermissionResource> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询权限列表
     */
    List<PermissionResource> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 查询子权限列表
     */
    List<PermissionResource> selectChildPermissions(@Param("parentId") Long parentId);

    /**
     * 查询所有父权限ID（递归）
     */
    List<Long> selectParentPermissionIds(@Param("permissionId") Long permissionId);

    /**
     * 查询所有子权限ID（递归）
     */
    List<Long> selectChildPermissionIds(@Param("permissionId") Long permissionId);

    /**
     * 批量更新权限状态
     */
    int batchUpdateStatus(@Param("permissionIds") List<Long> permissionIds, @Param("isActive") Boolean isActive);

    /**
     * 检查权限是否可以删除（没有子权限和角色关联）
     */
    boolean canDelete(@Param("permissionId") Long permissionId);

    /**
     * 根据资源类型查询权限
     */
    List<PermissionResource> selectPermissionsByType(@Param("resourceType") String resourceType);

    /**
     * 根据URL和方法查询权限
     */
    PermissionResource findByUrlAndMethod(@Param("resourceUrl") String resourceUrl, 
                                        @Param("httpMethod") String httpMethod);
}
