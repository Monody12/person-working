package com.example.netdisk.controller;

import com.example.feign.client.user.LoginClient;
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
@RequestMapping("/userservice/login")
public class LoginController {
    @Autowired
    LoginClient loginClient;
    @Autowired
    Base64 base64;

    static final int MAX_AGE = 365 * 24 * 24 * 60;

    @PostMapping("/")
    public BaseResponseEntity login(String username, String password, @ApiIgnore HttpServletResponse response) {
        BaseResponseEntity login = loginClient.login(username, password);
        log.debug("netdisk代理登录接口：{}",login);
        Cookie cookie = new Cookie("check",login.getMsg());
        cookie.setMaxAge(MAX_AGE);
        cookie.setPath("/");
        response.addCookie(cookie);
        return login;
    }

    @PostMapping("/verify")
    BaseResponseEntity verifyToken(String username, String token){
        return loginClient.verifyToken(username, token);
    }

    @GetMapping("/cookie")
    BaseResponseEntity cookieTest(@ApiIgnore HttpServletRequest request){
        // 测试能否获取到cookie
        Cookie[] cookies = request.getCookies();
        if (cookies==null){
            return BaseResponse.fail("未获取到cookie");
        }
        for (Cookie cookie : cookies) {
            if ("check".equals(cookie.getName())){
                log.debug("获取到UserChecker: {}",new String(base64.decode(cookie.getValue().getBytes(StandardCharsets.UTF_8))));
                continue;
            }
            log.debug("获取到cookie: {}, value: {}",cookie.getName(),cookie.getValue());
        }
        return BaseResponse.success();
    }
}
