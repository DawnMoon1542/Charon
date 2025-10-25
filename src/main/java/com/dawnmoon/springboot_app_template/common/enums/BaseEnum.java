package com.dawnmoon.springboot_app_template.common.enums;

/**
 * 枚举基础接口
 * @param <T> 枚举值类型
 */
public interface BaseEnum<T> {

    /**
     * 获取枚举编码
     */
    T getCode();

    /**
     * 获取枚举描述
     */
    String getDescription();
}



