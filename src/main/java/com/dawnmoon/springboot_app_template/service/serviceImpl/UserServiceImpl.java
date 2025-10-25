package com.dawnmoon.springboot_app_template.service.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dawnmoon.springboot_app_template.mapper.UserMapper;
import com.dawnmoon.springboot_app_template.mapper.UserRoleMapper;
import com.dawnmoon.springboot_app_template.model.entity.User;
import com.dawnmoon.springboot_app_template.model.response.PageResponse;
import com.dawnmoon.springboot_app_template.service.UserService;
import com.dawnmoon.springboot_app_template.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public User getByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (user != null) {
            // 加载用户角色
            user.setRoles(userRoleMapper.selectRoleNamesByUserId(user.getId()));
        }
        return user;
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            // 加载用户角色
            user.setRoles(userRoleMapper.selectRoleNamesByUserId(id));
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(User user) {
        userMapper.insert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(User user) {
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public PageResponse<User> list(Integer pageNum, Integer pageSize, String keyword) {
        // 使用 MyBatis-Plus 分页插件（自动进行 count 与 limit）
        IPage<User> page = userMapper.selectUsersByKeyword(new Page<>(pageNum, pageSize), keyword);
        return PageUtil.toPageResponse(page);
    }
}
