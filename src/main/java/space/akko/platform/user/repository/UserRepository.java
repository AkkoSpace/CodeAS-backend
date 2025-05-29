package space.akko.platform.user.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import space.akko.platform.user.model.dto.UserDTO;
import space.akko.platform.user.model.entity.UserProfile;
import space.akko.platform.user.model.request.UserQueryRequest;

import java.util.List;

/**
 * 用户Repository
 * 
 * @author akko
 * @since 1.0.0
 */
@Mapper
public interface UserRepository extends BaseMapper<UserProfile> {

    /**
     * 根据用户名查找用户
     */
    UserProfile findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查找用户
     */
    UserProfile findByEmail(@Param("email") String email);

    /**
     * 根据手机号查找用户
     */
    UserProfile findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    /**
     * 根据ASID查找用户
     */
    UserProfile findByAsid(@Param("asid") String asid);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(@Param("username") String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(@Param("email") String email);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    /**
     * 分页查询用户（带角色信息）
     */
    IPage<UserDTO> selectUserPage(Page<UserDTO> page, @Param("query") UserQueryRequest query);

    /**
     * 根据用户ID查询用户详情（带角色和权限信息）
     */
    UserDTO selectUserDetailById(@Param("userId") Long userId);

    /**
     * 根据角色ID查询用户列表
     */
    List<UserProfile> selectUsersByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询用户的角色列表
     */
    List<String> selectUserRoles(@Param("userId") Long userId);

    /**
     * 查询用户的权限列表
     */
    List<String> selectUserPermissions(@Param("userId") Long userId);

    /**
     * 更新用户最后登录信息
     */
    int updateLastLoginInfo(@Param("userId") Long userId, 
                           @Param("loginTime") java.time.LocalDateTime loginTime,
                           @Param("loginIp") String loginIp);

    /**
     * 批量更新用户状态
     */
    int batchUpdateStatus(@Param("userIds") List<Long> userIds, @Param("isActive") Boolean isActive);
}
