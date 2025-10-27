package com.dawnmoon.charon.service;

import com.dawnmoon.charon.model.request.AuthRequests;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录（含单点登录/互踢机制）
     * @param request 登录请求
     * @return Token
     */
    String login(AuthRequests.LoginRequest request);

    /**
     * 用户注册
     * @param request 注册请求
     */
    void register(AuthRequests.RegisterRequest request);

    /**
     * 用户自己手动退出登录
     * @param token 当前用户的 Token
     */
    void logoutSelf(String token);

    /**
     * 管理员强制其他用户退出
     * @param targetUserId 要强制退出的用户 ID
     */
    void forceLogoutByAdmin(Long targetUserId);


    /**
     * 获取用户的登录时间
     * @param userId 用户 ID
     * @return 登录时间戳（毫秒）
     */
    Long getLoginTime(Long userId);
}


