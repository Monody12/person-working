package com.example.netdisk;

import com.example.feign.client.user.InfoClient;
import com.example.feign.client.user.LoginClient;
import com.example.feign.client.user.RegisterClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author monody
 * @date 2022/4/25 9:49 下午
 */
//@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.example.feign.client.user")
@SpringBootApplication
@ImportResource(value = {"classpath:spring-rabbitmq-producer.xml"})
public class NetdiskApplication {
    public static void main(String[] args) {
        SpringApplication.run(NetdiskApplication.class, args);
    }
}
