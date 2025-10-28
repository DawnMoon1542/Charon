package com.dawnmoon.charon.controller;

import com.dawnmoon.charon.common.api.ApiResponse;
import com.dawnmoon.charon.common.enums.ErrorCode;
import com.dawnmoon.charon.common.exception.BusinessException;
import com.dawnmoon.charon.common.security.RequirePermission;
import com.dawnmoon.charon.model.entity.Role;
import com.dawnmoon.charon.model.entity.User;
import com.dawnmoon.charon.model.request.UserRequests;
import com.dawnmoon.charon.model.response.PageResponse;
import com.dawnmoon.charon.model.response.RoleResponse;
import com.dawnmoon.charon.model.response.UserResponse;
import com.dawnmoon.charon.service.RoleService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private final RoleService roleService;
    private final com.dawnmoon.charon.service.UserRoleService userRoleService;

    @Operation(
        summary = "分页查询用户列表",
        description = "支持按关键词（用户名、真实姓名、手机号、邮箱）和状态查询",
        security = @SecurityRequirement(name = "Authorization")
    )
    @GetMapping("/list")
    public ApiResponse<PageResponse<UserResponse>> list(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键词（用户名、真实姓名、手机号、邮箱）", example = "张三") @RequestParam(required = false) String keyword,
            @Parameter(description = "账号状态：0-启用，1-禁用", example = "0") @RequestParam(required = false) Integer status
    ) {
        log.info("分页查询用户列表 - pageNum: {}, pageSize: {}, keyword: {}, status: {}", pageNum, pageSize, keyword, status);
        PageResponse<User> pageResponse = userService.list(pageNum, pageSize, keyword);
        
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
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getByUserId(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId
    ) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return ApiResponse.success(convertToResponse(user));
    }

    @Operation(
        summary = "创建用户",
            description = "创建新用户，需要USER:CREATE权限",
        security = @SecurityRequirement(name = "Authorization")
    )
    @RequirePermission("USER:CREATE")
    @PostMapping
    public ApiResponse<Long> create(@RequestBody @Valid UserRequests.CreateRequest request) {
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

        log.info("管理员创建用户成功：{}, ID: {}", request.getUsername(), user.getId());
        return ApiResponse.success("创建用户成功", user.getId());
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
            summary = "管理员更新指定用户信息",
            description = "管理员更新用户信息，支持部分更新",
        security = @SecurityRequirement(name = "Authorization")
    )
    @RequirePermission("USER:UPDATE")
    @PutMapping("/{userId}")
    public ApiResponse<String> updateByUserId(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @RequestBody @Valid UserRequests.AdminUpdateRequest request
    ) {
        // 检查用户是否存在
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 只更新非null字段
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

        log.info("管理员更新用户信息成功：userId={}", userId);
        return ApiResponse.success("更新用户信息成功");
    }

    @Operation(
            summary = "获取用户的所有角色",
            description = "查询指定用户拥有的所有角色",
            security = @SecurityRequirement(name = "Authorization")
    )
    @RequirePermission("USER:VIEW")
    @GetMapping("/{userId}/roles")
    public ApiResponse<List<RoleResponse>> getUserRoles(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId
    ) {
        List<Role> roles = roleService.getRolesByUserId(userId);
        List<RoleResponse> responses = roles.stream()
                .map(this::convertRoleToResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    @Operation(
            summary = "分配角色给用户",
            description = "批量分配角色给指定用户",
            security = @SecurityRequirement(name = "Authorization")
    )
    @RequirePermission("USER:UPDATE")
    @PostMapping("/{userId}/roles")
    public ApiResponse<Void> assignRolesToUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @RequestBody List<Long> roleIds
    ) {
        // 检查用户是否存在
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        userRoleService.assignRolesToUser(userId, roleIds);

        log.info("分配角色给用户成功：userId={}, roleIds={}", userId, roleIds);
        return ApiResponse.success();
    }

    @Operation(
            summary = "移除用户的单个角色",
            description = "移除用户的指定角色",
            security = @SecurityRequirement(name = "Authorization")
    )
    @RequirePermission("USER:UPDATE")
    @DeleteMapping("/{userId}/role/{roleId}")
    public ApiResponse<Void> removeUserRole(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId
    ) {
        userRoleService.removeUserRole(userId, roleId);
        log.info("移除用户角色成功：userId={}, roleId={}", userId, roleId);
        return ApiResponse.success();
    }

    @Operation(
            summary = "移除用户的所有角色",
            description = "移除用户的所有角色",
            security = @SecurityRequirement(name = "Authorization")
    )
    @RequirePermission("USER:UPDATE")
    @DeleteMapping("/{userId}/roles")
    public ApiResponse<Void> removeAllUserRoles(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId
    ) {
        userRoleService.removeAllUserRoles(userId);
        log.info("移除用户所有角色成功：userId={}", userId);
        return ApiResponse.success();
    }

    @Operation(
        summary = "删除用户",
        description = "删除指定用户（逻辑删除），仅管理员可操作",
        security = @SecurityRequirement(name = "Authorization")
    )
    @RequirePermission("USER:DELETE")
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

    /**
     * 转换 Role 实体为 RoleResponse
     */
    private RoleResponse convertRoleToResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .status(role.getStatus())
                .createAt(role.getCreateAt())
                .updateAt(role.getUpdateAt())
                .build();
    }
}


