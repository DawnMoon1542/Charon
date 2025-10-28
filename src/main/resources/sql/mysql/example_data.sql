-- ==========================================
-- SpringBoot 应用模板 - MySQL 示例数据脚本
-- ==========================================

USE springboot_template;

-- ==========================================
-- 角色数据
-- ==========================================
-- 注意：这里使用固定 ID，生产环境应使用雪花算法生成
INSERT INTO `sys_role` (`id`, `role_name`, `description`, `status`, `create_at`, `update_at`, `create_by`, `update_by`, `is_deleted`) VALUES (1,
                                                                                                                                              'SUPER_ADMIN',
                                                                                                                                              '超级管理员 - 拥有所有权限',
                                                                                                                                              0,
                                                                                                                                              NOW(),
                                                                                                                                              NOW(),
                                                                                                                                              NULL,
                                                                                                                                              NULL,
                                                                                                                                              0),
                                                                                                                                             (2,
                                                                                                                                              'ADMIN',
                                                                                                                                              '管理员 - 拥有除系统配置外的所有权限',
                                                                                                                                              0,
                                                                                                                                              NOW(),
                                                                                                                                              NOW(),
                                                                                                                                              NULL,
                                                                                                                                              NULL,
                                                                                                                                              0),
                                                                                                                                             (3,
                                                                                                                                              'USER',
                                                                                                                                              '普通用户 - 仅拥有查看权限',
                                                                                                                                              0,
                                                                                                                                              NOW(),
                                                                                                                                              NOW(),
                                                                                                                                              NULL,
                                                                                                                                              NULL,
                                                                                                                                              0);

-- ==========================================
-- 用户数据
-- ==========================================
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `avatar`, `status`, `create_at`, `update_at`, `create_by`, `update_by`, `is_deleted`) VALUES
-- 超级管理员账号：superadmin / qut@123456
(1001, 'superadmin', '$2a$10$xyCRP6s8ZOIUBwHzE67.UOUZKw0.NbFsT3gpPdYj.vk1fuGrqdNGa', '超级管理员', '13800138001',
 'superadmin@example.com', NULL, 0, NOW(), NOW(), NULL, NULL, 0),
-- 管理员账号：admin / qut@123456
(1002, 'admin', '$2a$10$xyCRP6s8ZOIUBwHzE67.UOUZKw0.NbFsT3gpPdYj.vk1fuGrqdNGa', '管理员', '13800138002',
 'admin@example.com', NULL, 0, NOW(), NOW(), NULL, NULL, 0),
-- 普通用户：user / qut@123456
(1003, 'user', '$2a$10$xyCRP6s8ZOIUBwHzE67.UOUZKw0.NbFsT3gpPdYj.vk1fuGrqdNGa', '普通用户', '13800138003',
 'user@example.com', NULL, 0, NOW(), NOW(), NULL, NULL, 0);

-- ==========================================
-- 用户角色关联数据
-- ==========================================
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `create_at`, `update_at`, `create_by`, `update_by`, `is_deleted`) VALUES
-- superadmin 用户拥有 SUPER_ADMIN 角色
(2001, 1001, 1, NOW(), NOW(), NULL, NULL, 0),
-- admin 用户拥有 ADMIN 角色
(2002, 1002, 2, NOW(), NOW(), NULL, NULL, 0),
-- user 用户拥有 USER 角色
(2003, 1003, 3, NOW(), NOW(), NULL, NULL, 0);

-- ==========================================
-- 权限数据
-- ==========================================
INSERT INTO `sys_permission` (`id`, `permission_code`, `permission_name`, `description`, `create_at`, `update_at`,
                              `create_by`, `update_by`, `is_deleted`)
VALUES
-- 用户管理权限（5个）
(3001, 'USER:VIEW', '查看用户', '查看用户列表和详情', NOW(), NOW(), NULL, NULL, 0),
(3002, 'USER:CREATE', '创建用户', '创建新用户', NOW(), NOW(), NULL, NULL, 0),
(3003, 'USER:UPDATE', '更新用户', '编辑用户信息', NOW(), NOW(), NULL, NULL, 0),
(3004, 'USER:DELETE', '删除用户', '删除用户（逻辑删除）', NOW(), NOW(), NULL, NULL, 0),
(3005, 'USER:EXPORT', '导出用户', '导出用户数据', NOW(), NOW(), NULL, NULL, 0),

-- 角色管理权限（4个）
(3006, 'ROLE:VIEW', '查看角色', '查看角色列表和详情', NOW(), NOW(), NULL, NULL, 0),
(3007, 'ROLE:CREATE', '创建角色', '创建新角色', NOW(), NOW(), NULL, NULL, 0),
(3008, 'ROLE:UPDATE', '更新角色', '编辑角色信息和分配权限', NOW(), NOW(), NULL, NULL, 0),
(3009, 'ROLE:DELETE', '删除角色', '删除角色', NOW(), NOW(), NULL, NULL, 0),

-- 权限管理权限（4个）
(3010, 'PERMISSION:VIEW', '查看权限', '查看权限列表和详情', NOW(), NOW(), NULL, NULL, 0),
(3011, 'PERMISSION:CREATE', '创建权限', '创建新权限', NOW(), NOW(), NULL, NULL, 0),
(3012, 'PERMISSION:UPDATE', '更新权限', '编辑权限信息', NOW(), NOW(), NULL, NULL, 0),
(3013, 'PERMISSION:DELETE', '删除权限', '删除权限', NOW(), NOW(), NULL, NULL, 0),

