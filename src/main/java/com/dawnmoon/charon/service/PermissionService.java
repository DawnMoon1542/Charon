package com.dawnmoon.charon.service;

import com.dawnmoon.charon.model.entity.Permission;
import com.dawnmoon.charon.model.response.PageResponse;

import java.util.List;

/**
 * 权限服务接口
 */
public interface PermissionService {

    /**
     * 创建权限
     *
     * @param permission 权限信息
     */
    void createPermission(Permission permission);

    /**
     * 更新权限
     *
     * @param id         权限ID
     * @param permission 权限信息
     */
    void updatePermission(Long id, Permission permission);

    /**
     * 删除权限
     *
     * @param id 权限ID
     */
    void deletePermission(Long id);

    /**
     * 根据ID获取权限
     *
     * @param id 权限ID
     * @return 权限信息
     */
    Permission getById(Long id);

    /**
     * 分页查询权限列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param keyword  关键词（权限编码、权限名称或描述）
     * @return 分页结果
     */
    PageResponse<Permission> listPermissions(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 根据权限编码获取权限
     *
     * @param permissionCode 权限编码
     * @return 权限信息
     */
    Permission getByPermissionCode(String permissionCode);

    /**
     * 根据角色ID获取权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByRoleId(Long roleId);

    /**
     * 根据用户ID获取权限列表（用于权限检查）
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByUserId(Long userId);
}

