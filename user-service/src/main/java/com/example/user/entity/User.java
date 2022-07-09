package com.example.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author monody
 * @date 2022/4/24 8:07 下午
 */
@Data
public class User {
    private String username;
    private String nickname;
    @JsonIgnore
    private String password;
    private Integer level;
    private String email;
    private String image;
    private String message;
    private LocalDateTime createTime;
}
