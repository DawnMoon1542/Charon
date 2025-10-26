package com.dawnmoon.charon.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / OpenAPI 3 配置
 * 访问地址：http://localhost:8081/doc.html
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "SpringBoot 应用模板 API",
        description = "基于 Spring Boot 的模板项目 RESTful API 文档",
        version = "1.0.0",
        contact = @Contact(
            name = "DawnMoon",
            email = "dmoon1542@gmail.com",
            url = "https://github.com/DawnMoon1542/Charon/"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8081", description = "Local"),
        @Server(url = "https://test.example.com", description = "Test"),
        @Server(url = "https://api.example.com", description = "Prod")
    }
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    in = SecuritySchemeIn.HEADER
)
public class Knife4jConfig {
}

