package com.example.springboot.service;

import jakarta.annotation.Resource;

import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.util.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

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
        return dbUser;
    }

    /**
     * 用户注册
     */
    public User register(User user) {
        logger.info("开始注册，用户名: {}, 邮箱: {}", user.getUsername(), user.getEmail());
        try {
            // 检查用户名是否已存在
            User existUser = userMapper.selectByUsername(user.getUsername());
            if (existUser != null) {
                logger.error("用户名已存在，用户名: {}", user.getUsername());
                throw new ServiceException("用户名已存在");
            }

            // 检查邮箱是否已存在
            existUser = userMapper.selectByEmail(user.getEmail());
            if (existUser != null) {
                logger.error("邮箱已存在，邮箱: {}", user.getEmail());
                throw new ServiceException("邮箱已存在");
            }

            // 加密密码
            String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            // 设置默认角色
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }

            // 插入用户
            int result = userMapper.insert(user);
            if (result <= 0) {
                logger.error("注册失败，用户名: {}", user.getUsername());
                throw new ServiceException("注册失败，请稍后重试");
            }

            logger.info("注册成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
            return user;
        } catch (ServiceException e) {
            logger.error("注册失败，用户名: {}, 错误信息: {}", user.getUsername(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("注册失败，用户名: {}, 错误信息: {}", user.getUsername(), e.getMessage(), e);
            throw new ServiceException("注册失败，请稍后重试");
        }
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
     * 根据用户ID获取用户
     */
    public User getById(Integer id) {
        logger.info("根据用户ID获取用户，用户ID: {}", id);
        try {
            User user = userMapper.selectById(id);
            if (user == null) {
                logger.error("用户不存在，用户ID: {}", id);
                throw new ServiceException("用户不存在");
            }
            logger.info("获取用户信息成功，用户ID: {}, 用户名: {}", id, user.getUsername());
            return user;
        } catch (ServiceException e) {
            logger.error("获取用户信息失败，用户ID: {}, 错误信息: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("获取用户信息失败，用户ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw new ServiceException("获取用户信息失败，请稍后重试");
        }
    }

    /**
     * 用户登出
     */
    public void logout(String token) {
        try {
            // 清除JWT相关缓存
            JwtTokenUtils.clearUserCache(token);
            logger.info("用户登出成功");
        } catch (Exception e) {
            logger.error("用户登出异常", e);
        }
    }
}