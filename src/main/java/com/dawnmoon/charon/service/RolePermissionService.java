package com.dawnmoon.charon.service;

import com.dawnmoon.charon.model.entity.Role;
import com.dawnmoon.charon.model.entity.RolePermission;

import java.util.List;

/**
 * 角色权限关联服务接口
 */
public interface RolePermissionService {

    /**
     * 分配权限给角色（支持批量分配）
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);

    /**
     * 移除角色的某个权限
     *
     * @param roleId       角色ID
     * @param permissionId 权限ID
     */
    void removeRolePermission(Long roleId, Long permissionId);

    /**
     * 移除角色的所有权限
     *
     * @param roleId 角色ID
     */
    void removeAllRolePermissions(Long roleId);

    /**
     * 获取角色的所有权限关联
     *
     * @param roleId 角色ID
     * @return 角色权限关联列表
     */
    List<RolePermission> getRolePermissions(Long roleId);

    /**
     * 获取拥有某权限的所有角色
     *
     * @param permissionId 权限ID
     * @return 角色列表
     */
    List<Role> getRolesByPermissionId(Long permissionId);
}

