package com.example.springboot.service;

import jakarta.annotation.Resource;
import com.example.springboot.common.PageResult;
import com.example.springboot.entity.Comment;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.CommentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Resource
    private CommentMapper commentMapper;

    public Comment addComment(Comment comment) {
        logger.info("添加评论, 用户ID: {}, 景点ID: {}", comment.getUserId(), comment.getScenicId());
        try {
            if (comment.getLikes() == null) {
                comment.setLikes(0);
            }
            int result = commentMapper.insert(comment);
            if (result <= 0) {
                throw new ServiceException("评论失败，请稍后重试");
            }
            logger.info("添加评论成功, 评论ID: {}", comment.getId());
            return comment;
        } catch (Exception e) {
            logger.error("添加评论失败", e);
            throw new ServiceException("评论失败，请稍后重试");
        }
    }

    public Comment getById(Integer id) {
        logger.info("根据ID获取评论, 评论ID: {}", id);
        try {
            Comment comment = commentMapper.selectById(id);
            if (comment == null) {
                throw new ServiceException("评论不存在");
            }
            logger.info("获取评论成功, 评论ID: {}", id);
            return comment;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("获取评论失败", e);
            throw new ServiceException("获取评论失败，请稍后重试");
        }
    }

    public PageResult<Comment> getPage(String content, Integer scenicId, Integer userId, String scenicName, String userName, Integer currentPage, Integer size) {
        logger.info("分页查询评论列表, 当前页: {}, 每页条数: {}", currentPage, size);
        try {
            int offset = (currentPage - 1) * size;
            List<Comment> records = commentMapper.selectPage(content, scenicId, userId, scenicName, userName, offset, size);
            Long total = commentMapper.count(content, scenicId, userId, scenicName, userName);
            logger.info("查询成功, 总条数: {}", total);
            return new PageResult<>(records, total, currentPage, size);
        } catch (Exception e) {
            logger.error("分页查询评论列表失败", e);
            throw new ServiceException("查询评论列表失败，请稍后重试");
        }
    }

    public void deleteById(Integer id) {
        logger.info("删除评论, 评论ID: {}", id);
        try {
            int result = commentMapper.deleteById(id);
            if (result <= 0) {
                throw new ServiceException("删除失败，评论不存在");
            }
            logger.info("删除评论成功, 评论ID: {}", id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("删除评论失败", e);
            throw new ServiceException("删除失败，请稍后重试");
        }
    }

    public void update(Comment comment) {
        logger.info("更新评论信息, 评论ID: {}", comment.getId());
        try {
            Comment dbComment = commentMapper.selectById(comment.getId());
            if (dbComment == null) {
                throw new ServiceException("评论不存在");
            }

            if (comment.getContent() != null) {
                dbComment.setContent(comment.getContent());
            }
            if (comment.getRating() != null) {
                dbComment.setRating(comment.getRating());
            }

            int result = commentMapper.update(dbComment);
            logger.info("更新评论信息结果: {}", result);
            logger.info("更新评论信息成功, 评论ID: {}", comment.getId());
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("更新评论信息失败", e);
            throw new ServiceException("更新失败，请稍后重试");
        }
    }

    public List<Comment> getAllByScenicId(Integer scenicId) {
        logger.info("获取景点所有评论, 景点ID: {}", scenicId);
        try {
            return commentMapper.selectByScenicId(scenicId);
        } catch (Exception e) {
            logger.error("获取景点评论失败", e);
            throw new ServiceException("获取评论失败，请稍后重试");
        }
    }

    public void incrementLikes(Integer id) {
        logger.info("点赞, 评论ID: {}", id);
        try {
            int result = commentMapper.incrementLikes(id);
            if (result <= 0) {
                throw new ServiceException("点赞失败");
            }
            logger.info("点赞成功, 评论ID: {}", id);
        } catch (Exception e) {
            logger.error("点赞失败", e);
            throw new ServiceException("点赞失败，请稍后重试");
        }
    }

    public void decrementLikes(Integer id) {
        logger.info("取消点赞, 评论ID: {}", id);
        try {
            int result = commentMapper.decrementLikes(id);
            if (result <= 0) {
                throw new ServiceException("取消点赞失败");
            }
            logger.info("取消点赞成功, 评论ID: {}", id);
        } catch (Exception e) {
            logger.error("取消点赞失败", e);
            throw new ServiceException("取消点赞失败，请稍后重试");
        }
    }
}