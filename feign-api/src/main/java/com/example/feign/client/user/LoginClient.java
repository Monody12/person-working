package com.example.feign.client.user;

import com.example.feign.pojo.response.BaseResponseEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author monody
 * @date 2022/5/14 17:48
 */
//@FeignClient(name = "userservice")
@FeignClient(value = "userservice",contextId = "LoginClient")
public interface LoginClient {

    @PostMapping("/userservice/login")
    BaseResponseEntity login(@RequestParam("username") String username, @RequestParam("password")  String password);

    @PostMapping("/userservice/login/verify")
    BaseResponseEntity verifyToken(@RequestParam("username")  String username,@RequestParam("token")  String token);

    @GetMapping("/userservice/login/cookie")
    BaseResponseEntity cookieTest();
}
