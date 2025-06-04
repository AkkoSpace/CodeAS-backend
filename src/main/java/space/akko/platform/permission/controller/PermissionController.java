package space.akko.platform.permission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import space.akko.foundation.annotation.OperationLog;
import space.akko.foundation.annotation.RequirePermission;
import space.akko.foundation.common.PageResult;
import space.akko.foundation.common.Result;
import space.akko.platform.permission.model.entity.PermissionResource;
import space.akko.platform.permission.model.request.PermissionCreateRequest;
import space.akko.platform.permission.model.request.PermissionQueryRequest;
import space.akko.platform.permission.model.vo.PermissionVO;
import space.akko.platform.permission.service.PermissionService;

import java.util.List;

/**
 * 权限管理控制器
 * 
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限管理相关接口")
@RequirePermission("PERMISSION_MANAGEMENT")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @Operation(summary = "创建权限", description = "创建新的权限资源")
    @RequirePermission("PERMISSION_CREATE")
    @OperationLog(operationName = "创建权限", resourceType = "PERMISSION")
    public Result<PermissionVO> createPermission(
            @Valid @RequestBody PermissionCreateRequest request) {
        
        PermissionVO permission = permissionService.createPermission(request);
        return Result.success(permission);
    }

    @PutMapping("/{permissionId}")
    @Operation(summary = "更新权限", description = "更新指定权限的信息")
    @RequirePermission("PERMISSION_UPDATE")
    @OperationLog(operationName = "更新权限", resourceType = "PERMISSION")
    public Result<PermissionVO> updatePermission(
            @Parameter(description = "权限ID") @PathVariable Long permissionId,
            @Valid @RequestBody PermissionCreateRequest request) {
        
        PermissionVO permission = permissionService.updatePermission(permissionId, request);
        return Result.success(permission);
    }

    @DeleteMapping("/{permissionId}")
    @Operation(summary = "删除权限", description = "删除指定的权限")
    @RequirePermission("PERMISSION_DELETE")
    @OperationLog(operationName = "删除权限", resourceType = "PERMISSION")
    public Result<Void> deletePermission(
            @Parameter(description = "权限ID") @PathVariable Long permissionId) {
        
        permissionService.deletePermission(permissionId);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除权限", description = "批量删除多个权限")
    @RequirePermission("PERMISSION_DELETE")
    @OperationLog(operationName = "批量删除权限", resourceType = "PERMISSION")
    public Result<Void> batchDeletePermissions(
            @Parameter(description = "权限ID列表") @RequestBody List<Long> permissionIds) {
        
        permissionService.batchDeletePermissions(permissionIds);
        return Result.success();
    }

    @GetMapping("/{permissionId}")
    @Operation(summary = "获取权限详情", description = "根据ID获取权限详细信息")
    @RequirePermission("PERMISSION_READ")
    public Result<PermissionVO> getPermissionById(
            @Parameter(description = "权限ID") @PathVariable Long permissionId) {
        
        PermissionVO permission = permissionService.getPermissionById(permissionId);
        return Result.success(permission);
    }

    @GetMapping("/code/{resourceCode}")
    @Operation(summary = "根据编码获取权限", description = "根据资源编码获取权限信息")
    @RequirePermission("PERMISSION_READ")
    public Result<PermissionResource> getPermissionByCode(
            @Parameter(description = "资源编码") @PathVariable String resourceCode) {
        
        PermissionResource permission = permissionService.getPermissionByCode(resourceCode);
        return Result.success(permission);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询权限", description = "分页查询权限列表")
    @RequirePermission("PERMISSION_READ")
    public Result<PageResult<PermissionVO>> getPermissionPage(
            @Parameter(description = "查询条件") PermissionQueryRequest request) {
        
        PageResult<PermissionVO> result = permissionService.getPermissionPage(request);
        return Result.success(result);
    }

    @GetMapping("/tree")
    @Operation(summary = "获取权限树", description = "获取权限的树形结构")
    @RequirePermission("PERMISSION_READ")
    public Result<List<PermissionVO>> getPermissionTree(
            @Parameter(description = "父权限ID") @RequestParam(required = false) Long parentId) {
        
        List<PermissionVO> tree = permissionService.getPermissionTree(parentId);
        return Result.success(tree);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有权限", description = "获取所有权限的扁平列表")
    @RequirePermission("PERMISSION_READ")
    public Result<List<PermissionVO>> getAllPermissions() {
        
        List<PermissionVO> permissions = permissionService.getAllPermissions();
        return Result.success(permissions);
    }

    @PatchMapping("/{permissionId}/status")
    @Operation(summary = "更新权限状态", description = "启用或禁用指定权限")
    @RequirePermission("PERMISSION_UPDATE")
    @OperationLog(operationName = "更新权限状态", resourceType = "PERMISSION")
    public Result<Void> updatePermissionStatus(
            @Parameter(description = "权限ID") @PathVariable Long permissionId,
            @Parameter(description = "是否激活") @RequestParam Boolean isActive) {
        
        permissionService.updatePermissionStatus(permissionId, isActive);
        return Result.success();
    }

    @PatchMapping("/batch/status")
    @Operation(summary = "批量更新权限状态", description = "批量启用或禁用权限")
    @RequirePermission("PERMISSION_UPDATE")
    @OperationLog(operationName = "批量更新权限状态", resourceType = "PERMISSION")
    public Result<Void> batchUpdatePermissionStatus(
            @Parameter(description = "权限ID列表") @RequestBody List<Long> permissionIds,
            @Parameter(description = "是否激活") @RequestParam Boolean isActive) {
        
        permissionService.batchUpdatePermissionStatus(permissionIds, isActive);
        return Result.success();
    }

    @GetMapping("/role/{roleId}")
    @Operation(summary = "获取角色权限", description = "获取指定角色的权限列表")
    @RequirePermission("PERMISSION_READ")
    public Result<List<PermissionResource>> getPermissionsByRoleId(
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        
        List<PermissionResource> permissions = permissionService.getPermissionsByRoleId(roleId);
        return Result.success(permissions);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户权限", description = "获取指定用户的权限列表")
    @RequirePermission("PERMISSION_READ")
    public Result<List<PermissionResource>> getPermissionsByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        
        List<PermissionResource> permissions = permissionService.getPermissionsByUserId(userId);
        return Result.success(permissions);
    }

    @GetMapping("/check/code/{resourceCode}")
    @Operation(summary = "检查资源编码", description = "检查资源编码是否已存在")
    @RequirePermission("PERMISSION_READ")
    public Result<Boolean> checkResourceCodeExists(
            @Parameter(description = "资源编码") @PathVariable String resourceCode) {
        
        boolean exists = permissionService.existsByResourceCode(resourceCode);
        return Result.success(exists);
    }

    @GetMapping("/check/delete/{permissionId}")
    @Operation(summary = "检查删除权限", description = "检查权限是否可以删除")
    @RequirePermission("PERMISSION_READ")
    public Result<Boolean> checkCanDeletePermission(
            @Parameter(description = "权限ID") @PathVariable Long permissionId) {
        
        boolean canDelete = permissionService.canDeletePermission(permissionId);
        return Result.success(canDelete);
    }

    @GetMapping("/{permissionId}/hierarchy")
    @Operation(summary = "获取权限层级路径", description = "获取从根权限到指定权限的完整路径")
    @RequirePermission("PERMISSION_READ")
    public Result<List<PermissionVO>> getPermissionHierarchyPath(
            @Parameter(description = "权限ID") @PathVariable Long permissionId) {
        
        List<PermissionVO> path = permissionService.getPermissionHierarchyPath(permissionId);
        return Result.success(path);
    }

    @PatchMapping("/{permissionId}/move")
    @Operation(summary = "移动权限", description = "将权限移动到新的父权限下")
    @RequirePermission("PERMISSION_UPDATE")
    @OperationLog(operationName = "移动权限", resourceType = "PERMISSION")
    public Result<Void> movePermission(
            @Parameter(description = "权限ID") @PathVariable Long permissionId,
            @Parameter(description = "新父权限ID") @RequestParam Long newParentId) {
        
        permissionService.movePermission(permissionId, newParentId);
        return Result.success();
    }

    @GetMapping("/type/{resourceType}")
    @Operation(summary = "按类型获取权限", description = "根据资源类型获取权限列表")
    @RequirePermission("PERMISSION_READ")
    public Result<List<PermissionResource>> getPermissionsByType(
            @Parameter(description = "资源类型") @PathVariable String resourceType) {
        
        List<PermissionResource> permissions = permissionService.getPermissionsByType(resourceType);
        return Result.success(permissions);
    }

    @GetMapping("/api")
    @Operation(summary = "根据URL和方法获取权限", description = "根据资源URL和HTTP方法获取API权限")
    @RequirePermission("PERMISSION_READ")
    public Result<PermissionResource> getPermissionByUrlAndMethod(
            @Parameter(description = "资源URL") @RequestParam String resourceUrl,
            @Parameter(description = "HTTP方法") @RequestParam String httpMethod) {
        
        PermissionResource permission = permissionService.getPermissionByUrlAndMethod(resourceUrl, httpMethod);
        return Result.success(permission);
    }

    @PostMapping("/sync/api")
    @Operation(summary = "同步API权限", description = "扫描Controller注解自动创建或更新API权限")
    @RequirePermission("PERMISSION_SYNC")
    @OperationLog(operationName = "同步API权限", resourceType = "PERMISSION")
    public Result<Void> syncApiPermissions() {
        
        permissionService.syncApiPermissions();
        return Result.success();
    }
}