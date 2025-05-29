package space.akko.platform.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.akko.foundation.annotation.OperationLog;
import space.akko.foundation.common.Result;
import space.akko.foundation.constant.SecurityConstants;
import space.akko.foundation.utils.JwtUtils;
import space.akko.platform.user.model.request.LoginRequest;
import space.akko.platform.user.model.response.LoginResponse;
import space.akko.platform.user.service.UserService;

/**
 * 认证控制器
 * 
 * @author akko
 * @since 1.0.0
 */
@Tag(name = "用户认证", description = "用户认证相关接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "用户登录", description = "用户登录获取访问令牌")
    @PostMapping("/login")
    @OperationLog(operationType = "LOGIN", operationName = "用户登录", resourceType = "AUTH", 
                 includeRequestBody = true, async = false)
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success("登录成功", response);
    }

    @Operation(summary = "用户登出", description = "用户登出，令牌失效")
    @PostMapping("/logout")
    @OperationLog(operationType = "LOGOUT", operationName = "用户登出", resourceType = "AUTH")
    public Result<Void> logout() {
        userService.logout();
        return Result.success("登出成功");
    }

    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    @PostMapping("/refresh")
    @OperationLog(operationType = "REFRESH_TOKEN", operationName = "刷新令牌", resourceType = "AUTH")
    public Result<LoginResponse> refreshToken(
            @Parameter(description = "刷新令牌", required = true)
            @RequestHeader(SecurityConstants.TOKEN_HEADER) String authHeader) {
        
        String refreshToken = JwtUtils.extractTokenFromHeader(authHeader);
        LoginResponse response = userService.refreshToken(refreshToken);
        return Result.success("令牌刷新成功", response);
    }
}
