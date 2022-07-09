package com.example.user;

import com.example.feign.client.user.InfoClient;
import com.example.feign.client.user.LoginClient;
import com.example.feign.client.user.RegisterClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author monody
 * @date 2022/4/23 1:17 下午
 */
//@EnableFeignClients(clients = {InfoClient.class, LoginClient.class, RegisterClient.class})
@EnableFeignClients(clients = {LoginClient.class})
@SpringBootApplication(scanBasePackages = {"com.example.user","com.example.redis"})
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
    }
}
