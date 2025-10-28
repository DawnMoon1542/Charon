package com.dawnmoon.charon.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 权限响应对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "权限响应对象")
public class PermissionResponse {

    @Schema(description = "权限ID", example = "1")
    private Long id;

    @Schema(description = "权限编码", example = "USER:CREATE")
    private String permissionCode;

    @Schema(description = "权限名称", example = "创建用户")
    private String permissionName;

    @Schema(description = "权限描述", example = "创建新用户的权限")
    private String description;

    @Schema(description = "创建时间", example = "2025-10-27 23:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;

    @Schema(description = "更新时间", example = "2025-10-27 23:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;
}

