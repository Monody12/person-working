package com.example.netdisk.config;

import com.example.netdisk.utils.SnowflakeIdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author monody
 * @date 2022/4/27 11:13 下午
 */
@Configuration
public class SnowFlakeIdWorkerConfig {

    @Bean
    public SnowflakeIdWorker snowFlakeIdWorker(){
        return new SnowflakeIdWorker(0L,0L);
    }

}
