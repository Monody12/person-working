package com.example.netdisk.controller;

import com.example.feign.client.user.InfoClient;
import com.example.feign.pojo.response.BaseResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author monody
 * @date 2022/6/10 18:25
 */
@RestController
@Slf4j
@RequestMapping("/userinfo")
public class UserInfoController {
    @Autowired
    InfoClient infoClient;

    @RequestMapping(value = "/get/full",method = {RequestMethod.GET})
    public BaseResponseEntity getFull(@RequestParam String username) throws JsonProcessingException {
        return infoClient.getFull(username);
    }
}
