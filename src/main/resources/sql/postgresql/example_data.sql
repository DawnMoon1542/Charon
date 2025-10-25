-- ==========================================
-- SpringBoot 应用模板 - PostgreSQL 测试数据脚本
-- 版本: 1.0.0
-- 说明: 包含测试用户、角色和关联数据
-- ==========================================

-- \c springboot_template;

-- ==========================================
-- 角色数据
-- ==========================================
INSERT INTO sys_role (id, role_name, description, status, create_at, update_at, create_by, update_by, is_deleted) VALUES
(1, 'ADMIN', '系统管理员', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, 0),
(2, 'USER', '普通用户', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, 0),
(3, 'GUEST', '访客', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, 0);

-- ==========================================
-- 用户数据
-- ==========================================
-- 密码说明：
-- admin / admin123 -> BCrypt 加密
-- user / user123 -> BCrypt 加密
-- guest / guest123 -> BCrypt 加密
-- disabled / disabled123 -> BCrypt 加密

INSERT INTO sys_user (id, username, password, real_name, phone, email, avatar, status, create_at, update_at, create_by, update_by, is_deleted) VALUES
-- 管理员账号：admin / admin123
(1001, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8MYdLn2z/6N8uPHVqK', '系统管理员', '13800138001', 'admin@example.com', NULL, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, 0),
-- 普通用户：user / user123
(1002, 'user', '$2a$10$5SLBGXipPSM6t9LKsFiMXe7TGKkAqgWb0fTmBs8FPVZxXqVcQ/1i2', '测试用户', '13800138002', 'user@example.com', NULL, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, 0),
-- 访客账号：guest / guest123
(1003, 'guest', '$2a$10$8TLDHZqRQTN7u0MLmTnJYOBpJpLBrZvYj1UoDnClPcZyYrWeR/2yS', '访客用户', '13800138003', 'guest@example.com', NULL, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, 0),
-- 禁用用户：disabled / disabled123
(1004, 'disabled', '$2a$10$9UMEIArSSUO8v1NMnUoKZPCqKqMCsAwZk2VpEoDmQdAzZsXfS/3zS', '禁用用户', '13800138004', 'disabled@example.com', NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, 0);

-- ==========================================
-- 用户角色关联数据
-- ==========================================
INSERT INTO sys_user_role (id, user_id, role_id, create_at, update_at, create_by, update_by, is_deleted) VALUES
-- admin 用户拥有 ADMIN 和 USER 角色
(2001, 1001, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, 0),
(2002, 1001, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, 0),
-- user 用户拥有 USER 角色
(2003, 1002, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, 0),
-- guest 用户拥有 GUEST 角色
(2004, 1003, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, 0),
-- disabled 用户拥有 USER 角色
(2005, 1004, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, 0);

-- ==========================================
-- 测试账号说明
-- ==========================================
-- 1. 管理员账号
--    用户名：admin
--    密码：admin123
--    角色：ADMIN, USER
--    状态：启用
--
-- 2. 普通用户
--    用户名：user
--    密码：user123
--    角色：USER
--    状态：启用
--
-- 3. 访客账号
--    用户名：guest
--    密码：guest123
--    角色：GUEST
--    状态：启用
--
-- 4. 禁用用户
--    用户名：disabled
--    密码：disabled123
--    角色：USER
--    状态：禁用（无法登录）
-- ==========================================


