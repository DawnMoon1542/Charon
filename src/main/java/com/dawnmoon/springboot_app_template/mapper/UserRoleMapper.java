package com.dawnmoon.springboot_app_template.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dawnmoon.springboot_app_template.model.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
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
}


