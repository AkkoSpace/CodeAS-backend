package space.akko.platform.role.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import space.akko.platform.role.model.dto.RoleDTO;
import space.akko.platform.role.model.entity.RoleDefinition;
import space.akko.platform.role.model.request.RoleQueryRequest;

import java.util.List;

/**
 * 角色Repository
 * 
 * @author akko
 * @since 1.0.0
 */
@Mapper
public interface RoleRepository extends BaseMapper<RoleDefinition> {

    /**
     * 根据角色编码查找角色
     */
    RoleDefinition findByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 检查角色编码是否存在
     */
    boolean existsByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 分页查询角色
     */
    IPage<RoleDTO> selectRolePage(Page<RoleDTO> page, @Param("query") RoleQueryRequest query);

    /**
     * 根据角色ID查询角色详情（带权限信息）
     */
    RoleDTO selectRoleDetailById(@Param("roleId") Long roleId);

    /**
     * 查询角色树（层级结构）
     */
    List<RoleDTO> selectRoleTree(@Param("parentId") Long parentId);

    /**
     * 根据用户ID查询角色列表
     */
    List<RoleDefinition> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 查询角色的权限列表
     */
    List<String> selectRolePermissions(@Param("roleId") Long roleId);

    /**
     * 查询角色的用户数量
     */
    Long countUsersByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询子角色列表
     */
    List<RoleDefinition> selectChildRoles(@Param("parentId") Long parentId);

    /**
     * 查询所有父角色ID（递归）
     */
    List<Long> selectParentRoleIds(@Param("roleId") Long roleId);

    /**
     * 查询所有子角色ID（递归）
     */
    List<Long> selectChildRoleIds(@Param("roleId") Long roleId);

    /**
     * 批量更新角色状态
     */
    int batchUpdateStatus(@Param("roleIds") List<Long> roleIds, @Param("isActive") Boolean isActive);

    /**
     * 检查角色是否可以删除（没有子角色和用户）
     */
    boolean canDelete(@Param("roleId") Long roleId);

    /**
     * 查询最大角色级别
     */
    Integer selectMaxRoleLevel();
}
