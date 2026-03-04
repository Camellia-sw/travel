package com.example.springboot.mapper;

import com.example.springboot.entity.AccommodationReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccommodationReviewMapper {
    /**
     * 插入住宿评价
     * @param review 住宿评价对象
     * @return 影响行数
     */
    int insert(AccommodationReview review);

    /**
     * 根据ID删除住宿评价
     * @param id 评价ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 根据ID更新住宿评价
     * @param review 住宿评价对象
     * @return 影响行数
     */
    int update(AccommodationReview review);

    /**
     * 根据ID获取住宿评价
     * @param id 评价ID
     * @return 住宿评价对象
     */
    AccommodationReview selectById(@Param("id") Integer id);

    /**
     * 分页查询住宿评价
     * @param accommodationId 住宿ID
     * @param userId 用户ID
     * @param offset 偏移量
     * @param size 每页大小
     * @return 住宿评价列表
     */
    List<AccommodationReview> selectPage(@Param("accommodationId") Integer accommodationId, @Param("userId") Integer userId, @Param("offset") int offset, @Param("size") int size);

    /**
     * 统计住宿评价数量
     * @param accommodationId 住宿ID
     * @param userId 用户ID
     * @return 评价数量
     */
    Long count(@Param("accommodationId") Integer accommodationId, @Param("userId") Integer userId);
}