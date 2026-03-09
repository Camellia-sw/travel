package com.example.springboot.controller;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.example.springboot.config.AliPayConfig;
import com.example.springboot.entity.Order;
import com.example.springboot.service.OrderService;
import com.example.springboot.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

//  https://natapp.cn/
// ekihat7647@sandbox.com
@Tag(name = "支付接口")
@RestController
@RequestMapping("/pay")
public class AliPayController {

    @Resource
    private AliPayConfig aliPayConfig;

    @Resource
    private OrderService orderService;

    @Operation(summary = "支付宝支付")
    @RequestMapping(value = "/alipay/{orderId}", method = {RequestMethod.GET, RequestMethod.POST})
    public void aliPay(@PathVariable Integer orderId, HttpServletResponse httpResponse) throws Exception {
        // 硬编码charset为UTF-8，确保参数完整
        String charset = "UTF-8";
        try {
            Order order = orderService.getById(orderId);
            if (order == null) {
                httpResponse.setContentType("text/html;charset=" + charset);
                httpResponse.getWriter().write("<h1>订单不存在</h1>");
                httpResponse.getWriter().flush();
                httpResponse.getWriter().close();
                return;
            }
            if (order.getStatus() != 0) {
                httpResponse.setContentType("text/html;charset=" + charset);
                httpResponse.getWriter().write("<h1>订单状态不正确，无法支付</h1>");
                httpResponse.getWriter().flush();
                httpResponse.getWriter().close();
                return;
            }

            // 1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
            System.out.println("支付宝配置信息:");
            System.out.println("GatewayUrl: " + aliPayConfig.getGatewayUrl());
            System.out.println("AppId: " + aliPayConfig.getAppId());
            System.out.println("Charset: " + charset);
            System.out.println("SignType: " + aliPayConfig.getSignType());
            System.out.println("NotifyUrl: " + aliPayConfig.getNotifyUrl());
            System.out.println("ReturnUrl: " + aliPayConfig.getReturnUrl());
            System.out.println("订单信息:");
            System.out.println("OrderNo: " + order.getOrderNo());
            System.out.println("TotalAmount: " + order.getTotalAmount().setScale(2, java.math.RoundingMode.HALF_UP).toString());
            System.out.println("TicketName: " + order.getTicketName());

            AlipayClient alipayClient = new DefaultAlipayClient(
                    aliPayConfig.getGatewayUrl(),
                    aliPayConfig.getAppId(),
                    aliPayConfig.getAppPrivateKey(),
                    "json",
                    charset,
                    aliPayConfig.getAlipayPublicKey(),
                    aliPayConfig.getSignType()
            );

            // 2. 创建 Request并设置Request参数
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 发送请求的 Request类
            request.setNotifyUrl(aliPayConfig.getNotifyUrl());
            request.setReturnUrl("http://localhost:8080/pay/orders"); // 支付完成后自动跳转到本地页面的路径

            // 构建请求参数
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", order.getOrderNo());
            bizContent.put("total_amount", order.getTotalAmount().setScale(2, java.math.RoundingMode.HALF_UP).toString());
            bizContent.put("subject", "门票订单：" + order.getTicketName());
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

            // 使用hutool的JSON工具将Map转换为JSON字符串
            String jsonContent = cn.hutool.json.JSONUtil.toJsonStr(bizContent);
            request.setBizContent(jsonContent);

            // 执行请求，拿到响应的结果，返回给浏览器
            String form = "";
            try {
                System.out.println("开始调用支付宝API...");
                form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
                System.out.println("支付宝API调用成功，表单长度: " + (form != null ? form.length() : 0));
                if (form == null || form.isEmpty()) {
                    throw new Exception("支付宝返回的表单为空");
                }
                // 打印表单的前100个字符，以便调试
                if (form.length() > 100) {
                    System.out.println("表单预览: " + form.substring(0, 100) + "...");
                } else {
                    System.out.println("表单内容: " + form);
                }
            } catch (AlipayApiException e) {
                e.printStackTrace();
                System.err.println("支付宝API调用失败: " + e.getMessage());
                throw new Exception("支付宝API调用失败: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("其他错误: " + e.getMessage());
                throw e;
            }
            httpResponse.setContentType("text/html;charset=" + charset);
            httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        } catch (Exception e) {
            e.printStackTrace();
            httpResponse.setContentType("text/html;charset=" + charset);
            httpResponse.getWriter().write("<h1>支付失败：" + e.getMessage() + "</h1>");
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        }
    }

    @Operation(summary = "支付宝支付（GET方式，用于直接跳转）")
    @GetMapping({"/alipay/pay", "/pay/alipay/pay"})
    public void aliPayGet(@RequestParam("orderNo") String orderNo, HttpServletResponse httpResponse) throws Exception {
        // 硬编码charset为UTF-8，确保参数完整
        String charset = "UTF-8";
        try {
            // 根据订单号查询订单
            Order order = orderService.getByOrderNo(orderNo);
            if (order == null) {
                httpResponse.setContentType("text/html;charset=" + charset);
                httpResponse.getWriter().write("<h1>订单不存在</h1>");
                httpResponse.getWriter().flush();
                httpResponse.getWriter().close();
                return;
            }
            if (order.getStatus() != 0) {
                httpResponse.setContentType("text/html;charset=" + charset);
                httpResponse.getWriter().write("<h1>订单状态不正确，无法支付</h1>");
                httpResponse.getWriter().flush();
                httpResponse.getWriter().close();
                return;
            }

            // 1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
            AlipayClient alipayClient = new DefaultAlipayClient(
                    aliPayConfig.getGatewayUrl(),
                    aliPayConfig.getAppId(),
                    aliPayConfig.getAppPrivateKey(),
                    "json",
                    charset,
                    aliPayConfig.getAlipayPublicKey(),
                    aliPayConfig.getSignType()
            );

            // 2. 创建 Request并设置Request参数
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 发送请求的 Request类
            request.setNotifyUrl(aliPayConfig.getNotifyUrl());
            request.setReturnUrl("http://localhost:8080/orders"); // 支付完成后自动跳转到本地页面的路径

            // 构建请求参数
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", order.getOrderNo());
            bizContent.put("total_amount", order.getTotalAmount().setScale(2, java.math.RoundingMode.HALF_UP).toString());
            bizContent.put("subject", "门票订单：" + order.getTicketName());
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

            // 使用hutool的JSON工具将Map转换为JSON字符串
            String jsonContent = cn.hutool.json.JSONUtil.toJsonStr(bizContent);
            request.setBizContent(jsonContent);

            // 执行请求，拿到响应的结果，返回给浏览器
            String form = "";
            try {
                form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
                if (form == null || form.isEmpty()) {
                    throw new Exception("支付宝返回的表单为空");
                }
            } catch (AlipayApiException e) {
                e.printStackTrace();
                throw new Exception("支付宝API调用失败: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            httpResponse.setContentType("text/html;charset=" + charset);
            httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        } catch (Exception e) {
            e.printStackTrace();
            httpResponse.setContentType("text/html;charset=" + charset);
            httpResponse.getWriter().write("<h1>支付失败：" + e.getMessage() + "</h1>");
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        }
    }

    @Operation(summary = "支付宝回调")
    @PostMapping({"/alipay/callback", "/pay/alipay/callback"})
    public String aliPayCallback(HttpServletRequest request) {
        try {
            Map<String, String> params = request.getParameterMap().entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue()[0]
                    ));

            // 验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(params,
                    aliPayConfig.getAlipayPublicKey(),
                    "UTF-8",
                    aliPayConfig.getSignType());
            if (!signVerified) {
                return "failure";
            }

            // 处理回调逻辑
            String tradeStatus = params.get("trade_status");
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                String orderNo = params.get("out_trade_no");
                // 更新订单状态
                Order order = orderService.getByOrderNo(orderNo);
                if (order != null && order.getStatus() == 0) {
                    orderService.payOrder(order.getId());
                }
                return "success";
            }
            return "failure";
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
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

            System.out.println("开始处理退款请求，订单ID: " + orderId + ", 退款金额: " + refundAmount);
            System.out.println("订单号: " + order.getOrderNo());

            // 1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
            AlipayClient alipayClient = new DefaultAlipayClient(
                    aliPayConfig.getGatewayUrl(),
                    aliPayConfig.getAppId(),
                    aliPayConfig.getAppPrivateKey(),
                    "json",
                    "UTF-8",
                    aliPayConfig.getAlipayPublicKey(),
                    aliPayConfig.getSignType()
            );

            // 2. 创建 Request并设置Request参数
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", order.getOrderNo());
            bizContent.put("refund_amount", String.valueOf(refundAmount));
            bizContent.put("out_request_no", "REFUND" + System.currentTimeMillis() + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            request.setBizContent(cn.hutool.json.JSONUtil.toJsonStr(bizContent));

            System.out.println("退款请求参数: " + cn.hutool.json.JSONUtil.toJsonStr(bizContent));

            try {
                // 退款调用接口
                AlipayTradeRefundResponse response = alipayClient.execute(request);
                System.out.println("支付宝退款响应: " + (response.isSuccess() ? "成功" : "失败"));
                System.out.println("响应码: " + response.getCode());
                System.out.println("响应消息: " + response.getMsg());
                System.out.println("响应子码: " + response.getSubCode());
                System.out.println("响应子消息: " + response.getSubMsg());

                if (response.isSuccess()) {
                    orderService.refundOrder(orderId);
                    return Result.success("退款成功");
                } else {
                    return Result.error("退款失败：" + response.getMsg());
                }
            } catch (AlipayApiException e) {
                e.printStackTrace();
                System.err.println("支付宝退款API调用异常: " + e.getMessage());
                return Result.error("退款失败：" + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("退款失败：" + e.getMessage());
        }
    }

    @Operation(summary = "订单页面重定向")
    @RequestMapping(value = "/orders", method = {RequestMethod.GET, RequestMethod.POST})
    public void redirectToOrders(HttpServletRequest request, HttpServletResponse httpResponse) throws Exception {
        // 获取支付宝返回的参数
        String outTradeNo = request.getParameter("out_trade_no");
        String tradeStatus = request.getParameter("trade_status");

        System.out.println("支付宝同步回调 - 订单号: " + outTradeNo + ", 交易状态: " + tradeStatus);

        // 只要有订单号就更新订单状态（支付宝沙箱环境可能不返回trade_status）
        if (outTradeNo != null) {
            Order order = orderService.getByOrderNo(outTradeNo);
            if (order != null && order.getStatus() == 0) {
                try {
                    orderService.payOrder(order.getId());
                    System.out.println("订单支付成功，订单号: " + outTradeNo);
                } catch (Exception e) {
                    System.err.println("更新订单状态失败: " + e.getMessage());
                }
            }
        }

        // 重定向到前端的订单页面
        httpResponse.sendRedirect("http://localhost:5173/orders");
    }

}