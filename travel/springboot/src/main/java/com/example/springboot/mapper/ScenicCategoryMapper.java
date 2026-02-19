package com.example.springboot.mapper;

import org.apache.ibatis.annotations.*;
import com.example.springboot.entity.ScenicCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 景点分类数据访问接口
 */
@Mapper
public interface ScenicCategoryMapper {
    @Select("SELECT * FROM `scenic_category` WHERE id = #{id}")
    ScenicCategory selectById(Integer id);

    @Insert("INSERT INTO `scenic_category` (name, description, icon, parent_id, sort_order) " +
            "VALUES (#{name}, #{description}, #{icon}, #{parentId}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ScenicCategory category);

    @Update("UPDATE `scenic_category` SET name = #{name}, description = #{description}, " +
            "icon = #{icon}, parent_id = #{parentId}, sort_order = #{sortOrder} WHERE id = #{id}")
    int update(ScenicCategory category);

    @Delete("DELETE FROM `scenic_category` WHERE id = #{id}")
    int deleteById(Integer id);

    List<ScenicCategory> selectPage(@Param("name") String name,
                                    @Param("offset") Integer offset,
                                    @Param("size") Integer size);

    Long count(@Param("name") String name);

    @Select("SELECT * FROM `scenic_category` ORDER BY sort_order ASC")
    List<ScenicCategory> selectAll();

    @Select("SELECT * FROM `scenic_category` WHERE parent_id = #{parentId} ORDER BY sort_order ASC")
    List<ScenicCategory> selectByParentId(Integer parentId);

    @Select("SELECT COUNT(*) FROM `scenic_category` WHERE parent_id = #{parentId}")
    Long countChildren(Integer parentId);
}