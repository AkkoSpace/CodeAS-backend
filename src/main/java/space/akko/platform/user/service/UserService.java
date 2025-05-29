package space.akko.platform.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import space.akko.foundation.common.PageResult;
import space.akko.platform.user.model.dto.UserDTO;
import space.akko.platform.user.model.entity.UserProfile;
import space.akko.platform.user.model.request.LoginRequest;
import space.akko.platform.user.model.request.UserCreateRequest;
import space.akko.platform.user.model.request.UserQueryRequest;
import space.akko.platform.user.model.request.UserUpdateRequest;
import space.akko.platform.user.model.response.LoginResponse;
import space.akko.platform.user.model.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口
 * 
 * @author akko
 * @since 1.0.0
 */
public interface UserService {

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 刷新令牌
     */
    LoginResponse refreshToken(String refreshToken);

    /**
     * 创建用户
     */
    UserVO createUser(UserCreateRequest request);

    /**
     * 更新用户
     */
    UserVO updateUser(Long userId, UserUpdateRequest request);

    /**
     * 删除用户
     */
    void deleteUser(Long userId);

    /**
     * 批量删除用户
     */
    void batchDeleteUsers(List<Long> userIds);

    /**
     * 根据ID获取用户
     */
    UserVO getUserById(Long userId);

    /**
     * 根据用户名获取用户
     */
    UserProfile getUserByUsername(String username);

    /**
     * 根据ASID获取用户
     */
    UserProfile getUserByAsid(String asid);

    /**
     * 分页查询用户
     */
    PageResult<UserVO> getUserPage(UserQueryRequest request);

    /**
     * 启用/禁用用户
     */
    void updateUserStatus(Long userId, Boolean isActive);

    /**
     * 批量更新用户状态
     */
    void batchUpdateUserStatus(List<Long> userIds, Boolean isActive);

    /**
     * 重置用户密码
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * 修改用户密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 分配角色给用户
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 移除用户角色
     */
    void removeRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户角色列表
     */
    List<String> getUserRoles(Long userId);

    /**
     * 获取用户权限列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * 验证用户凭证
     */
    boolean validateCredentials(String username, String password);

    /**
     * 更新最后登录信息
     */
    void updateLastLoginInfo(Long userId, String loginIp);

    /**
     * 获取当前登录用户信息
     */
    UserVO getCurrentUser();
}
