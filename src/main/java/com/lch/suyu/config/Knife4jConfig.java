package com.lch.suyu.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SuYu接口文档")
                        .version("1.0")
                        .description("素遇项目接口文档")
                        .termsOfService("")
                        .contact(new Contact().name("刘辰浩").url("").email(""))
                );
    }
}
