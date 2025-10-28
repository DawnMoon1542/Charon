package com.dawnmoon.charon.common.security;

import com.dawnmoon.charon.common.enums.ErrorCode;
import com.dawnmoon.charon.common.exception.BusinessException;
import com.dawnmoon.charon.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 权限检查切面
 * 拦截 @RequirePermission 注解，进行权限校验
 * 支持重复注解，多个注解之间是AND关系
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionAspect {

    private final PermissionCheckService permissionCheckService;

    /**
     * 环绕通知：拦截 @RequirePermission 和 @RequirePermissions 注解的方法
     *
     * @param joinPoint 连接点
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Around("@annotation(com.dawnmoon.charon.common.security.RequirePermission) || " +
            "@annotation(com.dawnmoon.charon.common.security.RequirePermissions)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            log.warn("未登录用户尝试访问需要权限的接口: method={}",
                    joinPoint.getSignature().toShortString());
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 2. 获取方法上的所有权限注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取重复注解（如果使用@Repeatable，会自动处理为数组）
        RequirePermission[] permissions = method.getAnnotationsByType(RequirePermission.class);

        if (permissions.length == 0) {
            // 如果方法上没有注解，执行方法
            return joinPoint.proceed();
        }

        // 3. 检查所有权限注解（多个注解之间是AND关系）
        for (RequirePermission permission : permissions) {
            String[] requiredPermissions = permission.value();
            LogicType logical = permission.logical();

            // 检查单个注解的权限
            boolean hasPermission = permissionCheckService.checkPermission(userId, requiredPermissions, logical);

            if (!hasPermission) {
                log.warn("权限不足: userId={}, requiredPermissions={}, logical={}, method={}",
                        userId, Arrays.toString(requiredPermissions), logical, joinPoint.getSignature().toShortString());
                throw new BusinessException(ErrorCode.FORBIDDEN);
            }
        }

        // 4. 所有权限检查通过，执行方法
        log.debug("权限检查通过: userId={}, permissionCount={}, method={}",
                userId, permissions.length, joinPoint.getSignature().toShortString());
        return joinPoint.proceed();
    }
}

