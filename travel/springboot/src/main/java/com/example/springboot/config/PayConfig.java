package com.example.springboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付配置类
 */
@Component
@ConfigurationProperties(prefix = "pay")
public class PayConfig {
    // 支付宝配置
    private AliPay aliPay = new AliPay();
    // 微信支付配置
    private WeChatPay weChatPay = new WeChatPay();

    public AliPay getAliPay() {
        return aliPay;
    }

    public void setAliPay(AliPay aliPay) {
        this.aliPay = aliPay;
    }

    public WeChatPay getWeChatPay() {
        return weChatPay;
    }

    public void setWeChatPay(WeChatPay weChatPay) {
        this.weChatPay = weChatPay;
    }

    /**
     * 支付宝配置
     */
    public static class AliPay {
        private String appId;
        private String merchantPrivateKey;
        private String alipayPublicKey;
        private String notifyUrl;
        private String returnUrl;
        private String charset = "UTF-8";
        private String signType = "RSA2";
        private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getMerchantPrivateKey() {
            return merchantPrivateKey;
        }

        public void setMerchantPrivateKey(String merchantPrivateKey) {
            this.merchantPrivateKey = merchantPrivateKey;
        }

        public String getAlipayPublicKey() {
            return alipayPublicKey;
        }

        public void setAlipayPublicKey(String alipayPublicKey) {
            this.alipayPublicKey = alipayPublicKey;
        }

        public String getNotifyUrl() {
            return notifyUrl;
        }

        public void setNotifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
        }

        public String getReturnUrl() {
            return returnUrl;
        }

        public void setReturnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
        }

        public String getCharset() {
            return charset;
        }

        public void setCharset(String charset) {
            this.charset = charset;
        }

        public String getSignType() {
            return signType;
        }

        public void setSignType(String signType) {
            this.signType = signType;
        }

        public String getGatewayUrl() {
            return gatewayUrl;
        }

        public void setGatewayUrl(String gatewayUrl) {
            this.gatewayUrl = gatewayUrl;
        }
    }

    /**
     * 微信支付配置
     */
    public static class WeChatPay {
        private String appId;
        private String mchId;
        private String apiV3Key;
        private String serialNo;
        private String privateKeyPath;
        private String notifyUrl;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getMchId() {
            return mchId;
        }

        public void setMchId(String mchId) {
            this.mchId = mchId;
        }

        public String getApiV3Key() {
            return apiV3Key;
        }

        public void setApiV3Key(String apiV3Key) {
            this.apiV3Key = apiV3Key;
        }

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }

        public String getPrivateKeyPath() {
            return privateKeyPath;
        }

        public void setPrivateKeyPath(String privateKeyPath) {
            this.privateKeyPath = privateKeyPath;
        }

        public String getNotifyUrl() {
            return notifyUrl;
        }

        public void setNotifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
        }
    }
}