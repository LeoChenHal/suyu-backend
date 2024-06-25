package com.lch.suyu.config;


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author miemie
 * @since 2018-08-10
 */
@Configuration
public class MybatisPlusConfig {
        @Bean
        public MybatisPlusInterceptor mpInterceptor(){
            MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
            return interceptor;
        }
}