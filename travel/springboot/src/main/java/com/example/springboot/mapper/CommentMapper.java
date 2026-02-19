package com.example.springboot.mapper;

import org.apache.ibatis.annotations.*;
import com.example.springboot.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Select("SELECT * FROM `comment` WHERE id = #{id}")
    Comment selectById(Integer id);

    @Insert("INSERT INTO `comment` (user_id, scenic_id, content, rating, likes) " +
            "VALUES (#{userId}, #{scenicId}, #{content}, #{rating}, #{likes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    @Update("UPDATE `comment` SET content = #{content}, rating = #{rating} WHERE id = #{id}")
    int update(Comment comment);

    @Update("UPDATE `comment` SET likes = likes + 1 WHERE id = #{id}")
    int incrementLikes(@Param("id") Integer id);

    @Update("UPDATE `comment` SET likes = likes - 1 WHERE id = #{id}")
    int decrementLikes(@Param("id") Integer id);

    @Delete("DELETE FROM `comment` WHERE id = #{id}")
    int deleteById(Integer id);

    List<Comment> selectPage(@Param("content") String content,
                             @Param("scenicId") Integer scenicId,
                             @Param("userId") Integer userId,
                             @Param("scenicName") String scenicName,
                             @Param("userName") String userName,
                             @Param("offset") Integer offset,
                             @Param("size") Integer size);

    Long count(@Param("content") String content,
               @Param("scenicId") Integer scenicId,
               @Param("userId") Integer userId,
               @Param("scenicName") String scenicName,
               @Param("userName") String userName);

    List<Comment> selectByScenicId(Integer scenicId);
}