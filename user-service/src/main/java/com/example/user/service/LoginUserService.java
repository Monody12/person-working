package com.example.user.service;

/**
 * @author monody
 * @date 2022/4/24 4:39 下午
 */
public interface LoginUserService {
    String getUserPassword(String username);

    String getUserToken(String username);
}
