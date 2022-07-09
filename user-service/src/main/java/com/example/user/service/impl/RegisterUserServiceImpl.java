package com.example.user.service.impl;

import com.example.user.mapper.UserBaseMapper;
import com.example.user.mapper.UserExtraMapper;
import com.example.user.service.RegisterUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author monody
 * @date 2022/4/24 4:53 下午
 */
@Service
public class RegisterUserServiceImpl implements RegisterUserService {
    @Autowired
    UserBaseMapper baseMapper;
    @Autowired
    UserExtraMapper extraMapper;
    @Value("${userservice.default-user-image}")
    String defaultImage;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUser(String username, String nickname, String password, String mail) {
        baseMapper.insert(username, nickname, password);
        extraMapper.insert(username, mail, defaultImage, LocalDateTime.now());
    }

    @Override
    public boolean usernameExist(String username) {
        return baseMapper.countUsername(username) >= 1;
    }

    @Override
    public boolean mailExist(String mail) {
        return extraMapper.countEmail(mail) >= 1;
    }
}
