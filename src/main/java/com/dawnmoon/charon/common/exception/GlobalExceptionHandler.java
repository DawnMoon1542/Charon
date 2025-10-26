package com.dawnmoon.charon.common.exception;

import com.dawnmoon.charon.common.api.ApiResponse;
import com.dawnmoon.charon.common.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理应用中的所有异常，并返回标准化的错误响应
 * 
 * 异常分类：
 * 1. 预期异常：记录WARN日志，返回错误码，不记录堆栈
 * 2. 非预期异常：记录ERROR日志，返回500错误，记录完整堆栈
 * 
 * @author DawnMoon
 * @since 2025-10-22
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== 预期异常（记录日志，返回错误码，不抛出异常） ====================

    /**
     * 业务异常处理
     * 场景：业务逻辑中的可预期错误（如用户不存在、数据已存在等）
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: [{}] {}", e.getErrorCode().getCode(), e.getMessage());
        return ApiResponse.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 参数校验异常处理 - @Valid 注解触发
     * 场景：Controller方法参数使用@Valid注解，校验失败时触发
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.warn("参数校验失败: {}", message);
        return ApiResponse.error(ErrorCode.VALIDATE_FAILED, message);
    }

    /**
     * 参数绑定异常处理 - @Validated 注解触发
     * 场景：Controller类使用@Validated注解，参数绑定失败时触发
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: {}", message);
        return ApiResponse.error(ErrorCode.VALIDATE_FAILED, message);
    }

    /**
     * 约束违反异常处理
     * 场景：方法参数或返回值违反约束条件时触发
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("约束违反: {}", message);
        return ApiResponse.error(ErrorCode.VALIDATE_FAILED, message);
    }

    /**
     * 认证失败异常处理
     * 场景：用户名或密码错误时触发
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleBadCredentialsException(BadCredentialsException e) {
        log.warn("认证失败: {}", e.getMessage());
        return ApiResponse.error(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
    }

    /**
     * 权限不足异常处理
     * 场景：用户没有足够权限访问资源时触发
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return ApiResponse.error(ErrorCode.FORBIDDEN);
    }

    // ==================== 非预期异常（记录日志并抛出） ====================

    /**
     * 所有未捕获的异常处理
     * 场景：系统运行时出现的技术故障（如数据库连接失败、空指针等）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, "系统繁忙，请稍后再试");
    }
}


