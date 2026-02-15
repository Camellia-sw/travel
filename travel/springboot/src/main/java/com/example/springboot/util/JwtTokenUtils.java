package com.example.springboot.util;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Component
public class JwtTokenUtils {
    private static UserService staticUserService;

    @Resource
    private UserService userService;

    public static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtils.class);

    @PostConstruct
    public void setServices() {
        staticUserService = userService;
    }

    /**
     * 生成token
     */
    public static String genToken(String userId, String sign) {
        return JWT.create()
                .withAudience(userId)
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2))
                .sign(Algorithm.HMAC256(sign));
    }

    /**
     * 获取当前登录用户
     */
    public static User getCurrentUser() {
        String token = null;
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            token = request.getHeader("token");
            if (StringUtils.isEmpty(token)) {
                token = request.getParameter("token");
            }
            if (StringUtils.isEmpty(token)) {
                LOGGER.error("获取当前登录的token失败");
                return null;
            }

            // 从JWT中解析用户ID
            String userIdStr = JWT.decode(token).getAudience().get(0);
            if (userIdStr == null || userIdStr.isEmpty()) {
                LOGGER.error("从token中解析用户ID失败");
                return null;
            }

            Integer userId = Integer.parseInt(userIdStr);

            // 从数据库查询用户信息
            User user = staticUserService.getById(userId);
            return user;
        } catch (Exception e) {
            LOGGER.error("获取当前用户信息失败", e);
            return null;
        }
    }

    /**
     * 清除用户的缓存信息
     */
    public static void clearUserCache(String token) {
        // 由于不使用Redis，此方法暂时为空
    }
}