-- 系统管理权限（4个）
(3014, 'SYSTEM:CONFIG', '系统配置', '修改系统配置', NOW(), NOW(), NULL, NULL, 0),
(3015, 'SYSTEM:LOG', '系统日志', '查看系统日志', NOW(), NOW(), NULL, NULL, 0),
(3016, 'SYSTEM:MONITOR', '系统监控', '查看系统监控信息', NOW(), NOW(), NULL, NULL, 0),
(3017, 'SYSTEM:BACKUP', '系统备份', '备份系统数据', NOW(), NOW(), NULL, NULL, 0);

-- ==========================================
-- 角色权限关联数据
-- ==========================================
-- SUPER_ADMIN 角色拥有所有权限（17个）
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_at`, `update_at`, `create_by`, `update_by`,
                                   `is_deleted`)
VALUES (4001, 1, 3001, NOW(), NOW(), NULL, NULL, 0), -- USER:VIEW
       (4002, 1, 3002, NOW(), NOW(), NULL, NULL, 0), -- USER:CREATE
       (4003, 1, 3003, NOW(), NOW(), NULL, NULL, 0), -- USER:UPDATE
       (4004, 1, 3004, NOW(), NOW(), NULL, NULL, 0), -- USER:DELETE
       (4005, 1, 3005, NOW(), NOW(), NULL, NULL, 0), -- USER:EXPORT
       (4006, 1, 3006, NOW(), NOW(), NULL, NULL, 0), -- ROLE:VIEW
       (4007, 1, 3007, NOW(), NOW(), NULL, NULL, 0), -- ROLE:CREATE
       (4008, 1, 3008, NOW(), NOW(), NULL, NULL, 0), -- ROLE:UPDATE
       (4009, 1, 3009, NOW(), NOW(), NULL, NULL, 0), -- ROLE:DELETE
       (4010, 1, 3010, NOW(), NOW(), NULL, NULL, 0), -- PERMISSION:VIEW
       (4011, 1, 3011, NOW(), NOW(), NULL, NULL, 0), -- PERMISSION:CREATE
       (4012, 1, 3012, NOW(), NOW(), NULL, NULL, 0), -- PERMISSION:UPDATE
       (4013, 1, 3013, NOW(), NOW(), NULL, NULL, 0), -- PERMISSION:DELETE
       (4014, 1, 3014, NOW(), NOW(), NULL, NULL, 0), -- SYSTEM:CONFIG
       (4015, 1, 3015, NOW(), NOW(), NULL, NULL, 0), -- SYSTEM:LOG
       (4016, 1, 3016, NOW(), NOW(), NULL, NULL, 0), -- SYSTEM:MONITOR
       (4017, 1, 3017, NOW(), NOW(), NULL, NULL, 0);
-- SYSTEM:BACKUP

-- ADMIN 角色拥有除 SYSTEM:CONFIG 外的所有权限（16个）
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_at`, `update_at`, `create_by`, `update_by`,
                                   `is_deleted`)
VALUES (4018, 2, 3001, NOW(), NOW(), NULL, NULL, 0), -- USER:VIEW
       (4019, 2, 3002, NOW(), NOW(), NULL, NULL, 0), -- USER:CREATE
       (4020, 2, 3003, NOW(), NOW(), NULL, NULL, 0), -- USER:UPDATE
       (4021, 2, 3004, NOW(), NOW(), NULL, NULL, 0), -- USER:DELETE
       (4022, 2, 3005, NOW(), NOW(), NULL, NULL, 0), -- USER:EXPORT
       (4023, 2, 3006, NOW(), NOW(), NULL, NULL, 0), -- ROLE:VIEW
       (4024, 2, 3007, NOW(), NOW(), NULL, NULL, 0), -- ROLE:CREATE
       (4025, 2, 3008, NOW(), NOW(), NULL, NULL, 0), -- ROLE:UPDATE
       (4026, 2, 3009, NOW(), NOW(), NULL, NULL, 0), -- ROLE:DELETE
       (4027, 2, 3010, NOW(), NOW(), NULL, NULL, 0), -- PERMISSION:VIEW
       (4028, 2, 3011, NOW(), NOW(), NULL, NULL, 0), -- PERMISSION:CREATE
       (4029, 2, 3012, NOW(), NOW(), NULL, NULL, 0), -- PERMISSION:UPDATE
       (4030, 2, 3013, NOW(), NOW(), NULL, NULL, 0), -- PERMISSION:DELETE
-- 注意：ADMIN 没有 SYSTEM:CONFIG 权限
       (4031, 2, 3015, NOW(), NOW(), NULL, NULL, 0), -- SYSTEM:LOG
       (4032, 2, 3016, NOW(), NOW(), NULL, NULL, 0), -- SYSTEM:MONITOR
       (4033, 2, 3017, NOW(), NOW(), NULL, NULL, 0);
-- SYSTEM:BACKUP

-- USER 角色仅拥有查看权限（6个）
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_at`, `update_at`, `create_by`, `update_by`,
                                   `is_deleted`)
VALUES (4034, 3, 3001, NOW(), NOW(), NULL, NULL, 0), -- USER:VIEW
       (4035, 3, 3005, NOW(), NOW(), NULL, NULL, 0), -- USER:EXPORT
       (4036, 3, 3006, NOW(), NOW(), NULL, NULL, 0), -- ROLE:VIEW
       (4037, 3, 3010, NOW(), NOW(), NULL, NULL, 0), -- PERMISSION:VIEW
       (4038, 3, 3015, NOW(), NOW(), NULL, NULL, 0), -- SYSTEM:LOG
       (4039, 3, 3016, NOW(), NOW(), NULL, NULL, 0); -- SYSTEM:MONITOR
