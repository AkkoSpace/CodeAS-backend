package space.akko.platform.role.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import space.akko.foundation.common.PageResult;
import space.akko.foundation.constant.CacheConstants;
import space.akko.foundation.constant.CommonConstants;
import space.akko.foundation.enums.ResultCode;
import space.akko.foundation.exception.BusinessException;
import space.akko.platform.role.model.dto.RoleDTO;
import space.akko.platform.role.model.entity.RoleDefinition;
import space.akko.platform.role.model.entity.UserRoleMapping;
import space.akko.platform.role.model.request.RoleCreateRequest;
import space.akko.platform.role.model.request.RoleQueryRequest;
import space.akko.platform.role.model.vo.RoleVO;
import space.akko.platform.role.repository.RoleRepository;
import space.akko.platform.role.repository.UserRoleMappingRepository;
import space.akko.platform.permission.repository.RolePermissionMappingRepository;
import space.akko.platform.permission.model.entity.RolePermissionMapping;
import space.akko.platform.role.service.RoleService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 * 
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;
    private final RolePermissionMappingRepository rolePermissionMappingRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.ROLE_CACHE, allEntries = true)
    public RoleVO createRole(RoleCreateRequest request) {
        // 检查角色编码是否已存在
        if (existsByRoleCode(request.getRoleCode())) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS, "角色编码已存在");
        }

        // 验证父角色
        if (request.getParentId() != null && !request.getParentId().equals(CommonConstants.ROOT_NODE_ID)) {
            RoleDefinition parentRole = roleRepository.selectById(request.getParentId());
            if (parentRole == null) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "父角色不存在");
            }
        }

        // 创建角色实体
        RoleDefinition role = new RoleDefinition();
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setParentId(request.getParentId());
        role.setRoleLevel(request.getRoleLevel());
        role.setSortOrder(request.getSortOrder());
        role.setDescription(request.getDescription());
        role.setIsActive(request.getIsActive());

        // 保存到数据库
        roleRepository.insert(role);

        log.info("创建角色成功: {}", role.getRoleCode());
        return convertEntityToVO(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.ROLE_CACHE, allEntries = true)
    public RoleVO updateRole(Long roleId, RoleCreateRequest request) {
        // 检查角色是否存在
        RoleDefinition role = roleRepository.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }

        // 检查角色编码是否已被其他角色使用
        if (!role.getRoleCode().equals(request.getRoleCode()) && existsByRoleCode(request.getRoleCode())) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS, "角色编码已存在");
        }

        // 验证父角色（不能设置自己为父角色）
        if (request.getParentId() != null && 
            !request.getParentId().equals(CommonConstants.ROOT_NODE_ID) &&
            !request.getParentId().equals(roleId)) {
            
            RoleDefinition parentRole = roleRepository.selectById(request.getParentId());
            if (parentRole == null) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "父角色不存在");
            }

            // 检查是否会形成循环引用
            if (isCircularReference(roleId, request.getParentId())) {
                throw new BusinessException(ResultCode.INVALID_PARAMETER, "不能设置子角色为父角色，会形成循环引用");
            }
        }

        // 更新角色信息
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setParentId(request.getParentId());
        role.setRoleLevel(request.getRoleLevel());
        role.setSortOrder(request.getSortOrder());
        role.setDescription(request.getDescription());
        role.setIsActive(request.getIsActive());

        // 保存到数据库
        roleRepository.updateById(role);

        log.info("更新角色成功: {}", role.getRoleCode());
        return convertEntityToVO(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.ROLE_CACHE, allEntries = true)
    public void deleteRole(Long roleId) {
        // 检查角色是否存在
        RoleDefinition role = roleRepository.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }

        // 检查是否可以删除
        if (!canDeleteRole(roleId)) {
            throw new BusinessException(ResultCode.OPERATION_NOT_ALLOWED, "角色存在子角色或已被用户使用，无法删除");
        }

        // 删除角色
        roleRepository.deleteById(roleId);

        log.info("删除角色成功: {}", role.getRoleCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.ROLE_CACHE, allEntries = true)
    public void batchDeleteRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new BusinessException(ResultCode.INVALID_PARAMETER, "角色ID列表不能为空");
        }

        // 检查所有角色是否都可以删除
        for (Long roleId : roleIds) {
            if (!canDeleteRole(roleId)) {
                RoleDefinition role = roleRepository.selectById(roleId);
                String roleCode = role != null ? role.getRoleCode() : String.valueOf(roleId);
                throw new BusinessException(ResultCode.OPERATION_NOT_ALLOWED, 
                    String.format("角色 %s 存在子角色或已被用户使用，无法删除", roleCode));
            }
        }

        // 批量删除
        roleRepository.deleteBatchIds(roleIds);

        log.info("批量删除角色成功，数量: {}", roleIds.size());
    }

    @Override
    @Cacheable(value = CacheConstants.ROLE_CACHE, key = "'detail:' + #roleId")
    public RoleVO getRoleById(Long roleId) {
        RoleDTO roleDTO = roleRepository.selectRoleDetailById(roleId);
        if (roleDTO == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }
        return convertDTOToVO(roleDTO);
    }

    @Override
    @Cacheable(value = CacheConstants.ROLE_CACHE, key = "'code:' + #roleCode")
    public RoleDefinition getRoleByCode(String roleCode) {
        return roleRepository.findByRoleCode(roleCode);
    }

    @Override
    public PageResult<RoleVO> getRolePage(Object request) {
        RoleQueryRequest queryRequest = (RoleQueryRequest) request;
        Page<RoleDTO> page = new Page<>(queryRequest.getCurrent(), queryRequest.getSize());
        IPage<RoleDTO> result = roleRepository.selectRolePage(page, queryRequest);
        
        List<RoleVO> records = result.getRecords().stream()
                .map(this::convertDTOToVO)
                .collect(Collectors.toList());
        
        return PageResult.of(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Cacheable(value = CacheConstants.ROLE_CACHE, key = "'tree:' + #parentId")
    public List<RoleVO> getRoleTree(Long parentId) {
        List<RoleDTO> roleDTOs = roleRepository.selectRoleTree(parentId);
        return buildRoleTree(roleDTOs, parentId);
    }

    @Override
    @Cacheable(value = CacheConstants.ROLE_CACHE, key = "'all'")
    public List<RoleVO> getAllRoles() {
        List<RoleDTO> roleDTOs = roleRepository.selectRoleTree(null);
        return roleDTOs.stream()
                .map(this::convertDTOToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.ROLE_CACHE, allEntries = true)
    public void updateRoleStatus(Long roleId, Boolean isActive) {
        RoleDefinition role = roleRepository.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }

        role.setIsActive(isActive);
        roleRepository.updateById(role);

        log.info("更新角色状态成功: {} -> {}", role.getRoleCode(), isActive ? "启用" : "禁用");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.ROLE_CACHE, allEntries = true)
    public void batchUpdateRoleStatus(List<Long> roleIds, Boolean isActive) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new BusinessException(ResultCode.INVALID_PARAMETER, "角色ID列表不能为空");
        }

        int updatedCount = roleRepository.batchUpdateStatus(roleIds, isActive);
        
        log.info("批量更新角色状态成功，数量: {}, 状态: {}", updatedCount, isActive ? "启用" : "禁用");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.ROLE_CACHE, allEntries = true)
    public void assignUsersToRole(Long roleId, List<Long> userIds, LocalDateTime expiresAt) {
        // 检查角色是否存在
        RoleDefinition role = roleRepository.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }

        if (userIds != null && !userIds.isEmpty()) {
            List<UserRoleMapping> mappings = new ArrayList<>();
            
            for (Long userId : userIds) {
                // 检查是否已存在映射
                if (!userRoleMappingRepository.existsByUserIdAndRoleId(userId, roleId)) {
                    UserRoleMapping mapping = new UserRoleMapping();
                    mapping.setUserId(userId);
                    mapping.setRoleId(roleId);
                    mapping.setExpiresAt(expiresAt);
                    mappings.add(mapping);
                }
            }
            
            if (!mappings.isEmpty()) {
                userRoleMappingRepository.batchInsert(mappings);
            }
        }

        log.info("角色用户分配成功: roleId={}, userCount={}", roleId, 
                userIds != null ? userIds.size() : 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.ROLE_CACHE, allEntries = true)
    public void removeUsersFromRole(Long roleId, List<Long> userIds) {
        // 检查角色是否存在
        RoleDefinition role = roleRepository.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }

        if (userIds != null && !userIds.isEmpty()) {
            userRoleMappingRepository.batchDeleteByRoleIdAndUserIds(roleId, userIds);
            
            log.info("角色用户移除成功: roleId={}, userCount={}", roleId, userIds.size());
        }
    }

    @Override
    @Cacheable(value = CacheConstants.ROLE_CACHE, key = "'user:' + #userId")
    public List<RoleDefinition> getRolesByUserId(Long userId) {
        return roleRepository.selectRolesByUserId(userId);
    }

    @Override
    public boolean existsByRoleCode(String roleCode) {
        return roleRepository.existsByRoleCode(roleCode);
    }

    @Override
    public boolean canDeleteRole(Long roleId) {
        return roleRepository.canDelete(roleId);
    }

    // 需要继续实现的方法...
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {CacheConstants.ROLE_CACHE, CacheConstants.PERMISSION_CACHE}, allEntries = true)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 检查角色是否存在
        RoleDefinition role = roleRepository.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }

        // 先删除角色现有的所有权限
        rolePermissionMappingRepository.deleteByRoleId(roleId);

        // 分配新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<RolePermissionMapping> mappings = new ArrayList<>();
            
            for (Long permissionId : permissionIds) {
                // 为每个权限创建默认的读取权限映射
                RolePermissionMapping mapping = new RolePermissionMapping();
                mapping.setRoleId(roleId);
                mapping.setResourceId(permissionId);
                mapping.setActionId(1L); // 假设1是默认的读取动作ID
                mapping.setIsGranted(true);
                mappings.add(mapping);
            }
            
            if (!mappings.isEmpty()) {
                rolePermissionMappingRepository.batchInsert(mappings);
            }
        }

        log.info("角色权限分配成功: roleId={}, permissionCount={}", roleId, 
                permissionIds != null ? permissionIds.size() : 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {CacheConstants.ROLE_CACHE, CacheConstants.PERMISSION_CACHE}, allEntries = true)
    public void removePermissions(Long roleId, List<Long> permissionIds) {
        // 检查角色是否存在
        RoleDefinition role = roleRepository.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }

        if (permissionIds != null && !permissionIds.isEmpty()) {
            // 批量删除指定权限
            rolePermissionMappingRepository.batchDeleteByRoleIdAndResourceIds(roleId, permissionIds);
            
            log.info("角色权限移除成功: roleId={}, permissionCount={}", roleId, permissionIds.size());
        }
    }

    @Override
    @Cacheable(value = CacheConstants.ROLE_CACHE, key = "'hierarchy:' + #roleId")
    public List<RoleVO> getRoleHierarchyPath(Long roleId) {
        List<RoleVO> path = new ArrayList<>();
        
        RoleDefinition current = roleRepository.selectById(roleId);
        while (current != null) {
            path.add(0, convertEntityToVO(current)); // 添加到开头，保持从根到叶的顺序
            
            if (current.getParentId() == null || current.getParentId().equals(CommonConstants.ROOT_NODE_ID)) {
                break;
            }
            
            current = roleRepository.selectById(current.getParentId());
        }
        
        return path;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.ROLE_CACHE, allEntries = true)
    public void moveRole(Long roleId, Long newParentId) {
        // 检查角色是否存在
        RoleDefinition role = roleRepository.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }

        // 检查新父角色是否存在（除了根节点）
        if (newParentId != null && !newParentId.equals(CommonConstants.ROOT_NODE_ID)) {
            RoleDefinition newParent = roleRepository.selectById(newParentId);
            if (newParent == null) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "新父角色不存在");
            }
        }

        // 检查是否会形成循环引用
        if (newParentId != null && !newParentId.equals(CommonConstants.ROOT_NODE_ID)) {
            if (isCircularReference(roleId, newParentId)) {
                throw new BusinessException(ResultCode.INVALID_PARAMETER, "不能移动到子角色下，会形成循环引用");
            }
        }

        // 更新父角色
        role.setParentId(newParentId);
        roleRepository.updateById(role);

        log.info("移动角色成功: {} -> 新父角色ID: {}", role.getRoleCode(), newParentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.ROLE_CACHE, allEntries = true)
    public RoleVO copyRole(Long sourceRoleId, String newRoleCode, String newRoleName) {
        // 检查源角色是否存在
        RoleDefinition sourceRole = roleRepository.selectById(sourceRoleId);
        if (sourceRole == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "源角色不存在");
        }

        // 检查新角色编码是否已存在
        if (existsByRoleCode(newRoleCode)) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS, "角色编码已存在");
        }

        // 创建新角色
        RoleDefinition newRole = new RoleDefinition();
        newRole.setRoleCode(newRoleCode);
        newRole.setRoleName(newRoleName);
        newRole.setParentId(sourceRole.getParentId());
        newRole.setRoleLevel(sourceRole.getRoleLevel());
        newRole.setSortOrder(sourceRole.getSortOrder());
        newRole.setDescription("复制自: " + sourceRole.getDescription());
        newRole.setIsActive(sourceRole.getIsActive());

        // 保存新角色
        roleRepository.insert(newRole);

        // 复制权限
        List<RolePermissionMapping> sourcePermissions = rolePermissionMappingRepository.selectGrantedMappingsByRoleId(sourceRoleId);
        if (!sourcePermissions.isEmpty()) {
            List<RolePermissionMapping> newPermissions = sourcePermissions.stream()
                    .map(mapping -> {
                        RolePermissionMapping newMapping = new RolePermissionMapping();
                        newMapping.setRoleId(newRole.getId());
                        newMapping.setResourceId(mapping.getResourceId());
                        newMapping.setActionId(mapping.getActionId());
                        newMapping.setIsGranted(mapping.getIsGranted());
                        return newMapping;
                    })
                    .collect(Collectors.toList());
            
            rolePermissionMappingRepository.batchInsert(newPermissions);
        }

        log.info("复制角色成功: {} -> {}", sourceRole.getRoleCode(), newRole.getRoleCode());
        return convertEntityToVO(newRole);
    }

    /**
     * 构建角色树
     */
    private List<RoleVO> buildRoleTree(List<RoleDTO> roleDTOs, Long parentId) {
        List<RoleVO> result = new ArrayList<>();
        
        for (RoleDTO dto : roleDTOs) {
            if ((parentId == null && dto.getParentId() == null) || 
                (parentId != null && parentId.equals(dto.getParentId()))) {
                
                RoleVO vo = convertDTOToVO(dto);
                vo.setChildren(buildRoleTree(roleDTOs, dto.getId()));
                result.add(vo);
            }
        }
        
        return result;
    }

    /**
     * 检查是否会形成循环引用
     */
    private boolean isCircularReference(Long roleId, Long parentId) {
        if (parentId == null || parentId.equals(CommonConstants.ROOT_NODE_ID)) {
            return false;
        }
        
        List<Long> parentIds = roleRepository.selectParentRoleIds(parentId);
        return parentIds.contains(roleId);
    }

    /**
     * 转换实体到VO
     */
    private RoleVO convertEntityToVO(RoleDefinition entity) {
        RoleVO vo = new RoleVO();
        vo.setId(entity.getId());
        vo.setRoleCode(entity.getRoleCode());
        vo.setRoleName(entity.getRoleName());
        vo.setParentId(entity.getParentId());
        vo.setRoleLevel(entity.getRoleLevel());
        vo.setSortOrder(entity.getSortOrder());
        vo.setDescription(entity.getDescription());
        vo.setIsActive(entity.getIsActive());
        vo.setStatusName(entity.getIsActive() ? "正常" : "禁用");
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    /**
     * 转换DTO到VO
     */
    private RoleVO convertDTOToVO(RoleDTO dto) {
        RoleVO vo = new RoleVO();
        vo.setId(dto.getId());
        vo.setRoleCode(dto.getRoleCode());
        vo.setRoleName(dto.getRoleName());
        vo.setParentId(dto.getParentId());
        vo.setParentRoleName(dto.getParentRoleName());
        vo.setRoleLevel(dto.getRoleLevel());
        vo.setSortOrder(dto.getSortOrder());
        vo.setDescription(dto.getDescription());
        vo.setIsActive(dto.getIsActive());
        vo.setStatusName(dto.getIsActive() ? "正常" : "禁用");
        vo.setCreatedAt(dto.getCreatedAt());
        vo.setUpdatedAt(dto.getUpdatedAt());
        vo.setUserCount(dto.getUserCount());
        vo.setPermissionCount(dto.getPermissionCount());
        return vo;
    }
}