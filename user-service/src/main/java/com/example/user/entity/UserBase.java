package com.example.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 用户基本信息表
 * @author monody
 * @date 2022/4/23 6:20 下午
 */
@Data
public class UserBase {
    private String username;
    private String nickname;
    @JsonIgnore
    private String password;
    private Integer level;
}
