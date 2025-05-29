package space.akko.platform.user.controller;

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
import space.akko.platform.user.model.request.UserCreateRequest;
import space.akko.platform.user.model.request.UserQueryRequest;
import space.akko.platform.user.model.request.UserUpdateRequest;
import space.akko.platform.user.model.vo.UserVO;
import space.akko.platform.user.service.UserService;

import java.util.List;

/**
 * 用户管理控制器
 * 
 * @author akko
 * @since 1.0.0
 */
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户", description = "根据条件分页查询用户列表")
    @GetMapping
    @RequirePermission("USER_LIST")
    @OperationLog(operationType = "QUERY", operationName = "分页查询用户", resourceType = "USER")
    public Result<PageResult<UserVO>> getUserPage(@Parameter(description = "查询条件") UserQueryRequest request) {
        PageResult<UserVO> result = userService.getUserPage(request);
        return Result.success(result);
    }

    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    @GetMapping("/{userId}")
    @RequirePermission("USER_DETAIL")
    @OperationLog(operationType = "QUERY", operationName = "获取用户详情", resourceType = "USER")
    public Result<UserVO> getUserById(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        UserVO user = userService.getUserById(userId);
        return Result.success(user);
    }

    @Operation(summary = "创建用户", description = "创建新用户")
    @PostMapping
    @RequirePermission("USER_CREATE")
    @OperationLog(operationType = "CREATE", operationName = "创建用户", resourceType = "USER", 
                 includeRequestBody = true)
    public Result<UserVO> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserVO user = userService.createUser(request);
        return Result.success("用户创建成功", user);
    }

    @Operation(summary = "更新用户", description = "更新用户信息")
    @PutMapping("/{userId}")
    @RequirePermission("USER_UPDATE")
    @OperationLog(operationType = "UPDATE", operationName = "更新用户", resourceType = "USER", 
                 includeRequestBody = true)
    public Result<UserVO> updateUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserVO user = userService.updateUser(userId, request);
        return Result.success("用户更新成功", user);
    }

    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @DeleteMapping("/{userId}")
    @RequirePermission("USER_DELETE")
    @OperationLog(operationType = "DELETE", operationName = "删除用户", resourceType = "USER")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        userService.deleteUser(userId);
        return Result.success("用户删除成功");
    }

    @Operation(summary = "批量删除用户", description = "根据用户ID列表批量删除用户")
    @DeleteMapping("/batch")
    @RequirePermission("USER_DELETE")
    @OperationLog(operationType = "DELETE", operationName = "批量删除用户", resourceType = "USER")
    public Result<Void> batchDeleteUsers(
            @Parameter(description = "用户ID列表", required = true) @RequestBody List<Long> userIds) {
        userService.batchDeleteUsers(userIds);
        return Result.success("用户批量删除成功");
    }

    @Operation(summary = "启用/禁用用户", description = "更新用户状态")
    @PutMapping("/{userId}/status")
    @RequirePermission("USER_UPDATE")
    @OperationLog(operationType = "UPDATE", operationName = "更新用户状态", resourceType = "USER")
    public Result<Void> updateUserStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "是否激活", required = true) @RequestParam Boolean isActive) {
        userService.updateUserStatus(userId, isActive);
        return Result.success("用户状态更新成功");
    }

    @Operation(summary = "批量更新用户状态", description = "批量启用/禁用用户")
    @PutMapping("/batch/status")
    @RequirePermission("USER_UPDATE")
    @OperationLog(operationType = "UPDATE", operationName = "批量更新用户状态", resourceType = "USER")
    public Result<Void> batchUpdateUserStatus(
            @Parameter(description = "用户ID列表", required = true) @RequestParam List<Long> userIds,
            @Parameter(description = "是否激活", required = true) @RequestParam Boolean isActive) {
        userService.batchUpdateUserStatus(userIds, isActive);
        return Result.success("用户状态批量更新成功");
    }

    @Operation(summary = "重置用户密码", description = "重置用户密码为默认密码")
    @PutMapping("/{userId}/password/reset")
    @RequirePermission("USER_UPDATE")
    @OperationLog(operationType = "UPDATE", operationName = "重置用户密码", resourceType = "USER")
    public Result<Void> resetPassword(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "新密码") @RequestParam(required = false) String newPassword) {
        userService.resetPassword(userId, newPassword);
        return Result.success("密码重置成功");
    }

    @Operation(summary = "分配角色", description = "为用户分配角色")
    @PutMapping("/{userId}/roles")
    @RequirePermission("USER_UPDATE")
    @OperationLog(operationType = "UPDATE", operationName = "分配用户角色", resourceType = "USER")
    public Result<Void> assignRoles(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "角色ID列表", required = true) @RequestBody List<Long> roleIds) {
        userService.assignRoles(userId, roleIds);
        return Result.success("角色分配成功");
    }

    @Operation(summary = "获取用户角色", description = "获取用户的角色列表")
    @GetMapping("/{userId}/roles")
    @RequirePermission("USER_DETAIL")
    @OperationLog(operationType = "QUERY", operationName = "获取用户角色", resourceType = "USER")
    public Result<List<String>> getUserRoles(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        List<String> roles = userService.getUserRoles(userId);
        return Result.success(roles);
    }

    @Operation(summary = "获取用户权限", description = "获取用户的权限列表")
    @GetMapping("/{userId}/permissions")
    @RequirePermission("USER_DETAIL")
    @OperationLog(operationType = "QUERY", operationName = "获取用户权限", resourceType = "USER")
    public Result<List<String>> getUserPermissions(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        List<String> permissions = userService.getUserPermissions(userId);
        return Result.success(permissions);
    }

    @Operation(summary = "检查用户名是否存在", description = "检查用户名是否已被使用")
    @GetMapping("/check/username")
    public Result<Boolean> checkUsername(
            @Parameter(description = "用户名", required = true) @RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return Result.success(exists);
    }

    @Operation(summary = "检查邮箱是否存在", description = "检查邮箱是否已被使用")
    @GetMapping("/check/email")
    public Result<Boolean> checkEmail(
            @Parameter(description = "邮箱", required = true) @RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return Result.success(exists);
    }

    @Operation(summary = "检查手机号是否存在", description = "检查手机号是否已被使用")
    @GetMapping("/check/phone")
    public Result<Boolean> checkPhoneNumber(
            @Parameter(description = "手机号", required = true) @RequestParam String phoneNumber) {
        boolean exists = userService.existsByPhoneNumber(phoneNumber);
        return Result.success(exists);
    }

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/current")
    @OperationLog(operationType = "QUERY", operationName = "获取当前用户信息", resourceType = "USER")
    public Result<UserVO> getCurrentUser() {
        UserVO user = userService.getCurrentUser();
        return Result.success(user);
    }
}
