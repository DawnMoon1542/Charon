package com.dawnmoon.springboot_app_template.controller;

import com.dawnmoon.springboot_app_template.common.api.ApiResponse;
import com.dawnmoon.springboot_app_template.model.request.AuthRequests;
import com.dawnmoon.springboot_app_template.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 认证授权 Controller
 */
@Tag(name = "认证授权", description = "登录、注册、登出相关接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录", description = "验证用户名密码，返回 Token")
    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody @Valid AuthRequests.LoginRequest request) {
        String token = authService.login(request);
        return ApiResponse.success("登录成功", token);
    }

    @Operation(summary = "用户注册", description = "创建新用户账号")
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody @Valid AuthRequests.RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success("注册成功");
    }

    @Operation(
        summary = "用户自己退出登录",
        description = "用户主动退出登录，删除当前 Token 对应的会话",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest request) {
        // 从请求头获取 Token
        String token = getTokenFromRequest(request);
        authService.logoutSelf(token);
        return ApiResponse.success("退出登录成功");
    }

    @Operation(
        summary = "管理员强制用户退出",
        description = "管理员强制指定用户下线（需要管理员权限）",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PreAuthorize("hasRole('ADMIN')") // 限制只有管理员可以调用
    @PostMapping("/admin/force-logout/{userId}")
    public ApiResponse<String> forceLogout(@PathVariable Long userId) {
        authService.forceLogoutByAdmin(userId);
        return ApiResponse.success("已强制用户退出登录");
    }

    @Operation(
        summary = "查询在线用户数",
        description = "获取当前在线用户总数",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/online-count")
    public ApiResponse<Long> getOnlineUserCount() {
        Long count = authService.getOnlineUserCount();
        return ApiResponse.success(count);
    }

    @Operation(
        summary = "查询用户登录时间",
        description = "获取指定用户的登录时间",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/login-time/{userId}")
    public ApiResponse<Long> getLoginTime(@PathVariable Long userId) {
        Long loginTime = authService.getLoginTime(userId);
        return ApiResponse.success(loginTime);
    }

    /**
     * 从请求头提取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}


