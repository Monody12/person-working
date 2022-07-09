package com.example.user.controller;

import com.example.user.entity.User;
import com.example.user.entity.UserBase;
import com.example.user.entity.UserExtra;
import com.example.user.entity.response.BaseResponse;
import com.example.user.entity.response.BaseResponseEntity;
import com.example.user.service.InfoUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author monody
 * @date 2022/4/24 12:09 上午
 */
@RestController("/info")
public class InfoController {

    @Autowired
    InfoUserService infoUserService;
    @Autowired
    ObjectMapper objectMapper;

    @PutMapping("/modify")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponseEntity modify(String username, String image, String message, String nickname) {
        boolean flag = false;
        if (nickname != null) {
            UserBase userBase = new UserBase();
            userBase.setUsername(username);
            userBase.setNickname(nickname);
            flag = infoUserService.updateUserBase(userBase);
        }
        if (image != null || message != null) {
            UserExtra userExtra = new UserExtra();
            userExtra.setUsername(username);
            userExtra.setImage(image);
            userExtra.setMessage(message);
            boolean res = infoUserService.updateUserExtra(userExtra);
            if (!flag&&res) {
                flag = true;
            }
        }
        if (!flag) {
            BaseResponse.fail("修改失败");
        }
        return BaseResponse.success("修改成功");
    }

    @PutMapping("/modify/mail")
    public BaseResponseEntity modify(String username, String mail,int code) {
        // TODO 用户换绑邮箱，需要提交新邮箱和验证码
        return BaseResponse.success("修改成功");
    }

    @PutMapping("/modify/password")
    public BaseResponseEntity modify(String username, String oldPassword,String newPassword) {
        // TODO 根据旧密码来修改密码
        return BaseResponse.success("修改成功");
    }

    @GetMapping("/get/base")
    public BaseResponseEntity getBase(String username) throws JsonProcessingException {
        UserBase user = infoUserService.getBase(username);
        if (user == null)
            return BaseResponse.userNotFound();
        String json = objectMapper.writeValueAsString(user);
        return BaseResponse.success(json);
    }

    @GetMapping("/get/extra")
    public BaseResponseEntity getExtra(String username) throws JsonProcessingException {
        UserExtra user = infoUserService.getExtra(username);
        if (user == null)
            return BaseResponse.userNotFound();
        String json = objectMapper.writeValueAsString(user);
        return BaseResponse.success(json);
    }

    @GetMapping("/get/full")
    public BaseResponseEntity getFull(String username) throws JsonProcessingException {
        User user = infoUserService.getFull(username);
        if (user == null)
            return BaseResponse.fail("用户不存在");
        String json = objectMapper.writeValueAsString(user);
        return BaseResponse.success(json);

    }
}
