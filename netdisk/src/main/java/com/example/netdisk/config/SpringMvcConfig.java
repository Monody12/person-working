package com.example.netdisk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * @author monody
 * @date 2022/4/28 10:47 上午
 */
@Configuration
public class SpringMvcConfig {
    @Bean
    public CommonsMultipartResolver commonsMultipartResolver(){
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("UTF-8");
        // 最多上传1024MB
        multipartResolver.setMaxUploadSize(1073741824L);
        return multipartResolver;
    }

    @Bean
    public InternalResourceViewResolver internalResourceViewResolver(){
        return new InternalResourceViewResolver();
    }

}
