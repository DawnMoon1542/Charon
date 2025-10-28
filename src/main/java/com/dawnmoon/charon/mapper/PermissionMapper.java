package com.dawnmoon.charon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dawnmoon.charon.model.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限 Mapper 接口
 * <p>
 * 查询策略：
 * - 简单查询：使用注解 @Select 直接在接口中定义SQL
 * - 复杂查询：使用 XML 映射文件定义SQL（动态条件、复杂JOIN等）
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据权限编码查询权限 - 简单查询，使用注解
     */
    @Select("SELECT * FROM sys_permission WHERE permission_code = #{permissionCode} AND is_deleted = 0")
    Permission selectByPermissionCode(@Param("permissionCode") String permissionCode);

    /**
     * 根据关键词分页查询权限列表 - 复杂查询，使用XML实现（支持模糊搜索）
     */
    IPage<Permission> selectPermissionsByKeyword(Page<Permission> page, @Param("keyword") String keyword);

    /**
     * 根据角色ID查询权限列表 - 复杂查询，使用XML实现（多表JOIN）
     */
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询权限列表 - 复杂查询，使用XML实现（多表JOIN）
     */
    List<Permission> selectPermissionsByUserId(@Param("userId") Long userId);
}

