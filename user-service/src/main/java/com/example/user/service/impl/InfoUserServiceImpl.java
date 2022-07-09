package com.example.user.service.impl;

import com.example.user.entity.User;
import com.example.user.entity.UserBase;
import com.example.user.entity.UserExtra;
import com.example.user.mapper.UserBaseMapper;
import com.example.user.mapper.UserExtraMapper;
import com.example.user.service.InfoUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author monody
 * @date 2022/4/24 4:49 下午
 */
@Service
public class InfoUserServiceImpl implements InfoUserService {
    @Autowired
    UserBaseMapper baseMapper;
    @Autowired
    UserExtraMapper extraMapper;

    @Override
    public UserBase queryUserBase(String username) {
        return baseMapper.query(username);
    }

    @Override
    public UserExtra queryUserExtra(String username) {
        return extraMapper.query(username);
    }

    @Override
    public boolean updateUserBase(UserBase userBase) {
        return baseMapper.update(userBase) >= 1;
    }

    @Override
    public boolean updateUserExtra(UserExtra userExtra) {
        return extraMapper.update(userExtra) >= 1;
    }


    @Override
    public UserBase getBase(String username) {
        return baseMapper.query(username);
    }

    @Override
    public UserExtra getExtra(String username) {
        return extraMapper.query(username);
    }

    @Override
    public User getFull(String username) {
        return baseMapper.queryFull(username);
    }


}
