package com.dawnmoon.springboot_app_template.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.dawnmoon.springboot_app_template.common.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 配置类
 */
@Configuration
@MapperScan("com.dawnmoon.springboot_app_template.mapper")
public class MybatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器配置：启用分页插件
     * 不显式设置 DbType，按数据源自动识别（支持 MySQL / PostgreSQL）
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    /**
     * 自动填充处理器
     */
    @Slf4j
    @Component
    public static class MyMetaObjectHandler implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject) {
            // 填充创建时间
            this.strictInsertFill(metaObject, "createAt", LocalDateTime.class, LocalDateTime.now());

            // 填充更新时间
            this.strictInsertFill(metaObject, "updateAt", LocalDateTime.class, LocalDateTime.now());

            // 使用当前登录用户填充创建人和更新人
            Long currentUserId = SecurityUtil.getCurrentUserId();
            if (currentUserId != null) {
                this.strictInsertFill(metaObject, "createBy", Long.class, currentUserId);
                this.strictInsertFill(metaObject, "updateBy", Long.class, currentUserId);
            }
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            // 填充更新时间
            this.strictUpdateFill(metaObject, "updateAt", LocalDateTime.class, LocalDateTime.now());

            // 填充更新人
            Long currentUserId = SecurityUtil.getCurrentUserId();
            if (currentUserId != null) {
                this.strictUpdateFill(metaObject, "updateBy", Long.class, currentUserId);
            }
        }
    }
}


