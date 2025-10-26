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
    DATA_NOT_FOUND("DATA_NOT_FOUND", "数据不存在");

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



