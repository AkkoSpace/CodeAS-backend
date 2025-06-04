package space.akko.platform.role.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.akko.foundation.common.ResultCode;
import space.akko.foundation.exception.BusinessException;
import space.akko.platform.permission.model.entity.RolePermissionMapping;
import space.akko.platform.permission.repository.RolePermissionMappingRepository;
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
 * 角色服务权限相关功能测试
 * 
 * @author akko
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("角色服务权限相关功能测试")
class RoleServicePermissionTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RolePermissionMappingRepository rolePermissionMappingRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private RoleDefinition testRole;

    @BeforeEach
    void setUp() {
        testRole = createTestRole();
    }

    @Test
    @DisplayName("分配权限给角色 - 成功")
    void assignPermissions_Success() {
        // Given
        Long roleId = 1L;
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        
        when(roleRepository.selectById(roleId)).thenReturn(testRole);
        when(rolePermissionMappingRepository.selectByRoleId(roleId)).thenReturn(Arrays.asList());

        // When
        assertDoesNotThrow(() -> roleService.assignPermissions(roleId, permissionIds));

        // Then
        verify(roleRepository).selectById(roleId);
        verify(rolePermissionMappingRepository).selectByRoleId(roleId);
        verify(rolePermissionMappingRepository).batchInsert(anyList());
    }

    @Test
    @DisplayName("分配权限给角色 - 角色不存在")
    void assignPermissions_RoleNotFound() {
        // Given
        Long roleId = 1L;
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        
        when(roleRepository.selectById(roleId)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.assignPermissions(roleId, permissionIds));
        assertEquals(ResultCode.DATA_NOT_FOUND, exception.getResultCode());
        assertEquals("角色不存在", exception.getMessage());
        
        verify(roleRepository).selectById(roleId);
        verify(rolePermissionMappingRepository, never()).selectByRoleId(any());
        verify(rolePermissionMappingRepository, never()).batchInsert(anyList());
    }

    @Test
    @DisplayName("分配权限给角色 - 权限列表为空")
    void assignPermissions_EmptyPermissionList() {
        // Given
        Long roleId = 1L;
        List<Long> permissionIds = Arrays.asList();
        
        when(roleRepository.selectById(roleId)).thenReturn(testRole);

        // When
        assertDoesNotThrow(() -> roleService.assignPermissions(roleId, permissionIds));

        // Then
        verify(roleRepository).selectById(roleId);
        verify(rolePermissionMappingRepository, never()).selectByRoleId(any());
        verify(rolePermissionMappingRepository, never()).batchInsert(anyList());
    }

    @Test
    @DisplayName("分配权限给角色 - 部分权限已存在")
    void assignPermissions_PartialExists() {
        // Given
        Long roleId = 1L;
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        
        // 模拟已存在的权限映射
        RolePermissionMapping existingMapping = new RolePermissionMapping();
        existingMapping.setRoleId(roleId);
        existingMapping.setResourceId(1L);
        
        when(roleRepository.selectById(roleId)).thenReturn(testRole);
        when(rolePermissionMappingRepository.selectByRoleId(roleId)).thenReturn(Arrays.asList(existingMapping));

        // When
        assertDoesNotThrow(() -> roleService.assignPermissions(roleId, permissionIds));

        // Then
        verify(roleRepository).selectById(roleId);
        verify(rolePermissionMappingRepository).selectByRoleId(roleId);
        verify(rolePermissionMappingRepository).batchInsert(anyList());
    }

    @Test
    @DisplayName("移除角色权限 - 成功")
    void removePermissions_Success() {
        // Given
        Long roleId = 1L;
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        
        when(roleRepository.selectById(roleId)).thenReturn(testRole);

        // When
        assertDoesNotThrow(() -> roleService.removePermissions(roleId, permissionIds));

        // Then
        verify(roleRepository).selectById(roleId);
        verify(rolePermissionMappingRepository).batchDeleteByRoleIdAndResourceIds(roleId, permissionIds);
    }

    @Test
    @DisplayName("移除角色权限 - 角色不存在")
    void removePermissions_RoleNotFound() {
        // Given
        Long roleId = 1L;
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        
        when(roleRepository.selectById(roleId)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.removePermissions(roleId, permissionIds));
        assertEquals(ResultCode.DATA_NOT_FOUND, exception.getResultCode());
        assertEquals("角色不存在", exception.getMessage());
        
        verify(roleRepository).selectById(roleId);
        verify(rolePermissionMappingRepository, never()).batchDeleteByRoleIdAndResourceIds(any(), any());
    }

    @Test
    @DisplayName("移除角色权限 - 权限列表为空")
    void removePermissions_EmptyPermissionList() {
        // Given
        Long roleId = 1L;
        List<Long> permissionIds = Arrays.asList();
        
        when(roleRepository.selectById(roleId)).thenReturn(testRole);

        // When
        assertDoesNotThrow(() -> roleService.removePermissions(roleId, permissionIds));

        // Then
        verify(roleRepository).selectById(roleId);
        verify(rolePermissionMappingRepository, never()).batchDeleteByRoleIdAndResourceIds(any(), any());
    }

    @Test
    @DisplayName("获取角色权限列表 - 成功")
    void getRolePermissions_Success() {
        // Given
        Long roleId = 1L;
        List<String> expectedPermissions = Arrays.asList("USER_READ", "USER_WRITE", "USER_DELETE");
        
        when(roleRepository.selectById(roleId)).thenReturn(testRole);
        when(rolePermissionMappingRepository.selectPermissionCodesByRoleId(roleId)).thenReturn(expectedPermissions);

        // When
        List<String> result = roleService.getRolePermissions(roleId);

        // Then
        assertNotNull(result);
        assertEquals(expectedPermissions.size(), result.size());
        assertEquals(expectedPermissions, result);
        verify(roleRepository).selectById(roleId);
        verify(rolePermissionMappingRepository).selectPermissionCodesByRoleId(roleId);
    }

    @Test
    @DisplayName("获取角色权限列表 - 角色不存在")
    void getRolePermissions_RoleNotFound() {
        // Given
        Long roleId = 1L;
        
        when(roleRepository.selectById(roleId)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.getRolePermissions(roleId));
        assertEquals(ResultCode.DATA_NOT_FOUND, exception.getResultCode());
        assertEquals("角色不存在", exception.getMessage());
        
        verify(roleRepository).selectById(roleId);
        verify(rolePermissionMappingRepository, never()).selectPermissionCodesByRoleId(any());
    }

    @Test
    @DisplayName("获取角色权限列表 - 角色无权限")
    void getRolePermissions_NoPermissions() {
        // Given
        Long roleId = 1L;
        
        when(roleRepository.selectById(roleId)).thenReturn(testRole);
        when(rolePermissionMappingRepository.selectPermissionCodesByRoleId(roleId)).thenReturn(Arrays.asList());

        // When
        List<String> result = roleService.getRolePermissions(roleId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roleRepository).selectById(roleId);
        verify(rolePermissionMappingRepository).selectPermissionCodesByRoleId(roleId);
    }

    @Test
    @DisplayName("分配权限给角色 - 系统角色不允许修改权限")
    void assignPermissions_SystemRoleNotAllowed() {
        // Given
        Long roleId = 1L;
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        
        RoleDefinition systemRole = createTestRole();
        systemRole.setIsSystem(true);
        
        when(roleRepository.selectById(roleId)).thenReturn(systemRole);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.assignPermissions(roleId, permissionIds));
        assertEquals(ResultCode.OPERATION_NOT_ALLOWED, exception.getResultCode());
        assertEquals("系统角色不允许修改权限", exception.getMessage());
        
        verify(roleRepository).selectById(roleId);
        verify(rolePermissionMappingRepository, never()).selectByRoleId(any());
        verify(rolePermissionMappingRepository, never()).batchInsert(anyList());
    }

    @Test
    @DisplayName("移除角色权限 - 系统角色不允许修改权限")
    void removePermissions_SystemRoleNotAllowed() {
        // Given
        Long roleId = 1L;
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        
        RoleDefinition systemRole = createTestRole();
        systemRole.setIsSystem(true);
        
        when(roleRepository.selectById(roleId)).thenReturn(systemRole);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.removePermissions(roleId, permissionIds));
        assertEquals(ResultCode.OPERATION_NOT_ALLOWED, exception.getResultCode());
        assertEquals("系统角色不允许修改权限", exception.getMessage());
        
        verify(roleRepository).selectById(roleId);
        verify(rolePermissionMappingRepository, never()).batchDeleteByRoleIdAndResourceIds(any(), any());
    }

    @Test
    @DisplayName("分配权限给角色 - 非活跃角色不允许修改权限")
    void assignPermissions_InactiveRoleNotAllowed() {
        // Given
        Long roleId = 1L;
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        
        RoleDefinition inactiveRole = createTestRole();
        inactiveRole.setIsActive(false);
        
        when(roleRepository.selectById(roleId)).thenReturn(inactiveRole);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.assignPermissions(roleId, permissionIds));
        assertEquals(ResultCode.OPERATION_NOT_ALLOWED, exception.getResultCode());
        assertEquals("非活跃角色不允许修改权限", exception.getMessage());
        
        verify(roleRepository).selectById(roleId);
        verify(rolePermissionMappingRepository, never()).selectByRoleId(any());
        verify(rolePermissionMappingRepository, never()).batchInsert(anyList());
    }

    @Test
    @DisplayName("移除角色权限 - 非活跃角色不允许修改权限")
    void removePermissions_InactiveRoleNotAllowed() {
        // Given
        Long roleId = 1L;
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        
        RoleDefinition inactiveRole = createTestRole();
        inactiveRole.setIsActive(false);
        
        when(roleRepository.selectById(roleId)).thenReturn(inactiveRole);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.removePermissions(roleId, permissionIds));
        assertEquals(ResultCode.OPERATION_NOT_ALLOWED, exception.getResultCode());
        assertEquals("非活跃角色不允许修改权限", exception.getMessage());
        
        verify(roleRepository).selectById(roleId);
        verify(rolePermissionMappingRepository, never()).batchDeleteByRoleIdAndResourceIds(any(), any());
    }

    // 辅助方法
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
