package com.example.feign.client.user;

import com.example.feign.pojo.response.BaseResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author monody
 * @date 2022/5/14 17:41
 */
//@FeignClient("InfoClient")
@FeignClient(value = "userservice",contextId = "InfoClient")
public interface InfoClient {
    @PutMapping("/userservice/modify")
    public BaseResponseEntity modify(@RequestParam String username,@RequestParam String image,@RequestParam String message,@RequestParam String nickname);

    @PutMapping("/userservice/modify/mail")
    public BaseResponseEntity modify(@RequestParam String username,@RequestParam String mail,@RequestParam int code);

    @PutMapping("/userservice/modify/password")
    public BaseResponseEntity modify(@RequestParam String username,@RequestParam String oldPassword,@RequestParam String newPassword);

    @GetMapping("/userservice/get/base")
    public BaseResponseEntity getBase(@RequestParam String username) throws JsonProcessingException;

    @GetMapping("/userservice/get/extra")
    public BaseResponseEntity getExtra(@RequestParam String username) throws JsonProcessingException;

    @GetMapping("/userservice/get/full")
    public BaseResponseEntity getFull(@RequestParam String username) throws JsonProcessingException;


}
