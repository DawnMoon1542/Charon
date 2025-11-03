package com.dawnmoon.charon.service.serviceImpl;

import com.dawnmoon.charon.common.enums.ErrorCode;
import com.dawnmoon.charon.common.exception.BusinessException;
import com.dawnmoon.charon.common.security.UserPrincipal;
import com.dawnmoon.charon.mapper.PermissionMapper;
import com.dawnmoon.charon.model.entity.Permission;
import com.dawnmoon.charon.model.entity.User;
import com.dawnmoon.charon.model.request.AuthRequests;
import com.dawnmoon.charon.service.AuthService;
import com.dawnmoon.charon.service.UserService;
import com.dawnmoon.charon.util.CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserService userService;
    private final PermissionMapper permissionMapper;

    @Value("${app.security.token.prefix:token:}")
    private String tokenKeyPrefix;

    @Value("${app.security.token.ttl:86400}")
    private long tokenTtlSeconds;

    private static final String USER_ID_TO_TOKEN_KEY_PREFIX = "user_id_to_token:";

    @Override
    public String login(AuthRequests.LoginRequest request) {
        // 1. 验证用户名
        User user = userService.getByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 2. 验证密码
        if (!CryptoUtil.bcryptMatches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 3. 检查账号状态
        if (!user.getEnabled()) {
            throw new BusinessException(ErrorCode.USER_ACCOUNT_DISABLED);
        }

        // 4. 查找并踢出旧设备
        Long userId = user.getId();
        String oldToken = (String) redisTemplate.opsForValue()
            .get(USER_ID_TO_TOKEN_KEY_PREFIX + userId);

        if (StringUtils.hasText(oldToken)) {
            // 删除旧 Token，踢出先登录的设备
            redisTemplate.delete(tokenKeyPrefix + oldToken);
            log.info("用户 [{}] 在新设备登录，旧设备已被踢出", userId);
        }

        // 5. 生成新 Token
        String newToken = UUID.randomUUID().toString().replace("-", "");

        // 6. 查询用户权限（从数据库）
        List<Permission> permissionList = permissionMapper.selectPermissionsByUserId(userId);
        List<String> permissions = permissionList.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toList());

        // 7. 创建 UserPrincipal 对象
        UserPrincipal userPrincipal = new UserPrincipal(
            userId,
            user.getUsername(),
            user.getRoles(),
                permissions,
                System.currentTimeMillis()
        );

        // 8. 存储 Token → UserPrincipal 映射（设置过期时间）
        redisTemplate.opsForValue().set(
            tokenKeyPrefix + newToken,
            userPrincipal,
            tokenTtlSeconds,
            TimeUnit.SECONDS
        );

        // 9. 存储 UserId → Token 映射（设置相同的过期时间）
        redisTemplate.opsForValue().set(
            USER_ID_TO_TOKEN_KEY_PREFIX + userId,
            newToken,
            tokenTtlSeconds,
            TimeUnit.SECONDS
        );

        log.info("用户 [{}] 登录成功，拥有 {} 个权限，Token: {}...", userId, permissions.size(), newToken.substring(0, 7));
        return newToken;
    }

    @Override
    public void register(AuthRequests.RegisterRequest request) {
        // 1. 检查用户名是否已存在
        User existUser = userService.getByUsername(request.getUsername());
        if (existUser != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "用户名已存在");
        }

        // 2. 创建新用户
        User user = new User(
                request.getUsername(),
                CryptoUtil.bcryptEncode(request.getPassword()),
                request.getRealName(),
                request.getPhone(),
                request.getEmail(),
                null,
                0,
                null
        );

        // 3. 保存用户
        userService.create(user);

        log.info("用户 [{}] 注册成功", request.getUsername());
    }

    @Override
    public void logoutSelf(String token) {
        if (!StringUtils.hasText(token)) {
            log.warn("退出登录失败：Token 为空");
            return;
        }

        // 1. 获取用户信息
        String tokenKey = tokenKeyPrefix + token;
        UserPrincipal userPrincipal = (UserPrincipal) redisTemplate.opsForValue().get(tokenKey);

        if (userPrincipal == null) {
            log.warn("退出登录失败：Token 无效或已过期");
            return;
        }

        Long userId = userPrincipal.getUserId();

        // 2. 删除 Token → UserPrincipal 映射
        redisTemplate.delete(tokenKey);

        // 3. 删除 UserId → Token 映射
        redisTemplate.delete(USER_ID_TO_TOKEN_KEY_PREFIX + userId);

        log.info("用户 [{}] 手动退出登录成功", userId);
    }

    @Override
    public void forceLogoutByAdmin(Long targetUserId) {
        if (targetUserId == null) {
            log.warn("强制退出失败：用户 ID 为空");
            return;
        }

        // 1. 根据 userId 查找当前有效的 Token
        String token = (String) redisTemplate.opsForValue()
            .get(USER_ID_TO_TOKEN_KEY_PREFIX + targetUserId);

        if (!StringUtils.hasText(token)) {
            log.warn("强制退出失败：用户 [{}] 当前未登录", targetUserId);
            return;
        }

        // 2. 删除 Token → UserPrincipal 映射
        redisTemplate.delete(tokenKeyPrefix + token);

        // 3. 删除 UserId → Token 映射
        redisTemplate.delete(USER_ID_TO_TOKEN_KEY_PREFIX + targetUserId);

        log.info("管理员强制用户 [{}] 退出登录成功", targetUserId);
    }

    @Override
    public Long getLoginTime(Long userId) {
        String token = (String) redisTemplate.opsForValue()
            .get(USER_ID_TO_TOKEN_KEY_PREFIX + userId);

        if (!StringUtils.hasText(token)) {
            return null;
        }

        UserPrincipal userPrincipal = (UserPrincipal) redisTemplate.opsForValue()
            .get(tokenKeyPrefix + token);

        return userPrincipal != null ? userPrincipal.getLoginTime() : null;
    }

    @Override
    public Long getUserIdFromToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }

        String tokenKey = tokenKeyPrefix + token;
        UserPrincipal userPrincipal = (UserPrincipal) redisTemplate.opsForValue().get(tokenKey);

        return userPrincipal != null ? userPrincipal.getUserId() : null;
    }
}

