package com.example.user.controller;

import com.example.redis.template.RedisTemplateUtil;
import com.example.user.entity.po.UserCheck;
import com.example.user.entity.response.BaseResponse;
import com.example.user.entity.response.BaseResponseEntity;
import com.example.user.service.LoginUserService;
import com.example.user.utils.UUIDUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author monody
 * @date 2022/4/24 12:04 上午
 */
@RestController
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Value("${userservice.token-prefix}")
    String prefix;

    @Autowired
    LoginUserService loginUserService;
    @Autowired
    RedisTemplateUtil redisTemplateUtil;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    Base64 base64;

    static final int MAX_AGE = 365 * 24 * 24 * 60;

    @PostMapping("")
    BaseResponseEntity login(String username, String password, @ApiIgnore HttpServletResponse response) throws JsonProcessingException {
        String userPassword = loginUserService.getUserPassword(username);
        if (userPassword == null) {
            return BaseResponse.userNotFound();
        }
        if (!userPassword.equals(password)) {
            return BaseResponse.passwordError();
        }
        // 为用户设置cookie
        String uuid = UUIDUtil.get();
        redisTemplateUtil.insertString(prefix + username, uuid, 14, TimeUnit.DAYS);
        UserCheck userCheck = new UserCheck(username,uuid);
        // 将用户的身份以base64编码存入cookie中
        String cookieValue = Base64.encodeBase64String(
                objectMapper.writeValueAsString(userCheck).getBytes(StandardCharsets.UTF_8));
        Cookie cookie = new Cookie("check",cookieValue);
        cookie.setMaxAge(MAX_AGE);
        response.addCookie(cookie);
//        Map<String, Object> map1 = new HashMap<>(1);
//        map1.put("token", uuid);
        return BaseResponse.success(cookieValue);
    }

    @GetMapping("/cookie")
    BaseResponseEntity cookieTest(@ApiIgnore HttpServletRequest request) {
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

    @PostMapping("/verify")
    BaseResponseEntity verifyToken(String username, String token) {
//        String key = prefix + username;
//        String userToken = redisTemplateUtil.getString(key);
//        if (userToken==null||!userToken.equals(token)) {
//            return BaseResponse.fail("登录已过期");
//        }
//        // 登录成功，为key续期
//        redisTemplateUtil.expire(key,14,TimeUnit.DAYS);
        return BaseResponse.success("验证通过");
    }

}
