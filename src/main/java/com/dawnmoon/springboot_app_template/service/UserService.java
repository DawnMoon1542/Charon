package com.dawnmoon.springboot_app_template.service;

import com.dawnmoon.springboot_app_template.model.entity.User;
import com.dawnmoon.springboot_app_template.model.response.PageResponse;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    /**
     * 根据用户ID查询用户
     */
    User getById(Long id);

    /**
     * 创建用户
     */
    void create(User user);

    /**
     * 更新用户
     */
    void update(User user);

    /**
     * 删除用户（逻辑删除）
     */
    void delete(Long id);

    /**
     * 分页查询用户列表
     */
    PageResponse<User> list(Integer pageNum, Integer pageSize, String keyword);
}



