package com.example.springboot.service;

import jakarta.annotation.Resource;

import com.example.springboot.common.PageResult;
import com.example.springboot.entity.Collection;
import com.example.springboot.entity.TravelGuide;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.CollectionMapper;
import com.example.springboot.mapper.TravelGuideMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.util.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionService {
    private static final Logger logger = LoggerFactory.getLogger(CollectionService.class);

    @Resource
    private CollectionMapper collectionMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private TravelGuideMapper travelGuideMapper;

    public void addCollection(Collection collection) {
        logger.info("添加收藏，用户ID: {}, 攻略ID: {}", collection.getUserId(), collection.getGuideId());
        try {
            // 检查是否已收藏
            Collection existCollection = collectionMapper.selectByUserIdAndGuideId(collection.getUserId(), collection.getGuideId());
            if (existCollection != null) {
                throw new ServiceException("已收藏该攻略");
            }
            // 检查攻略是否存在
            TravelGuide guide = travelGuideMapper.selectById(collection.getGuideId());
            if (guide == null) {
                throw new ServiceException("攻略不存在");
            }
            int result = collectionMapper.insert(collection);
            if (result <= 0) {
                throw new ServiceException("收藏失败，请稍后重试");
            }
            logger.info("收藏成功，收藏ID: {}", collection.getId());
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("收藏失败", e);
            throw new ServiceException("收藏失败，请稍后重试");
        }
    }

    public void cancelCollection(Integer userId, Integer guideId) {
        logger.info("取消收藏，用户ID: {}, 攻略ID: {}", userId, guideId);
        try {
            int result = collectionMapper.deleteByUserIdAndGuideId(userId, guideId);
            if (result <= 0) {
                throw new ServiceException("取消收藏失败，收藏不存在");
            }
            logger.info("取消收藏成功");
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("取消收藏失败", e);
            throw new ServiceException("取消收藏失败，请稍后重试");
        }
    }

    public PageResult<Collection> getPage(String username, String guideTitle, Integer currentPage, Integer size) {
        logger.info("分页查询收藏列表, 当前页: {}, 每页条数: {}", currentPage, size);
        try {
            int offset = (currentPage - 1) * size;
            List<Collection> records = collectionMapper.selectPage(username, guideTitle, offset, size);
            Long total = collectionMapper.count(username, guideTitle);
            logger.info("查询成功, 总条数: {}", total);
            return new PageResult<>(records, total, currentPage, size);
        } catch (Exception e) {
            logger.error("分页查询收藏列表失败", e);
            throw new ServiceException("查询收藏列表失败，请稍后重试");
        }
    }

    public PageResult<Collection> getCurrentUserCollections(Integer currentPage, Integer size) {
        User currentUser = JwtTokenUtils.getCurrentUser();
        if (currentUser == null) {
            throw new ServiceException("请先登录");
        }
        logger.info("查询当前用户收藏列表, 用户ID: {}, 当前页: {}, 每页条数: {}", currentUser.getId(), currentPage, size);
        try {
            int offset = (currentPage - 1) * size;
            List<Collection> records = collectionMapper.selectByUserId(currentUser.getId(), offset, size);
            // 填充攻略信息
            for (Collection collection : records) {
                if (collection.getGuideId() != null) {
                    TravelGuide guide = travelGuideMapper.selectById(collection.getGuideId());
                    if (guide != null) {
                        collection.setGuideTitle(guide.getTitle());
                        collection.setGuideCoverImage(guide.getCoverImage());
                    }
                }
            }
            Long total = collectionMapper.countByUserId(currentUser.getId());
            logger.info("查询成功, 总条数: {}", total);
            return new PageResult<>(records, total, currentPage, size);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("查询当前用户收藏列表失败", e);
            throw new ServiceException("查询收藏列表失败，请稍后重试");
        }
    }

    public boolean isCollected(Integer userId, Integer guideId) {
        logger.info("检查是否已收藏，用户ID: {}, 攻略ID: {}", userId, guideId);
        try {
            Collection collection = collectionMapper.selectByUserIdAndGuideId(userId, guideId);
            return collection != null;
        } catch (Exception e) {
            logger.error("检查收藏状态失败", e);
            return false;
        }
    }

    public Collection getById(Integer id) {
        logger.info("根据ID获取收藏，收藏ID: {}", id);
        try {
            Collection collection = collectionMapper.selectById(id);
            if (collection == null) {
                throw new ServiceException("收藏不存在");
            }
            logger.info("获取收藏成功，收藏ID: {}", id);
            return collection;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("获取收藏失败，收藏ID: {}", id, e);
            throw new ServiceException("获取收藏失败，请稍后重试");
        }
    }

    public void deleteById(Integer id) {
        logger.info("删除收藏，收藏ID: {}", id);
        try {
            int result = collectionMapper.deleteById(id);
            if (result <= 0) {
                throw new ServiceException("删除失败，收藏不存在");
            }
            logger.info("删除收藏成功，收藏ID: {}", id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("删除收藏失败，收藏ID: {}", id, e);
            throw new ServiceException("删除失败，请稍后重试");
        }
    }
}