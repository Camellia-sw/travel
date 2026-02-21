package com.example.springboot.service;

import com.example.springboot.common.PageResult;
import com.example.springboot.entity.Accommodation;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.AccommodationMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccommodationService {
    private static final Logger logger = LoggerFactory.getLogger(AccommodationService.class);

    @Resource
    private AccommodationMapper accommodationMapper;

    public PageResult<Accommodation> getPage(String name, Integer scenicId, String type, Integer currentPage, Integer size) {
        logger.info("分页查询住宿列表, 当前页: {}, 每页条数: {}", currentPage, size);
        try {
            int offset = (currentPage - 1) * size;
            List<Accommodation> records = accommodationMapper.selectPage(name, scenicId, type, offset, size);
            Long total = accommodationMapper.count(name, scenicId, type);
            logger.info("查询成功, 总条数: {}", total);
            return new PageResult<>(records, total, currentPage, size);
        } catch (Exception e) {
            logger.error("分页查询住宿列表失败", e);
            throw new ServiceException("查询住宿列表失败，请稍后重试");
        }
    }

    public Accommodation getById(Integer id) {
        logger.info("根据ID获取住宿，ID: {}", id);
        try {
            Accommodation accommodation = accommodationMapper.selectById(id);
            if (accommodation == null) {
                logger.error("住宿不存在，ID: {}", id);
                throw new ServiceException("住宿不存在");
            }
            logger.info("获取住宿信息成功，ID: {}, 名称: {}", id, accommodation.getName());
            return accommodation;
        } catch (ServiceException e) {
            logger.error("获取住宿信息失败，ID: {}, 错误信息: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("获取住宿信息失败，ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw new ServiceException("获取住宿信息失败，请稍后重试");
        }
    }

    public void add(Accommodation accommodation) {
        logger.info("添加住宿信息，名称: {}", accommodation.getName());
        try {
            int result = accommodationMapper.insert(accommodation);
            if (result <= 0) {
                logger.error("添加住宿信息失败，名称: {}", accommodation.getName());
                throw new ServiceException("添加住宿信息失败，请稍后重试");
            }
            logger.info("添加住宿信息成功，ID: {}, 名称: {}", accommodation.getId(), accommodation.getName());
        } catch (ServiceException e) {
            logger.error("添加住宿信息失败，名称: {}, 错误信息: {}", accommodation.getName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("添加住宿信息失败，名称: {}, 错误信息: {}", accommodation.getName(), e.getMessage(), e);
            throw new ServiceException("添加住宿信息失败，请稍后重试");
        }
    }

    public void update(Accommodation accommodation) {
        logger.info("更新住宿信息，ID: {}", accommodation.getId());
        try {
            Accommodation dbAccommodation = accommodationMapper.selectById(accommodation.getId());
            if (dbAccommodation == null) {
                throw new ServiceException("住宿不存在");
            }

            if (accommodation.getName() != null) {
                dbAccommodation.setName(accommodation.getName());
            }
            if (accommodation.getType() != null) {
                dbAccommodation.setType(accommodation.getType());
            }
            if (accommodation.getAddress() != null) {
                dbAccommodation.setAddress(accommodation.getAddress());
            }
            if (accommodation.getScenicId() != null) {
                dbAccommodation.setScenicId(accommodation.getScenicId());
            }
            if (accommodation.getDescription() != null) {
                dbAccommodation.setDescription(accommodation.getDescription());
            }
            if (accommodation.getContactPhone() != null) {
                dbAccommodation.setContactPhone(accommodation.getContactPhone());
            }
            if (accommodation.getPriceRange() != null) {
                dbAccommodation.setPriceRange(accommodation.getPriceRange());
            }
            if (accommodation.getStarLevel() != null) {
                dbAccommodation.setStarLevel(accommodation.getStarLevel());
            }
            if (accommodation.getImageUrl() != null) {
                dbAccommodation.setImageUrl(accommodation.getImageUrl());
            }
            if (accommodation.getImageList() != null) {
                dbAccommodation.setImageList(accommodation.getImageList());
            }
            if (accommodation.getFeatures() != null) {
                dbAccommodation.setFeatures(accommodation.getFeatures());
            }
            if (accommodation.getDistance() != null) {
                dbAccommodation.setDistance(accommodation.getDistance());
            }

            int result = accommodationMapper.update(dbAccommodation);
            logger.info("更新住宿信息结果: {}", result);
            logger.info("更新住宿信息成功，ID: {}", accommodation.getId());
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("更新住宿信息失败，ID: {}", accommodation.getId(), e);
            throw new ServiceException("更新失败，请稍后重试");
        }
    }

    public void deleteById(Integer id) {
        logger.info("删除住宿，ID: {}", id);
        try {
            int result = accommodationMapper.deleteById(id);
            if (result <= 0) {
                throw new ServiceException("删除失败，住宿不存在");
            }
            logger.info("删除住宿成功，ID: {}", id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("删除住宿失败，ID: {}", id, e);
            throw new ServiceException("删除失败，请稍后重试");
        }
    }

    public List<String> getTypes() {
        logger.info("获取住宿类型列表");
        try {
            List<String> types = accommodationMapper.selectTypes();
            logger.info("获取住宿类型列表成功，类型数量: {}", types.size());
            return types;
        } catch (Exception e) {
            logger.error("获取住宿类型列表失败", e);
            throw new ServiceException("获取住宿类型列表失败，请稍后重试");
        }
    }
}
