package com.dawnmoon.charon.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dawnmoon.charon.common.api.ApiResponse;
import com.dawnmoon.charon.common.enums.ErrorCode;
import com.dawnmoon.charon.common.exception.BusinessException;
import com.dawnmoon.charon.common.security.RequirePermission;
import com.dawnmoon.charon.model.entity.Role;
import com.dawnmoon.charon.model.entity.User;
import com.dawnmoon.charon.model.request.RoleRequests;
import com.dawnmoon.charon.model.response.PageResponse;
import com.dawnmoon.charon.model.response.RoleResponse;
import com.dawnmoon.charon.model.response.UserResponse;
import com.dawnmoon.charon.service.RoleService;
import com.dawnmoon.charon.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理 Controller
 */
@Tag(name = "角色管理", description = "角色相关接口")
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
@Validated
public class RoleController {

    private final RoleService roleService;
    private final UserRoleService userRoleService;

    @Operation(summary = "创建角色", description = "创建新的角色")
    @RequirePermission("ROLE:CREATE")
    @PostMapping
    public ApiResponse<Long> createRole(@RequestBody @Valid RoleRequests.CreateRequest request) {
        Role role = new Role();
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        role.setStatus(request.getStatus());

        roleService.createRole(role);
        return ApiResponse.success(role.getId());
    }

    @Operation(summary = "更新角色", description = "更新指定角色的信息")
    @RequirePermission("ROLE:UPDATE")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateRole(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @RequestBody @Valid RoleRequests.UpdateRequest request) {
        // 获取现有角色
        Role existingRole = roleService.getById(id);
        if (existingRole == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "角色不存在");
        }

        // 只更新非null字段
        if (StringUtils.isNotBlank(request.getRoleName())) {
            existingRole.setRoleName(request.getRoleName());
        }
        if (StringUtils.isNotBlank(request.getDescription())) {
            existingRole.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            existingRole.setStatus(request.getStatus());
        }

        roleService.updateRole(id, existingRole);
        return ApiResponse.success();
    }

    @Operation(summary = "删除角色", description = "删除指定角色（逻辑删除）")
    @RequirePermission("ROLE:DELETE")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@Parameter(description = "角色ID") @PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.success();
    }

    @Operation(summary = "获取角色详情", description = "根据ID获取角色详细信息")
    @RequirePermission("ROLE:VIEW")
    @GetMapping("/{id}")
    public ApiResponse<RoleResponse> getById(@Parameter(description = "角色ID") @PathVariable Long id) {
        Role role = roleService.getById(id);
        RoleResponse response = convertToResponse(role);
        return ApiResponse.success(response);
    }

    @Operation(summary = "分页查询角色列表", description = "支持按关键词搜索角色")
    @RequirePermission("ROLE:VIEW")
    @GetMapping("/list")
    public ApiResponse<PageResponse<RoleResponse>> listRoles(@Valid RoleRequests.QueryRequest request) {
        PageResponse<Role> page = roleService.listRoles(
                request.getPageNum(),
                request.getPageSize(),
                request.getKeyword()
        );

        PageResponse<RoleResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setPageNum(page.getPageNum());
        response.setPageSize(page.getPageSize());
        response.setPages(page.getPages());
        response.setList(page.getList().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));

        return ApiResponse.success(response);
    }

    @Operation(summary = "根据角色编码获取角色", description = "通过角色编码查询角色信息")
    @RequirePermission("ROLE:VIEW")
    @GetMapping("/code/{roleCode}")
    public ApiResponse<RoleResponse> getByRoleCode(
            @Parameter(description = "角色编码") @PathVariable String roleCode) {
        Role role = roleService.getByRoleCode(roleCode);
        RoleResponse response = convertToResponse(role);
        return ApiResponse.success(response);
    }

    @Operation(summary = "分配角色给用户", description = "批量分配角色给指定用户")
    @RequirePermission("ROLE:UPDATE")
    @PostMapping("/assign")
    public ApiResponse<Void> assignRolesToUser(@RequestBody @Valid RoleRequests.AssignToUserRequest request) {
        userRoleService.assignRolesToUser(request.getUserId(), request.getRoleIds());
        return ApiResponse.success();
    }

    @Operation(summary = "移除用户角色", description = "移除用户的指定角色")
    @RequirePermission("ROLE:UPDATE")
    @DeleteMapping("/user/{userId}/{roleId}")
    public ApiResponse<Void> removeUserRole(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        userRoleService.removeUserRole(userId, roleId);
        return ApiResponse.success();
    }

    @Operation(summary = "获取用户的所有角色", description = "查询指定用户拥有的所有角色")
    @RequirePermission("ROLE:VIEW")
    @GetMapping("/user/{userId}")
    public ApiResponse<List<RoleResponse>> getUserRoles(@Parameter(description = "用户ID") @PathVariable Long userId) {
        List<Role> roles = roleService.getRolesByUserId(userId);
        List<RoleResponse> responses = roles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    @Operation(summary = "获取角色的所有用户", description = "查询指定角色的所有用户")
    @RequirePermission("ROLE:VIEW")
    @GetMapping("/{roleId}/users")
    public ApiResponse<List<UserResponse>> getRoleUsers(
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        List<User> users = userRoleService.getUsersByRoleId(roleId);
        List<UserResponse> responses = users.stream()
                .map(this::convertUserToResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    /**
     * 将 Role 实体转换为 RoleResponse
     */
    private RoleResponse convertToResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .status(role.getStatus())
                .createAt(role.getCreateAt())
                .updateAt(role.getUpdateAt())
                .build();
    }

    /**
     * 将 User 实体转换为 UserResponse
     */
    private UserResponse convertUserToResponse(User user) {
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

