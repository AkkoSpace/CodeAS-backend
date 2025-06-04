package space.akko.platform.permission.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.akko.platform.permission.model.dto.PermissionCreateRequest;
import space.akko.platform.permission.model.dto.PermissionDTO;
import space.akko.platform.permission.model.entity.PermissionResource;
import space.akko.platform.permission.model.entity.RolePermissionMapping;
import space.akko.platform.permission.model.vo.PermissionVO;
import space.akko.platform.permission.repository.PermissionRepository;
import space.akko.platform.permission.repository.RolePermissionMappingRepository;
import space.akko.platform.permission.service.impl.PermissionServiceImpl;
import space.akko.platform.role.model.entity.RoleDefinition;
import space.akko.platform.role.repository.RoleRepository;
import space.akko.platform.role.service.impl.RoleServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 权限管理集成测试
 * 测试权限和角色的完整交互流程
 * 
 * @author akko
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限管理集成测试")
class PermissionIntegrationTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RolePermissionMappingRepository rolePermissionMappingRepository;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    @DisplayName("完整权限管理流程 - 创建权限并分配给角色")
    void completePermissionManagementFlow() {
        // Given - 准备测试数据
        PermissionCreateRequest createRequest = createPermissionCreateRequest();
        PermissionResource createdPermission = createTestPermission();
        PermissionDTO permissionDTO = createTestPermissionDTO();
        RoleDefinition testRole = createTestRole();

        // Mock 权限创建
        when(permissionRepository.existsByResourceCode(createRequest.getResourceCode())).thenReturn(false);
        when(permissionRepository.insert(any(PermissionResource.class))).thenReturn(1);
        when(permissionRepository.selectPermissionDetailById(any(Long.class))).thenReturn(permissionDTO);

        // Mock 角色查询
        when(roleRepository.selectById(1L)).thenReturn(testRole);
        when(rolePermissionMappingRepository.selectByRoleId(1L)).thenReturn(Arrays.asList());

        // When - 执行完整流程
        // 1. 创建权限
        PermissionVO createdPermissionVO = permissionService.createPermission(createRequest);
        
        // 2. 分配权限给角色
        List<Long> permissionIds = Arrays.asList(createdPermissionVO.getId());
        roleService.assignPermissions(1L, permissionIds);

        // Then - 验证结果
        // 验证权限创建
        assertNotNull(createdPermissionVO);
        assertEquals(createRequest.getResourceCode(), createdPermissionVO.getResourceCode());
        assertEquals(createRequest.getResourceName(), createdPermissionVO.getResourceName());

        // 验证权限分配
        verify(permissionRepository).existsByResourceCode(createRequest.getResourceCode());
        verify(permissionRepository).insert(any(PermissionResource.class));
        verify(roleRepository).selectById(1L);
        verify(rolePermissionMappingRepository).selectByRoleId(1L);
        verify(rolePermissionMappingRepository).batchInsert(anyList());
    }

    @Test
    @DisplayName("权限层级管理 - 创建父子权限关系")
    void hierarchicalPermissionManagement() {
        // Given - 准备父子权限数据
        PermissionCreateRequest parentRequest = createPermissionCreateRequest();
        parentRequest.setResourceCode("USER_MODULE");
        parentRequest.setResourceName("用户模块");
        parentRequest.setParentId(null);

        PermissionCreateRequest childRequest = createPermissionCreateRequest();
        childRequest.setResourceCode("USER_READ");
        childRequest.setResourceName("用户查看");
        childRequest.setParentId(1L);

        PermissionDTO parentDTO = createTestPermissionDTO();
        parentDTO.setId(1L);
        parentDTO.setResourceCode("USER_MODULE");
        parentDTO.setParentId(null);

        PermissionDTO childDTO = createTestPermissionDTO();
        childDTO.setId(2L);
        childDTO.setResourceCode("USER_READ");
        childDTO.setParentId(1L);

        // Mock 父权限创建
        when(permissionRepository.existsByResourceCode("USER_MODULE")).thenReturn(false);
        when(permissionRepository.insert(any(PermissionResource.class))).thenReturn(1);
        when(permissionRepository.selectPermissionDetailById(1L)).thenReturn(parentDTO);

        // Mock 子权限创建
        when(permissionRepository.existsByResourceCode("USER_READ")).thenReturn(false);
        when(permissionRepository.selectPermissionDetailById(2L)).thenReturn(childDTO);

        // Mock 权限树查询
        when(permissionRepository.selectPermissionTree(null)).thenReturn(Arrays.asList(parentDTO));
        when(permissionRepository.selectPermissionTree(1L)).thenReturn(Arrays.asList(childDTO));

        // When - 执行层级权限创建
        // 1. 创建父权限
        PermissionVO parentPermission = permissionService.createPermission(parentRequest);
        
        // 2. 创建子权限
        PermissionVO childPermission = permissionService.createPermission(childRequest);
        
        // 3. 获取权限树
        List<PermissionVO> permissionTree = permissionService.getPermissionTree(null);

        // Then - 验证层级关系
        assertNotNull(parentPermission);
        assertNotNull(childPermission);
        assertNotNull(permissionTree);
        
        assertEquals("USER_MODULE", parentPermission.getResourceCode());
        assertEquals("USER_READ", childPermission.getResourceCode());
        assertEquals(1L, childPermission.getParentId());
        
        verify(permissionRepository, times(2)).insert(any(PermissionResource.class));
        verify(permissionRepository).selectPermissionTree(null);
    }

    @Test
    @DisplayName("权限状态管理 - 批量更新权限状态")
    void permissionStatusManagement() {
        // Given
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        Boolean newStatus = false;
        
        when(permissionRepository.batchUpdateStatus(permissionIds, newStatus)).thenReturn(3);

        // When
        permissionService.batchUpdatePermissionStatus(permissionIds, newStatus);

        // Then
        verify(permissionRepository).batchUpdateStatus(permissionIds, newStatus);
    }

    @Test
    @DisplayName("角色权限查询 - 获取角色的所有权限")
    void rolePermissionQuery() {
        // Given
        Long roleId = 1L;
        RoleDefinition testRole = createTestRole();
        List<String> expectedPermissions = Arrays.asList("USER_READ", "USER_WRITE", "USER_DELETE");
        
        when(roleRepository.selectById(roleId)).thenReturn(testRole);
        when(rolePermissionMappingRepository.selectPermissionCodesByRoleId(roleId)).thenReturn(expectedPermissions);

        // When
        List<String> rolePermissions = roleService.getRolePermissions(roleId);

        // Then
        assertNotNull(rolePermissions);
        assertEquals(expectedPermissions.size(), rolePermissions.size());
        assertEquals(expectedPermissions, rolePermissions);
        
        verify(roleRepository).selectById(roleId);
        verify(rolePermissionMappingRepository).selectPermissionCodesByRoleId(roleId);
    }

    @Test
    @DisplayName("用户权限查询 - 通过角色获取用户权限")
    void userPermissionQuery() {
        // Given
        Long userId = 1L;
        List<PermissionResource> expectedPermissions = Arrays.asList(createTestPermission());
        
        when(permissionRepository.selectPermissionsByUserId(userId)).thenReturn(expectedPermissions);

        // When
        List<PermissionResource> userPermissions = permissionService.getPermissionsByUserId(userId);

        // Then
        assertNotNull(userPermissions);
        assertFalse(userPermissions.isEmpty());
        assertEquals(expectedPermissions.size(), userPermissions.size());
        
        verify(permissionRepository).selectPermissionsByUserId(userId);
    }

    @Test
    @DisplayName("权限删除流程 - 检查依赖关系后删除")
    void permissionDeletionFlow() {
        // Given
        Long permissionId = 1L;
        PermissionResource testPermission = createTestPermission();
        
        when(permissionRepository.selectById(permissionId)).thenReturn(testPermission);
        when(permissionRepository.canDelete(permissionId)).thenReturn(true);
        when(permissionRepository.deleteById(permissionId)).thenReturn(1);

        // When
        permissionService.deletePermission(permissionId);

        // Then
        verify(permissionRepository).selectById(permissionId);
        verify(permissionRepository).canDelete(permissionId);
        verify(permissionRepository).deleteById(permissionId);
    }

    @Test
    @DisplayName("权限同步流程 - 模拟API权限自动同步")
    void permissionSyncFlow() {
        // Given
        String resourceType = "API";
        List<PermissionResource> existingApiPermissions = Arrays.asList(createTestPermission());
        
        when(permissionRepository.selectPermissionsByType(resourceType)).thenReturn(existingApiPermissions);

        // When
        List<PermissionResource> apiPermissions = permissionService.getPermissionsByType(resourceType);

        // Then
        assertNotNull(apiPermissions);
        assertFalse(apiPermissions.isEmpty());
        assertEquals(existingApiPermissions.size(), apiPermissions.size());
        
        verify(permissionRepository).selectPermissionsByType(resourceType);
    }

    // 辅助方法
    private PermissionCreateRequest createPermissionCreateRequest() {
        PermissionCreateRequest request = new PermissionCreateRequest();
        request.setResourceCode("USER_MANAGEMENT");
        request.setResourceName("用户管理");
        request.setResourceType("API");
        request.setResourceUrl("/api/users");
        request.setHttpMethod("GET");
        request.setParentId(0L);
        request.setSortOrder(1);
        request.setDescription("用户管理相关接口");
        return request;
    }

    private PermissionResource createTestPermission() {
        PermissionResource permission = new PermissionResource();
        permission.setId(1L);
        permission.setResourceCode("USER_MANAGEMENT");
        permission.setResourceName("用户管理");
        permission.setResourceType("API");
        permission.setResourceUrl("/api/users");
        permission.setHttpMethod("GET");
        permission.setParentId(0L);
        permission.setSortOrder(1);
        permission.setDescription("用户管理相关接口");
        permission.setIsActive(true);
        return permission;
    }

    private PermissionDTO createTestPermissionDTO() {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(1L);
        dto.setResourceCode("USER_MANAGEMENT");
        dto.setResourceName("用户管理");
        dto.setResourceType("API");
        dto.setResourceUrl("/api/users");
        dto.setHttpMethod("GET");
        dto.setParentId(0L);
        dto.setSortOrder(1);
        dto.setDescription("用户管理相关接口");
        dto.setIsActive(true);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        return dto;
    }

    private RoleDefinition createTestRole() {
        RoleDefinition role = new RoleDefinition();
        role.setId(1L);
        role.setRoleCode("TEST_ROLE");
        role.setRoleName("测试角色");
        role.setRoleLevel(1);
        role.setDescription("测试角色描述");
        role.setIsSystem(false);
        role.setIsActive(true);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        return role;
    }
}
