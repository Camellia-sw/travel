package com.example.springboot.service;

import jakarta.annotation.Resource;

import com.example.springboot.common.PageResult;
import com.example.springboot.entity.ScenicCategory;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.ScenicCategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScenicCategoryService {
    private static final Logger logger = LoggerFactory.getLogger(ScenicCategoryService.class);

    @Resource
    private ScenicCategoryMapper scenicCategoryMapper;

    public PageResult<ScenicCategory> getPage(String name, Integer currentPage, Integer size) {
        logger.info("分页查询分类列表, 当前页: {}, 每页条数: {}", currentPage, size);
        try {
            int offset = (currentPage - 1) * size;
            List<ScenicCategory> records = scenicCategoryMapper.selectPage(name, offset, size);
            Long total = scenicCategoryMapper.count(name);
            logger.info("查询成功, 总条数: {}", total);
            return new PageResult<>(records, total, currentPage, size);
        } catch (Exception e) {
            logger.error("分页查询分类列表失败", e);
            throw new ServiceException("查询分类列表失败，请稍后重试");
        }
    }

    public ScenicCategory getById(Integer id) {
        logger.info("根据分类ID获取分类，分类ID: {}", id);
        try {
            ScenicCategory category = scenicCategoryMapper.selectById(id);
            if (category == null) {
                logger.error("分类不存在，分类ID: {}", id);
                throw new ServiceException("分类不存在");
            }
            logger.info("获取分类信息成功，分类ID: {}, 分类名称: {}", id, category.getName());
            return category;
        } catch (ServiceException e) {
            logger.error("获取分类信息失败，分类ID: {}, 错误信息: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("获取分类信息失败，分类ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw new ServiceException("获取分类信息失败，请稍后重试");
        }
    }

    public List<ScenicCategory> getCategoryTree() {
        logger.info("获取分类树");
        try {
            List<ScenicCategory> allCategories = scenicCategoryMapper.selectAll();
            return buildTree(allCategories, null);
        } catch (Exception e) {
            logger.error("获取分类树失败", e);
            throw new ServiceException("获取分类树失败，请稍后重试");
        }
    }

    private List<ScenicCategory> buildTree(List<ScenicCategory> categories, Integer parentId) {
        List<ScenicCategory> tree = new ArrayList<>();
        for (ScenicCategory category : categories) {
            boolean match = false;
            if (parentId == null) {
                match = (category.getParentId() == null || category.getParentId() == 0);
            } else {
                match = parentId.equals(category.getParentId());
            }
            if (match) {
                category.setChildren(buildTree(categories, category.getId()));
                tree.add(category);
            }
        }
        return tree;
    }

    public boolean addCategory(ScenicCategory category) {
        logger.info("新增分类，分类名称: {}", category.getName());
        try {
            int result = scenicCategoryMapper.insert(category);
            if (result <= 0) {
                logger.error("新增分类失败，分类名称: {}", category.getName());
                throw new ServiceException("新增失败，请稍后重试");
            }
            logger.info("新增分类成功，分类ID: {}, 分类名称: {}", category.getId(), category.getName());
            return true;
        } catch (ServiceException e) {
            logger.error("新增分类失败，分类名称: {}, 错误信息: {}", category.getName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("新增分类失败，分类名称: {}, 错误信息: {}", category.getName(), e.getMessage(), e);
            throw new ServiceException("新增失败，请稍后重试");
        }
    }

    public boolean updateCategory(ScenicCategory category) {
        logger.info("更新分类信息，分类ID: {}", category.getId());
        try {
            int result = scenicCategoryMapper.update(category);
            if (result <= 0) {
                throw new ServiceException("更新失败，分类不存在");
            }
            logger.info("更新分类信息成功，分类ID: {}", category.getId());
            return true;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("更新分类信息失败，分类ID: {}", category.getId(), e);
            throw new ServiceException("更新失败，请稍后重试");
        }
    }

    public boolean deleteCategory(Integer id) {
        logger.info("删除分类，分类ID: {}", id);
        try {
            Long childCount = scenicCategoryMapper.countChildren(id);
            if (childCount > 0) {
                logger.error("删除分类失败，该分类下有子分类，分类ID: {}", id);
                throw new ServiceException("删除失败，请确保该分类下没有子分类");
            }
            int result = scenicCategoryMapper.deleteById(id);
            if (result <= 0) {
                throw new ServiceException("删除失败，分类不存在");
            }
            logger.info("删除分类成功，分类ID: {}", id);
            return true;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("删除分类失败，分类ID: {}", id, e);
            throw new ServiceException("删除失败，请稍后重试");
        }
    }
}