package com.example.springboot.mapper;

import org.apache.ibatis.annotations.*;
import com.example.springboot.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    @Select("SELECT * FROM `order` WHERE id = #{id}")
    Order selectById(Integer id);

    @Select("SELECT * FROM `order` WHERE order_no = #{orderNo}")
    Order selectByOrderNo(String orderNo);

    @Insert("INSERT INTO `order` (order_no, user_id, ticket_id, quantity, visitor_name, visitor_phone, id_card, visit_date, total_amount, status) " +
            "VALUES (#{orderNo}, #{userId}, #{ticketId}, #{quantity}, #{visitorName}, #{visitorPhone}, #{idCard}, #{visitDate}, #{totalAmount}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Update("UPDATE `order` SET status = #{status}, payment_time = #{paymentTime}, payment_method = #{paymentMethod} WHERE id = #{id}")
    int updateStatus(Order order);

    @Update("UPDATE `order` SET status = #{status} WHERE id = #{id}")
    int updateStatusOnly(@Param("id") Integer id, @Param("status") Integer status);

    @Delete("DELETE FROM `order` WHERE id = #{id}")
    int deleteById(Integer id);

    List<Order> selectPage(@Param("orderNo") String orderNo,
                           @Param("visitorName") String visitorName,
                           @Param("visitorPhone") String visitorPhone,
                           @Param("status") Integer status,
                           @Param("offset") Integer offset,
                           @Param("size") Integer size);

    Long count(@Param("orderNo") String orderNo,
               @Param("visitorName") String visitorName,
               @Param("visitorPhone") String visitorPhone,
               @Param("status") Integer status);

    List<Order> selectByUserId(@Param("userId") Integer userId,
                               @Param("status") Integer status,
                               @Param("offset") Integer offset,
                               @Param("size") Integer size);

    Long countByUserId(@Param("userId") Integer userId,
                       @Param("status") Integer status);
}