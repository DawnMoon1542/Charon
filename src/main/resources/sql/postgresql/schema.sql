-- ==========================================
-- SpringBoot 应用模板 - PostgreSQL 数据库初始化脚本
-- 版本: 1.0.0
-- 说明: 包含阶段一到三所需的全部数据表
-- ==========================================

-- 创建数据库（如果不存在）
-- CREATE DATABASE springboot_template ENCODING 'UTF8';

-- \c springboot_template;

-- ==========================================
-- 用户表
-- ==========================================
DROP TABLE IF EXISTS sys_user CASCADE;
CREATE TABLE sys_user (
    id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    avatar VARCHAR(255),
    status SMALLINT NOT NULL DEFAULT 0,
    create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    is_deleted SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_username UNIQUE (username)
);

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.id IS '主键ID（雪花算法）';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.password IS '密码（BCrypt加密）';
COMMENT ON COLUMN sys_user.real_name IS '真实姓名';
COMMENT ON COLUMN sys_user.phone IS '手机号';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.avatar IS '头像URL';
COMMENT ON COLUMN sys_user.status IS '账号状态：0-启用，1-禁用';
COMMENT ON COLUMN sys_user.create_at IS '创建时间';
COMMENT ON COLUMN sys_user.update_at IS '更新时间';
COMMENT ON COLUMN sys_user.create_by IS '创建人';
COMMENT ON COLUMN sys_user.update_by IS '更新人';
COMMENT ON COLUMN sys_user.is_deleted IS '逻辑删除：0-未删除，1-已删除';

CREATE INDEX idx_user_status ON sys_user(status);
CREATE INDEX idx_user_create_at ON sys_user(create_at);
CREATE INDEX idx_user_is_deleted ON sys_user(is_deleted);

-- ==========================================
-- 角色表
-- ==========================================
DROP TABLE IF EXISTS sys_role CASCADE;
CREATE TABLE sys_role (
    id BIGINT NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    status SMALLINT NOT NULL DEFAULT 0,
    create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    is_deleted SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_role_name UNIQUE (role_name)
);

COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.id IS '主键ID（雪花算法）';
COMMENT ON COLUMN sys_role.role_name IS '角色名称（如：ADMIN、USER）';
COMMENT ON COLUMN sys_role.description IS '角色描述';
COMMENT ON COLUMN sys_role.status IS '角色状态：0-启用，1-禁用';
COMMENT ON COLUMN sys_role.create_at IS '创建时间';
COMMENT ON COLUMN sys_role.update_at IS '更新时间';
COMMENT ON COLUMN sys_role.create_by IS '创建人';
COMMENT ON COLUMN sys_role.update_by IS '更新人';
COMMENT ON COLUMN sys_role.is_deleted IS '逻辑删除：0-未删除，1-已删除';

CREATE INDEX idx_role_status ON sys_role(status);
CREATE INDEX idx_role_is_deleted ON sys_role(is_deleted);

-- ==========================================
-- 用户角色关联表
-- ==========================================
DROP TABLE IF EXISTS sys_user_role CASCADE;
CREATE TABLE sys_user_role (
    id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    is_deleted SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id)
);

COMMENT ON TABLE sys_user_role IS '用户角色关联表';
COMMENT ON COLUMN sys_user_role.id IS '主键ID（雪花算法）';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';
COMMENT ON COLUMN sys_user_role.create_at IS '创建时间';
COMMENT ON COLUMN sys_user_role.update_at IS '更新时间';
COMMENT ON COLUMN sys_user_role.create_by IS '创建人';
COMMENT ON COLUMN sys_user_role.update_by IS '更新人';
COMMENT ON COLUMN sys_user_role.is_deleted IS '逻辑删除：0-未删除，1-已删除';

CREATE INDEX idx_user_role_user_id ON sys_user_role(user_id);
CREATE INDEX idx_user_role_role_id ON sys_user_role(role_id);
CREATE INDEX idx_user_role_is_deleted ON sys_user_role(is_deleted);


