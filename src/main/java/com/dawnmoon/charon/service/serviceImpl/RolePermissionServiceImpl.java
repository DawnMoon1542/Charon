package com.dawnmoon.charon.service.serviceImpl;

import com.dawnmoon.charon.common.enums.ErrorCode;
import com.dawnmoon.charon.common.exception.BusinessException;
import com.dawnmoon.charon.common.security.PermissionCheckService;
import com.dawnmoon.charon.mapper.PermissionMapper;
import com.dawnmoon.charon.mapper.RoleMapper;
import com.dawnmoon.charon.mapper.RolePermissionMapper;
import com.dawnmoon.charon.model.entity.Permission;
import com.dawnmoon.charon.model.entity.Role;
import com.dawnmoon.charon.model.entity.RolePermission;
import com.dawnmoon.charon.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色权限关联服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionMapper rolePermissionMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final PermissionCheckService permissionCheckService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        // 1. 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            log.warn("角色不存在: roleId={}", roleId);
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }

        // 2. 批量分配权限
        for (Long permissionId : permissionIds) {
            // 检查权限是否存在
            Permission permission = permissionMapper.selectById(permissionId);
            if (permission == null) {
                log.warn("权限不存在: permissionId={}", permissionId);
                throw new BusinessException(ErrorCode.PERMISSION_NOT_FOUND);
            }

            // 检查是否已经分配
            RolePermission existingRolePermission = rolePermissionMapper.selectByRoleIdAndPermissionId(roleId, permissionId);
            if (existingRolePermission != null) {
                log.warn("角色已拥有该权限: roleId={}, permissionId={}", roleId, permissionId);
                throw new BusinessException(ErrorCode.ROLE_PERMISSION_ALREADY_EXISTS);
            }

            // 创建关联
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermissionMapper.insert(rolePermission);
            log.info("分配权限给角色成功: roleId={}, permissionId={}, rolePermissionId={}",
                    roleId, permissionId, rolePermission.getId());
        }

        // 3. 更新该角色下所有用户的Redis缓存
        permissionCheckService.updateRoleUsersPermissions(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRolePermission(Long roleId, Long permissionId) {
        // 1. 查询关联是否存在
        RolePermission rolePermission = rolePermissionMapper.selectByRoleIdAndPermissionId(roleId, permissionId);
        if (rolePermission == null) {
            log.warn("角色权限关联不存在: roleId={}, permissionId={}", roleId, permissionId);
            throw new BusinessException(ErrorCode.ROLE_PERMISSION_NOT_FOUND);
        }

        // 2. 删除关联（逻辑删除）
        rolePermissionMapper.deleteById(rolePermission.getId());
        log.info("移除角色权限成功: roleId={}, permissionId={}", roleId, permissionId);

        // 3. 更新该角色下所有用户的Redis缓存
        permissionCheckService.updateRoleUsersPermissions(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAllRolePermissions(Long roleId) {
        // 1. 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            log.warn("角色不存在: roleId={}", roleId);
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }

        // 2. 删除角色的所有权限关联（逻辑删除）
        int count = rolePermissionMapper.deleteByRoleId(roleId);
        log.info("移除角色所有权限成功: roleId={}, count={}", roleId, count);

        // 3. 更新该角色下所有用户的Redis缓存
        permissionCheckService.updateRoleUsersPermissions(roleId);
    }

    @Override
    public List<RolePermission> getRolePermissions(Long roleId) {
        return rolePermissionMapper.selectByRoleId(roleId);
    }

    @Override
    public List<Role> getRolesByPermissionId(Long permissionId) {
        // 1. 查询该权限的所有角色关联
        List<RolePermission> rolePermissions = rolePermissionMapper.selectByPermissionId(permissionId);

        // 2. 查询角色信息
        List<Role> roles = new ArrayList<>();
        for (RolePermission rolePermission : rolePermissions) {
            Role role = roleMapper.selectById(rolePermission.getRoleId());
            if (role != null) {
                roles.add(role);
            }
        }

        return roles;
    }
}

