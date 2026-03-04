package com.example.springboot.service;

import com.example.springboot.common.PageResult;
import com.example.springboot.entity.AccommodationReview;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.AccommodationReviewMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccommodationReviewService {
    private static final Logger logger = LoggerFactory.getLogger(AccommodationReviewService.class);

    @Resource
    private AccommodationReviewMapper reviewMapper;

    public void addReview(AccommodationReview review) {
        logger.info("添加住宿评价: {}", review);
        try {
            int result = reviewMapper.insert(review);
            if (result != 1) {
                throw new ServiceException("添加住宿评价失败");
            }
            logger.info("添加住宿评价成功");
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("添加住宿评价失败", e);
            throw new ServiceException("添加住宿评价失败，请稍后重试");
        }
    }

    public PageResult<AccommodationReview> getPage(Integer accommodationId, Integer userId, Integer currentPage, Integer size) {
        logger.info("分页查询住宿评价列表, 当前页: {}, 每页条数: {}, 住宿ID: {}, 用户ID: {}", currentPage, size, accommodationId, userId);
        try {
            int offset = (currentPage - 1) * size;
            List<AccommodationReview> records = reviewMapper.selectPage(accommodationId, userId, offset, size);
            Long total = reviewMapper.count(accommodationId, userId);
            logger.info("查询成功, 总条数: {}", total);
            return new PageResult<>(records, total, currentPage, size);
        } catch (Exception e) {
            logger.error("分页查询住宿评价列表失败", e);
            throw new ServiceException("查询住宿评价列表失败，请稍后重试");
        }
    }

    public AccommodationReview getById(Integer id) {
        logger.info("根据ID获取住宿评价, ID: {}", id);
        try {
            AccommodationReview review = reviewMapper.selectById(id);
            if (review == null) {
                throw new ServiceException("住宿评价不存在");
            }
            logger.info("获取住宿评价成功");
            return review;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("根据ID获取住宿评价失败", e);
            throw new ServiceException("获取住宿评价失败，请稍后重试");
        }
    }

    public void deleteReview(Integer id) {
        logger.info("删除住宿评价, ID: {}", id);
        try {
            AccommodationReview review = reviewMapper.selectById(id);
            if (review == null) {
                throw new ServiceException("住宿评价不存在");
            }
            int result = reviewMapper.deleteById(id);
            if (result != 1) {
                throw new ServiceException("删除住宿评价失败");
            }
            logger.info("删除住宿评价成功");
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("删除住宿评价失败", e);
            throw new ServiceException("删除住宿评价失败，请稍后重试");
        }
    }

    public void update(AccommodationReview review) {
        logger.info("更新住宿评价, ID: {}", review.getId());
        try {
            AccommodationReview dbReview = reviewMapper.selectById(review.getId());
            if (dbReview == null) {
                throw new ServiceException("住宿评价不存在");
            }

            if (review.getContent() != null) {
                dbReview.setContent(review.getContent());
            }
            if (review.getRating() != null) {
                dbReview.setRating(review.getRating());
            }

            int result = reviewMapper.update(dbReview);
            if (result != 1) {
                throw new ServiceException("更新住宿评价失败");
            }
            logger.info("更新住宿评价成功");
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("更新住宿评价失败", e);
            throw new ServiceException("更新住宿评价失败，请稍后重试");
        }
    }
}