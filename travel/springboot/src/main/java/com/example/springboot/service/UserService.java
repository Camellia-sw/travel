package com.example.springboot.service;

import jakarta.annotation.Resource;

import com.example.springboot.common.PageResult;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.util.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    /**
     * 账号密码登录
     */
    public User login(User user) {
        logger.info("开始登录，用户名: {}", user.getUsername());
        try {
            User dbUser = getByUsername(user.getUsername());
            logger.info("获取用户信息成功，用户ID: {}", dbUser.getId());
            // 验证密码（明文比较）
            if (!user.getPassword().equals(dbUser.getPassword())) {
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
            logger.info("用户登出成功");
        } catch (Exception e) {
            logger.error("用户登出异常", e);
        }
    }

    /**
     * 分页查询用户列表
     */
    public PageResult<User> getPage(String username, String role, Integer currentPage, Integer size) {
        logger.info("分页查询用户列表, 当前页: {}, 每页条数: {}", currentPage, size);
        try {
            int offset = (currentPage - 1) * size;
            List<User> records = userMapper.selectPage(username, role, offset, size);
            Long total = userMapper.count(username, role);
            logger.info("查询成功, 总条数: {}", total);
            return new PageResult<>(records, total, currentPage, size);
        } catch (Exception e) {
            logger.error("分页查询用户列表失败", e);
            throw new ServiceException("查询用户列表失败，请稍后重试");
        }
    }

    /**
     * 删除用户
     */
    public void deleteById(Integer id) {
        logger.info("删除用户, 用户ID: {}", id);
        try {
            int result = userMapper.deleteById(id);
            if (result <= 0) {
                throw new ServiceException("删除失败，用户不存在");
            }
            logger.info("删除用户成功, 用户ID: {}", id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("删除用户失败, 用户ID: {}", id, e);
            throw new ServiceException("删除失败，请稍后重试");
        }
    }

    /**
     * 更新用户信息
     */
    public void update(User user) {
        logger.info("更新用户信息, 用户ID: {}", user.getId());
        try {
            int result = userMapper.update(user);
            if (result <= 0) {
                throw new ServiceException("更新失败，用户不存在");
            }
            logger.info("更新用户信息成功, 用户ID: {}", user.getId());
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("更新用户信息失败, 用户ID: {}", user.getId(), e);
            throw new ServiceException("更新失败，请稍后重试");
        }
    }
    /**
     * 修改密码
     */
    public void updatePassword(Integer id, String oldPassword, String newPassword) {
        logger.info("修改密码, 用户ID: {}", id);
        try {
            User user = userMapper.selectById(id);
            if (user == null) {
                throw new ServiceException("用户不存在");
            }
            // 验证原密码
            if (!user.getPassword().equals(oldPassword)) {
                throw new ServiceException("原密码错误");
            }
            // 更新密码
            int result = userMapper.updatePassword(id, newPassword);
            if (result <= 0) {
                throw new ServiceException("密码修改失败");
            }
            logger.info("密码修改成功, 用户ID: {}", id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("修改密码失败, 用户ID: {}", id, e);
            throw new ServiceException("修改密码失败，请稍后重试");
        }
    }
}