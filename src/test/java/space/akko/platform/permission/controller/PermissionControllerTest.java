package space.akko.platform.permission.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import space.akko.foundation.common.PageResult;
import space.akko.foundation.common.Result;
import space.akko.platform.permission.model.dto.PermissionCreateRequest;
import space.akko.platform.permission.model.dto.PermissionQueryRequest;
import space.akko.platform.permission.model.dto.PermissionUpdateRequest;
import space.akko.platform.permission.model.entity.PermissionResource;
import space.akko.platform.permission.model.vo.PermissionVO;
import space.akko.platform.permission.service.PermissionService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 权限控制器测试
 * 
 * @author akko
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限控制器测试")
class PermissionControllerTest {

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private PermissionController permissionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(permissionController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("创建权限 - 成功")
    void createPermission_Success() throws Exception {
        // Given
        PermissionCreateRequest request = createPermissionCreateRequest();
        PermissionVO expectedVO = createPermissionVO();
        
        when(permissionService.createPermission(any(PermissionCreateRequest.class)))
                .thenReturn(expectedVO);

        // When & Then
        mockMvc.perform(post("/api/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(expectedVO.getId()))
                .andExpect(jsonPath("$.data.resourceCode").value(expectedVO.getResourceCode()))
                .andExpect(jsonPath("$.data.resourceName").value(expectedVO.getResourceName()));

        verify(permissionService).createPermission(any(PermissionCreateRequest.class));
    }

    @Test
    @DisplayName("更新权限 - 成功")
    void updatePermission_Success() throws Exception {
        // Given
        Long permissionId = 1L;
        PermissionUpdateRequest request = createPermissionUpdateRequest();
        PermissionVO expectedVO = createPermissionVO();
        
        when(permissionService.updatePermission(eq(permissionId), any(PermissionUpdateRequest.class)))
                .thenReturn(expectedVO);

        // When & Then
        mockMvc.perform(put("/api/permissions/{id}", permissionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(expectedVO.getId()));

        verify(permissionService).updatePermission(eq(permissionId), any(PermissionUpdateRequest.class));
    }

    @Test
    @DisplayName("删除权限 - 成功")
    void deletePermission_Success() throws Exception {
        // Given
        Long permissionId = 1L;
        doNothing().when(permissionService).deletePermission(permissionId);

        // When & Then
        mockMvc.perform(delete("/api/permissions/{id}", permissionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(permissionService).deletePermission(permissionId);
    }

    @Test
    @DisplayName("批量删除权限 - 成功")
    void batchDeletePermissions_Success() throws Exception {
        // Given
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        doNothing().when(permissionService).batchDeletePermissions(permissionIds);

        // When & Then
        mockMvc.perform(delete("/api/permissions/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(permissionService).batchDeletePermissions(permissionIds);
    }

    @Test
    @DisplayName("根据ID获取权限 - 成功")
    void getPermissionById_Success() throws Exception {
        // Given
        Long permissionId = 1L;
        PermissionVO expectedVO = createPermissionVO();
        
        when(permissionService.getPermissionById(permissionId)).thenReturn(expectedVO);

        // When & Then
        mockMvc.perform(get("/api/permissions/{id}", permissionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(expectedVO.getId()))
                .andExpect(jsonPath("$.data.resourceCode").value(expectedVO.getResourceCode()));

        verify(permissionService).getPermissionById(permissionId);
    }

    @Test
    @DisplayName("根据编码获取权限 - 成功")
    void getPermissionByCode_Success() throws Exception {
        // Given
        String resourceCode = "USER_MANAGEMENT";
        PermissionResource expectedResource = createPermissionResource();
        
        when(permissionService.getPermissionByCode(resourceCode)).thenReturn(expectedResource);

        // When & Then
        mockMvc.perform(get("/api/permissions/code/{resourceCode}", resourceCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.resourceCode").value(expectedResource.getResourceCode()));

        verify(permissionService).getPermissionByCode(resourceCode);
    }

    @Test
    @DisplayName("分页查询权限 - 成功")
    void getPermissionPage_Success() throws Exception {
        // Given
        PermissionQueryRequest request = new PermissionQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        
        PageResult<PermissionVO> expectedResult = createPageResult();
        when(permissionService.getPermissionPage(any(PermissionQueryRequest.class)))
                .thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(get("/api/permissions/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total").value(expectedResult.getTotal()));

        verify(permissionService).getPermissionPage(any(PermissionQueryRequest.class));
    }

    @Test
    @DisplayName("获取权限树 - 成功")
    void getPermissionTree_Success() throws Exception {
        // Given
        Long parentId = null;
        List<PermissionVO> expectedTree = Arrays.asList(createPermissionVO());
        
        when(permissionService.getPermissionTree(parentId)).thenReturn(expectedTree);

        // When & Then
        mockMvc.perform(get("/api/permissions/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(expectedTree.size()));

        verify(permissionService).getPermissionTree(parentId);
    }

    @Test
    @DisplayName("获取所有权限 - 成功")
    void getAllPermissions_Success() throws Exception {
        // Given
        List<PermissionVO> expectedPermissions = Arrays.asList(createPermissionVO());
        
        when(permissionService.getAllPermissions()).thenReturn(expectedPermissions);

        // When & Then
        mockMvc.perform(get("/api/permissions/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(expectedPermissions.size()));

        verify(permissionService).getAllPermissions();
    }

    @Test
    @DisplayName("更新权限状态 - 成功")
    void updatePermissionStatus_Success() throws Exception {
        // Given
        Long permissionId = 1L;
        Boolean isActive = true;
        doNothing().when(permissionService).updatePermissionStatus(permissionId, isActive);

        // When & Then
        mockMvc.perform(patch("/api/permissions/{id}/status", permissionId)
                        .param("isActive", isActive.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(permissionService).updatePermissionStatus(permissionId, isActive);
    }

    @Test
    @DisplayName("批量更新权限状态 - 成功")
    void batchUpdatePermissionStatus_Success() throws Exception {
        // Given
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        Boolean isActive = false;
        doNothing().when(permissionService).batchUpdatePermissionStatus(permissionIds, isActive);

        // When & Then
        mockMvc.perform(patch("/api/permissions/batch/status")
                        .param("isActive", isActive.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(permissionService).batchUpdatePermissionStatus(permissionIds, isActive);
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

    private PermissionVO createPermissionVO() {
        PermissionVO vo = new PermissionVO();
        vo.setId(1L);
        vo.setResourceCode("USER_MANAGEMENT");
        vo.setResourceName("用户管理");
        vo.setResourceType("API");
        vo.setResourceTypeName("接口");
        vo.setResourceUrl("/api/users");
        vo.setHttpMethod("GET");
        vo.setParentId(0L);
        vo.setSortOrder(1);
        vo.setDescription("用户管理相关接口");
        vo.setIsActive(true);
        vo.setStatusName("正常");
        vo.setCreatedAt(LocalDateTime.now());
        vo.setUpdatedAt(LocalDateTime.now());
        return vo;
    }

    private PermissionResource createPermissionResource() {
        PermissionResource resource = new PermissionResource();
        resource.setId(1L);
        resource.setResourceCode("USER_MANAGEMENT");
        resource.setResourceName("用户管理");
        resource.setResourceType("API");
        resource.setResourceUrl("/api/users");
        resource.setHttpMethod("GET");
        resource.setParentId(0L);
        resource.setSortOrder(1);
        resource.setDescription("用户管理相关接口");
        resource.setIsActive(true);
        return resource;
    }

    private PageResult<PermissionVO> createPageResult() {
        PageResult<PermissionVO> result = new PageResult<>();
        result.setRecords(Arrays.asList(createPermissionVO()));
        result.setTotal(1L);
        result.setPageNum(1);
        result.setPageSize(10);
        result.setPages(1);
        return result;
    }
}
