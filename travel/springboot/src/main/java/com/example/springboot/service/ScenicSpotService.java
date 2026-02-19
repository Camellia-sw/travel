package com.example.springboot.service;

import jakarta.annotation.Resource;

import com.example.springboot.common.PageResult;
import com.example.springboot.entity.ScenicSpot;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.ScenicSpotMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ScenicSpotService {
    private static final Logger logger = LoggerFactory.getLogger(ScenicSpotService.class);

    @Resource
    private ScenicSpotMapper scenicSpotMapper;

    public PageResult<ScenicSpot> getPage(String name, String location, Integer categoryId, Integer currentPage, Integer size) {
        logger.info("分页查询景点列表, 当前页: {}, 每页条数: {}", currentPage, size);
        try {
            int offset = (currentPage - 1) * size;
            List<ScenicSpot> records = scenicSpotMapper.selectPage(name, location, categoryId, offset, size);
            Long total = scenicSpotMapper.count(name, location, categoryId);
            logger.info("查询成功, 总条数: {}", total);
            return new PageResult<>(records, total, currentPage, size);
        } catch (Exception e) {
            logger.error("分页查询景点列表失败", e);
            throw new ServiceException("查询景点列表失败，请稍后重试");
        }
    }

    public ScenicSpot getById(Integer id) {
        logger.info("根据景点ID获取景点，景点ID: {}", id);
        try {
            ScenicSpot scenicSpot = scenicSpotMapper.selectById(id);
            if (scenicSpot == null) {
                logger.error("景点不存在，景点ID: {}", id);
                throw new ServiceException("景点不存在");
            }
            logger.info("获取景点信息成功，景点ID: {}, 景点名称: {}", id, scenicSpot.getName());
            return scenicSpot;
        } catch (ServiceException e) {
            logger.error("获取景点信息失败，景点ID: {}, 错误信息: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("获取景点信息失败，景点ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw new ServiceException("获取景点信息失败，请稍后重试");
        }
    }

    public List<ScenicSpot> getByCategoryId(Integer categoryId) {
        logger.info("根据分类ID获取景点列表，分类ID: {}", categoryId);
        try {
            List<ScenicSpot> list = scenicSpotMapper.selectByCategoryId(categoryId);
            logger.info("查询成功，景点数量: {}", list.size());
            return list;
        } catch (Exception e) {
            logger.error("根据分类ID获取景点列表失败", e);
            throw new ServiceException("查询失败，请稍后重试");
        }
    }

    public void createScenicSpot(ScenicSpot scenicSpot) {
        logger.info("新增景点，景点名称: {}", scenicSpot.getName());
        try {
            int result = scenicSpotMapper.insert(scenicSpot);
            if (result <= 0) {
                logger.error("新增景点失败，景点名称: {}", scenicSpot.getName());
                throw new ServiceException("新增失败，请稍后重试");
            }
            logger.info("新增景点成功，景点ID: {}, 景点名称: {}", scenicSpot.getId(), scenicSpot.getName());
        } catch (ServiceException e) {
            logger.error("新增景点失败，景点名称: {}, 错误信息: {}", scenicSpot.getName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("新增景点失败，景点名称: {}, 错误信息: {}", scenicSpot.getName(), e.getMessage(), e);
            throw new ServiceException("新增失败，请稍后重试");
        }
    }

    public void updateScenicSpot(Integer id, ScenicSpot scenicSpot) {
        logger.info("更新景点信息，景点ID: {}", id);
        try {
            scenicSpot.setId(id);
            int result = scenicSpotMapper.update(scenicSpot);
            if (result <= 0) {
                throw new ServiceException("更新失败，景点不存在");
            }
            logger.info("更新景点信息成功，景点ID: {}", id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("更新景点信息失败，景点ID: {}", id, e);
            throw new ServiceException("更新失败，请稍后重试");
        }
    }

    public void deleteScenicSpot(Integer id) {
        logger.info("删除景点，景点ID: {}", id);
        try {
            int result = scenicSpotMapper.deleteById(id);
            if (result <= 0) {
                throw new ServiceException("删除失败，景点不存在");
            }
            logger.info("删除景点成功，景点ID: {}", id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("删除景点失败，景点ID: {}", id, e);
            throw new ServiceException("删除失败，请稍后重试");
        }
    }

    public List<ScenicSpot> getAll() {
        logger.info("获取所有景点");
        try {
            List<ScenicSpot> list = scenicSpotMapper.selectAll();
            logger.info("查询成功，景点数量: {}", list.size());
            return list;
        } catch (Exception e) {
            logger.error("获取所有景点失败", e);
            throw new ServiceException("查询失败，请稍后重试");
        }
    }
}