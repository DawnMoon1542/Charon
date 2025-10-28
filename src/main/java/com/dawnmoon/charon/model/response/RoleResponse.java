package com.dawnmoon.charon.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色响应对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "角色响应对象")
public class RoleResponse {

    @Schema(description = "角色ID", example = "1")
    private Long id;

    @Schema(description = "角色名称", example = "ADMIN")
    private String roleName;

    @Schema(description = "角色描述", example = "管理员角色")
    private String description;

    @Schema(description = "角色状态：0-启用，1-禁用", example = "0")
    private Integer status;

    @Schema(description = "创建时间", example = "2025-10-27 23:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;

    @Schema(description = "更新时间", example = "2025-10-27 23:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

    @Schema(description = "权限列表（可选）")
    private List<PermissionResponse> permissions;
}

