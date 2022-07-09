package com.example.netdisk.interceptor;

import com.example.feign.client.user.LoginClient;
import com.example.feign.pojo.response.BaseResponseEntity;
import com.example.netdisk.entity.po.UserCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author monody
 * @date 2022/4/30 9:19 下午
 */
@Slf4j
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private final Base64 base64;
    private final ObjectMapper objectMapper;
    private final LoginClient loginClient;

    public LoginInterceptor(Base64 base64, ObjectMapper objectMapper, LoginClient loginClient) {
        this.base64 = base64;
        this.objectMapper = objectMapper;
        this.loginClient = loginClient;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("request:{} ; response:{} ; handler:{}", request, response, handler);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("check".equals(cookie.getName())) {
                    byte[] decode = base64.decode(cookie.getValue().getBytes(StandardCharsets.UTF_8));
                    UserCheck userCheck = objectMapper.readValue(new String(decode), UserCheck.class);
                    BaseResponseEntity baseResponseEntity = loginClient.verifyToken(userCheck.getUsername(), userCheck.getUuid());
                    if (baseResponseEntity.getCode() == 200) {
                        return true;
                    }
                    log.debug("baseResponseEntity: {}",baseResponseEntity);
                    break;
                }
            }
        }
        log.info("{} 请求被拦截",request.getRequestURL());
        request.getRequestDispatcher("/nologin").forward(request,response);
        return false;
    }
}
