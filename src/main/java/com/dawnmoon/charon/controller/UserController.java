package com.dawnmoon.charon.controller;

import com.dawnmoon.charon.common.api.ApiResponse;
import com.dawnmoon.charon.common.enums.ErrorCode;
import com.dawnmoon.charon.common.exception.BusinessException;
import com.dawnmoon.charon.model.entity.User;
import com.dawnmoon.charon.model.request.UserRequests;
import com.dawnmoon.charon.model.response.PageResponse;
import com.dawnmoon.charon.model.response.UserResponse;
import com.dawnmoon.charon.service.UserService;
import com.dawnmoon.charon.util.CryptoUtil;
import com.dawnmoon.charon.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * 用户管理 Controller
 */
@Slf4j
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "分页查询用户列表",
        description = "支持按关键词（用户名、真实姓名、手机号、邮箱）和状态查询",
        security = @SecurityRequirement(name = "Authorization")
    )
    @GetMapping("/list")
    public ApiResponse<PageResponse<UserResponse>> list(@Valid UserRequests.QueryRequest request) {
        System.out.println(request);
        PageResponse<User> pageResponse = userService.list(
            request.getPageNum(), 
            request.getPageSize(), 
            request.getKeyword()
        );
        
        // 转换为 UserResponse
        PageResponse<UserResponse> result = new PageResponse<>(
                pageResponse.getTotal(),
                pageResponse.getPageNum(),
                pageResponse.getPageSize(),
                pageResponse.getPages(),
                pageResponse.getList().stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList())
        );
        
        return ApiResponse.success(result);
    }

    @Operation(
        summary = "根据 ID 获取用户详情",
        description = "获取用户的详细信息，包括角色列表",
        security = @SecurityRequirement(name = "Authorization")
    )
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(
        @Parameter(description = "用户ID", required = true) @PathVariable Long id
    ) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return ApiResponse.success(convertToResponse(user));
    }

    @Operation(
        summary = "创建用户",
        description = "创建新用户，仅管理员可操作",
        security = @SecurityRequirement(name = "Authorization")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<String> create(@RequestBody @Valid UserRequests.CreateRequest request) {
        // 检查用户名是否已存在
        User existingUser = userService.getByUsername(request.getUsername());
        if (existingUser != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "用户名已存在");
        }
        
        // 创建用户
        User user = new User();
        BeanUtils.copyProperties(request, user);
        
        // 加密密码
        user.setPassword(CryptoUtil.bcryptEncode(request.getPassword()));
        
        userService.create(user);
        
        log.info("管理员创建用户成功：{}", request.getUsername());
        return ApiResponse.success("创建用户成功");
    }

    @Operation(
        summary = "更新用户信息（普通用户）",
        description = "用户只能更新自己的基本信息（真实姓名、手机号、邮箱、头像），不能修改状态",
        security = @SecurityRequirement(name = "Authorization")
    )
    @PutMapping("/update")
    public ApiResponse<String> update(@RequestBody @Valid UserRequests.UpdateRequest request) {
        // 获取当前登录用户 ID
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        
        // 只能更新自己的信息
        if (!currentUserId.equals(request.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能修改自己的信息");
        }
        
        // 检查用户是否存在
        User user = userService.getById(request.getId());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 更新基本信息
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        
        userService.update(user);
        
        log.info("用户更新自己的信息成功：userId={}", currentUserId);
        return ApiResponse.success("更新用户信息成功");
    }

    @Operation(
        summary = "管理员更新用户信息",
        description = "管理员可以更新用户的所有信息，包括状态",
        security = @SecurityRequirement(name = "Authorization")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/update")
    public ApiResponse<String> adminUpdate(@RequestBody @Valid UserRequests.AdminUpdateRequest request) {
        // 检查用户是否存在
        User user = userService.getById(request.getId());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 更新所有字段
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        
        userService.update(user);
        
        log.info("管理员更新用户信息成功：userId={}", request.getId());
        return ApiResponse.success("更新用户信息成功");
    }

    @Operation(
        summary = "删除用户",
        description = "删除指定用户（逻辑删除），仅管理员可操作",
        security = @SecurityRequirement(name = "Authorization")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(
        @Parameter(description = "用户ID", required = true) @PathVariable Long id
    ) {
        // 检查用户是否存在
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 不能删除自己
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId != null && currentUserId.equals(id)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "不能删除自己");
        }
        
        userService.delete(id);
        
        log.info("管理员删除用户成功：userId={}", id);
        return ApiResponse.success("删除用户成功");
    }

    @Operation(
        summary = "修改密码",
        description = "用户修改自己的密码",
        security = @SecurityRequirement(name = "Authorization")
    )
    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody @Valid UserRequests.ChangePasswordRequest request) {
        // 获取当前登录用户 ID
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        
        // 获取当前用户信息
        User user = userService.getById(currentUserId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 验证旧密码
        if (!CryptoUtil.bcryptMatches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "旧密码不正确");
        }
        
        // 更新密码
        user.setPassword(CryptoUtil.bcryptEncode(request.getNewPassword()));
        userService.update(user);
        
        log.info("用户修改密码成功：userId={}", currentUserId);
        return ApiResponse.success("修改密码成功");
    }

    /**
     * 转换 User 实体为 UserResponse
     */
    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .realName(user.getRealName())
            .phone(user.getPhone())
            .email(user.getEmail())
            .avatar(user.getAvatar())
            .status(user.getStatus())
            .roles(user.getRoles())
            .createAt(user.getCreateAt())
            .updateAt(user.getUpdateAt())
            .build();
    }
}


