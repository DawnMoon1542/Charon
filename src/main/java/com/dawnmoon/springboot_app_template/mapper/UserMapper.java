package com.dawnmoon.springboot_app_template.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dawnmoon.springboot_app_template.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户 Mapper 接口
 *
 * 查询策略：
 * - 简单查询：使用注解 @Select 直接在接口中定义SQL
 * - 复杂查询：使用 XML 映射文件定义SQL（动态条件、复杂JOIN等）
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户 - 简单查询，使用注解
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND is_deleted = 0")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户 - 简单查询，使用注解
     */
    @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND is_deleted = 0")
    User selectByPhone(@Param("phone") String phone);

    /**
     * 根据邮箱查询用户 - 简单查询，使用注解
     */
    @Select("SELECT * FROM sys_user WHERE email = #{email} AND is_deleted = 0")
    User selectByEmail(@Param("email") String email);

    /**
     * 根据关键词分页查询用户列表 - 复杂查询，使用XML实现（MyBatis-Plus 分页）
     */
    IPage<User> selectUsersByKeyword(Page<User> page, @Param("keyword") String keyword);
}
