package com.dawnmoon.charon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dawnmoon.charon.model.entity.RolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色权限关联 Mapper 接口
 * <p>
 * 查询策略：
 * - 简单查询：使用注解 @Select 直接在接口中定义SQL
 * - 删除操作：使用注解 @Delete
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 根据角色ID和权限ID查询关联 - 简单查询，使用注解
     */
    @Select("SELECT * FROM sys_role_permission WHERE role_id = #{roleId} AND permission_id = #{permissionId} AND is_deleted = 0")
    RolePermission selectByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * 根据角色ID查询所有权限关联 - 简单查询，使用注解
     */
    @Select("SELECT * FROM sys_role_permission WHERE role_id = #{roleId} AND is_deleted = 0")
    List<RolePermission> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限ID查询所有角色关联 - 简单查询，使用注解
     */
    @Select("SELECT * FROM sys_role_permission WHERE permission_id = #{permissionId} AND is_deleted = 0")
    List<RolePermission> selectByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 根据角色ID删除所有权限关联（逻辑删除） - 简单删除，使用注解
     * 注意：MyBatis-Plus 会自动处理逻辑删除
     */
    @Delete("UPDATE sys_role_permission SET is_deleted = 1 WHERE role_id = #{roleId} AND is_deleted = 0")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限ID删除所有角色关联（逻辑删除） - 简单删除，使用注解
     * 注意：MyBatis-Plus 会自动处理逻辑删除
     */
    @Delete("UPDATE sys_role_permission SET is_deleted = 1 WHERE permission_id = #{permissionId} AND is_deleted = 0")
    int deleteByPermissionId(@Param("permissionId") Long permissionId);
}

