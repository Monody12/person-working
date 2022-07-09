package com.example.user.config;

import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author monody
 * @date 2022/5/20 20:42
 */
@Configuration
public class BeanConfig {

    @Bean
    public Base64 base64(){
        return new Base64();
    }
}
