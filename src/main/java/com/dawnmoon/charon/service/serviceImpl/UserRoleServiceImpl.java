package com.dawnmoon.charon.service.serviceImpl;

import com.dawnmoon.charon.common.enums.ErrorCode;
import com.dawnmoon.charon.common.exception.BusinessException;
import com.dawnmoon.charon.common.security.PermissionCheckService;
import com.dawnmoon.charon.mapper.RoleMapper;
import com.dawnmoon.charon.mapper.UserMapper;
import com.dawnmoon.charon.mapper.UserRoleMapper;
import com.dawnmoon.charon.model.entity.Role;
import com.dawnmoon.charon.model.entity.User;
import com.dawnmoon.charon.model.entity.UserRole;
import com.dawnmoon.charon.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户角色关联服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleMapper userRoleMapper;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionCheckService permissionCheckService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        // 1. 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("用户不存在: userId={}", userId);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 批量分配角色
        for (Long roleId : roleIds) {
            // 检查角色是否存在
            Role role = roleMapper.selectById(roleId);
            if (role == null) {
                log.warn("角色不存在: roleId={}", roleId);
                throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
            }

            // 检查是否已经分配
            UserRole existingUserRole = userRoleMapper.selectByUserIdAndRoleId(userId, roleId);
            if (existingUserRole != null) {
                log.warn("用户已拥有该角色: userId={}, roleId={}", userId, roleId);
                throw new BusinessException(ErrorCode.USER_ROLE_ALREADY_EXISTS);
            }

            // 创建关联
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
            log.info("分配角色给用户成功: userId={}, roleId={}, userRoleId={}", userId, roleId, userRole.getId());
        }

        // 3. 更新Redis中用户的角色和权限
        permissionCheckService.updateUserRolesAndPermissions(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserRole(Long userId, Long roleId) {
        // 1. 查询关联是否存在
        UserRole userRole = userRoleMapper.selectByUserIdAndRoleId(userId, roleId);
        if (userRole == null) {
            log.warn("用户角色关联不存在: userId={}, roleId={}", userId, roleId);
            throw new BusinessException(ErrorCode.USER_ROLE_NOT_FOUND);
        }

        // 2. 删除关联（逻辑删除）
        userRoleMapper.deleteById(userRole.getId());
        log.info("移除用户角色成功: userId={}, roleId={}", userId, roleId);

        // 3. 更新Redis中用户的角色和权限
        permissionCheckService.updateUserRolesAndPermissions(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAllUserRoles(Long userId) {
        // 1. 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("用户不存在: userId={}", userId);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 删除用户的所有角色关联（逻辑删除）
        int count = userRoleMapper.deleteByUserId(userId);
        log.info("移除用户所有角色成功: userId={}, count={}", userId, count);

        // 3. 更新Redis中用户的角色和权限
        permissionCheckService.updateUserRolesAndPermissions(userId);
    }

    @Override
    public List<UserRole> getUserRoles(Long userId) {
        return userRoleMapper.selectByUserId(userId);
    }

    @Override
    public List<User> getUsersByRoleId(Long roleId) {
        // 1. 查询该角色的所有用户关联
        List<UserRole> userRoles = userRoleMapper.selectByRoleId(roleId);

        // 2. 查询用户信息
        List<User> users = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            User user = userMapper.selectById(userRole.getUserId());
            if (user != null) {
                users.add(user);
            }
        }

        return users;
    }
}

