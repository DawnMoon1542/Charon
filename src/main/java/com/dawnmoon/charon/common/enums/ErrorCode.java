package com.dawnmoon.charon.common.enums;

import lombok.Getter;

/**
 * 统一错误码枚举
 */
@Getter
public enum ErrorCode implements BaseEnum<String> {

    // 成功
    SUCCESS("SUCCESS", "操作成功"),

    // 客户端错误 4xx
    BAD_REQUEST("BAD_REQUEST", "请求参数错误"),
    UNAUTHORIZED("UNAUTHORIZED", "未认证或认证已过期"),
    FORBIDDEN("FORBIDDEN", "没有访问权限"),
    NOT_FOUND("NOT_FOUND", "请求的资源不存在"),
    VALIDATE_FAILED("VALIDATE_FAILED", "参数校验失败"),

    // 业务错误 5xx
    BUSINESS_ERROR("BUSINESS_ERROR", "业务处理失败"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "服务器内部错误"),

    // 用户相关错误
    USER_NOT_FOUND("USER_NOT_FOUND", "用户不存在"),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "用户已存在"),
    USERNAME_OR_PASSWORD_ERROR("USERNAME_OR_PASSWORD_ERROR", "用户名或密码错误"),
    USER_ACCOUNT_LOCKED("USER_ACCOUNT_LOCKED", "账号已被锁定"),
    USER_ACCOUNT_DISABLED("USER_ACCOUNT_DISABLED", "账号已被禁用"),

    // Token 相关错误
    TOKEN_INVALID("TOKEN_INVALID", "Token 无效"),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token 已过期"),
    TOKEN_MISSING("TOKEN_MISSING", "缺少 Token"),

    // 数据库相关错误
    DATABASE_ERROR("DATABASE_ERROR", "数据库操作失败"),
    DATA_ALREADY_EXISTS("DATA_ALREADY_EXISTS", "数据已存在"),
    DATA_NOT_FOUND("DATA_NOT_FOUND", "数据不存在"),

    // 角色相关错误
    ROLE_NOT_FOUND("ROLE_NOT_FOUND", "角色不存在"),
    ROLE_ALREADY_EXISTS("ROLE_ALREADY_EXISTS", "角色已存在"),
    ROLE_CODE_ALREADY_EXISTS("ROLE_CODE_ALREADY_EXISTS", "角色编码已存在"),
    ROLE_IN_USE("ROLE_IN_USE", "角色正在使用中，无法删除"),

    // 权限相关错误
    PERMISSION_NOT_FOUND("PERMISSION_NOT_FOUND", "权限不存在"),
    PERMISSION_ALREADY_EXISTS("PERMISSION_ALREADY_EXISTS", "权限已存在"),
    PERMISSION_CODE_ALREADY_EXISTS("PERMISSION_CODE_ALREADY_EXISTS", "权限编码已存在"),
    PERMISSION_IN_USE("PERMISSION_IN_USE", "权限正在使用中，无法删除"),

    // 用户角色关联错误
    USER_ROLE_ALREADY_EXISTS("USER_ROLE_ALREADY_EXISTS", "用户已拥有该角色"),
    USER_ROLE_NOT_FOUND("USER_ROLE_NOT_FOUND", "用户角色关联不存在"),

    // 角色权限关联错误
    ROLE_PERMISSION_ALREADY_EXISTS("ROLE_PERMISSION_ALREADY_EXISTS", "角色已拥有该权限"),
    ROLE_PERMISSION_NOT_FOUND("ROLE_PERMISSION_NOT_FOUND", "角色权限关联不存在");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}



