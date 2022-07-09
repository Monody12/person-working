package com.example.user.controller;

import com.example.redis.template.RedisTemplateUtil;
import com.example.user.entity.User;
import com.example.user.entity.response.BaseResponse;
import com.example.user.entity.response.BaseResponseEntity;
import com.example.user.mail.SendEmail;
import com.example.user.service.InfoUserService;
import com.example.user.service.RegisterUserService;
import com.example.user.utils.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author monody
 * @date 2022/4/23 11:58 下午
 */
@Slf4j
@RestController
@RequestMapping("/register")
public class RegisterController {

    @Value("${mail.verification-code.timeout}")
    int timeout;

    @Autowired
    SendEmail sendEmail;
    @Autowired
    RedisTemplateUtil redisTemplateUtil;
    @Autowired
    RegisterUserService registerUserService;
    @Autowired
    InfoUserService infoUserService;

    @PostMapping("/send-mail")
    @Transactional
    BaseResponseEntity sendMail(String mail, String username) {
        if (registerUserService.mailExist(mail))
            return BaseResponse.fail("该邮箱已被注册");
        // 生成随机数
        int code = RandomGenerator.codeGenerate();
        // 发送邮件
        String error = sendEmail.sendVerificationCode(mail, "用户注册", username, code);
        if (error!=null)
            return BaseResponse.fail(error);
        // 记录信息
        redisTemplateUtil.insertString("user-register-" + mail, String.valueOf(code), timeout, TimeUnit.MINUTES);
        return BaseResponse.success("邮件发送成功，请注意接收");
    }

    @PostMapping("/verify")
    @Transactional
    BaseResponseEntity verify(String username, String password, String nickname, String code, String mail) {
        // 打印日志
        log.debug("注册验证服务，用户提交的信息： username: {}, password: {}, nickname: {}, code: {}, mail: {}", username, password, nickname, code, mail);
        if (registerUserService.usernameExist(username))
            return BaseResponse.fail("该用户名已被注册");
        else if (registerUserService.mailExist(mail))
            return BaseResponse.fail("该邮箱已被注册");
        // 获取该用户的验证码  由于邮件功能异常，所以暂时不用验证验证码就能够注册
        String realCode = redisTemplateUtil.getString("user-register-" + mail);
        log.debug("从redis中获取的验证码：{}", realCode);
        if (realCode == null)
            return BaseResponse.fail("验证码已过期");
        else if (!realCode.equals(code))
            return BaseResponse.fail("验证码不正确");
        registerUserService.insertUser(username, nickname, password, mail);
        return BaseResponse.success(username + " 注册成功");
    }

}
