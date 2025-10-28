package com.dawnmoon.charon.common.security;

import com.dawnmoon.charon.mapper.PermissionMapper;
import com.dawnmoon.charon.mapper.RoleMapper;
import com.dawnmoon.charon.mapper.UserRoleMapper;
import com.dawnmoon.charon.model.entity.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 权限检查服务
 * 负责检查用户是否拥有特定权限
 * 权限信息从Redis中的UserPrincipal获取，不再单独缓存
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionCheckService {

    /**
     * 用户ID到Token的映射key前缀
     */
    private static final String USER_ID_TO_TOKEN_KEY_PREFIX = "user_id_to_token:";
    private final PermissionMapper permissionMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${app.security.token.prefix:token:}")
    private String tokenKeyPrefix;

    /**
     * 检查用户是否拥有权限
     *
     * @param userId              用户ID
     * @param requiredPermissions 所需权限列表
     * @param logical             权限逻辑（AND/OR）
     * @return true-有权限，false-无权限
     */
    public boolean checkPermission(Long userId, String[] requiredPermissions, LogicType logical) {
        // 1. 从缓存获取用户权限列表
        List<String> userPermissions = getUserPermissions(userId);

        // 2. 判断逻辑
        if (logical == LogicType.AND) {
            // 必须拥有所有权限
            boolean hasAll = Arrays.stream(requiredPermissions)
                    .allMatch(userPermissions::contains);
            log.debug("权限检查(AND): userId={}, required={}, userPermissions={}, result={}",
                    userId, Arrays.toString(requiredPermissions), userPermissions, hasAll);
            return hasAll;
        } else {
            // 拥有任一权限即可
            boolean hasAny = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
            log.debug("权限检查(OR): userId={}, required={}, userPermissions={}, result={}",
                    userId, Arrays.toString(requiredPermissions), userPermissions, hasAny);
            return hasAny;
        }
    }

    /**
     * 获取用户的所有权限
     * 从Redis中的UserPrincipal获取，如果不存在则从数据库查询
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    public List<String> getUserPermissions(Long userId) {
        // 1. 根据userId查找Token
        String token = (String) redisTemplate.opsForValue().get(USER_ID_TO_TOKEN_KEY_PREFIX + userId);

        if (token != null) {
            // 2. 根据Token获取UserPrincipal
            UserPrincipal userPrincipal = (UserPrincipal) redisTemplate.opsForValue()
                    .get(tokenKeyPrefix + token);

            if (userPrincipal != null && userPrincipal.getPermissions() != null) {
                log.debug("从Redis UserPrincipal获取用户权限: userId={}, permissions={}",
                        userId, userPrincipal.getPermissions());
                return userPrincipal.getPermissions();
            }
        }

        // 3. 如果Redis中没有，从数据库查询（兜底方案）
        log.warn("Redis中不存在用户信息，从数据库查询权限: userId={}", userId);
        List<Permission> permissionList = permissionMapper.selectPermissionsByUserId(userId);
        List<String> permissions = permissionList.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toList());

        log.debug("从数据库获取用户权限: userId={}, permissions={}", userId, permissions);
        return permissions;
    }

    /**
     * 更新Redis中用户的角色和权限信息
     * 当用户的角色或权限发生变化时调用
     *
     * @param userId 用户ID
     */
    public void updateUserRolesAndPermissions(Long userId) {
        // 1. 根据userId查找Token
        String token = (String) redisTemplate.opsForValue().get(USER_ID_TO_TOKEN_KEY_PREFIX + userId);

        if (token == null) {
            log.warn("用户未登录，无需更新Redis缓存: userId={}", userId);
            return;
        }

        // 2. 获取当前的UserPrincipal
        String tokenKey = tokenKeyPrefix + token;
        UserPrincipal userPrincipal = (UserPrincipal) redisTemplate.opsForValue().get(tokenKey);

        if (userPrincipal == null) {
            log.warn("Redis中不存在用户信息: userId={}, token={}", userId, token);
            return;
        }

        // 3. 从数据库查询最新的角色和权限
        List<Long> roleIds = userRoleMapper.selectByUserId(userId).stream()
                .map(ur -> ur.getRoleId())
                .collect(Collectors.toList());

        List<String> newRoles = roleIds.isEmpty() ? new ArrayList<>() :
                roleMapper.selectBatchIds(roleIds).stream()
                        .map(role -> role.getRoleName())
                        .collect(Collectors.toList());

        List<String> newPermissions = permissionMapper.selectPermissionsByUserId(userId).stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toList());

        // 4. 更新UserPrincipal中的角色和权限
        userPrincipal.setRoles(newRoles);
        userPrincipal.setPermissions(newPermissions);

        // 5. 获取剩余过期时间
        Long ttl = redisTemplate.getExpire(tokenKey, TimeUnit.SECONDS);
        if (ttl == null || ttl <= 0) {
            ttl = 86400L; // 默认1天
        }

        // 6. 重新存储到Redis（保持原有的过期时间）
        redisTemplate.opsForValue().set(tokenKey, userPrincipal, ttl, TimeUnit.SECONDS);

        log.info("更新Redis中用户的角色和权限: userId={}, roles={}, permissions={}, ttl={}s",
                userId, newRoles, newPermissions, ttl);
    }

    /**
     * 更新角色下所有用户的角色和权限信息
     * 当角色的权限发生变化时调用
     *
     * @param roleId 角色ID
     */
    public void updateRoleUsersPermissions(Long roleId) {
        // 查询该角色的所有用户ID
        List<Long> userIds = userRoleMapper.selectByRoleId(roleId).stream()
                .map(ur -> ur.getUserId())
                .collect(Collectors.toList());

        // 更新所有用户的角色和权限
        for (Long userId : userIds) {
            updateUserRolesAndPermissions(userId);
        }

        log.info("更新角色下所有用户的权限: roleId={}, userCount={}", roleId, userIds.size());
    }
}

