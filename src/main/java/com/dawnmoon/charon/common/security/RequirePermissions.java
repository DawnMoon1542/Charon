package com.dawnmoon.charon.common.security;

import java.lang.annotation.*;

/**
 * RequirePermission 注解的容器注解
 * 用于支持 @RequirePermission 的重复使用
 *
 * <p>该注解由Java编译器自动处理，无需手动使用</p>
 * <p>多个 @RequirePermission 注解之间是 AND 关系</p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermissions {

    /**
     * @return 权限注解数组
     * @RequirePermission 注解数组
     */
    RequirePermission[] value();
}

