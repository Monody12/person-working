package com.example.user.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户扩展信息表
 *
 * @author monody
 * @date 2022/4/23 6:21 下午
 */
@Data
public class UserExtra {
    private String username;
    private String email;
    private String image;
    private String message;
    private LocalDateTime createTime;
}
