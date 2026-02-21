package com.example.springboot.mapper;

import com.example.springboot.entity.Accommodation;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccommodationMapper {
    @Select("SELECT * FROM `accommodation` WHERE id = #{id}")
    Accommodation selectById(Integer id);

    @Insert("INSERT INTO `accommodation` (name, type, address, scenic_id, description, contact_phone, " +
            "price_range, star_level, image_url, image_list, features, distance) " +
            "VALUES (#{name}, #{type}, #{address}, #{scenicId}, #{description}, #{contactPhone}, " +
            "#{priceRange}, #{starLevel}, #{imageUrl}, #{imageList}, #{features}, #{distance})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Accommodation accommodation);

    @Update("UPDATE `accommodation` SET name = #{name}, type = #{type}, address = #{address}, " +
            "scenic_id = #{scenicId}, description = #{description}, contact_phone = #{contactPhone}, " +
            "price_range = #{priceRange}, star_level = #{starLevel}, image_url = #{imageUrl}, " +
            "image_list = #{imageList}, features = #{features}, distance = #{distance} WHERE id = #{id}")
    int update(Accommodation accommodation);

    @Delete("DELETE FROM `accommodation` WHERE id = #{id}")
    int deleteById(Integer id);

    List<Accommodation> selectPage(@Param("name") String name,
                                   @Param("scenicId") Integer scenicId,
                                   @Param("type") String type,
                                   @Param("offset") Integer offset,
                                   @Param("size") Integer size);

    Long count(@Param("name") String name,
               @Param("scenicId") Integer scenicId,
               @Param("type") String type);

    @Select("SELECT DISTINCT type FROM `accommodation` WHERE type IS NOT NULL AND type != ''")
    List<String> selectTypes();
}