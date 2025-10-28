package com.dawnmoon.charon.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * 角色相关请求对象
 */
public class RoleRequests {

    /**
     * 创建角色请求
     */
    @Data
    @Schema(description = "创建角色请求")
    public static class CreateRequest {

        @Schema(description = "角色名称", example = "ADMIN")
        @NotBlank(message = "角色名称不能为空")
        @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "角色名称必须以大写字母开头,只能包含大写字母、数字和下划线")
        @Size(min = 2, max = 50, message = "角色名称长度必须在2-50个字符之间")
        private String roleName;

        @Schema(description = "角色描述", example = "管理员角色")
        @Size(max = 255, message = "角色描述长度不能超过255个字符")
        private String description;

        @Schema(description = "角色状态：0-启用，1-禁用", example = "0")
        @NotNull(message = "角色状态不能为空")
        @Min(value = 0, message = "角色状态值必须为0或1")
        @Max(value = 1, message = "角色状态值必须为0或1")
        private Integer status;
    }

    /**
     * 更新角色请求
     */
    @Data
    @Schema(description = "更新角色请求")
    public static class UpdateRequest {

        @Schema(description = "角色名称", example = "ADMIN")
        @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "角色名称必须以大写字母开头,只能包含大写字母、数字和下划线")
        @Size(min = 2, max = 50, message = "角色名称长度必须在2-50个字符之间")
        private String roleName;

        @Schema(description = "角色描述", example = "管理员角色")
        @Size(max = 255, message = "角色描述长度不能超过255个字符")
        private String description;

        @Schema(description = "角色状态：0-启用，1-禁用", example = "0")
        @Min(value = 0, message = "角色状态值必须为0或1")
        @Max(value = 1, message = "角色状态值必须为0或1")
        private Integer status;
    }

    /**
     * 查询角色请求
     */
    @Data
    @Schema(description = "查询角色请求")
    public static class QueryRequest {

        @Schema(description = "关键词（角色名称或描述）", example = "ADMIN")
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
     * 分配角色给用户请求
     */
    @Data
    @Schema(description = "分配角色给用户请求")
    public static class AssignToUserRequest {

        @Schema(description = "用户ID", example = "1")
        @NotNull(message = "用户ID不能为空")
        private Long userId;

        @Schema(description = "角色ID列表", example = "[1, 2]")
        @NotEmpty(message = "角色ID列表不能为空")
        private List<Long> roleIds;
    }
}

