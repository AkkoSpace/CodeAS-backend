package space.akko.platform.role.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import space.akko.foundation.common.PageResult;
import space.akko.foundation.common.ResultCode;
import space.akko.foundation.constant.CacheConstants;
import space.akko.foundation.exception.BusinessException;
import space.akko.platform.role.model.dto.RoleDTO;
import space.akko.platform.role.model.entity.RoleDefinition;
import space.akko.platform.role.model.entity.UserRoleMapping;
import space.akko.platform.role.model.request.RoleCreateRequest;
import space.akko.platform.role.model.request.RoleQueryRequest;
import space.akko.platform.role.model.request.RoleUpdateRequest;
import space.akko.platform.role.model.vo.RoleVO;
import space.akko.platform.role.repository.RoleRepository;
import space.akko.platform.role.repository.UserRoleMappingRepository;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleVO createRole(RoleCreateRequest request) {
        // 检查角色编码是否已存在
        if (existsByRoleCode(request.getRoleCode())) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS, "角色编码已存在");
        }

        // 验证父角色
        if (request.getParentId() != null) {
            RoleDefinition parentRole = roleRepository.selectById(request.getParentId());
            if (parentRole == null) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "父角色不存在");
            }
        }

        // 创建角色
        RoleDefinition role = new RoleDefinition();
        BeanUtil.copyProperties(request, role);
        role.setIsSystem(false); // 非系统创建的角色都是普通角色

        roleRepository.insert(role);

        // 分配权限
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            assignPermissions(role.getId(), request.getPermissionIds());
        }

        log.info("创建角色成功: {}", role.getRoleCode());
        return convertToVO(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.ROLE_CACHE, key = "#roleId")
    public RoleVO updateRole(Long roleId, RoleUpdateRequest request) {
        RoleDefinition role = roleRepository.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }

        // 系统角色不允许修改某些字段
        if (role.getIsSystem()) {
            if (request.getParentId() != null || request.getRoleLevel() != null) {
                throw new BusinessException(ResultCode.BUSINESS_ERROR, "系统角色不允许修改层级结构");
            }
        }

        // 验证父角色
        if (request.getParentId() != null && !request.getParentId().equals(role.getParentId())) {
            // 不能将角色设置为自己的子角色
            List<Long> childRoleIds = roleRepository.selectChildRoleIds(roleId);
            if (childRoleIds.contains(request.getParentId())) {
                throw new BusinessException(ResultCode.BUSINESS_ERROR, "不能将角色设置为自己的子角色");
            }

            RoleDefinition parentRole = roleRepository.selectById(request.getParentId());
            if (parentRole == null) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "父角色不存在");
            }
        }

        // 更新角色信息
        BeanUtil.copyProperties(request, role, "id", "roleCode", "isSystem");
        roleRepository.updateById(role);

        // 更新权限
        if (request.getPermissionIds() != null) {
            assignPermissions(roleId, request.getPermissionIds());
        }

        log.info("更新角色成功: {}", role.getRoleCode());
        return convertToVO(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.ROLE_CACHE, key = "#roleId")
    public void deleteRole(Long roleId) {
        RoleDefinition role = roleRepository.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }

        // 系统角色不允许删除
        if (role.getIsSystem()) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "系统角色不允许删除");
        }

        // 检查是否可以删除
        if (!canDeleteRole(roleId)) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "角色下还有子角色或用户，不能删除");
        }

        // 删除角色
        roleRepository.deleteById(roleId);

        // 删除相关关联数据
        userRoleMappingRepository.deleteByRoleId(roleId);

        log.info("删除角色成功: {}", role.getRoleCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }

        for (Long roleId : roleIds) {
            deleteRole(roleId);
        }

        log.info("批量删除角色成功，数量: {}", roleIds.size());
    }

    @Override
    @Cacheable(value = CacheConstants.ROLE_CACHE, key = "#roleId")
    public RoleVO getRoleById(Long roleId) {
        RoleDTO roleDTO = roleRepository.selectRoleDetailById(roleId);
        if (roleDTO == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }
        return convertDTOToVO(roleDTO);
    }

    @Override
    public RoleDefinition getRoleByCode(String roleCode) {
        return roleRepository.findByRoleCode(roleCode);
    }

    @Override
    public PageResult<RoleVO> getRolePage(RoleQueryRequest request) {
        Page<RoleDTO> page = new Page<>(request.getCurrent(), request.getSize());
        IPage<RoleDTO> result = roleRepository.selectRolePage(page, request);
        
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
    public List<RoleVO> getAllRoles() {
        RoleQueryRequest request = new RoleQueryRequest();
        request.setSize(1000L); // 获取所有角色
        PageResult<RoleVO> result = getRolePage(request);
        return result.getRecords();
    }

    @Override
    @CacheEvict(value = CacheConstants.ROLE_CACHE, key = "#roleId")
    public void updateRoleStatus(Long roleId, Boolean isActive) {
        RoleDefinition role = roleRepository.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }

        role.setIsActive(isActive);
        roleRepository.updateById(role);

        log.info("更新角色状态成功: {} -> {}", role.getRoleCode(), isActive);
    }

    @Override
    public void batchUpdateRoleStatus(List<Long> roleIds, Boolean isActive) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }

        roleRepository.batchUpdateStatus(roleIds, isActive);
        log.info("批量更新角色状态成功，数量: {}", roleIds.size());
    }

    // 其他方法的实现...
    // 由于篇幅限制，这里只展示部分核心方法的实现

    /**
     * 转换为VO
     */
    private RoleVO convertToVO(RoleDefinition role) {
        RoleVO vo = new RoleVO();
        BeanUtil.copyProperties(role, vo);
        return vo;
    }

    /**
     * 转换DTO为VO
     */
    private RoleVO convertDTOToVO(RoleDTO dto) {
        RoleVO vo = new RoleVO();
        BeanUtil.copyProperties(dto, vo);
        return vo;
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

    // 需要继续实现的方法...
    @Override
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // TODO: 实现权限分配
    }

    @Override
    public void removePermissions(Long roleId, List<Long> permissionIds) {
        // TODO: 实现权限移除
    }

    @Override
    public List<String> getRolePermissions(Long roleId) {
        return roleRepository.selectRolePermissions(roleId);
    }

    @Override
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        // 先删除用户现有角色
        userRoleMappingRepository.deleteByUserId(userId);
        
        // 添加新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRoleMapping> mappings = roleIds.stream()
                    .map(roleId -> {
                        UserRoleMapping mapping = new UserRoleMapping();
                        mapping.setUserId(userId);
                        mapping.setRoleId(roleId);
                        mapping.setEffectiveFrom(LocalDateTime.now());
                        mapping.setIsActive(true);
                        return mapping;
                    })
                    .collect(Collectors.toList());
            
            userRoleMappingRepository.batchInsert(mappings);
        }
    }

    @Override
    public void removeRolesFromUser(Long userId, List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                userRoleMappingRepository.deleteByUserIdAndRoleId(userId, roleId);
            }
        }
    }

    @Override
    public void setUserRoleEffectiveTime(Long userId, Long roleId, 
                                       LocalDateTime effectiveFrom, LocalDateTime effectiveTo) {
        userRoleMappingRepository.updateEffectiveTime(userId, roleId, effectiveFrom, effectiveTo);
    }

    @Override
    public List<RoleDefinition> getUserRoles(Long userId) {
        return roleRepository.selectRolesByUserId(userId);
    }

    @Override
    public Long getRoleUserCount(Long roleId) {
        return roleRepository.countUsersByRoleId(roleId);
    }

    @Override
    public boolean existsByRoleCode(String roleCode) {
        return roleRepository.existsByRoleCode(roleCode);
    }

    @Override
    public boolean canDeleteRole(Long roleId) {
        return roleRepository.canDelete(roleId);
    }

    @Override
    public List<RoleVO> getRoleHierarchyPath(Long roleId) {
        // TODO: 实现角色层级路径获取
        return new ArrayList<>();
    }

    @Override
    public void moveRole(Long roleId, Long newParentId) {
        // TODO: 实现角色移动
    }

    @Override
    public RoleVO copyRole(Long sourceRoleId, String newRoleCode, String newRoleName) {
        // TODO: 实现角色复制
        return null;
    }

    @Override
    public void cleanExpiredRoleMappings() {
        List<UserRoleMapping> expiredMappings = userRoleMappingRepository.selectExpiredMappings();
        if (!expiredMappings.isEmpty()) {
            List<Long> mappingIds = expiredMappings.stream()
                    .map(UserRoleMapping::getId)
                    .collect(Collectors.toList());
            userRoleMappingRepository.batchUpdateStatus(mappingIds, false);
            log.info("清理过期角色关联，数量: {}", mappingIds.size());
        }
    }
}
