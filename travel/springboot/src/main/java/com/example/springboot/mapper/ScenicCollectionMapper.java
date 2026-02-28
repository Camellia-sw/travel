package com.example.springboot.mapper;

import org.apache.ibatis.annotations.*;
import com.example.springboot.entity.ScenicCollection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScenicCollectionMapper {
    @Select("SELECT * FROM `scenic_collection` WHERE id = #{id}")
    ScenicCollection selectById(Integer id);

    @Select("SELECT * FROM `scenic_collection` WHERE user_id = #{userId} AND scenic_id = #{scenicId}")
    ScenicCollection selectByUserAndScenic(@Param("userId") Integer userId, @Param("scenicId") Integer scenicId);

    @Select("SELECT * FROM `scenic_collection` WHERE user_id = #{userId}")
    List<ScenicCollection> selectByUserId(Integer userId);

    @Insert("INSERT INTO `scenic_collection` (user_id, scenic_id) VALUES (#{userId}, #{scenicId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ScenicCollection scenicCollection);

    @Delete("DELETE FROM `scenic_collection` WHERE id = #{id}")
    int deleteById(Integer id);

    @Delete("DELETE FROM `scenic_collection` WHERE user_id = #{userId} AND scenic_id = #{scenicId}")
    int deleteByUserAndScenic(@Param("userId") Integer userId, @Param("scenicId") Integer scenicId);

    List<ScenicCollection> selectPage(@Param("userId") Integer userId,
                                      @Param("scenicId") Integer scenicId,
                                      @Param("offset") Integer offset,
                                      @Param("size") Integer size);

    Long count(@Param("userId") Integer userId, @Param("scenicId") Integer scenicId);

    @Select({"<script>",
            "SELECT scenic_id FROM `scenic_collection` WHERE user_id = #{userId} AND scenic_id IN ",
            "<foreach item='item' collection='scenicIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    List<Integer> selectCollectedScenicIds(@Param("userId") Integer userId, @Param("scenicIds") List<Integer> scenicIds);
}