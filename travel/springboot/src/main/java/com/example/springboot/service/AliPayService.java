package com.example.springboot.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.example.springboot.config.AliPayConfig;
import com.example.springboot.entity.Order;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 支付服务类
 * 支持支付宝和微信支付
 */
@Service
public class AliPayService {
    private static final Logger logger = LoggerFactory.getLogger(AliPayService.class);

    @Resource
    private AliPayConfig aliPayConfig;

    @Resource
    private OrderService orderService;

    private AlipayClient alipayClient;

    /**
     * 初始化支付宝客户端
     */
    private void initAliPayClient() {
        if (alipayClient == null) {
            alipayClient = new DefaultAlipayClient(
                    aliPayConfig.getGatewayUrl(),
                    aliPayConfig.getAppId(),
                    aliPayConfig.getAppPrivateKey(),
                    "json",
                    aliPayConfig.getCharset(),
                    aliPayConfig.getAlipayPublicKey(),
                    aliPayConfig.getSignType()
            );
        }
    }

    /**
     * 支付宝支付
     * @param order 订单信息
     * @return 支付宝支付参数
     * @throws AlipayApiException 支付宝API异常
     */
    public Map<String, String> aliPay(Order order) throws AlipayApiException {
        logger.info("支付宝支付，订单ID: {}", order.getId());

        // 检查支付宝配置是否完整
        if (aliPayConfig.getAppId() == null ||
                aliPayConfig.getAppId().isEmpty() ||
                aliPayConfig.getAppPrivateKey() == null ||
                aliPayConfig.getAppPrivateKey().isEmpty()) {
            logger.warn("支付宝配置不完整，使用模拟支付");
            // 返回模拟支付表单
            Map<String, String> result = new HashMap<>();
            String ticketName = order.getTicketName() != null ? order.getTicketName() : "门票";
            String form = "<div style=\"text-align:center;padding:40px;\">" +
                    "<h3 style=\"color:#1677ff;margin-bottom:20px;\">支付宝支付演示</h3>" +
                    "<div style=\"background:#f5f5f5;padding:20px;border-radius:8px;margin-bottom:20px;\">" +
                    "<p style=\"margin:10px 0;\"><strong>订单号：</strong>" + order.getOrderNo() + "</p>" +
                    "<p style=\"margin:10px 0;\"><strong>商品名称：</strong>门票订单：" + ticketName + "</p>" +
                    "<p style=\"margin:10px 0;\"><strong>支付金额：</strong><span style=\"color:#e53e3e;font-size:24px;font-weight:bold;\">¥" + order.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP) + "</span></p>" +
                    "</div>" +
                    "<button id=\"simulatePayBtn\" style=\"background:linear-gradient(135deg,#1677ff,#4096ff);color:white;border:none;padding:12px 40px;font-size:16px;border-radius:8px;cursor:pointer;margin-right:10px;\">模拟支付</button>" +
                    "<button onclick=\"window.location.href='/orders'\" style=\"background:#fff;color:#666;border:1px solid #ddd;padding:12px 20px;font-size:16px;border-radius:8px;cursor:pointer;\">返回</button>" +
                    "</div>";
            result.put("form", form);
            result.put("orderNo", order.getOrderNo());
            result.put("simulate", "true");
            return result;
        }

        // 打印支付宝配置信息（仅用于调试）
        logger.info("支付宝配置：AppId={}, GatewayUrl={}",
                aliPayConfig.getAppId(),
                aliPayConfig.getGatewayUrl());

        // 检查订单信息
        if (order.getTicketName() == null) {
            order.setTicketName("门票订单");
            logger.warn("订单缺少门票名称，使用默认值");
        }

        initAliPayClient();

        // 创建支付宝支付请求
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        request.setReturnUrl(aliPayConfig.getReturnUrl());

        // 构建请求参数
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no", order.getOrderNo());
        bizContent.put("total_amount", order.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        bizContent.put("subject", "门票订单：" + order.getTicketName());
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

        // 使用hutool的JSON工具将Map转换为JSON字符串
        String jsonContent = cn.hutool.json.JSONUtil.toJsonStr(bizContent);
        request.setBizContent(jsonContent);

        // 生成支付参数
        try {
            String form = alipayClient.pageExecute(request).getBody();
            logger.info("支付宝支付参数生成成功，订单号: {}", order.getOrderNo());

            Map<String, String> result = new HashMap<>();
            result.put("form", form);
            result.put("orderNo", order.getOrderNo());

            return result;
        } catch (AlipayApiException e) {
            logger.error("支付宝API调用失败: {}", e.getMessage(), e);
            throw e;
        }
    }



    /**
     * 处理支付宝回调
     * @param params 回调参数
     * @return 处理结果
     */
    public boolean handleAliPayCallback(Map<String, String> params) {
        logger.info("处理支付宝回调，订单号: {}", params.get("out_trade_no"));
        try {
            // 验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(params,
                    aliPayConfig.getAlipayPublicKey(),
                    aliPayConfig.getCharset(),
                    aliPayConfig.getSignType());
            if (!signVerified) {
                logger.error("支付宝回调签名验证失败");
                return false;
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
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("处理支付宝回调失败", e);
            return false;
        }
    }



    /**
     * 关闭订单
     * @param orderNo 订单号
     * @param paymentMethod 支付方式
     * @return 关闭结果
     */
    public boolean closeOrder(String orderNo, String paymentMethod) {
        logger.info("关闭订单，订单号: {}, 支付方式: {}", orderNo, paymentMethod);
        try {
            // 简化处理，直接返回成功
            return true;
        } catch (Exception e) {
            logger.error("关闭订单失败", e);
            return false;
        }
    }

    /**
     * 退款
     * @param orderNo 订单号
     * @param refundAmount 退款金额
     * @param paymentMethod 支付方式
     * @return 退款结果
     */
    public boolean refund(String orderNo, double refundAmount, String paymentMethod) {
        logger.info("退款，订单号: {}, 退款金额: {}, 支付方式: {}", orderNo, refundAmount, paymentMethod);
        try {
            if ("支付宝".equals(paymentMethod)) {
                // 支付宝退款
                initAliPayClient();
                AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
                Map<String, Object> bizContent = new HashMap<>();
                bizContent.put("out_trade_no", orderNo);
                bizContent.put("refund_amount", refundAmount);
                bizContent.put("out_request_no", "REFUND" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                request.setBizContent(cn.hutool.json.JSONUtil.toJsonStr(bizContent));
                AlipayTradeRefundResponse response = alipayClient.execute(request);
                return response.isSuccess();
            }
            return false;
        } catch (Exception e) {
            logger.error("退款失败", e);
            return false;
        }
    }
}