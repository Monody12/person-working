package com.example.netdisk.config;

import com.example.feign.client.user.LoginClient;
import com.example.netdisk.interceptor.LoginInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author monody
 * @date 2022/5/21 21:08
 */
//@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    @Autowired
    Base64 base64;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    LoginClient loginClient;

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor(base64, objectMapper, loginClient);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/**")
                // 登录注册页面及swagger静态资源
                .excludePathPatterns("/userservice/login/**", "/userservice/register/**","/doc.html", "/webjars/**", "/error", "/swagger-resources", "/swagger-resources/**")
                // css js img 资源
                .excludePathPatterns("/css/**", "/js/**", "/img/**")
                // 允许分享资源通过
                .excludePathPatterns("/share/**")
                // web 资源
                .excludePathPatterns("/nologin","/index.html","/index.jsp","/login.html","/register.html");
    }

}
