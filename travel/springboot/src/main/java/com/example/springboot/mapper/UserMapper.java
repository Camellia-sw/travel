package com.example.springboot.mapper;

import org.apache.ibatis.annotations.*;
import com.example.springboot.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    @Insert("INSERT INTO `user` (username, password, email, phone, role, sex) " +
            "VALUES (#{username}, #{password}, #{email}, #{phone}, #{role}, #{sex})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 分页查询用户列表
     */
    List<User> selectPage(@Param("username") String username,
                          @Param("role") String role,
                          @Param("offset") Integer offset,
                          @Param("size") Integer size);

    /**
     * 统计用户总数
     */
    Long count(@Param("username") String username,
               @Param("role") String role);

    /**
     * 删除用户
     */
    @Delete("DELETE FROM `user` WHERE id = #{id}")
    int deleteById(Integer id);

    /**
     * 重置用户密码
     */
    @Update("UPDATE `user` SET password = #{newPassword} WHERE id = #{id}")
    int resetPassword(@Param("id") Integer id, @Param("newPassword") String newPassword);

    /**
     * 修改用户密码
     */
    @Update("UPDATE `user` SET password = #{newPassword} WHERE id = #{id}")
    int updatePassword(@Param("id") Integer id, @Param("newPassword") String newPassword);

    /**
     * 更新用户信息
     */
    @Update("UPDATE `user` SET username = #{username}, email = #{email}, phone = #{phone}, " +
            "sex = #{sex}, role = #{role} WHERE id = #{id}")
    int update(User user);
}