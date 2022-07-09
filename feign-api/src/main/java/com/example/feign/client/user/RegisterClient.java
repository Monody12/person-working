package com.example.feign.client.user;

import com.example.feign.pojo.response.BaseResponseEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author monody
 * @date 2022/5/14 17:50
 */
//@FeignClient("RegisterClient")
@FeignClient(value = "userservice",contextId = "RegisterClient")
public interface RegisterClient {
    @PostMapping("/userservice/register/send-mail")
    BaseResponseEntity sendMail(@RequestParam("mail") String mail,@RequestParam("username") String username);

    @PostMapping("/userservice/register//verify")
    BaseResponseEntity verify(@RequestParam("username") String username,@RequestParam("password") String password,
                              @RequestParam("nickname") String nickname,@RequestParam("code") String code,@RequestParam("mail")  String mail);

}
