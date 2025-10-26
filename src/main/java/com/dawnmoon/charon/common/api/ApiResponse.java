package com.dawnmoon.charon.common.api;

import com.dawnmoon.charon.common.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一 API 返回格式
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "统一响应体")
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "响应码", example = "SUCCESS")
    private String code;

    @Schema(description = "响应消息", example = "操作成功")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "时间戳", example = "1719876543210")
    private Long timestamp;

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(
            ErrorCode.SUCCESS.getCode(),
            ErrorCode.SUCCESS.getDescription(),
            null,
            System.currentTimeMillis()
        );
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
            ErrorCode.SUCCESS.getCode(),
            ErrorCode.SUCCESS.getDescription(),
            data,
            System.currentTimeMillis()
        );
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(
            ErrorCode.SUCCESS.getCode(),
            message,
            data,
            System.currentTimeMillis()
        );
    }

    /**
     * 失败响应（使用错误码）
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(
            errorCode.getCode(),
            errorCode.getDescription(),
            null,
            System.currentTimeMillis()
        );
    }

    /**
     * 失败响应（自定义消息）
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        return new ApiResponse<>(
            errorCode.getCode(),
            message,
            null,
            System.currentTimeMillis()
        );
    }

    /**
     * 失败响应（完全自定义）
     */
    public static <T> ApiResponse<T> error(String code, String message, T data) {
        return new ApiResponse<>(
            code,
            message,
            data,
            System.currentTimeMillis()
        );
    }
}



