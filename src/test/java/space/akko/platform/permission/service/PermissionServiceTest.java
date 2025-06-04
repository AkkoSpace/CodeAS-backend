package space.akko.platform.permission.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.akko.foundation.common.PageResult;
import space.akko.foundation.common.ResultCode;
import space.akko.foundation.exception.BusinessException;
import space.akko.platform.permission.model.dto.PermissionCreateRequest;
import space.akko.platform.permission.model.dto.PermissionDTO;
import space.akko.platform.permission.model.dto.PermissionQueryRequest;
import space.akko.platform.permission.model.dto.PermissionUpdateRequest;
import space.akko.platform.permission.model.entity.PermissionResource;
import space.akko.platform.permission.model.vo.PermissionVO;
import space.akko.platform.permission.repository.PermissionRepository;
import space.akko.platform.permission.service.impl.PermissionServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 权限服务测试
 * 
 * @author akko
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限服务测试")
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private PermissionResource testPermission;
    private PermissionDTO testPermissionDTO;
    private PermissionCreateRequest createRequest;
    private PermissionUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testPermission = createTestPermission();
        testPermissionDTO = createTestPermissionDTO();
        createRequest = createPermissionCreateRequest();
        updateRequest = createPermissionUpdateRequest();
    }

    @Test
    @DisplayName("创建权限 - 成功")
    void createPermission_Success() {
        // Given
        when(permissionRepository.existsByResourceCode(createRequest.getResourceCode())).thenReturn(false);
        when(permissionRepository.insert(any(PermissionResource.class))).thenReturn(1);
        when(permissionRepository.selectPermissionDetailById(any(Long.class))).thenReturn(testPermissionDTO);

        // When
        PermissionVO result = permissionService.createPermission(createRequest);

        // Then
        assertNotNull(result);
        assertEquals(createRequest.getResourceCode(), result.getResourceCode());
        assertEquals(createRequest.getResourceName(), result.getResourceName());
        verify(permissionRepository).existsByResourceCode(createRequest.getResourceCode());
        verify(permissionRepository).insert(any(PermissionResource.class));
    }

    @Test
    @DisplayName("创建权限 - 资源编码已存在")
    void createPermission_ResourceCodeExists() {
        // Given
        when(permissionRepository.existsByResourceCode(createRequest.getResourceCode())).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> permissionService.createPermission(createRequest));
        assertEquals(ResultCode.DATA_ALREADY_EXISTS, exception.getResultCode());
        assertEquals("资源编码已存在", exception.getMessage());
        
        verify(permissionRepository).existsByResourceCode(createRequest.getResourceCode());
        verify(permissionRepository, never()).insert(any(PermissionResource.class));
    }

    @Test
    @DisplayName("更新权限 - 成功")
    void updatePermission_Success() {
        // Given
        Long permissionId = 1L;
        when(permissionRepository.selectById(permissionId)).thenReturn(testPermission);
        when(permissionRepository.updateById(any(PermissionResource.class))).thenReturn(1);
        when(permissionRepository.selectPermissionDetailById(permissionId)).thenReturn(testPermissionDTO);

        // When
        PermissionVO result = permissionService.updatePermission(permissionId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(updateRequest.getResourceName(), result.getResourceName());
        verify(permissionRepository).selectById(permissionId);
        verify(permissionRepository).updateById(any(PermissionResource.class));
    }

    @Test
    @DisplayName("更新权限 - 权限不存在")
    void updatePermission_NotFound() {
        // Given
        Long permissionId = 1L;
        when(permissionRepository.selectById(permissionId)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> permissionService.updatePermission(permissionId, updateRequest));
        assertEquals(ResultCode.DATA_NOT_FOUND, exception.getResultCode());
        assertEquals("权限不存在", exception.getMessage());
        
        verify(permissionRepository).selectById(permissionId);
        verify(permissionRepository, never()).updateById(any(PermissionResource.class));
    }

    @Test
    @DisplayName("删除权限 - 成功")
    void deletePermission_Success() {
        // Given
        Long permissionId = 1L;
        when(permissionRepository.selectById(permissionId)).thenReturn(testPermission);
        when(permissionRepository.canDelete(permissionId)).thenReturn(true);
        when(permissionRepository.deleteById(permissionId)).thenReturn(1);

        // When
        assertDoesNotThrow(() -> permissionService.deletePermission(permissionId));

        // Then
        verify(permissionRepository).selectById(permissionId);
        verify(permissionRepository).canDelete(permissionId);
        verify(permissionRepository).deleteById(permissionId);
    }

    @Test
    @DisplayName("删除权限 - 权限不存在")
    void deletePermission_NotFound() {
        // Given
        Long permissionId = 1L;
        when(permissionRepository.selectById(permissionId)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> permissionService.deletePermission(permissionId));
        assertEquals(ResultCode.DATA_NOT_FOUND, exception.getResultCode());
        assertEquals("权限不存在", exception.getMessage());
        
        verify(permissionRepository).selectById(permissionId);
        verify(permissionRepository, never()).canDelete(any(Long.class));
        verify(permissionRepository, never()).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("删除权限 - 权限不可删除")
    void deletePermission_CannotDelete() {
        // Given
        Long permissionId = 1L;
        when(permissionRepository.selectById(permissionId)).thenReturn(testPermission);
        when(permissionRepository.canDelete(permissionId)).thenReturn(false);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> permissionService.deletePermission(permissionId));
        assertEquals(ResultCode.OPERATION_NOT_ALLOWED, exception.getResultCode());
        assertEquals("权限存在子权限或角色关联，无法删除", exception.getMessage());
        
        verify(permissionRepository).selectById(permissionId);
        verify(permissionRepository).canDelete(permissionId);
        verify(permissionRepository, never()).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("批量删除权限 - 成功")
    void batchDeletePermissions_Success() {
        // Given
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        when(permissionRepository.selectBatchIds(permissionIds)).thenReturn(Arrays.asList(testPermission));
        
        // When
        assertDoesNotThrow(() -> permissionService.batchDeletePermissions(permissionIds));

        // Then
        verify(permissionRepository).selectBatchIds(permissionIds);
        verify(permissionRepository).deleteBatchIds(permissionIds);
    }

    @Test
    @DisplayName("根据ID获取权限 - 成功")
    void getPermissionById_Success() {
        // Given
        Long permissionId = 1L;
        when(permissionRepository.selectPermissionDetailById(permissionId)).thenReturn(testPermissionDTO);

        // When
        PermissionVO result = permissionService.getPermissionById(permissionId);

        // Then
        assertNotNull(result);
        assertEquals(testPermissionDTO.getId(), result.getId());
        assertEquals(testPermissionDTO.getResourceCode(), result.getResourceCode());
        verify(permissionRepository).selectPermissionDetailById(permissionId);
    }

    @Test
    @DisplayName("根据ID获取权限 - 权限不存在")
    void getPermissionById_NotFound() {
        // Given
        Long permissionId = 1L;
        when(permissionRepository.selectPermissionDetailById(permissionId)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> permissionService.getPermissionById(permissionId));
        assertEquals(ResultCode.DATA_NOT_FOUND, exception.getResultCode());
        assertEquals("权限不存在", exception.getMessage());
        
        verify(permissionRepository).selectPermissionDetailById(permissionId);
    }

    @Test
    @DisplayName("根据编码获取权限 - 成功")
    void getPermissionByCode_Success() {
        // Given
        String resourceCode = "USER_MANAGEMENT";
        when(permissionRepository.findByResourceCode(resourceCode)).thenReturn(testPermission);

        // When
        PermissionResource result = permissionService.getPermissionByCode(resourceCode);

        // Then
        assertNotNull(result);
        assertEquals(testPermission.getResourceCode(), result.getResourceCode());
        verify(permissionRepository).findByResourceCode(resourceCode);
    }

    @Test
    @DisplayName("分页查询权限 - 成功")
    void getPermissionPage_Success() {
        // Given
        PermissionQueryRequest request = new PermissionQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        
        Page<PermissionDTO> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(testPermissionDTO));
        mockPage.setTotal(1);
        
        when(permissionRepository.selectPermissionPage(any(Page.class), any())).thenReturn(mockPage);

        // When
        PageResult<PermissionVO> result = permissionService.getPermissionPage(request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getRecords().size());
        verify(permissionRepository).selectPermissionPage(any(Page.class), any());
    }

    @Test
    @DisplayName("获取权限树 - 成功")
    void getPermissionTree_Success() {
        // Given
        Long parentId = null;
        when(permissionRepository.selectPermissionTree(parentId)).thenReturn(Arrays.asList(testPermissionDTO));

        // When
        List<PermissionVO> result = permissionService.getPermissionTree(parentId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(permissionRepository).selectPermissionTree(parentId);
    }

    @Test
    @DisplayName("获取所有权限 - 成功")
    void getAllPermissions_Success() {
        // Given
        when(permissionRepository.selectPermissionTree(null)).thenReturn(Arrays.asList(testPermissionDTO));

        // When
        List<PermissionVO> result = permissionService.getAllPermissions();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(permissionRepository).selectPermissionTree(null);
    }

    @Test
    @DisplayName("更新权限状态 - 成功")
    void updatePermissionStatus_Success() {
        // Given
        Long permissionId = 1L;
        Boolean isActive = false;
        when(permissionRepository.selectById(permissionId)).thenReturn(testPermission);
        when(permissionRepository.updateById(any(PermissionResource.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> permissionService.updatePermissionStatus(permissionId, isActive));

        // Then
        verify(permissionRepository).selectById(permissionId);
        verify(permissionRepository).updateById(any(PermissionResource.class));
    }

    @Test
    @DisplayName("批量更新权限状态 - 成功")
    void batchUpdatePermissionStatus_Success() {
        // Given
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        Boolean isActive = false;
        when(permissionRepository.batchUpdateStatus(permissionIds, isActive)).thenReturn(3);

        // When
        assertDoesNotThrow(() -> permissionService.batchUpdatePermissionStatus(permissionIds, isActive));

        // Then
        verify(permissionRepository).batchUpdateStatus(permissionIds, isActive);
    }

    @Test
    @DisplayName("根据角色ID获取权限列表 - 成功")
    void getPermissionsByRoleId_Success() {
        // Given
        Long roleId = 1L;
        when(permissionRepository.selectPermissionsByRoleId(roleId)).thenReturn(Arrays.asList(testPermission));

        // When
        List<PermissionResource> result = permissionService.getPermissionsByRoleId(roleId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(permissionRepository).selectPermissionsByRoleId(roleId);
    }

    @Test
    @DisplayName("根据用户ID获取权限列表 - 成功")
    void getPermissionsByUserId_Success() {
        // Given
        Long userId = 1L;
        when(permissionRepository.selectPermissionsByUserId(userId)).thenReturn(Arrays.asList(testPermission));

        // When
        List<PermissionResource> result = permissionService.getPermissionsByUserId(userId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(permissionRepository).selectPermissionsByUserId(userId);
    }

    @Test
    @DisplayName("根据资源类型获取权限列表 - 成功")
    void getPermissionsByType_Success() {
        // Given
        String resourceType = "API";
        when(permissionRepository.selectPermissionsByType(resourceType)).thenReturn(Arrays.asList(testPermission));

        // When
        List<PermissionResource> result = permissionService.getPermissionsByType(resourceType);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(permissionRepository).selectPermissionsByType(resourceType);
    }

    @Test
    @DisplayName("检查资源编码是否存在 - 存在")
    void existsByResourceCode_Exists() {
        // Given
        String resourceCode = "USER_MANAGEMENT";
        when(permissionRepository.existsByResourceCode(resourceCode)).thenReturn(true);

        // When
        boolean result = permissionService.existsByResourceCode(resourceCode);

        // Then
        assertTrue(result);
        verify(permissionRepository).existsByResourceCode(resourceCode);
    }

    @Test
    @DisplayName("检查权限是否可以删除 - 可以删除")
    void canDeletePermission_CanDelete() {
        // Given
        Long permissionId = 1L;
        when(permissionRepository.canDelete(permissionId)).thenReturn(true);

        // When
        boolean result = permissionService.canDeletePermission(permissionId);

        // Then
        assertTrue(result);
        verify(permissionRepository).canDelete(permissionId);
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

    private PermissionUpdateRequest createPermissionUpdateRequest() {
        PermissionUpdateRequest request = new PermissionUpdateRequest();
        request.setResourceName("用户管理(更新)");
        request.setResourceType("API");
        request.setResourceUrl("/api/users");
        request.setHttpMethod("GET");
        request.setParentId(0L);
        request.setSortOrder(1);
        request.setDescription("用户管理相关接口(更新)");
        return request;
    }
}
