package com.dawnmoon.springboot_app_template.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dawnmoon.springboot_app_template.model.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色 Mapper 接口
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}


