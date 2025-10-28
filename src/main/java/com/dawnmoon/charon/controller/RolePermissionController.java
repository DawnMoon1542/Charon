package com.dawnmoon.charon.controller;

import com.dawnmoon.charon.common.api.ApiResponse;
import com.dawnmoon.charon.common.security.RequirePermission;
import com.dawnmoon.charon.model.entity.Permission;
import com.dawnmoon.charon.model.entity.Role;
import com.dawnmoon.charon.model.request.PermissionRequests;
import com.dawnmoon.charon.model.response.PermissionResponse;
import com.dawnmoon.charon.model.response.RoleResponse;
import com.dawnmoon.charon.service.PermissionService;
import com.dawnmoon.charon.service.RolePermissionService;
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
 * 角色权限关联 Controller
 */
@Tag(name = "角色权限管理", description = "角色权限关联接口")
@RestController
@RequestMapping("/api/role-permission")
@RequiredArgsConstructor
@Validated
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;
    private final PermissionService permissionService;

    @Operation(summary = "分配权限给角色", description = "批量分配权限给指定角色")
    @RequirePermission("PERMISSION:UPDATE")
    @PostMapping("/assign")
    public ApiResponse<Void> assignPermissionsToRole(
            @RequestBody @Valid PermissionRequests.AssignToRoleRequest request) {
        rolePermissionService.assignPermissionsToRole(request.getRoleId(), request.getPermissionIds());
        return ApiResponse.success();
    }

    @Operation(summary = "移除角色权限", description = "移除角色的指定权限")
    @RequirePermission("PERMISSION:UPDATE")
    @DeleteMapping("/{roleId}/{permissionId}")
    public ApiResponse<Void> removeRolePermission(
            @Parameter(description = "角色ID") @PathVariable Long roleId,
            @Parameter(description = "权限ID") @PathVariable Long permissionId) {
        rolePermissionService.removeRolePermission(roleId, permissionId);
        return ApiResponse.success();
    }

    @Operation(summary = "移除角色的单个权限（兼容路径）", description = "移除角色的指定权限")
    @RequirePermission("PERMISSION:UPDATE")
    @DeleteMapping("/role/{roleId}/permission/{permissionId}")
    public ApiResponse<Void> removeRolePermissionAlias(
            @Parameter(description = "角色ID") @PathVariable Long roleId,
            @Parameter(description = "权限ID") @PathVariable Long permissionId) {
        rolePermissionService.removeRolePermission(roleId, permissionId);
        return ApiResponse.success();
    }

    @Operation(summary = "移除角色的所有权限", description = "移除角色的所有权限")
    @RequirePermission("PERMISSION:UPDATE")
    @DeleteMapping("/role/{roleId}/permissions")
    public ApiResponse<Void> removeAllRolePermissions(
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        rolePermissionService.removeAllRolePermissions(roleId);
        return ApiResponse.success();
    }

    @Operation(summary = "获取角色的所有权限", description = "查询指定角色拥有的所有权限")
    @RequirePermission("PERMISSION:VIEW")
    @GetMapping("/role/{roleId}")
    public ApiResponse<List<PermissionResponse>> getRolePermissions(
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        List<Permission> permissions = permissionService.getPermissionsByRoleId(roleId);
        List<PermissionResponse> responses = permissions.stream()
                .map(this::convertToPermissionResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    @Operation(summary = "获取角色的所有权限（兼容路径）", description = "查询指定角色拥有的所有权限")
    @RequirePermission("PERMISSION:VIEW")
    @GetMapping("/role/{roleId}/permissions")
    public ApiResponse<List<PermissionResponse>> getRolePermissionsAlias(
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        return getRolePermissions(roleId);
    }

    @Operation(summary = "获取拥有某权限的所有角色", description = "查询拥有指定权限的所有角色")
    @RequirePermission("PERMISSION:VIEW")
    @GetMapping("/permission/{permissionId}")
    public ApiResponse<List<RoleResponse>> getPermissionRoles(
            @Parameter(description = "权限ID") @PathVariable Long permissionId) {
        List<Role> roles = rolePermissionService.getRolesByPermissionId(permissionId);
        List<RoleResponse> responses = roles.stream()
                .map(this::convertToRoleResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    @Operation(summary = "获取拥有某权限的所有角色（兼容路径）", description = "查询拥有指定权限的所有角色")
    @RequirePermission("PERMISSION:VIEW")
    @GetMapping("/permission/{permissionId}/roles")
    public ApiResponse<List<RoleResponse>> getPermissionRolesAlias(
            @Parameter(description = "权限ID") @PathVariable Long permissionId) {
        return getPermissionRoles(permissionId);
    }

    @Operation(summary = "分配权限给角色（兼容路径）", description = "批量分配权限给指定角色")
    @RequirePermission("PERMISSION:UPDATE")
    @PostMapping("/role/{roleId}/permissions")
    public ApiResponse<Void> assignPermissionsToRoleByPath(
            @Parameter(description = "角色ID") @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        rolePermissionService.assignPermissionsToRole(roleId, permissionIds);
        return ApiResponse.success();
    }

    /**
     * 将 Permission 实体转换为 PermissionResponse
     */
    private PermissionResponse convertToPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .permissionCode(permission.getPermissionCode())
                .permissionName(permission.getPermissionName())
                .description(permission.getDescription())
                .createAt(permission.getCreateAt())
                .updateAt(permission.getUpdateAt())
                .build();
    }

    /**
     * 将 Role 实体转换为 RoleResponse
     */
    private RoleResponse convertToRoleResponse(Role role) {
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

