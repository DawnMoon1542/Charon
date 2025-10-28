package com.dawnmoon.charon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dawnmoon.charon.model.entity.UserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色关联 Mapper 接口
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户ID查询角色名称列表
     */
    @Select("SELECT r.role_name FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.is_deleted = 0 AND ur.is_deleted = 0")
    List<String> selectRoleNamesByUserId(Long userId);

    /**
     * 根据用户ID查询所有角色关联
     */
    @Select("SELECT * FROM sys_user_role WHERE user_id = #{userId} AND is_deleted = 0")
    List<UserRole> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询所有用户关联
     */
    @Select("SELECT * FROM sys_user_role WHERE role_id = #{roleId} AND is_deleted = 0")
    List<UserRole> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID和角色ID查询关联
     */
    @Select("SELECT * FROM sys_user_role WHERE user_id = #{userId} AND role_id = #{roleId} AND is_deleted = 0")
    UserRole selectByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 根据用户ID删除所有角色关联（逻辑删除）
     */
    @Delete("UPDATE sys_user_role SET is_deleted = 1 WHERE user_id = #{userId} AND is_deleted = 0")
    int deleteByUserId(@Param("userId") Long userId);
}


