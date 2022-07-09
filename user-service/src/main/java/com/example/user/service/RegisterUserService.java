package com.example.user.service;



/**
 * @author monody
 * @date 2022/4/23 6:23 下午
 */
public interface RegisterUserService {

    void insertUser(String username,String nickname,String password,String mail);

    /**
     * 查询用户名是否存在 （需要加共享锁-读锁）
     * 查user_extra表
     * @param username
     * @return
     */
    boolean usernameExist(String username);

    /**
     * 查询邮箱是否存在 （需要加共享锁-读锁）
     * @param mail
     * @return
     */
    boolean mailExist(String mail);





}
