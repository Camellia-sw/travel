package com.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import com.example.springboot.common.PageResult;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Order;
import com.example.springboot.entity.User;
import com.example.springboot.service.OrderService;
import com.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单接口")
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping("/add")
    public Result<?> createOrder(@RequestBody Order order) {
        User currentUser = JwtTokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        order.setUserId(currentUser.getId());
        Order createdOrder = orderService.createOrder(order);
        return Result.success(createdOrder);
    }

    @Operation(summary = "支付订单")
    @PostMapping("/{id}/pay")
    public Result<?> payOrder(@PathVariable Integer id, @RequestParam String paymentMethod) {
        orderService.payOrder(id, paymentMethod);
        return Result.success("支付成功");
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{id}/cancel")
    public Result<?> cancelOrder(@PathVariable Integer id) {
        orderService.cancelOrder(id);
        return Result.success("取消成功");
    }

    @Operation(summary = "退款订单（管理员）")
    @PostMapping("/{id}/refund")
    public Result<?> refundOrder(@PathVariable Integer id) {
        orderService.refundOrder(id);
        return Result.success("退款成功");
    }

    @Operation(summary = "完成订单（管理员）")
    @PostMapping("/{id}/complete")
    public Result<?> completeOrder(@PathVariable Integer id) {
        orderService.completeOrder(id);
        return Result.success("完成成功");
    }

    @Operation(summary = "分页查询订单列表（管理员）")
    @GetMapping("/page")
    public Result<?> getPage(@RequestParam(required = false) String orderNo,
                             @RequestParam(required = false) String visitorName,
                             @RequestParam(required = false) String visitorPhone,
                             @RequestParam(required = false) Integer status,
                             @RequestParam(defaultValue = "1") Integer currentPage,
                             @RequestParam(defaultValue = "10") Integer size) {
        PageResult<Order> pageResult = orderService.getPage(orderNo, visitorName, visitorPhone, status, currentPage, size);
        return Result.success(pageResult);
    }

    @Operation(summary = "管理员查询所有订单")
    @GetMapping("/admin/page")
    public Result<?> getAdminOrderList(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String visitorName,
            @RequestParam(required = false) String visitorPhone,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<Order> pageResult = orderService.getPage(orderNo, visitorName, visitorPhone, status, currentPage, size);
        return Result.success(pageResult);
    }

    @Operation(summary = "查询当前用户订单列表")
    @GetMapping("/my")
    public Result<?> getCurrentUserOrders(@RequestParam(required = false) Integer status,
                                          @RequestParam(defaultValue = "1") Integer currentPage,
                                          @RequestParam(defaultValue = "10") Integer size) {
        PageResult<Order> pageResult = orderService.getCurrentUserOrders(status, currentPage, size);
        return Result.success(pageResult);
    }

    @Operation(summary = "根据ID获取订单")
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        return Result.success(order);
    }

    @Operation(summary = "根据订单号查询订单")
    @GetMapping("/orderNo/{orderNo}")
    public Result<?> getByOrderNo(@PathVariable String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        return Result.success(order);
    }

    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    public Result<?> deleteById(@PathVariable Integer id) {
        orderService.deleteById(id);
        return Result.success("删除成功");
    }
}