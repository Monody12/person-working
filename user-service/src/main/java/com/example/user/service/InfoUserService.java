package com.example.user.service;

import com.example.user.entity.User;
import com.example.user.entity.UserBase;
import com.example.user.entity.UserExtra;

/**
 * @author monody
 * @date 2022/4/24 4:38 下午
 */
public interface InfoUserService {
    UserBase queryUserBase(String username);

    UserExtra queryUserExtra(String username);

    boolean updateUserBase(UserBase userBase);

    boolean updateUserExtra(UserExtra userExtra);

    UserBase getBase(String username);

    UserExtra getExtra(String username);

    User getFull(String username);

}
