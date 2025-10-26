package com.dawnmoon.charon.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户相关请求对象
 */
public class UserRequests {

    /**
     * 用户查询请求
     */
    @Data
    @Schema(description = "用户查询请求")
    public static class QueryRequest implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "页码", example = "1")
        @Min(value = 1, message = "页码必须大于0")
        private Integer pageNum = 1;

        @Schema(description = "每页大小", example = "10")
        @Min(value = 1, message = "每页大小必须大于0")
        @Max(value = 100, message = "每页大小不能超过100")
        private Integer pageSize = 10;

        @Schema(description = "关键词（用户名、真实姓名、手机号、邮箱）", example = "张三")
        private String keyword;

        @Schema(description = "账号状态：0-启用，1-禁用", example = "0")
        private Integer status;
    }

    /**
     * 用户创建请求（仅管理员）
     */
    @Data
    @Schema(description = "用户创建请求")
    public static class CreateRequest implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
        private String username;

        @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
        private String password;

        @Schema(description = "真实姓名", example = "张三")
        @Size(max = 50, message = "真实姓名长度不能超过50个字符")
        private String realName;

        @Schema(description = "手机号", example = "13800138000")
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        private String phone;

        @Schema(description = "邮箱", example = "zhangsan@example.com")
        @Email(message = "邮箱格式不正确")
        @Size(max = 100, message = "邮箱长度不能超过100个字符")
        private String email;

        @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
        @Size(max = 255, message = "头像URL长度不能超过255个字符")
        private String avatar;

        @Schema(description = "账号状态：0-启用，1-禁用", example = "0")
        private Integer status = 0;
    }

    /**
     * 用户更新请求（管理员：完整更新，用户：仅更新自己的基本信息）
     */
    @Data
    @Schema(description = "用户更新请求")
    public static class UpdateRequest implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "用户ID不能为空")
        private Long id;

        @Schema(description = "真实姓名", example = "张三")
        @Size(max = 50, message = "真实姓名长度不能超过50个字符")
        private String realName;

        @Schema(description = "手机号", example = "13800138000")
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        private String phone;

        @Schema(description = "邮箱", example = "zhangsan@example.com")
        @Email(message = "邮箱格式不正确")
        @Size(max = 100, message = "邮箱长度不能超过100个字符")
        private String email;

        @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
        @Size(max = 255, message = "头像URL长度不能超过255个字符")
        private String avatar;
    }

    /**
     * 管理员更新用户请求（包含状态字段）
     */
    @Data
    @Schema(description = "管理员更新用户请求")
    public static class AdminUpdateRequest implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "用户ID不能为空")
        private Long id;

        @Schema(description = "真实姓名", example = "张三")
        @Size(max = 50, message = "真实姓名长度不能超过50个字符")
        private String realName;

        @Schema(description = "手机号", example = "13800138000")
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        private String phone;

        @Schema(description = "邮箱", example = "zhangsan@example.com")
        @Email(message = "邮箱格式不正确")
        @Size(max = 100, message = "邮箱长度不能超过100个字符")
        private String email;

        @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
        @Size(max = 255, message = "头像URL长度不能超过255个字符")
        private String avatar;

        @Schema(description = "账号状态：0-启用，1-禁用", example = "0")
        private Integer status;
    }

    /**
     * 修改密码请求
     */
    @Data
    @Schema(description = "修改密码请求")
    public static class ChangePasswordRequest implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "旧密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "旧密码不能为空")
        private String oldPassword;

        @Schema(description = "新密码", example = "654321", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, max = 20, message = "新密码长度必须在6-20个字符之间")
        private String newPassword;
    }
}


