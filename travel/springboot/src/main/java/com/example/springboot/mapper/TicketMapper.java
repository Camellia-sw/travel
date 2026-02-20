package com.example.springboot.mapper;

import org.apache.ibatis.annotations.*;
import com.example.springboot.entity.Ticket;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 门票数据访问接口
 */
@Mapper
public interface TicketMapper {
    @Select("SELECT * FROM `ticket` WHERE id = #{id}")
    Ticket selectById(Integer id);

    @Insert("INSERT INTO `ticket` (scenic_id, ticket_name, price, discount_price, ticket_type, valid_period, description, stock, status) " +
            "VALUES (#{scenicId}, #{ticketName}, #{price}, #{discountPrice}, #{ticketType}, #{validPeriod}, #{description}, #{stock}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Ticket ticket);

    @Update("UPDATE `ticket` SET scenic_id = #{scenicId}, ticket_name = #{ticketName}, price = #{price}, " +
            "discount_price = #{discountPrice}, ticket_type = #{ticketType}, valid_period = #{validPeriod}, " +
            "description = #{description}, stock = #{stock}, status = #{status} WHERE id = #{id}")
    int update(Ticket ticket);

    @Delete("DELETE FROM `ticket` WHERE id = #{id}")
    int deleteById(Integer id);

    List<Ticket> selectPage(@Param("ticketName") String ticketName,
                            @Param("ticketType") String ticketType,
                            @Param("scenicId") Integer scenicId,
                            @Param("offset") Integer offset,
                            @Param("size") Integer size);

    Long count(@Param("ticketName") String ticketName,
               @Param("ticketType") String ticketType,
               @Param("scenicId") Integer scenicId);

    @Select("SELECT * FROM `ticket` WHERE scenic_id = #{scenicId}")
    List<Ticket> selectByScenicId(Integer scenicId);

    @Select("SELECT * FROM `ticket`")
    List<Ticket> selectAll();
}