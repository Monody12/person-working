package com.example.user.mapper;

import com.example.user.entity.User;
import com.example.user.entity.UserBase;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author monody
 * @date 2022/4/24 4:54 下午
 */
@Mapper
public interface UserBaseMapper {
    int insert(String username,String nickname,String password);

    UserBase query(String username);

    int update(UserBase userBase);

    int delete(String username);

    int countUsername(String username);

    User queryFull(String username);


}
