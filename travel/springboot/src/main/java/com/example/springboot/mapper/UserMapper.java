package com.example.springboot.mapper;

import org.apache.ibatis.annotations.*;
import com.example.springboot.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM `user` WHERE username = #{username}")
    User selectByUsername(String username);

    @Select("SELECT * FROM `user` WHERE email = #{email}")
    User selectByEmail(String email);

    @Select("SELECT * FROM `user` WHERE id = #{id}")
    User selectById(Integer id);

    @Insert("INSERT INTO `user` (username, password, email, phone, role, sex, avatar) " +
            "VALUES (#{username}, #{password}, #{email}, #{phone}, #{role}, #{sex}, #{avatar})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    List<User> selectPage(@Param("username") String username,
                          @Param("role") String role,
                          @Param("offset") Integer offset,
                          @Param("size") Integer size);

    Long count(@Param("username") String username,
               @Param("role") String role);

    @Delete("DELETE FROM `user` WHERE id = #{id}")
    int deleteById(Integer id);

    @Update("UPDATE `user` SET password = #{newPassword} WHERE id = #{id}")
    int resetPassword(@Param("id") Integer id, @Param("newPassword") String newPassword);

    @Update("UPDATE `user` SET password = #{newPassword} WHERE id = #{id}")
    int updatePassword(@Param("id") Integer id, @Param("newPassword") String newPassword);

    @Update("UPDATE `user` SET username = #{username}, email = #{email}, phone = #{phone}, " +
            "sex = #{sex}, role = #{role}, avatar = #{avatar} WHERE id = #{id}")
    int update(User user);

    @Update("UPDATE `user` SET avatar = #{avatar} WHERE id = #{id}")
    int updateAvatar(@Param("id") Integer id, @Param("avatar") String avatar);
}