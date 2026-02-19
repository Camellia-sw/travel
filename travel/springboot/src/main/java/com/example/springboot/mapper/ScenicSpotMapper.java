package com.example.springboot.mapper;

import org.apache.ibatis.annotations.*;
import com.example.springboot.entity.ScenicSpot;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 景点数据访问接口
 */
@Mapper
public interface ScenicSpotMapper {
    @Select("SELECT * FROM `scenic_spot` WHERE id = #{id}")
    ScenicSpot selectById(Integer id);

    @Insert("INSERT INTO `scenic_spot` (name, description, location, category_id, price, opening_hours, image_url, image_list) " +
            "VALUES (#{name}, #{description}, #{location}, #{categoryId}, #{price}, #{openingHours}, #{imageUrl}, #{imageList})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ScenicSpot scenicSpot);

    @Update("UPDATE `scenic_spot` SET name = #{name}, description = #{description}, " +
            "category_id = #{categoryId}, price = #{price}, opening_hours = #{openingHours}, " +
            "image_url = #{imageUrl}, image_list = #{imageList} " +
            "WHERE id = #{id}")
    int update(ScenicSpot scenicSpot);

    @Delete("DELETE FROM `scenic_spot` WHERE id = #{id}")
    int deleteById(Integer id);

    List<ScenicSpot> selectPage(@Param("name") String name,
                                @Param("location") String location,
                                @Param("categoryId") Integer categoryId,
                                @Param("offset") Integer offset,
                                @Param("size") Integer size);

    Long count(@Param("name") String name,
               @Param("location") String location,
               @Param("categoryId") Integer categoryId);

    @Select("SELECT * FROM `scenic_spot` WHERE category_id = #{categoryId}")
    List<ScenicSpot> selectByCategoryId(Integer categoryId);

    @Select("SELECT * FROM `scenic_spot`")
    List<ScenicSpot> selectAll();
}