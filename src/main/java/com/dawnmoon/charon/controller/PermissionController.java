package com.dawnmoon.charon.controller;

import com.dawnmoon.charon.common.api.ApiResponse;
import com.dawnmoon.charon.common.security.RequirePermission;
import com.dawnmoon.charon.model.entity.Permission;
import com.dawnmoon.charon.model.request.PermissionRequests;
import com.dawnmoon.charon.model.response.PageResponse;
import com.dawnmoon.charon.model.response.PermissionResponse;
import com.dawnmoon.charon.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * 权限管理 Controller
 */
@Tag(name = "权限管理", description = "权限相关接口")
@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
@Validated
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "创建权限", description = "创建新的权限")
    @RequirePermission("PERMISSION:CREATE")
    @PostMapping
    public ApiResponse<Long> createPermission(@RequestBody @Valid PermissionRequests.CreateRequest request) {
        Permission permission = new Permission();
        permission.setPermissionCode(request.getPermissionCode());
        permission.setPermissionName(request.getPermissionName());
        permission.setDescription(request.getDescription());

        permissionService.createPermission(permission);
        return ApiResponse.success(permission.getId());
    }

    @Operation(summary = "更新权限", description = "更新指定权限的信息，支持部分更新")
    @RequirePermission("PERMISSION:UPDATE")
    @PutMapping("/{id}")
    public ApiResponse<Void> updatePermission(
            @Parameter(description = "权限ID") @PathVariable Long id,
            @RequestBody @Valid PermissionRequests.UpdateRequest request) {
        // 获取现有权限
        Permission existingPermission = permissionService.getById(id);
        if (existingPermission == null) {
            throw new com.dawnmoon.charon.common.exception.BusinessException(
                    com.dawnmoon.charon.common.enums.ErrorCode.BUSINESS_ERROR, "权限不存在");
        }

        // 只更新非null字段
        if (request.getPermissionCode() != null) {
            existingPermission.setPermissionCode(request.getPermissionCode());
        }
        if (request.getPermissionName() != null) {
            existingPermission.setPermissionName(request.getPermissionName());
        }
        if (request.getDescription() != null) {
            existingPermission.setDescription(request.getDescription());
        }

        permissionService.updatePermission(id, existingPermission);
        return ApiResponse.success();
    }

    @Operation(summary = "删除权限", description = "删除指定权限（逻辑删除）")
    @RequirePermission("PERMISSION:DELETE")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePermission(@Parameter(description = "权限ID") @PathVariable Long id) {
        permissionService.deletePermission(id);
        return ApiResponse.success();
    }

    @Operation(summary = "获取权限详情", description = "根据ID获取权限详细信息")
    @RequirePermission("PERMISSION:VIEW")
    @GetMapping("/{id}")
    public ApiResponse<PermissionResponse> getById(@Parameter(description = "权限ID") @PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        PermissionResponse response = convertToResponse(permission);
        return ApiResponse.success(response);
    }

    @Operation(summary = "分页查询权限列表", description = "支持按关键词搜索权限")
    @RequirePermission("PERMISSION:VIEW")
    @GetMapping("/list")
    public ApiResponse<PageResponse<PermissionResponse>> listPermissions(
            @Valid PermissionRequests.QueryRequest request) {
        PageResponse<Permission> page = permissionService.listPermissions(
                request.getPageNum(),
                request.getPageSize(),
                request.getKeyword());

        PageResponse<PermissionResponse> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setPageNum(page.getPageNum());
        response.setPageSize(page.getPageSize());
        response.setPages(page.getPages());
        response.setList(page.getList().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));

        return ApiResponse.success(response);
    }

    @Operation(summary = "根据权限编码获取权限", description = "通过权限编码查询权限信息")
    @RequirePermission("PERMISSION:VIEW")
    @GetMapping("/code/{code}")
    public ApiResponse<PermissionResponse> getByPermissionCode(
            @Parameter(description = "权限编码") @PathVariable String code) {
        Permission permission = permissionService.getByPermissionCode(code);
        PermissionResponse response = convertToResponse(permission);
        return ApiResponse.success(response);
    }

    /**
     * 将 Permission 实体转换为 PermissionResponse
     */
    private PermissionResponse convertToResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .permissionCode(permission.getPermissionCode())
                .permissionName(permission.getPermissionName())
                .description(permission.getDescription())
                .createAt(permission.getCreateAt())
                .updateAt(permission.getUpdateAt())
                .build();
    }
}

