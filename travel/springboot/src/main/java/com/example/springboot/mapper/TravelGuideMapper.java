package com.example.springboot.mapper;

import org.apache.ibatis.annotations.*;
import com.example.springboot.entity.TravelGuide;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TravelGuideMapper {

    @Select("SELECT * FROM `travel_guide` WHERE id = #{id}")
    TravelGuide selectById(Integer id);

    @Insert("INSERT INTO `travel_guide` (title, content, user_id, cover_image, views) " +
            "VALUES (#{title}, #{content}, #{userId}, #{coverImage}, #{views})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TravelGuide guide);

    @Update("UPDATE `travel_guide` SET title = #{title}, content = #{content}, " +
            "cover_image = #{coverImage} WHERE id = #{id}")
    int update(TravelGuide guide);

    @Delete("DELETE FROM `travel_guide` WHERE id = #{id}")
    int deleteById(Integer id);

    @Update("UPDATE `travel_guide` SET views = views + 1 WHERE id = #{id}")
    int addView(Integer id);

    List<TravelGuide> selectPage(@Param("title") String title,
                                 @Param("userId") Integer userId,
                                 @Param("offset") Integer offset,
                                 @Param("size") Integer size);

    Long count(@Param("title") String title,
               @Param("userId") Integer userId);

    List<TravelGuide> selectHotGuides(@Param("limit") Integer limit);

    List<TravelGuide> selectSuggestions(@Param("keyword") String keyword,
                                        @Param("limit") Integer limit);
}