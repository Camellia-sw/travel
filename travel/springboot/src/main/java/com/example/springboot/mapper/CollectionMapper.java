package com.example.springboot.mapper;

import org.apache.ibatis.annotations.*;
import com.example.springboot.entity.Collection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CollectionMapper {

    @Select("SELECT * FROM `collection` WHERE id = #{id}")
    Collection selectById(Integer id);

    @Select("SELECT * FROM `collection` WHERE user_id = #{userId} AND guide_id = #{guideId}")
    Collection selectByUserIdAndGuideId(@Param("userId") Integer userId, @Param("guideId") Integer guideId);

    @Insert("INSERT INTO `collection` (user_id, guide_id) VALUES (#{userId}, #{guideId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Collection collection);

    @Delete("DELETE FROM `collection` WHERE user_id = #{userId} AND guide_id = #{guideId}")
    int deleteByUserIdAndGuideId(@Param("userId") Integer userId, @Param("guideId") Integer guideId);

    @Delete("DELETE FROM `collection` WHERE id = #{id}")
    int deleteById(Integer id);

    List<Collection> selectPage(@Param("username") String username,
                                @Param("guideTitle") String guideTitle,
                                @Param("offset") Integer offset,
                                @Param("size") Integer size);

    Long count(@Param("username") String username,
               @Param("guideTitle") String guideTitle);

    List<Collection> selectByUserId(@Param("userId") Integer userId,
                                    @Param("offset") Integer offset,
                                    @Param("size") Integer size);

    Long countByUserId(@Param("userId") Integer userId);
}