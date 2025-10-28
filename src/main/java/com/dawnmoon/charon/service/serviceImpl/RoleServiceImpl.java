package com.dawnmoon.charon.service.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dawnmoon.charon.common.enums.ErrorCode;
import com.dawnmoon.charon.common.exception.BusinessException;
import com.dawnmoon.charon.mapper.RoleMapper;
import com.dawnmoon.charon.mapper.UserRoleMapper;
import com.dawnmoon.charon.model.entity.Role;
import com.dawnmoon.charon.model.entity.UserRole;
import com.dawnmoon.charon.model.response.PageResponse;
import com.dawnmoon.charon.service.RoleService;
import com.dawnmoon.charon.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRole(Role role) {
        // 1. 检查角色编码是否已存在
        Role existingRole = roleMapper.selectByRoleCode(role.getRoleName());
        if (existingRole != null) {
            log.warn("角色编码已存在: roleCode={}", role.getRoleName());
            throw new BusinessException(ErrorCode.ROLE_CODE_ALREADY_EXISTS);
        }

        // 2. 创建角色
        roleMapper.insert(role);
        log.info("创建角色成功: id={}, roleCode={}", role.getId(), role.getRoleName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long id, Role role) {
        // 1. 检查角色是否存在
        Role existingRole = getById(id);

        // 2. 如果修改了角色编码，检查新编码是否已存在
        if (!existingRole.getRoleName().equals(role.getRoleName())) {
            Role duplicateRole = roleMapper.selectByRoleCode(role.getRoleName());
            if (duplicateRole != null) {
                log.warn("角色编码已存在: roleCode={}", role.getRoleName());
                throw new BusinessException(ErrorCode.ROLE_CODE_ALREADY_EXISTS);
            }
        }

        // 3. 更新角色
        role.setId(id);
        roleMapper.updateById(role);
        log.info("更新角色成功: id={}, roleCode={}", id, role.getRoleName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        // 1. 检查角色是否存在
        Role role = getById(id);

        // 2. 检查角色是否正在使用
        List<UserRole> userRoles = userRoleMapper.selectByRoleId(id);
        if (!userRoles.isEmpty()) {
            log.warn("角色正在使用中，无法删除: id={}, roleCode={}, userCount={}",
                    id, role.getRoleName(), userRoles.size());
            throw new BusinessException(ErrorCode.ROLE_IN_USE);
        }

        // 3. 删除角色（逻辑删除）
        roleMapper.deleteById(id);
        log.info("删除角色成功: id={}, roleCode={}", id, role.getRoleName());
    }

    @Override
    public Role getById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            log.warn("角色不存在: id={}", id);
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
        return role;
    }

    @Override
    public PageResponse<Role> listRoles(Integer pageNum, Integer pageSize, String keyword) {
        // 使用 MyBatis-Plus 分页插件
        IPage<Role> page = roleMapper.selectRolesByKeyword(new Page<>(pageNum, pageSize), keyword);
        return PageUtil.toPageResponse(page);
    }

    @Override
    public Role getByRoleCode(String roleCode) {
        Role role = roleMapper.selectByRoleCode(roleCode);
        if (role == null) {
            log.warn("角色不存在: roleCode={}", roleCode);
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
        return role;
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }
}

