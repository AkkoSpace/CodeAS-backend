package space.akko.platform.role.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import space.akko.platform.role.model.entity.UserRoleMapping;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户角色关联Repository
 * 
 * @author akko
 * @since 1.0.0
 */
@Mapper
public interface UserRoleMappingRepository extends BaseMapper<UserRoleMapping> {

    /**
     * 根据用户ID查询角色关联
     */
    List<UserRoleMapping> findByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询用户关联
     */
    List<UserRoleMapping> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID和角色ID查询关联
     */
    UserRoleMapping findByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 检查用户是否拥有角色
     */
    boolean existsByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 删除用户的所有角色
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除角色的所有用户关联
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除用户的指定角色
     */
    int deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 批量插入用户角色关联
     */
    int batchInsert(@Param("mappings") List<UserRoleMapping> mappings);

    /**
     * 更新角色关联的有效期
     */
    int updateEffectiveTime(@Param("userId") Long userId, 
                           @Param("roleId") Long roleId,
                           @Param("effectiveFrom") LocalDateTime effectiveFrom,
                           @Param("effectiveTo") LocalDateTime effectiveTo);

    /**
     * 查询过期的角色关联
     */
    List<UserRoleMapping> selectExpiredMappings();

    /**
     * 批量更新角色关联状态
     */
    int batchUpdateStatus(@Param("mappingIds") List<Long> mappingIds, @Param("isActive") Boolean isActive);
}
