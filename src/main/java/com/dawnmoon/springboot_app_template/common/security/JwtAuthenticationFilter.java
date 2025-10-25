package com.dawnmoon.springboot_app_template.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * JWT 认证过滤器
 * 从请求头中获取 Token，从 Redis 中查找用户信息，设置到 SecurityContext
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.security.token.prefix:token:}")
    private String tokenPrefix;

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX_BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        // 1. 从请求头获取 Token
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            try {
                // 2. 从 Redis 获取用户信息
                String redisKey = tokenPrefix + token;
                UserPrincipal userPrincipal = (UserPrincipal) redisTemplate.opsForValue().get(redisKey);

                if (userPrincipal != null) {
                    // 3. 创建认证对象
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userPrincipal,
                            null,
                            userPrincipal.getAuthorities()
                        );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 4. 设置到 SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // 5. 刷新 Token 过期时间（滑动过期）
                    Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
                    if (ttl != null && ttl > 0 && ttl < 3600) { // 如果剩余时间少于1小时，刷新过期时间
                        redisTemplate.expire(redisKey, 24, TimeUnit.HOURS);
                    }

                    log.debug("用户 [{}] 认证成功", userPrincipal.getUsername());
                }
            } catch (Exception e) {
                log.error("认证失败: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX_BEARER)) {
            return bearerToken.substring(TOKEN_PREFIX_BEARER.length());
        }
        return null;
    }
}


