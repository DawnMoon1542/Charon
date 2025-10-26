package com.dawnmoon.charon.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码（BCrypt加密）
     */
    @TableField("password")
    private String password;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 手机号（需要手动加解密）
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 账号状态：0-启用，1-禁用
     */
    @TableField("status")
    private Integer status;

    /**
     * 用户角色列表（不存储在数据库中，通过关联查询获取）
     */
    @TableField(exist = false)
    private List<String> roles;

    /**
     * 获取账号是否启用（用于 Spring Security）
     * @return true-启用，false-禁用
     */
    public Boolean getEnabled() {
        return status != null && status == 0;
    }
}


