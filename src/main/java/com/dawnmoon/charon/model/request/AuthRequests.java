package com.dawnmoon.charon.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 认证相关请求对象
 */
public class AuthRequests {

    /**
     * 登录请求
     */
    @Data
    @Schema(description = "登录请求")
    public static class LoginRequest {

        @Schema(description = "用户名", example = "admin")
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
        private String username;

        @Schema(description = "密码", example = "123456")
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
        private String password;
    }

    /**
     * 注册请求
     */
    @Data
    @Schema(description = "注册请求")
    public static class RegisterRequest {

        @Schema(description = "用户名", example = "newuser")
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
        private String username;

        @Schema(description = "密码", example = "123456")
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
        private String password;

        @Schema(description = "真实姓名", example = "张三")
        @NotBlank(message = "真实姓名不能为空")
        @Size(max = 50, message = "真实姓名长度不能超过50个字符")
        private String realName;

        @Schema(description = "手机号", example = "13800138000")
        @Size(max = 20, message = "手机号长度不能超过20个字符")
        private String phone;

        @Schema(description = "邮箱", example = "user@example.com")
        @Size(max = 100, message = "邮箱长度不能超过100个字符")
        private String email;
    }
}



