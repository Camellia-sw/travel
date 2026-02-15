package com.example.springboot.service;

import jakarta.annotation.Resource;

import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.util.JwtTokenUtils;
import com.example.springboot.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private static final String USER_CACHE_PREFIX = "user:";
    private static final long USER_CACHE_EXPIRE = 3600; // 缓存1小时

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private PasswordEncoder bCryptPasswordEncoder;

    /**
     * 账号密码登录
     */
    public User login(User user) {
        logger.info("开始登录，用户名: {}", user.getUsername());
        try {
            User dbUser = getByUsername(user.getUsername());
            logger.info("获取用户信息成功，用户ID: {}", dbUser.getId());
            // 验证密码
            boolean passwordMatch = false;
            String dbPassword = dbUser.getPassword();

            // 检查密码是否已经加密（BCrypt 加密的密码通常以 $2a$ 开头）
            if (dbPassword.startsWith("$2a$")) {
                // 密码已加密，使用 BCrypt 验证
                passwordMatch = bCryptPasswordEncoder.matches(user.getPassword(), dbPassword);
            } else {
                // 密码未加密，直接比较
                passwordMatch = user.getPassword().equals(dbPassword);
                // 可以在这里添加逻辑，将明文密码加密后更新到数据库
            }

            if (!passwordMatch) {
                logger.error("密码验证失败，用户名: {}", user.getUsername());
                throw new ServiceException("用户名或密码错误");
            }
            logger.info("密码验证成功，用户名: {}", user.getUsername());
            return generateLoginResponse(dbUser);
        } catch (ServiceException e) {
            logger.error("登录失败，用户名: {}, 错误信息: {}", user.getUsername(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("登录失败，用户名: {}, 错误信息: {}", user.getUsername(), e.getMessage(), e);
            throw new ServiceException("登录失败，请稍后重试");
        }
    }

    /**
     * 生成登录响应
     */
    private User generateLoginResponse(User dbUser) {
        String token = JwtTokenUtils.genToken(String.valueOf(dbUser.getId()), dbUser.getPassword());
        dbUser.setToken(token);

        // 将用户信息放入Redis，便于后续请求使用
        try {
            String userCacheKey = USER_CACHE_PREFIX + dbUser.getId();
            redisUtil.set(userCacheKey, dbUser, USER_CACHE_EXPIRE);
            logger.info("用户登录信息已缓存，key: {}", userCacheKey);
        } catch (Exception e) {
            logger.error("Redis缓存用户信息失败，将继续登录流程", e);
            // Redis缓存失败不影响登录流程
        }

        return dbUser;
    }

    /**
     * 根据用户名获取用户
     */
    public User getByUsername(String username) {
        logger.info("根据用户名获取用户，用户名: {}", username);
        try {
            User user = userMapper.selectByUsername(username);
            if (user == null) {
                logger.error("用户不存在，用户名: {}", username);
                throw new ServiceException("用户不存在");
            }
            logger.info("获取用户信息成功，用户名: {}, 用户ID: {}", username, user.getId());
            return user;
        } catch (ServiceException e) {
            logger.error("获取用户信息失败，用户名: {}, 错误信息: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("获取用户信息失败，用户名: {}, 错误信息: {}", username, e.getMessage(), e);
            throw new ServiceException("获取用户信息失败，请稍后重试");
        }
    }

    /**
     * 用户登出
     */
    public void logout(String token) {
        try {
            // 获取当前用户
            User currentUser = JwtTokenUtils.getCurrentUser();
            if (currentUser != null) {
                // 清除Redis中的用户缓存
                String userCacheKey = USER_CACHE_PREFIX + currentUser.getId();
                redisUtil.del(userCacheKey);

                // 清除JWT相关缓存
                JwtTokenUtils.clearUserCache(token);

                logger.info("用户登出成功，用户ID: {}", currentUser.getId());
            }
        } catch (Exception e) {
            logger.error("用户登出异常", e);
        }
    }
}