package space.akko.platform.permission.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import space.akko.foundation.common.PageResult;
import space.akko.foundation.constant.CacheConstants;
import space.akko.foundation.constant.CommonConstants;
import space.akko.foundation.enums.ResultCode;
import space.akko.foundation.exception.BusinessException;
import space.akko.platform.permission.model.dto.PermissionDTO;
import space.akko.platform.permission.model.entity.PermissionResource;
import space.akko.platform.permission.model.request.PermissionCreateRequest;
import space.akko.platform.permission.model.request.PermissionQueryRequest;
import space.akko.platform.permission.model.vo.PermissionVO;
import space.akko.platform.permission.repository.PermissionRepository;
import space.akko.platform.permission.service.PermissionService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现
 * 
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.PERMISSION_CACHE, allEntries = true)
    public PermissionVO createPermission(PermissionCreateRequest request) {
        // 检查资源编码是否已存在
        if (existsByResourceCode(request.getResourceCode())) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS, "资源编码已存在");
        }

        // 验证父权限
        if (request.getParentId() != null && !request.getParentId().equals(CommonConstants.ROOT_NODE_ID)) {
            PermissionResource parentPermission = permissionRepository.selectById(request.getParentId());
            if (parentPermission == null) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "父权限不存在");
            }
        }

        // 验证URL和方法的唯一性（对于API类型）
        if ("API".equals(request.getResourceType()) && 
            StringUtils.hasText(request.getResourceUrl()) && 
            StringUtils.hasText(request.getHttpMethod())) {
            
            PermissionResource existing = getPermissionByUrlAndMethod(request.getResourceUrl(), request.getHttpMethod());
            if (existing != null) {
                throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS, 
                    String.format("API权限已存在: %s %s", request.getHttpMethod(), request.getResourceUrl()));
            }
        }

        // 创建权限实体
        PermissionResource permission = new PermissionResource();
        permission.setResourceCode(request.getResourceCode());
        permission.setResourceName(request.getResourceName());
        permission.setResourceType(request.getResourceType());
        permission.setResourceUrl(request.getResourceUrl());
        permission.setHttpMethod(request.getHttpMethod());
        permission.setParentId(request.getParentId());
        permission.setSortOrder(request.getSortOrder());
        permission.setDescription(request.getDescription());
        permission.setIsActive(request.getIsActive());

        // 保存到数据库
        permissionRepository.insert(permission);

        log.info("创建权限成功: {}", permission.getResourceCode());
        return convertEntityToVO(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.PERMISSION_CACHE, allEntries = true)
    public PermissionVO updatePermission(Long permissionId, PermissionCreateRequest request) {
        // 检查权限是否存在
        PermissionResource permission = permissionRepository.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "权限不存在");
        }

        // 检查资源编码是否已被其他权限使用
        if (!permission.getResourceCode().equals(request.getResourceCode()) && 
            existsByResourceCode(request.getResourceCode())) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS, "资源编码已存在");
        }

        // 验证父权限（不能设置自己为父权限）
        if (request.getParentId() != null && 
            !request.getParentId().equals(CommonConstants.ROOT_NODE_ID) &&
            !request.getParentId().equals(permissionId)) {
            
            PermissionResource parentPermission = permissionRepository.selectById(request.getParentId());
            if (parentPermission == null) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "父权限不存在");
            }

            // 检查是否会形成循环引用
            if (isCircularReference(permissionId, request.getParentId())) {
                throw new BusinessException(ResultCode.INVALID_PARAMETER, "不能设置子权限为父权限，会形成循环引用");
            }
        }

        // 验证URL和方法的唯一性（对于API类型）
        if ("API".equals(request.getResourceType()) && 
            StringUtils.hasText(request.getResourceUrl()) && 
            StringUtils.hasText(request.getHttpMethod())) {
            
            PermissionResource existing = getPermissionByUrlAndMethod(request.getResourceUrl(), request.getHttpMethod());
            if (existing != null && !existing.getId().equals(permissionId)) {
                throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS, 
                    String.format("API权限已存在: %s %s", request.getHttpMethod(), request.getResourceUrl()));
            }
        }

        // 更新权限信息
        permission.setResourceCode(request.getResourceCode());
        permission.setResourceName(request.getResourceName());
        permission.setResourceType(request.getResourceType());
        permission.setResourceUrl(request.getResourceUrl());
        permission.setHttpMethod(request.getHttpMethod());
        permission.setParentId(request.getParentId());
        permission.setSortOrder(request.getSortOrder());
        permission.setDescription(request.getDescription());
        permission.setIsActive(request.getIsActive());

        // 保存到数据库
        permissionRepository.updateById(permission);

        log.info("更新权限成功: {}", permission.getResourceCode());
        return convertEntityToVO(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.PERMISSION_CACHE, allEntries = true)
    public void deletePermission(Long permissionId) {
        // 检查权限是否存在
        PermissionResource permission = permissionRepository.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "权限不存在");
        }

        // 检查是否可以删除
        if (!canDeletePermission(permissionId)) {
            throw new BusinessException(ResultCode.OPERATION_NOT_ALLOWED, "权限存在子权限或已被角色使用，无法删除");
        }

        // 删除权限
        permissionRepository.deleteById(permissionId);

        log.info("删除权限成功: {}", permission.getResourceCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.PERMISSION_CACHE, allEntries = true)
    public void batchDeletePermissions(List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new BusinessException(ResultCode.INVALID_PARAMETER, "权限ID列表不能为空");
        }

        // 检查所有权限是否都可以删除
        for (Long permissionId : permissionIds) {
            if (!canDeletePermission(permissionId)) {
                PermissionResource permission = permissionRepository.selectById(permissionId);
                String resourceCode = permission != null ? permission.getResourceCode() : String.valueOf(permissionId);
                throw new BusinessException(ResultCode.OPERATION_NOT_ALLOWED, 
                    String.format("权限 %s 存在子权限或已被角色使用，无法删除", resourceCode));
            }
        }

        // 批量删除
        permissionRepository.deleteBatchIds(permissionIds);

        log.info("批量删除权限成功，数量: {}", permissionIds.size());
    }

    @Override
    @Cacheable(value = CacheConstants.PERMISSION_CACHE, key = "'detail:' + #permissionId")
    public PermissionVO getPermissionById(Long permissionId) {
        PermissionDTO permissionDTO = permissionRepository.selectPermissionDetailById(permissionId);
        if (permissionDTO == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "权限不存在");
        }
        return convertDTOToVO(permissionDTO);
    }

    @Override
    @Cacheable(value = CacheConstants.PERMISSION_CACHE, key = "'code:' + #resourceCode")
    public PermissionResource getPermissionByCode(String resourceCode) {
        return permissionRepository.findByResourceCode(resourceCode);
    }

    @Override
    public PageResult<PermissionVO> getPermissionPage(PermissionQueryRequest request) {
        PermissionQueryRequest queryRequest = request;
        Page<PermissionDTO> page = new Page<>(queryRequest.getCurrent(), queryRequest.getSize());
        IPage<PermissionDTO> result = permissionRepository.selectPermissionPage(page, queryRequest);
        
        List<PermissionVO> records = result.getRecords().stream()
                .map(this::convertDTOToVO)
                .collect(Collectors.toList());
        
        return PageResult.of(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Cacheable(value = CacheConstants.PERMISSION_CACHE, key = "'tree:' + #parentId")
    public List<PermissionVO> getPermissionTree(Long parentId) {
        List<PermissionDTO> permissionDTOs = permissionRepository.selectPermissionTree(parentId);
        return buildPermissionTree(permissionDTOs, parentId);
    }

    @Override
    @Cacheable(value = CacheConstants.PERMISSION_CACHE, key = "'all'")
    public List<PermissionVO> getAllPermissions() {
        List<PermissionDTO> permissionDTOs = permissionRepository.selectPermissionTree(null);
        return permissionDTOs.stream()
                .map(this::convertDTOToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.PERMISSION_CACHE, allEntries = true)
    public void updatePermissionStatus(Long permissionId, Boolean isActive) {
        PermissionResource permission = permissionRepository.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "权限不存在");
        }

        permission.setIsActive(isActive);
        permissionRepository.updateById(permission);

        log.info("更新权限状态成功: {} -> {}", permission.getResourceCode(), isActive ? "启用" : "禁用");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.PERMISSION_CACHE, allEntries = true)
    public void batchUpdatePermissionStatus(List<Long> permissionIds, Boolean isActive) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new BusinessException(ResultCode.INVALID_PARAMETER, "权限ID列表不能为空");
        }

        int updatedCount = permissionRepository.batchUpdateStatus(permissionIds, isActive);
        
        log.info("批量更新权限状态成功，数量: {}, 状态: {}", updatedCount, isActive ? "启用" : "禁用");
    }

    @Override
    @Cacheable(value = CacheConstants.PERMISSION_CACHE, key = "'role:' + #roleId")
    public List<PermissionResource> getPermissionsByRoleId(Long roleId) {
        return permissionRepository.selectPermissionsByRoleId(roleId);
    }

    @Override
    @Cacheable(value = CacheConstants.PERMISSION_CACHE, key = "'user:' + #userId")
    public List<PermissionResource> getPermissionsByUserId(Long userId) {
        return permissionRepository.selectPermissionsByUserId(userId);
    }

    @Override
    public boolean existsByResourceCode(String resourceCode) {
        return permissionRepository.existsByResourceCode(resourceCode);
    }

    @Override
    public boolean canDeletePermission(Long permissionId) {
        return permissionRepository.canDelete(permissionId);
    }

    @Override
    @Cacheable(value = CacheConstants.PERMISSION_CACHE, key = "'hierarchy:' + #permissionId")
    public List<PermissionVO> getPermissionHierarchyPath(Long permissionId) {
        List<PermissionVO> path = new ArrayList<>();
        
        PermissionResource current = permissionRepository.selectById(permissionId);
        while (current != null) {
            path.add(0, convertEntityToVO(current)); // 添加到开头，保持从根到叶的顺序
            
            if (current.getParentId() == null || current.getParentId().equals(CommonConstants.ROOT_NODE_ID)) {
                break;
            }
            
            current = permissionRepository.selectById(current.getParentId());
        }
        
        return path;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.PERMISSION_CACHE, allEntries = true)
    public void movePermission(Long permissionId, Long newParentId) {
        // 检查权限是否存在
        PermissionResource permission = permissionRepository.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "权限不存在");
        }

        // 检查新父权限是否存在（除了根节点）
        if (newParentId != null && !newParentId.equals(CommonConstants.ROOT_NODE_ID)) {
            PermissionResource newParent = permissionRepository.selectById(newParentId);
            if (newParent == null) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "新父权限不存在");
            }
        }

        // 检查是否会形成循环引用
        if (newParentId != null && !newParentId.equals(CommonConstants.ROOT_NODE_ID)) {
            if (isCircularReference(permissionId, newParentId)) {
                throw new BusinessException(ResultCode.INVALID_PARAMETER, "不能移动到子权限下，会形成循环引用");
            }
        }

        // 更新父权限
        permission.setParentId(newParentId);
        permissionRepository.updateById(permission);

        log.info("移动权限成功: {} -> 新父权限ID: {}", permission.getResourceCode(), newParentId);
    }

    @Override
    public List<PermissionResource> getPermissionsByType(String resourceType) {
        return permissionRepository.selectPermissionsByType(resourceType);
    }

    @Override
    public PermissionResource getPermissionByUrlAndMethod(String resourceUrl, String httpMethod) {
        return permissionRepository.findByUrlAndMethod(resourceUrl, httpMethod);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.PERMISSION_CACHE, allEntries = true)
    public void syncApiPermissions() {
        // TODO: 实现API权限同步功能
        // 这个功能需要扫描所有Controller类和方法上的注解
        // 自动创建或更新API权限
        log.info("API权限同步功能待实现");
        throw new BusinessException(ResultCode.NOT_IMPLEMENTED, "API权限同步功能待实现");
    }
    
    /**
     * 构建权限树
     */
    private List<PermissionVO> buildPermissionTree(List<PermissionDTO> permissionDTOs, Long parentId) {
        List<PermissionVO> result = new ArrayList<>();
        
        for (PermissionDTO dto : permissionDTOs) {
            if ((parentId == null && dto.getParentId() == null) || 
                (parentId != null && parentId.equals(dto.getParentId()))) {
                
                PermissionVO vo = convertDTOToVO(dto);
                vo.setChildren(buildPermissionTree(permissionDTOs, dto.getId()));
                result.add(vo);
            }
        }
        
        return result;
    }

    /**
     * 检查是否会形成循环引用
     */
    private boolean isCircularReference(Long permissionId, Long parentId) {
        if (parentId == null || parentId.equals(CommonConstants.ROOT_NODE_ID)) {
            return false;
        }
        
        List<Long> parentIds = permissionRepository.selectParentPermissionIds(parentId);
        return parentIds.contains(permissionId);
    }

    /**
     * 转换实体到VO
     */
    private PermissionVO convertEntityToVO(PermissionResource entity) {
        PermissionVO vo = new PermissionVO();
        vo.setId(entity.getId());
        vo.setResourceCode(entity.getResourceCode());
        vo.setResourceName(entity.getResourceName());
        vo.setResourceType(entity.getResourceType());
        vo.setResourceTypeName(getResourceTypeName(entity.getResourceType()));
        vo.setResourceUrl(entity.getResourceUrl());
        vo.setHttpMethod(entity.getHttpMethod());
        vo.setParentId(entity.getParentId());
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
    private PermissionVO convertDTOToVO(PermissionDTO dto) {
        PermissionVO vo = new PermissionVO();
        vo.setId(dto.getId());
        vo.setResourceCode(dto.getResourceCode());
        vo.setResourceName(dto.getResourceName());
        vo.setResourceType(dto.getResourceType());
        vo.setResourceTypeName(getResourceTypeName(dto.getResourceType()));
        vo.setResourceUrl(dto.getResourceUrl());
        vo.setHttpMethod(dto.getHttpMethod());
        vo.setParentId(dto.getParentId());
        vo.setParentResourceName(dto.getParentResourceName());
        vo.setSortOrder(dto.getSortOrder());
        vo.setDescription(dto.getDescription());
        vo.setIsActive(dto.getIsActive());
        vo.setStatusName(dto.getIsActive() ? "正常" : "禁用");
        vo.setCreatedAt(dto.getCreatedAt());
        vo.setUpdatedAt(dto.getUpdatedAt());
        
        // 转换动作信息
        if (dto.getActions() != null) {
            List<PermissionVO.ActionInfo> actions = dto.getActions().stream()
                    .map(this::convertActionDTOToVO)
                    .collect(Collectors.toList());
            vo.setActions(actions);
        }
        
        return vo;
    }

    /**
     * 转换动作DTO到VO
     */
    private PermissionVO.ActionInfo convertActionDTOToVO(PermissionDTO.ActionInfo dto) {
        PermissionVO.ActionInfo vo = new PermissionVO.ActionInfo();
        vo.setId(dto.getId());
        vo.setActionCode(dto.getActionCode());
        vo.setActionName(dto.getActionName());
        vo.setDescription(dto.getDescription());
        vo.setIsActive(dto.getIsActive());
        vo.setStatusName(dto.getIsActive() ? "正常" : "禁用");
        return vo;
    }

    /**
     * 获取资源类型显示名称
     */
    private String getResourceTypeName(String resourceType) {
        return switch (resourceType) {
            case "API" -> "接口";
            case "MENU" -> "菜单";
            case "BUTTON" -> "按钮";
            case "DATA" -> "数据";
            default -> resourceType;
        };
    }
}