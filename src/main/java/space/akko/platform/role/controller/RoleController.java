package space.akko.platform.role.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import space.akko.foundation.annotation.OperationLog;
import space.akko.foundation.annotation.RequirePermission;
import space.akko.foundation.common.PageResult;
import space.akko.foundation.common.Result;
import space.akko.platform.role.model.request.RoleCreateRequest;
import space.akko.platform.role.model.request.RoleQueryRequest;
import space.akko.platform.role.model.request.RoleUpdateRequest;
import space.akko.platform.role.model.vo.RoleVO;
import space.akko.platform.role.service.RoleService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色管理控制器
 * 
 * @author akko
 * @since 1.0.0
 */
@Tag(name = "角色管理", description = "角色管理相关接口")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "分页查询角色", description = "根据条件分页查询角色列表")
    @GetMapping
    @RequirePermission("ROLE_LIST")
    @OperationLog(operationType = "QUERY", operationName = "分页查询角色", resourceType = "ROLE")
    public Result<PageResult<RoleVO>> getRolePage(@Parameter(description = "查询条件") RoleQueryRequest request) {
        PageResult<RoleVO> result = roleService.getRolePage(request);
        return Result.success(result);
    }

    @Operation(summary = "获取角色详情", description = "根据角色ID获取角色详细信息")
    @GetMapping("/{roleId}")
    @RequirePermission("ROLE_DETAIL")
    @OperationLog(operationType = "QUERY", operationName = "获取角色详情", resourceType = "ROLE")
    public Result<RoleVO> getRoleById(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId) {
        RoleVO role = roleService.getRoleById(roleId);
        return Result.success(role);
    }

    @Operation(summary = "获取角色树", description = "获取角色层级树结构")
    @GetMapping("/tree")
    @RequirePermission("ROLE_LIST")
    @OperationLog(operationType = "QUERY", operationName = "获取角色树", resourceType = "ROLE")
    public Result<List<RoleVO>> getRoleTree(
            @Parameter(description = "父角色ID") @RequestParam(required = false) Long parentId) {
        List<RoleVO> roleTree = roleService.getRoleTree(parentId);
        return Result.success(roleTree);
    }

    @Operation(summary = "获取所有角色", description = "获取所有角色的扁平列表")
    @GetMapping("/all")
    @RequirePermission("ROLE_LIST")
    @OperationLog(operationType = "QUERY", operationName = "获取所有角色", resourceType = "ROLE")
    public Result<List<RoleVO>> getAllRoles() {
        List<RoleVO> roles = roleService.getAllRoles();
        return Result.success(roles);
    }

    @Operation(summary = "创建角色", description = "创建新角色")
    @PostMapping
    @RequirePermission("ROLE_CREATE")
    @OperationLog(operationType = "CREATE", operationName = "创建角色", resourceType = "ROLE", 
                 includeRequestBody = true)
    public Result<RoleVO> createRole(@Valid @RequestBody RoleCreateRequest request) {
        RoleVO role = roleService.createRole(request);
        return Result.success("角色创建成功", role);
    }

    @Operation(summary = "更新角色", description = "更新角色信息")
    @PutMapping("/{roleId}")
    @RequirePermission("ROLE_UPDATE")
    @OperationLog(operationType = "UPDATE", operationName = "更新角色", resourceType = "ROLE", 
                 includeRequestBody = true)
    public Result<RoleVO> updateRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId,
            @Valid @RequestBody RoleUpdateRequest request) {
        RoleVO role = roleService.updateRole(roleId, request);
        return Result.success("角色更新成功", role);
    }

    @Operation(summary = "删除角色", description = "根据角色ID删除角色")
    @DeleteMapping("/{roleId}")
    @RequirePermission("ROLE_DELETE")
    @OperationLog(operationType = "DELETE", operationName = "删除角色", resourceType = "ROLE")
    public Result<Void> deleteRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return Result.success("角色删除成功");
    }

    @Operation(summary = "批量删除角色", description = "根据角色ID列表批量删除角色")
    @DeleteMapping("/batch")
    @RequirePermission("ROLE_DELETE")
    @OperationLog(operationType = "DELETE", operationName = "批量删除角色", resourceType = "ROLE")
    public Result<Void> batchDeleteRoles(
            @Parameter(description = "角色ID列表", required = true) @RequestBody List<Long> roleIds) {
        roleService.batchDeleteRoles(roleIds);
        return Result.success("角色批量删除成功");
    }

    @Operation(summary = "启用/禁用角色", description = "更新角色状态")
    @PutMapping("/{roleId}/status")
    @RequirePermission("ROLE_UPDATE")
    @OperationLog(operationType = "UPDATE", operationName = "更新角色状态", resourceType = "ROLE")
    public Result<Void> updateRoleStatus(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId,
            @Parameter(description = "是否激活", required = true) @RequestParam Boolean isActive) {
        roleService.updateRoleStatus(roleId, isActive);
        return Result.success("角色状态更新成功");
    }

    @Operation(summary = "批量更新角色状态", description = "批量启用/禁用角色")
    @PutMapping("/batch/status")
    @RequirePermission("ROLE_UPDATE")
    @OperationLog(operationType = "UPDATE", operationName = "批量更新角色状态", resourceType = "ROLE")
    public Result<Void> batchUpdateRoleStatus(
            @Parameter(description = "角色ID列表", required = true) @RequestParam List<Long> roleIds,
            @Parameter(description = "是否激活", required = true) @RequestParam Boolean isActive) {
        roleService.batchUpdateRoleStatus(roleIds, isActive);
        return Result.success("角色状态批量更新成功");
    }

    @Operation(summary = "分配权限", description = "为角色分配权限")
    @PutMapping("/{roleId}/permissions")
    @RequirePermission("ROLE_UPDATE")
    @OperationLog(operationType = "UPDATE", operationName = "分配角色权限", resourceType = "ROLE")
    public Result<Void> assignPermissions(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId,
            @Parameter(description = "权限ID列表", required = true) @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return Result.success("权限分配成功");
    }

    @Operation(summary = "获取角色权限", description = "获取角色的权限列表")
    @GetMapping("/{roleId}/permissions")
    @RequirePermission("ROLE_DETAIL")
    @OperationLog(operationType = "QUERY", operationName = "获取角色权限", resourceType = "ROLE")
    public Result<List<String>> getRolePermissions(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId) {
        List<String> permissions = roleService.getRolePermissions(roleId);
        return Result.success(permissions);
    }

    @Operation(summary = "分配角色给用户", description = "为用户分配角色")
    @PutMapping("/assign/user/{userId}")
    @RequirePermission("ROLE_UPDATE")
    @OperationLog(operationType = "UPDATE", operationName = "分配用户角色", resourceType = "ROLE")
    public Result<Void> assignRolesToUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "角色ID列表", required = true) @RequestBody List<Long> roleIds) {
        roleService.assignRolesToUser(userId, roleIds);
        return Result.success("用户角色分配成功");
    }

    @Operation(summary = "设置用户角色有效期", description = "设置用户角色的有效期")
    @PutMapping("/user/{userId}/role/{roleId}/effective-time")
    @RequirePermission("ROLE_UPDATE")
    @OperationLog(operationType = "UPDATE", operationName = "设置用户角色有效期", resourceType = "ROLE")
    public Result<Void> setUserRoleEffectiveTime(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId,
            @Parameter(description = "生效开始时间") @RequestParam(required = false) LocalDateTime effectiveFrom,
            @Parameter(description = "生效结束时间") @RequestParam(required = false) LocalDateTime effectiveTo) {
        roleService.setUserRoleEffectiveTime(userId, roleId, effectiveFrom, effectiveTo);
        return Result.success("用户角色有效期设置成功");
    }

    @Operation(summary = "获取角色用户数量", description = "获取角色下的用户数量")
    @GetMapping("/{roleId}/user-count")
    @RequirePermission("ROLE_DETAIL")
    @OperationLog(operationType = "QUERY", operationName = "获取角色用户数量", resourceType = "ROLE")
    public Result<Long> getRoleUserCount(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId) {
        Long userCount = roleService.getRoleUserCount(roleId);
        return Result.success(userCount);
    }

    @Operation(summary = "检查角色编码是否存在", description = "检查角色编码是否已被使用")
    @GetMapping("/check/code")
    public Result<Boolean> checkRoleCode(
            @Parameter(description = "角色编码", required = true) @RequestParam String roleCode) {
        boolean exists = roleService.existsByRoleCode(roleCode);
        return Result.success(exists);
    }

    @Operation(summary = "检查角色是否可删除", description = "检查角色是否可以删除")
    @GetMapping("/{roleId}/can-delete")
    @RequirePermission("ROLE_DELETE")
    public Result<Boolean> canDeleteRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId) {
        boolean canDelete = roleService.canDeleteRole(roleId);
        return Result.success(canDelete);
    }

    @Operation(summary = "清理过期角色关联", description = "清理过期的用户角色关联")
    @PostMapping("/clean-expired")
    @RequirePermission("ROLE_UPDATE")
    @OperationLog(operationType = "UPDATE", operationName = "清理过期角色关联", resourceType = "ROLE")
    public Result<Void> cleanExpiredRoleMappings() {
        roleService.cleanExpiredRoleMappings();
        return Result.success("过期角色关联清理成功");
    }
}
