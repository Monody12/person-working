package com.example.user;

import com.example.redis.bean.RedisConfigBean;
import com.example.redis.template.RedisTemplateUtil;
import com.example.user.mail.SendEmail;
import com.example.user.service.LoginUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.TimeUnit;

/**
 * @author monody
 * @date 2022/4/23 1:00 下午
 */
@SpringBootTest
public class UserApplicationTests {

    @Autowired
    private SendEmail sendEmail;


    @Test
    public void test1(){
        sendEmail.sendVerificationCode("614908309@qq.com","用户注册","测试用户",946412);
    }

    @Autowired
    private LoginUserService loginUserService;

    @Test
    public void test2(){
        loginUserService.getUserPassword("asas");
    }

    @Autowired
    RedisTemplateUtil redisTemplateUtil;

    @Test
    public void test(){
        redisTemplateUtil.insertString("key","value",1, TimeUnit.MINUTES);
    }
}
