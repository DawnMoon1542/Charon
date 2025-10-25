package com.dawnmoon.springboot_app_template.common.exception;

import com.dawnmoon.springboot_app_template.common.enums.ErrorCode;
import lombok.Getter;

/**
 * 业务异常
 * 用于业务逻辑中的可预期异常，不记录堆栈跟踪
 * 
 * @author DawnMoon
 * @since 2025-10-22
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

    /**
     * 构造业务异常（使用错误码）
     * 
     * @param errorCode 错误码
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.message = errorCode.getDescription();
    }

    /**
     * 构造业务异常（使用错误码和自定义消息）
     * 
     * @param errorCode 错误码
     * @param message 自定义错误消息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    /**
     * 构造业务异常（仅使用自定义消息）
     * 
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = ErrorCode.BUSINESS_ERROR;
        this.message = message;
    }
}


