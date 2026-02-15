package com.example.springboot.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import com.example.springboot.entity.User;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserMapper {
    @Select("SELECT * FROM `user` WHERE username = #{username}")
    User selectByUsername(String username);

    @Select("SELECT * FROM `user` WHERE email = #{email}")
    User selectByEmail(String email);

    @Select("SELECT * FROM `user` WHERE id = #{id}")
    User selectById(Integer id);

    @Insert("INSERT INTO `user` (username, password, email, phone, role, status) " +
            "VALUES (#{username}, #{password}, #{email}, #{phone}, #{role}, 1)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
}