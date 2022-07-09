package com.example.netdisk.controller;

import com.example.feign.client.user.LoginClient;
import com.example.feign.client.user.RegisterClient;
import com.example.feign.pojo.response.BaseResponse;
import com.example.feign.pojo.response.BaseResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * @author monody
 * @date 2022/5/20 21:19
 */
@RestController
@Slf4j
@RequestMapping("/userservice/register")
public class RegisterController {
    @Autowired
    RegisterClient registerClient;

    @PostMapping("/send-mail")
    public BaseResponseEntity sendMail(String username, String mail) {
        return registerClient.sendMail(mail, username);
    }

    @PostMapping("/verify")
    BaseResponseEntity verify(String username, String nickname, String mail, String password, String code) {
        return registerClient.verify(username, password, nickname, code, mail);
    }


}
