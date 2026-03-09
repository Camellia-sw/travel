package com.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Order;
import com.example.springboot.service.OrderService;
import com.example.springboot.service.PayService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付控制器
 */
@Tag(name = "支付接口")
@RestController
@RequestMapping("/pay")
public class PayController {
    @Resource
    private PayService payService;

    @Resource
    private OrderService orderService;

    @Operation(summary = "支付宝支付")
    @PostMapping("/alipay/{orderId}")
    public Result<?> aliPay(@PathVariable Integer orderId) {
        try {
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            if (order.getStatus() != 0) {
                return Result.error("订单状态不正确，无法支付");
            }
            Map<String, String> result = payService.aliPay(order);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("支付失败：" + e.getMessage());
        }
    }

    @Operation(summary = "微信支付")
    @PostMapping("/wechat/{orderId}")
    public Result<?> weChatPay(@PathVariable Integer orderId) {
        try {
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            if (order.getStatus() != 0) {
                return Result.error("订单状态不正确，无法支付");
            }
            Map<String, String> result = payService.weChatPay(order);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("支付失败：" + e.getMessage());
        }
    }

    @Operation(summary = "支付宝回调")
    @PostMapping("/alipay/callback")
    public String aliPayCallback(HttpServletRequest request) {
        try {
            Map<String, String> params = request.getParameterMap().entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue()[0]
                    ));
            boolean result = payService.handleAliPayCallback(params);
            return result ? "success" : "failure";
        } catch (Exception e) {
            return "failure";
        }
    }

    @Operation(summary = "微信支付回调")
    @PostMapping("/wechat/callback")
    public String weChatPayCallback(@RequestBody String body) {
        try {
            boolean result = payService.handleWeChatPayCallback(body);
            return result ? "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>" : "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
        } catch (Exception e) {
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
        }
    }

    @Operation(summary = "关闭订单")
    @PostMapping("/close/{orderId}")
    public Result<?> closeOrder(@PathVariable Integer orderId) {
        try {
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            boolean result = payService.closeOrder(order.getOrderNo(), order.getPaymentMethod());
            return result ? Result.success("关闭订单成功") : Result.error("关闭订单失败");
        } catch (Exception e) {
            return Result.error("关闭订单失败：" + e.getMessage());
        }
    }

    @Operation(summary = "退款")
    @PostMapping("/refund/{orderId}")
    public Result<?> refund(@PathVariable Integer orderId, @RequestParam double refundAmount) {
        try {
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            if (order.getStatus() != 1) {
                return Result.error("订单状态不正确，无法退款");
            }
            boolean result = payService.refund(order.getOrderNo(), refundAmount, order.getPaymentMethod());
            if (result) {
                orderService.refundOrder(orderId);
                return Result.success("退款成功");
            } else {
                return Result.error("退款失败");
            }
        } catch (Exception e) {
            return Result.error("退款失败：" + e.getMessage());
        }
    }
}