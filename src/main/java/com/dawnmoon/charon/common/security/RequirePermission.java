package com.dawnmoon.charon.common.security;

import java.lang.annotation.*;

/**
 * 权限检查注解
 * 用于方法或类级别的权限控制
 * 支持重复使用，多个注解之间是AND关系
 *
 * <p>使用示例：</p>
 * <pre>
 * // 单个权限
 * {@literal @}RequirePermission("USER:CREATE")
 * public void createUser() { }
 *
 * // 多个权限（OR逻辑）
 * {@literal @}RequirePermission(value = {"USER:UPDATE", "USER:CREATE"}, logical = LogicType.OR)
 * public void updateUser() { }
 *
 * // 多个权限（AND逻辑）
 * {@literal @}RequirePermission(value = {"USER:DELETE", "SYSTEM:CONFIG"}, logical = LogicType.AND)
 * public void deleteUser() { }
 *
 * // 重复注解（多个注解之间是AND关系）
 * {@literal @}RequirePermission("USER:VIEW")
 * {@literal @}RequirePermission(value = {"USER:CREATE", "USER:UPDATE"}, logical = LogicType.OR)
 * public void complexPermission() { }
 * // 上述示例要求：USER:VIEW AND (USER:CREATE OR USER:UPDATE)
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(RequirePermissions.class)
public @interface RequirePermission {

    /**
     * 所需权限列表
     * 权限编码格式：模块:操作，如 USER:CREATE、ROLE:UPDATE
     *
     * @return 权限编码数组
     */
    String[] value();

    /**
     * 权限逻辑：默认OR（满足任一权限即可）
     *
     * @return 权限逻辑类型
     */
    LogicType logical() default LogicType.AND;
}

