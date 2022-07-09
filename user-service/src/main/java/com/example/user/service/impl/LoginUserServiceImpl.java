package com.example.user.service.impl;

import com.example.redis.template.RedisTemplateUtil;
import com.example.user.entity.UserBase;
import com.example.user.mapper.UserBaseMapper;
import com.example.user.mapper.UserExtraMapper;
import com.example.user.service.LoginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author monody
 * @date 2022/4/24 4:53 下午
 */
@Service
public class LoginUserServiceImpl implements LoginUserService {
    @Autowired
    UserBaseMapper baseMapper;
    @Autowired
    UserExtraMapper extraMapper;

    @Autowired
    RedisTemplateUtil redisTemplateUtil;
    @Value("${userservice.token-prefix}")
    String prefix;

    @Override
    public String getUserPassword(String username) {
        UserBase user = baseMapper.query(username);
        return user != null ? user.getPassword() : null;
    }

    @Override
    public String getUserToken(String username) {
        return redisTemplateUtil.getString(prefix + username);
    }
}
