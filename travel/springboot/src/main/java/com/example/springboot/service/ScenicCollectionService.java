package com.example.springboot.service;

import jakarta.annotation.Resource;

import com.example.springboot.common.PageResult;
import com.example.springboot.entity.ScenicCollection;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.ScenicCollectionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class ScenicCollectionService {
    private static final Logger logger = LoggerFactory.getLogger(ScenicCollectionService.class);

    @Resource
    private ScenicCollectionMapper scenicCollectionMapper;

    public ScenicCollection addCollection(ScenicCollection collection) {
        logger.info("开始添加景点收藏，用户ID: {}, 景点ID: {}", collection.getUserId(), collection.getScenicId());
        try {
            // 检查是否已经收藏
            ScenicCollection existing = scenicCollectionMapper.selectByUserAndScenic(collection.getUserId(), collection.getScenicId());
            if (existing != null) {
                logger.error("景点已收藏，用户ID: {}, 景点ID: {}", collection.getUserId(), collection.getScenicId());
                throw new ServiceException("景点已收藏");
            }

            int result = scenicCollectionMapper.insert(collection);
            if (result <= 0) {
                logger.error("添加收藏失败，用户ID: {}, 景点ID: {}", collection.getUserId(), collection.getScenicId());
                throw new ServiceException("添加收藏失败，请稍后重试");
            }

            logger.info("添加收藏成功，收藏ID: {}", collection.getId());
            return collection;
        } catch (ServiceException e) {
            logger.error("添加收藏失败，用户ID: {}, 景点ID: {}, 错误信息: {}", collection.getUserId(), collection.getScenicId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("添加收藏失败，用户ID: {}, 景点ID: {}, 错误信息: {}", collection.getUserId(), collection.getScenicId(), e.getMessage(), e);
            throw new ServiceException("添加收藏失败，请稍后重试");
        }
    }

    public void removeCollection(Integer userId, Integer scenicId) {
        logger.info("开始移除景点收藏，用户ID: {}, 景点ID: {}", userId, scenicId);
        try {
            int result = scenicCollectionMapper.deleteByUserAndScenic(userId, scenicId);
            if (result <= 0) {
                logger.error("移除收藏失败，用户ID: {}, 景点ID: {}", userId, scenicId);
                throw new ServiceException("移除收藏失败，请稍后重试");
            }

            logger.info("移除收藏成功，用户ID: {}, 景点ID: {}", userId, scenicId);
        } catch (ServiceException e) {
            logger.error("移除收藏失败，用户ID: {}, 景点ID: {}, 错误信息: {}", userId, scenicId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("移除收藏失败，用户ID: {}, 景点ID: {}, 错误信息: {}", userId, scenicId, e.getMessage(), e);
            throw new ServiceException("移除收藏失败，请稍后重试");
        }
    }

    public List<ScenicCollection> getByUserId(Integer userId) {
        logger.info("根据用户ID获取收藏列表，用户ID: {}", userId);
        try {
            List<ScenicCollection> collections = scenicCollectionMapper.selectByUserId(userId);
            logger.info("获取收藏列表成功，用户ID: {}, 收藏数量: {}", userId, collections.size());
            return collections;
        } catch (Exception e) {
            logger.error("获取收藏列表失败，用户ID: {}, 错误信息: {}", userId, e.getMessage(), e);
            throw new ServiceException("获取收藏列表失败，请稍后重试");
        }
    }

    public ScenicCollection getById(Integer id) {
        logger.info("根据收藏ID获取收藏信息，收藏ID: {}", id);
        try {
            ScenicCollection collection = scenicCollectionMapper.selectById(id);
            if (collection == null) {
                logger.error("收藏不存在，收藏ID: {}", id);
                throw new ServiceException("收藏不存在");
            }
            logger.info("获取收藏信息成功，收藏ID: {}", id);
            return collection;
        } catch (ServiceException e) {
            logger.error("获取收藏信息失败，收藏ID: {}, 错误信息: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("获取收藏信息失败，收藏ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw new ServiceException("获取收藏信息失败，请稍后重试");
        }
    }

    public PageResult<ScenicCollection> getPage(Integer userId, Integer scenicId, Integer currentPage, Integer size) {
        logger.info("分页查询收藏列表, 当前页: {}, 每页条数: {}", currentPage, size);
        try {
            int offset = (currentPage - 1) * size;
            List<ScenicCollection> records = scenicCollectionMapper.selectPage(userId, scenicId, offset, size);
            Long total = scenicCollectionMapper.count(userId, scenicId);
            logger.info("查询成功, 总条数: {}", total);
            return new PageResult<>(records, total, currentPage, size);
        } catch (Exception e) {
            logger.error("分页查询收藏列表失败", e);
            throw new ServiceException("查询收藏列表失败，请稍后重试");
        }
    }

    public boolean isCollected(Integer userId, Integer scenicId) {
        logger.info("检查景点是否已收藏，用户ID: {}, 景点ID: {}", userId, scenicId);
        try {
            ScenicCollection collection = scenicCollectionMapper.selectByUserAndScenic(userId, scenicId);
            boolean result = collection != null;
            logger.info("检查收藏状态成功，用户ID: {}, 景点ID: {}, 结果: {}", userId, scenicId, result);
            return result;
        } catch (Exception e) {
            logger.error("检查收藏状态失败，用户ID: {}, 景点ID: {}, 错误信息: {}", userId, scenicId, e.getMessage(), e);
            throw new ServiceException("检查收藏状态失败，请稍后重试");
        }
    }

    public Map<Integer, Boolean> batchIsCollected(Integer userId, List<Integer> scenicIds) {
        logger.info("批量检查景点收藏状态，用户ID: {}, 景点ID数量: {}", userId, scenicIds.size());
        try {
            List<Integer> collectedScenicIds = scenicCollectionMapper.selectCollectedScenicIds(userId, scenicIds);
            Map<Integer, Boolean> result = new HashMap<>();
            for (Integer scenicId : scenicIds) {
                result.put(scenicId, collectedScenicIds.contains(scenicId));
            }
            logger.info("批量检查收藏状态成功，用户ID: {}, 结果数量: {}", userId, result.size());
            return result;
        } catch (Exception e) {
            logger.error("批量检查收藏状态失败，用户ID: {}, 错误信息: {}", userId, e.getMessage(), e);
            throw new ServiceException("批量检查收藏状态失败，请稍后重试");
        }
    }
}