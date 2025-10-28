package com.dawnmoon.charon.service.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dawnmoon.charon.common.enums.ErrorCode;
import com.dawnmoon.charon.common.exception.BusinessException;
import com.dawnmoon.charon.mapper.PermissionMapper;
import com.dawnmoon.charon.mapper.RolePermissionMapper;
import com.dawnmoon.charon.model.entity.Permission;
import com.dawnmoon.charon.model.entity.RolePermission;
import com.dawnmoon.charon.model.response.PageResponse;
import com.dawnmoon.charon.service.PermissionService;
import com.dawnmoon.charon.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 权限服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPermission(Permission permission) {
        // 1. 检查权限编码是否已存在
        Permission existingPermission = permissionMapper.selectByPermissionCode(permission.getPermissionCode());
        if (existingPermission != null) {
            log.warn("权限编码已存在: permissionCode={}", permission.getPermissionCode());
            throw new BusinessException(ErrorCode.PERMISSION_CODE_ALREADY_EXISTS);
        }

        // 2. 创建权限
        permissionMapper.insert(permission);
        log.info("创建权限成功: id={}, permissionCode={}", permission.getId(), permission.getPermissionCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(Long id, Permission permission) {
        // 1. 检查权限是否存在
        Permission existingPermission = getById(id);

        // 2. 如果修改了权限编码，检查新编码是否已存在
        if (!existingPermission.getPermissionCode().equals(permission.getPermissionCode())) {
            Permission duplicatePermission = permissionMapper.selectByPermissionCode(permission.getPermissionCode());
            if (duplicatePermission != null) {
                log.warn("权限编码已存在: permissionCode={}", permission.getPermissionCode());
                throw new BusinessException(ErrorCode.PERMISSION_CODE_ALREADY_EXISTS);
            }
        }

        // 3. 更新权限
        permission.setId(id);
        permissionMapper.updateById(permission);
        log.info("更新权限成功: id={}, permissionCode={}", id, permission.getPermissionCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        // 1. 检查权限是否存在
        Permission permission = getById(id);

        // 2. 检查权限是否正在使用
        List<RolePermission> rolePermissions = rolePermissionMapper.selectByPermissionId(id);
        if (!rolePermissions.isEmpty()) {
            log.warn("权限正在使用中，无法删除: id={}, permissionCode={}, roleCount={}",
                    id, permission.getPermissionCode(), rolePermissions.size());
            throw new BusinessException(ErrorCode.PERMISSION_IN_USE);
        }

        // 3. 删除权限（逻辑删除）
        permissionMapper.deleteById(id);
        log.info("删除权限成功: id={}, permissionCode={}", id, permission.getPermissionCode());
    }

    @Override
    public Permission getById(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            log.warn("权限不存在: id={}", id);
            throw new BusinessException(ErrorCode.PERMISSION_NOT_FOUND);
        }
        return permission;
    }

    @Override
    public PageResponse<Permission> listPermissions(Integer pageNum, Integer pageSize, String keyword) {
        // 使用 MyBatis-Plus 分页插件
        IPage<Permission> page = permissionMapper.selectPermissionsByKeyword(new Page<>(pageNum, pageSize), keyword);
        return PageUtil.toPageResponse(page);
    }

    @Override
    public Permission getByPermissionCode(String permissionCode) {
        Permission permission = permissionMapper.selectByPermissionCode(permissionCode);
        if (permission == null) {
            log.warn("权限不存在: permissionCode={}", permissionCode);
            throw new BusinessException(ErrorCode.PERMISSION_NOT_FOUND);
        }
        return permission;
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return permissionMapper.selectPermissionsByRoleId(roleId);
    }

    @Override
    public List<Permission> getPermissionsByUserId(Long userId) {
        return permissionMapper.selectPermissionsByUserId(userId);
    }
}

