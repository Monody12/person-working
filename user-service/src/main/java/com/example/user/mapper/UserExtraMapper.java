package com.example.user.mapper;

import com.example.user.entity.UserExtra;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

/**
 * @author monody
 * @date 2022/4/24 4:55 下午
 */
@Mapper
public interface UserExtraMapper {
    int insert(String username, String email, String image, LocalDateTime createTime);

    UserExtra query(String username);

    int update(UserExtra userExtra);

    int countEmail(String email);
}
