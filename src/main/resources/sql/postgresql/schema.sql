-- ==========================================
-- SpringBoot 应用模板 - PostgreSQL 数据库初始化脚本
-- ==========================================

-- 创建数据库（如果不存在）
CREATE DATABASE springboot_template ENCODING 'UTF8';

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
COMMENT ON COLUMN sys_user.id IS '主键ID';
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
COMMENT ON COLUMN sys_role.id IS '主键ID';
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
COMMENT ON COLUMN sys_user_role.id IS '主键ID';
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

-- ==========================================
-- 权限表
-- ==========================================
DROP TABLE IF EXISTS sys_permission CASCADE;
CREATE TABLE sys_permission
(
    id              BIGINT       NOT NULL,
    permission_code VARCHAR(100) NOT NULL,
    permission_name VARCHAR(100) NOT NULL,
    description     VARCHAR(500),
    create_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by       BIGINT,
    update_by       BIGINT,
    is_deleted      SMALLINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_permission_code UNIQUE (permission_code)
);

COMMENT ON TABLE sys_permission IS '权限表';
COMMENT ON COLUMN sys_permission.id IS '主键ID';
COMMENT ON COLUMN sys_permission.permission_code IS '权限编码（如：USER:CREATE）';
COMMENT ON COLUMN sys_permission.permission_name IS '权限名称';
COMMENT ON COLUMN sys_permission.description IS '描述';
COMMENT ON COLUMN sys_permission.create_at IS '创建时间';
COMMENT ON COLUMN sys_permission.update_at IS '更新时间';
COMMENT ON COLUMN sys_permission.create_by IS '创建人';
COMMENT ON COLUMN sys_permission.update_by IS '更新人';
COMMENT ON COLUMN sys_permission.is_deleted IS '逻辑删除：0-未删除，1-已删除';

CREATE INDEX idx_permission_create_at ON sys_permission (create_at);
CREATE INDEX idx_permission_is_deleted ON sys_permission (is_deleted);

-- ==========================================
-- 角色权限关联表
-- ==========================================
DROP TABLE IF EXISTS sys_role_permission CASCADE;
CREATE TABLE sys_role_permission
(
    id            BIGINT    NOT NULL,
    role_id       BIGINT    NOT NULL,
    permission_id BIGINT    NOT NULL,
    create_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by     BIGINT,
    update_by     BIGINT,
    is_deleted    SMALLINT  NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_role_permission UNIQUE (role_id, permission_id)
);

COMMENT ON TABLE sys_role_permission IS '角色权限关联表';
COMMENT ON COLUMN sys_role_permission.id IS '主键ID';
COMMENT ON COLUMN sys_role_permission.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_permission.permission_id IS '权限ID';
COMMENT ON COLUMN sys_role_permission.create_at IS '创建时间';
COMMENT ON COLUMN sys_role_permission.update_at IS '更新时间';
COMMENT ON COLUMN sys_role_permission.create_by IS '创建人';
COMMENT ON COLUMN sys_role_permission.update_by IS '更新人';
COMMENT ON COLUMN sys_role_permission.is_deleted IS '逻辑删除：0-未删除，1-已删除';

CREATE INDEX idx_role_permission_role_id ON sys_role_permission (role_id);
CREATE INDEX idx_role_permission_permission_id ON sys_role_permission (permission_id);
CREATE INDEX idx_role_permission_is_deleted ON sys_role_permission (is_deleted);


