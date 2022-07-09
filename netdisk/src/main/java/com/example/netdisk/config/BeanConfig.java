package com.example.netdisk.config;

import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author monody
 * @date 2022/5/20 21:07
 */
@Configuration
public class BeanConfig {

    @Bean
    public Base64 base64(){
        return new Base64();
    }


}
