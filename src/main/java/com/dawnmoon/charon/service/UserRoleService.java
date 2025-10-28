package com.dawnmoon.charon.service;

import com.dawnmoon.charon.model.entity.User;
import com.dawnmoon.charon.model.entity.UserRole;

import java.util.List;

/**
 * 用户角色关联服务接口
 */
public interface UserRoleService {

    /**
     * 分配角色给用户（支持批量分配）
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void assignRolesToUser(Long userId, List<Long> roleIds);

    /**
     * 移除用户的某个角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    void removeUserRole(Long userId, Long roleId);

    /**
     * 移除用户的所有角色
     *
     * @param userId 用户ID
     */
    void removeAllUserRoles(Long userId);

    /**
     * 获取用户的所有角色关联
     *
     * @param userId 用户ID
     * @return 用户角色关联列表
     */
    List<UserRole> getUserRoles(Long userId);

    /**
     * 获取拥有某角色的所有用户
     *
     * @param roleId 角色ID
     * @return 用户列表
     */
    List<User> getUsersByRoleId(Long roleId);
}

