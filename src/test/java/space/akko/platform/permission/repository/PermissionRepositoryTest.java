package space.akko.platform.permission.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.akko.platform.permission.model.dto.PermissionDTO;
import space.akko.platform.permission.model.entity.PermissionResource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 权限Repository测试
 * 
 * @author akko
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限Repository测试")
class PermissionRepositoryTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Test
    @DisplayName("根据资源编码查找权限 - 成功")
    void findByResourceCode_Success() {
        // Given
        String resourceCode = "USER_MANAGEMENT";
        PermissionResource expectedPermission = createTestPermission();
        
        when(permissionRepository.findByResourceCode(resourceCode)).thenReturn(expectedPermission);

        // When
        PermissionResource result = permissionRepository.findByResourceCode(resourceCode);

        // Then
        assertNotNull(result);
        assertEquals(expectedPermission.getResourceCode(), result.getResourceCode());
        verify(permissionRepository).findByResourceCode(resourceCode);
    }

    @Test
    @DisplayName("根据资源编码查找权限 - 不存在")
    void findByResourceCode_NotFound() {
        // Given
        String resourceCode = "NON_EXISTENT";
        
        when(permissionRepository.findByResourceCode(resourceCode)).thenReturn(null);

        // When
        PermissionResource result = permissionRepository.findByResourceCode(resourceCode);

        // Then
        assertNull(result);
        verify(permissionRepository).findByResourceCode(resourceCode);
    }

    @Test
    @DisplayName("检查资源编码是否存在 - 存在")
    void existsByResourceCode_Exists() {
        // Given
        String resourceCode = "USER_MANAGEMENT";
        
        when(permissionRepository.existsByResourceCode(resourceCode)).thenReturn(true);

        // When
        boolean result = permissionRepository.existsByResourceCode(resourceCode);

        // Then
        assertTrue(result);
        verify(permissionRepository).existsByResourceCode(resourceCode);
    }

    @Test
    @DisplayName("检查资源编码是否存在 - 不存在")
    void existsByResourceCode_NotExists() {
        // Given
        String resourceCode = "NON_EXISTENT";
        
        when(permissionRepository.existsByResourceCode(resourceCode)).thenReturn(false);

        // When
        boolean result = permissionRepository.existsByResourceCode(resourceCode);

        // Then
        assertFalse(result);
        verify(permissionRepository).existsByResourceCode(resourceCode);
    }

    @Test
    @DisplayName("根据权限ID查询权限详情 - 成功")
    void selectPermissionDetailById_Success() {
        // Given
        Long permissionId = 1L;
        PermissionDTO expectedDTO = createTestPermissionDTO();
        
        when(permissionRepository.selectPermissionDetailById(permissionId)).thenReturn(expectedDTO);

        // When
        PermissionDTO result = permissionRepository.selectPermissionDetailById(permissionId);

        // Then
        assertNotNull(result);
        assertEquals(expectedDTO.getId(), result.getId());
        assertEquals(expectedDTO.getResourceCode(), result.getResourceCode());
        verify(permissionRepository).selectPermissionDetailById(permissionId);
    }

    @Test
    @DisplayName("根据权限ID查询权限详情 - 不存在")
    void selectPermissionDetailById_NotFound() {
        // Given
        Long permissionId = 999L;
        
        when(permissionRepository.selectPermissionDetailById(permissionId)).thenReturn(null);

        // When
        PermissionDTO result = permissionRepository.selectPermissionDetailById(permissionId);

        // Then
        assertNull(result);
        verify(permissionRepository).selectPermissionDetailById(permissionId);
    }

    @Test
    @DisplayName("查询权限树 - 根节点")
    void selectPermissionTree_RootLevel() {
        // Given
        Long parentId = null;
        List<PermissionDTO> expectedTree = Arrays.asList(createTestPermissionDTO());
        
        when(permissionRepository.selectPermissionTree(parentId)).thenReturn(expectedTree);

        // When
        List<PermissionDTO> result = permissionRepository.selectPermissionTree(parentId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expectedTree.size(), result.size());
        verify(permissionRepository).selectPermissionTree(parentId);
    }

    @Test
    @DisplayName("查询权限树 - 指定父节点")
    void selectPermissionTree_SpecificParent() {
        // Given
        Long parentId = 1L;
        List<PermissionDTO> expectedTree = Arrays.asList(createTestPermissionDTO());
        
        when(permissionRepository.selectPermissionTree(parentId)).thenReturn(expectedTree);

        // When
        List<PermissionDTO> result = permissionRepository.selectPermissionTree(parentId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expectedTree.size(), result.size());
        verify(permissionRepository).selectPermissionTree(parentId);
    }

    @Test
    @DisplayName("根据角色ID查询权限列表 - 成功")
    void selectPermissionsByRoleId_Success() {
        // Given
        Long roleId = 1L;
        List<PermissionResource> expectedPermissions = Arrays.asList(createTestPermission());
        
        when(permissionRepository.selectPermissionsByRoleId(roleId)).thenReturn(expectedPermissions);

        // When
        List<PermissionResource> result = permissionRepository.selectPermissionsByRoleId(roleId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expectedPermissions.size(), result.size());
        verify(permissionRepository).selectPermissionsByRoleId(roleId);
    }

    @Test
    @DisplayName("根据角色ID查询权限列表 - 无权限")
    void selectPermissionsByRoleId_NoPermissions() {
        // Given
        Long roleId = 1L;
        
        when(permissionRepository.selectPermissionsByRoleId(roleId)).thenReturn(Arrays.asList());

        // When
        List<PermissionResource> result = permissionRepository.selectPermissionsByRoleId(roleId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(permissionRepository).selectPermissionsByRoleId(roleId);
    }

    @Test
    @DisplayName("根据用户ID查询权限列表 - 成功")
    void selectPermissionsByUserId_Success() {
        // Given
        Long userId = 1L;
        List<PermissionResource> expectedPermissions = Arrays.asList(createTestPermission());
        
        when(permissionRepository.selectPermissionsByUserId(userId)).thenReturn(expectedPermissions);

        // When
        List<PermissionResource> result = permissionRepository.selectPermissionsByUserId(userId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expectedPermissions.size(), result.size());
        verify(permissionRepository).selectPermissionsByUserId(userId);
    }

    @Test
    @DisplayName("根据用户ID查询权限列表 - 无权限")
    void selectPermissionsByUserId_NoPermissions() {
        // Given
        Long userId = 1L;
        
        when(permissionRepository.selectPermissionsByUserId(userId)).thenReturn(Arrays.asList());

        // When
        List<PermissionResource> result = permissionRepository.selectPermissionsByUserId(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(permissionRepository).selectPermissionsByUserId(userId);
    }

    @Test
    @DisplayName("查询子权限列表 - 成功")
    void selectChildPermissions_Success() {
        // Given
        Long parentId = 1L;
        List<PermissionResource> expectedChildren = Arrays.asList(createTestPermission());
        
        when(permissionRepository.selectChildPermissions(parentId)).thenReturn(expectedChildren);

        // When
        List<PermissionResource> result = permissionRepository.selectChildPermissions(parentId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expectedChildren.size(), result.size());
        verify(permissionRepository).selectChildPermissions(parentId);
    }

    @Test
    @DisplayName("查询所有父权限ID - 成功")
    void selectParentPermissionIds_Success() {
        // Given
        Long permissionId = 3L;
        List<Long> expectedParentIds = Arrays.asList(1L, 2L);
        
        when(permissionRepository.selectParentPermissionIds(permissionId)).thenReturn(expectedParentIds);

        // When
        List<Long> result = permissionRepository.selectParentPermissionIds(permissionId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expectedParentIds.size(), result.size());
        verify(permissionRepository).selectParentPermissionIds(permissionId);
    }

    @Test
    @DisplayName("查询所有子权限ID - 成功")
    void selectChildPermissionIds_Success() {
        // Given
        Long permissionId = 1L;
        List<Long> expectedChildIds = Arrays.asList(2L, 3L);
        
        when(permissionRepository.selectChildPermissionIds(permissionId)).thenReturn(expectedChildIds);

        // When
        List<Long> result = permissionRepository.selectChildPermissionIds(permissionId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expectedChildIds.size(), result.size());
        verify(permissionRepository).selectChildPermissionIds(permissionId);
    }

    @Test
    @DisplayName("批量更新权限状态 - 成功")
    void batchUpdateStatus_Success() {
        // Given
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        Boolean isActive = false;
        int expectedUpdateCount = 3;
        
        when(permissionRepository.batchUpdateStatus(permissionIds, isActive)).thenReturn(expectedUpdateCount);

        // When
        int result = permissionRepository.batchUpdateStatus(permissionIds, isActive);

        // Then
        assertEquals(expectedUpdateCount, result);
        verify(permissionRepository).batchUpdateStatus(permissionIds, isActive);
    }

    @Test
    @DisplayName("检查权限是否可以删除 - 可以删除")
    void canDelete_CanDelete() {
        // Given
        Long permissionId = 1L;
        
        when(permissionRepository.canDelete(permissionId)).thenReturn(true);

        // When
        boolean result = permissionRepository.canDelete(permissionId);

        // Then
        assertTrue(result);
        verify(permissionRepository).canDelete(permissionId);
    }

    @Test
    @DisplayName("检查权限是否可以删除 - 不能删除")
    void canDelete_CannotDelete() {
        // Given
        Long permissionId = 1L;
        
        when(permissionRepository.canDelete(permissionId)).thenReturn(false);

        // When
        boolean result = permissionRepository.canDelete(permissionId);

        // Then
        assertFalse(result);
        verify(permissionRepository).canDelete(permissionId);
    }

    @Test
    @DisplayName("根据资源类型查询权限 - 成功")
    void selectPermissionsByType_Success() {
        // Given
        String resourceType = "API";
        List<PermissionResource> expectedPermissions = Arrays.asList(createTestPermission());
        
        when(permissionRepository.selectPermissionsByType(resourceType)).thenReturn(expectedPermissions);

        // When
        List<PermissionResource> result = permissionRepository.selectPermissionsByType(resourceType);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expectedPermissions.size(), result.size());
        verify(permissionRepository).selectPermissionsByType(resourceType);
    }

    // 辅助方法
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
}
