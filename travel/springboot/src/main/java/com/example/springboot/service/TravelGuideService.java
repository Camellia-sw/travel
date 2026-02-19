package com.example.springboot.service;

import jakarta.annotation.Resource;

import com.example.springboot.common.PageResult;
import com.example.springboot.entity.TravelGuide;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.TravelGuideMapper;
import com.example.springboot.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TravelGuideService {
    private static final Logger logger = LoggerFactory.getLogger(TravelGuideService.class);

    @Resource
    private TravelGuideMapper travelGuideMapper;

    @Resource
    private UserMapper userMapper;

    public TravelGuide getById(Integer id) {
        logger.info("根据ID获取攻略，攻略ID: {}", id);
        try {
            TravelGuide guide = travelGuideMapper.selectById(id);
            if (guide == null) {
                logger.error("攻略不存在，攻略ID: {}", id);
                throw new ServiceException("攻略不存在");
            }
            // 填充用户信息
            if (guide.getUserId() != null) {
                User user = userMapper.selectById(guide.getUserId());
                if (user != null) {
                    guide.setUserInfo(user);
                    guide.setUsername(user.getUsername());
                }
            }
            logger.info("获取攻略成功，攻略ID: {}", id);
            return guide;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("获取攻略失败，攻略ID: {}", id, e);
            throw new ServiceException("获取攻略失败，请稍后重试");
        }
    }

    public void addGuide(TravelGuide guide) {
        logger.info("新增攻略，标题: {}", guide.getTitle());
        try {
            if (guide.getViews() == null) {
                guide.setViews(0);
            }
            int result = travelGuideMapper.insert(guide);
            if (result <= 0) {
                logger.error("新增攻略失败，标题: {}", guide.getTitle());
                throw new ServiceException("新增攻略失败，请稍后重试");
            }
            logger.info("新增攻略成功，攻略ID: {}, 标题: {}", guide.getId(), guide.getTitle());
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("新增攻略失败，标题: {}", guide.getTitle(), e);
            throw new ServiceException("新增攻略失败，请稍后重试");
        }
    }

    public void updateGuide(TravelGuide guide) {
        logger.info("更新攻略，攻略ID: {}", guide.getId());
        try {
            TravelGuide dbGuide = travelGuideMapper.selectById(guide.getId());
            if (dbGuide == null) {
                throw new ServiceException("攻略不存在");
            }
            int result = travelGuideMapper.update(guide);
            if (result <= 0) {
                logger.error("更新攻略失败，攻略ID: {}", guide.getId());
                throw new ServiceException("更新攻略失败，请稍后重试");
            }
            logger.info("更新攻略成功，攻略ID: {}", guide.getId());
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("更新攻略失败，攻略ID: {}", guide.getId(), e);
            throw new ServiceException("更新攻略失败，请稍后重试");
        }
    }

    public void deleteGuide(Integer id) {
        logger.info("删除攻略，攻略ID: {}", id);
        try {
            int result = travelGuideMapper.deleteById(id);
            if (result <= 0) {
                throw new ServiceException("删除失败，攻略不存在");
            }
            logger.info("删除攻略成功，攻略ID: {}", id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("删除攻略失败，攻略ID: {}", id, e);
            throw new ServiceException("删除攻略失败，请稍后重试");
        }
    }

    public void addView(Integer id) {
        logger.info("增加浏览量，攻略ID: {}", id);
        try {
            travelGuideMapper.addView(id);
        } catch (Exception e) {
            logger.error("增加浏览量失败，攻略ID: {}", id, e);
        }
    }

    public PageResult<TravelGuide> getPage(String title, Integer userId, Integer currentPage, Integer size) {
        logger.info("分页查询攻略列表, 当前页: {}, 每页条数: {}", currentPage, size);
        try {
            int offset = (currentPage - 1) * size;
            List<TravelGuide> records = travelGuideMapper.selectPage(title, userId, offset, size);
            // 填充用户信息
            for (TravelGuide guide : records) {
                if (guide.getUserId() != null) {
                    User user = userMapper.selectById(guide.getUserId());
                    if (user != null) {
                        guide.setUserInfo(user);
                        guide.setUsername(user.getUsername());
                    }
                }
            }
            Long total = travelGuideMapper.count(title, userId);
            logger.info("查询成功, 总条数: {}", total);
            return new PageResult<>(records, total, currentPage, size);
        } catch (Exception e) {
            logger.error("分页查询攻略列表失败", e);
            throw new ServiceException("查询攻略列表失败，请稍后重试");
        }
    }

    public List<Map<String, Object>> getHotGuides(Integer limit) {
        logger.info("获取热门攻略, 限制条数: {}", limit);
        try {
            List<TravelGuide> guides = travelGuideMapper.selectHotGuides(limit);
            List<Map<String, Object>> result = new ArrayList<>();
            for (TravelGuide guide : guides) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", guide.getId());
                map.put("title", guide.getTitle());
                map.put("coverImage", guide.getCoverImage());
                map.put("views", guide.getViews());
                // 填充用户信息
                if (guide.getUserId() != null) {
                    User user = userMapper.selectById(guide.getUserId());
                    if (user != null) {
                        map.put("username", user.getUsername());
                    }
                }
                result.add(map);
            }
            logger.info("获取热门攻略成功");
            return result;
        } catch (Exception e) {
            logger.error("获取热门攻略失败", e);
            throw new ServiceException("获取热门攻略失败，请稍后重试");
        }
    }

    public List<Map<String, Object>> getGuideSuggestions(String keyword, Integer limit) {
        logger.info("获取攻略搜索建议, 关键词: {}, 限制条数: {}", keyword, limit);
        try {
            List<TravelGuide> guides = travelGuideMapper.selectSuggestions(keyword, limit);
            List<Map<String, Object>> result = new ArrayList<>();
            for (TravelGuide guide : guides) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", guide.getId());
                map.put("title", guide.getTitle());
                result.add(map);
            }
            logger.info("获取攻略搜索建议成功");
            return result;
        } catch (Exception e) {
            logger.error("获取攻略搜索建议失败", e);
            throw new ServiceException("获取攻略搜索建议失败，请稍后重试");
        }
    }
}
