package com.dawnmoon.charon.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * 权限相关请求对象
 */
public class PermissionRequests {

    /**
     * 创建权限请求
     */
    @Data
    @Schema(description = "创建权限请求")
    public static class CreateRequest {

        @Schema(description = "权限编码", example = "USER:CREATE")
        @NotBlank(message = "权限编码不能为空")
        @Pattern(regexp = "^[A-Z][A-Z0-9_:]*$", message = "权限编码必须以大写字母开头,只能包含大写字母、数字、下划线和冒号")
        @Size(min = 3, max = 100, message = "权限编码长度必须在3-100个字符之间")
        private String permissionCode;

        @Schema(description = "权限名称", example = "创建用户")
        @NotBlank(message = "权限名称不能为空")
        @Size(min = 2, max = 100, message = "权限名称长度必须在2-100个字符之间")
        private String permissionName;

        @Schema(description = "权限描述", example = "创建新用户的权限")
        @Size(max = 500, message = "权限描述长度不能超过500个字符")
        private String description;
    }

    /**
     * 更新权限请求
     */
    @Data
    @Schema(description = "更新权限请求")
    public static class UpdateRequest {

        @Schema(description = "权限编码", example = "USER:CREATE")
        @Pattern(regexp = "^[A-Z][A-Z0-9_:]*$", message = "权限编码必须以大写字母开头,只能包含大写字母、数字、下划线和冒号")
        @Size(min = 3, max = 100, message = "权限编码长度必须在3-100个字符之间")
        private String permissionCode;

        @Schema(description = "权限名称", example = "创建用户")
        @Size(min = 2, max = 100, message = "权限名称长度必须在2-100个字符之间")
        private String permissionName;

        @Schema(description = "权限描述", example = "创建新用户的权限")
        @Size(max = 500, message = "权限描述长度不能超过500个字符")
        private String description;
    }

    /**
     * 查询权限请求
     */
    @Data
    @Schema(description = "查询权限请求")
    public static class QueryRequest {

        @Schema(description = "关键词（权限编码、权限名称或描述）", example = "USER")
        private String keyword;

        @Schema(description = "页码", example = "1")
        @Min(value = 1, message = "页码必须大于0")
        private Integer pageNum = 1;

        @Schema(description = "每页数量", example = "10")
        @Min(value = 1, message = "每页数量必须大于0")
        @Max(value = 100, message = "每页数量不能超过100")
        private Integer pageSize = 10;
    }

    /**
     * 分配权限给角色请求
     */
    @Data
    @Schema(description = "分配权限给角色请求")
    public static class AssignToRoleRequest {

        @Schema(description = "角色ID", example = "1")
        @NotNull(message = "角色ID不能为空")
        private Long roleId;

        @Schema(description = "权限ID列表", example = "[1, 2, 3]")
        @NotEmpty(message = "权限ID列表不能为空")
        private List<Long> permissionIds;
    }
}

