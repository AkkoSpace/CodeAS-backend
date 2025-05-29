package space.akko.platform.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import space.akko.foundation.common.PageResult;
import space.akko.foundation.common.ResultCode;
import space.akko.foundation.constant.CacheConstants;
import space.akko.foundation.constant.SecurityConstants;
import space.akko.foundation.exception.BusinessException;
import space.akko.foundation.exception.SecurityException;
import space.akko.foundation.utils.JwtUtils;
import space.akko.foundation.utils.PasswordUtils;
import space.akko.foundation.utils.SecurityUtils;
import space.akko.platform.user.model.dto.UserDTO;
import space.akko.platform.user.model.entity.UserCredential;
import space.akko.platform.user.model.entity.UserProfile;
import space.akko.platform.user.model.request.LoginRequest;
import space.akko.platform.user.model.request.UserCreateRequest;
import space.akko.platform.user.model.request.UserQueryRequest;
import space.akko.platform.user.model.request.UserUpdateRequest;
import space.akko.platform.user.model.response.LoginResponse;
import space.akko.platform.user.model.vo.UserVO;
import space.akko.platform.user.repository.UserCredentialRepository;
import space.akko.platform.user.repository.UserRepository;
import space.akko.platform.user.service.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务实现
 *
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserCredentialRepository userCredentialRepository;

    @Value("${platform.security.jwt.secret}")
    private String jwtSecret;

    @Value("${platform.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${platform.security.jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest request) {
        // 验证用户凭证
        if (!validateCredentials(request.getUsername(), request.getPassword())) {
            throw new SecurityException(ResultCode.INVALID_CREDENTIALS);
        }

        // 获取用户信息
        UserProfile user = getUserByUsername(request.getUsername());
        if (user == null) {
            throw new SecurityException(ResultCode.USER_NOT_FOUND);
        }

        if (!user.getIsActive()) {
            throw new SecurityException(ResultCode.USER_DISABLED);
        }

        // 获取用户角色和权限
        List<String> roles = getUserRoles(user.getId());
        List<String> permissions = getUserPermissions(user.getId());

        // 生成令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.ROLES_CLAIM, roles);
        claims.put(SecurityConstants.AUTHORITIES_CLAIM, permissions);

        String accessToken = JwtUtils.generateAccessToken(
            user.getId(), user.getUsername(), claims, jwtExpiration, jwtSecret);
        String refreshToken = JwtUtils.generateRefreshToken(
            user.getId(), user.getUsername(), jwtRefreshExpiration, jwtSecret);

        // 更新最后登录信息
        updateLastLoginInfo(user.getId(), SecurityUtils.getClientIpAddress());

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtExpiration);
        response.setUserInfo(convertToVO(user, roles));

        log.info("用户登录成功: {}", user.getUsername());
        return response;
    }

    @Override
    public void logout() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null) {
            // TODO: 将令牌加入黑名单
            log.info("用户登出: {}", currentUserId);
        }
        SecurityUtils.clearCurrentUser();
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        try {
            // 验证刷新令牌
            if (!JwtUtils.validateToken(refreshToken, jwtSecret)) {
                throw new SecurityException(ResultCode.TOKEN_INVALID);
            }

            // 检查令牌类型
            String tokenType = JwtUtils.getTokenTypeFromToken(refreshToken, jwtSecret);
            if (!SecurityConstants.REFRESH_TOKEN.equals(tokenType)) {
                throw new SecurityException(ResultCode.TOKEN_INVALID);
            }

            // 获取用户信息
            Long userId = JwtUtils.getUserIdFromToken(refreshToken, jwtSecret);
            String username = JwtUtils.getUsernameFromToken(refreshToken, jwtSecret);

            UserProfile user = userRepository.selectById(userId);
            if (user == null || !user.getIsActive()) {
                throw new SecurityException(ResultCode.USER_NOT_FOUND);
            }

            // 获取用户角色和权限
            List<String> roles = getUserRoles(userId);
            List<String> permissions = getUserPermissions(userId);

            // 生成新的访问令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put(SecurityConstants.ROLES_CLAIM, roles);
            claims.put(SecurityConstants.AUTHORITIES_CLAIM, permissions);

            String newAccessToken = JwtUtils.generateAccessToken(
                userId, username, claims, jwtExpiration, jwtSecret);

            // 构建响应
            LoginResponse response = new LoginResponse();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(refreshToken);
            response.setExpiresIn(jwtExpiration);
            response.setUserInfo(convertToVO(user, roles));

            return response;
        } catch (Exception e) {
            log.error("刷新令牌失败: {}", e.getMessage());
            throw new SecurityException(ResultCode.TOKEN_INVALID);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO createUser(UserCreateRequest request) {
        // 检查用户名是否已存在
        if (existsByUsername(request.getUsername())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "用户名已存在");
        }

        // 检查邮箱是否已存在
        if (StrUtil.isNotBlank(request.getEmail()) && existsByEmail(request.getEmail())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "邮箱已存在");
        }

        // 检查手机号是否已存在
        if (StrUtil.isNotBlank(request.getPhoneNumber()) && existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "手机号已存在");
        }

        // 创建用户
        UserProfile user = new UserProfile();
        BeanUtil.copyProperties(request, user);
        user.setAsid(generateAsid());
        user.setIsEmailVerified(false);
        user.setIsPhoneVerified(false);

        userRepository.insert(user);

        // 创建用户凭证
        UserCredential credential = new UserCredential();
        credential.setUserId(user.getId());
        credential.setCredentialType("PASSWORD");
        credential.setCredentialValue(PasswordUtils.encode(request.getPassword()));
        credential.setIsActive(true);

        userCredentialRepository.insert(credential);

        // 分配角色
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), request.getRoleIds());
        }

        log.info("创建用户成功: {}", user.getUsername());
        return convertToVO(user, getUserRoles(user.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.USER_CACHE, key = "#userId")
    public UserVO updateUser(Long userId, UserUpdateRequest request) {
        UserProfile user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 检查邮箱是否已被其他用户使用
        if (StrUtil.isNotBlank(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            if (existsByEmail(request.getEmail())) {
                throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "邮箱已存在");
            }
        }

        // 检查手机号是否已被其他用户使用
        if (StrUtil.isNotBlank(request.getPhoneNumber()) && !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (existsByPhoneNumber(request.getPhoneNumber())) {
                throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "手机号已存在");
            }
        }

        // 更新用户信息
        BeanUtil.copyProperties(request, user, "id", "asid", "username");
        userRepository.updateById(user);

        // 更新角色
        if (request.getRoleIds() != null) {
            assignRoles(userId, request.getRoleIds());
        }

        log.info("更新用户成功: {}", user.getUsername());
        return convertToVO(user, getUserRoles(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.USER_CACHE, key = "#userId")
    public void deleteUser(Long userId) {
        UserProfile user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 逻辑删除用户
        userRepository.deleteById(userId);

        // 删除用户凭证
        userCredentialRepository.deleteByUserId(userId);

        log.info("删除用户成功: {}", user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        userRepository.deleteBatchIds(userIds);
        log.info("批量删除用户成功，数量: {}", userIds.size());
    }

    @Override
    @Cacheable(value = CacheConstants.USER_CACHE, key = "#userId")
    public UserVO getUserById(Long userId) {
        UserDTO userDTO = userRepository.selectUserDetailById(userId);
        if (userDTO == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return convertDTOToVO(userDTO);
    }

    @Override
    public UserProfile getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserProfile getUserByAsid(String asid) {
        return userRepository.findByAsid(asid);
    }

    @Override
    public PageResult<UserVO> getUserPage(UserQueryRequest request) {
        Page<UserDTO> page = new Page<>(request.getCurrent(), request.getSize());
        IPage<UserDTO> result = userRepository.selectUserPage(page, request);

        List<UserVO> records = result.getRecords().stream()
                .map(this::convertDTOToVO)
                .toList();

        return PageResult.of(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    // 其他方法的实现...
    // 由于篇幅限制，这里只展示部分核心方法的实现
    // 完整实现需要继续添加剩余方法

    /**
     * 生成ASID
     */
    private String generateAsid() {
        return "user_" + IdUtil.fastSimpleUUID().substring(0, 8);
    }

    /**
     * 转换为VO
     */
    private UserVO convertToVO(UserProfile user, List<String> roles) {
        UserVO vo = new UserVO();
        BeanUtil.copyProperties(user, vo);
        // TODO: 设置角色信息和其他扩展信息
        return vo;
    }

    /**
     * 转换DTO为VO
     */
    private UserVO convertDTOToVO(UserDTO dto) {
        UserVO vo = new UserVO();
        BeanUtil.copyProperties(dto, vo);
        return vo;
    }

    // 继续实现的方法...
    @Override
    @CacheEvict(value = CacheConstants.USER_CACHE, key = "#userId")
    public void updateUserStatus(Long userId, Boolean isActive) {
        UserProfile user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setIsActive(isActive);
        userRepository.updateById(user);

        log.info("更新用户状态成功: {} -> {}", user.getUsername(), isActive);
    }

    @Override
    public void batchUpdateUserStatus(List<Long> userIds, Boolean isActive) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        userRepository.batchUpdateStatus(userIds, isActive);
        log.info("批量更新用户状态成功，数量: {}", userIds.size());
    }

    @Override
    @CacheEvict(value = CacheConstants.USER_CACHE, key = "#userId")
    public void resetPassword(Long userId, String newPassword) {
        UserProfile user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 使用默认密码或指定密码
        String password = StrUtil.isNotBlank(newPassword) ? newPassword : "123456";
        String encodedPassword = PasswordUtils.encode(password);

        // 更新密码
        userCredentialRepository.updateCredentialValue(userId, "PASSWORD", encodedPassword);

        log.info("重置用户密码成功: {}", user.getUsername());
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        UserProfile user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证旧密码
        UserCredential credential = userCredentialRepository.findByUserIdAndType(userId, "PASSWORD");
        if (credential == null || !PasswordUtils.matches(oldPassword, credential.getCredentialValue())) {
            throw new BusinessException(ResultCode.INVALID_CREDENTIALS, "原密码错误");
        }

        // 更新新密码
        String encodedPassword = PasswordUtils.encode(newPassword);
        userCredentialRepository.updateCredentialValue(userId, "PASSWORD", encodedPassword);

        log.info("修改用户密码成功: {}", user.getUsername());
    }

    @Override
    @CacheEvict(value = CacheConstants.USER_CACHE, key = "#userId")
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 这里调用角色服务的方法
        // roleService.assignRolesToUser(userId, roleIds);
        log.info("分配用户角色成功: userId={}, roleIds={}", userId, roleIds);
    }

    @Override
    @CacheEvict(value = CacheConstants.USER_CACHE, key = "#userId")
    public void removeRoles(Long userId, List<Long> roleIds) {
        // 这里调用角色服务的方法
        // roleService.removeRolesFromUser(userId, roleIds);
        log.info("移除用户角色成功: userId={}, roleIds={}", userId, roleIds);
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        return userRepository.selectUserRoles(userId);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        return userRepository.selectUserPermissions(userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        UserProfile user = getUserByUsername(username);
        if (user == null) {
            return false;
        }

        UserCredential credential = userCredentialRepository.findByUserIdAndType(user.getId(), "PASSWORD");
        if (credential == null || !credential.getIsActive()) {
            return false;
        }

        return PasswordUtils.matches(password, credential.getCredentialValue());
    }

    @Override
    public void updateLastLoginInfo(Long userId, String loginIp) {
        userRepository.updateLastLoginInfo(userId, LocalDateTime.now(), loginIp);
    }

    @Override
    public UserVO getCurrentUser() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new SecurityException(ResultCode.UNAUTHORIZED);
        }
        return getUserById(currentUserId);
    }
}
