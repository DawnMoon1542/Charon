package com.dawnmoon.charon.service;

import com.dawnmoon.charon.model.entity.Role;
import com.dawnmoon.charon.model.response.PageResponse;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 创建角色
     *
     * @param role 角色信息
     */
    void createRole(Role role);

    /**
     * 更新角色
     *
     * @param id   角色ID
     * @param role 角色信息
     */
    void updateRole(Long id, Role role);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void deleteRole(Long id);

    /**
     * 根据ID获取角色
     *
     * @param id 角色ID
     * @return 角色信息
     */
    Role getById(Long id);

    /**
     * 分页查询角色列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param keyword  关键词（角色名称或描述）
     * @return 分页结果
     */
    PageResponse<Role> listRoles(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 根据角色编码获取角色
     *
     * @param roleCode 角色编码
     * @return 角色信息
     */
    Role getByRoleCode(String roleCode);

    /**
     * 根据用户ID获取角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getRolesByUserId(Long userId);
}

