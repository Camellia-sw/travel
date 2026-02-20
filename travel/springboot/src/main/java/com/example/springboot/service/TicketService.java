package com.example.springboot.service;

import jakarta.annotation.Resource;

import com.example.springboot.common.PageResult;
import com.example.springboot.entity.Ticket;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.TicketMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Resource
    private TicketMapper ticketMapper;

    public PageResult<Ticket> getPage(String ticketName, String ticketType, Integer scenicId, Integer currentPage, Integer size) {
        logger.info("分页查询门票列表, 当前页: {}, 每页条数: {}", currentPage, size);
        try {
            int offset = (currentPage - 1) * size;
            List<Ticket> records = ticketMapper.selectPage(ticketName, ticketType, scenicId, offset, size);
            Long total = ticketMapper.count(ticketName, ticketType, scenicId);
            logger.info("查询成功, 总条数: {}", total);
            return new PageResult<>(records, total, currentPage, size);
        } catch (Exception e) {
            logger.error("分页查询门票列表失败", e);
            throw new ServiceException("查询门票列表失败，请稍后重试");
        }
    }

    public Ticket getById(Integer id) {
        logger.info("根据门票ID获取门票，门票ID: {}", id);
        try {
            Ticket ticket = ticketMapper.selectById(id);
            if (ticket == null) {
                logger.error("门票不存在，门票ID: {}", id);
                throw new ServiceException("门票不存在");
            }
            logger.info("获取门票信息成功，门票ID: {}, 门票名称: {}", id, ticket.getTicketName());
            return ticket;
        } catch (ServiceException e) {
            logger.error("获取门票信息失败，门票ID: {}, 错误信息: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("获取门票信息失败，门票ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw new ServiceException("获取门票信息失败，请稍后重试");
        }
    }

    public List<Ticket> getByScenicId(Integer scenicId) {
        logger.info("根据景点ID获取门票列表，景点ID: {}", scenicId);
        try {
            List<Ticket> list = ticketMapper.selectByScenicId(scenicId);
            logger.info("查询成功，门票数量: {}", list.size());
            return list;
        } catch (Exception e) {
            logger.error("根据景点ID获取门票列表失败", e);
            throw new ServiceException("查询失败，请稍后重试");
        }
    }

    public void createTicket(Ticket ticket) {
        logger.info("新增门票，门票名称: {}", ticket.getTicketName());
        try {
            int result = ticketMapper.insert(ticket);
            if (result <= 0) {
                logger.error("新增门票失败，门票名称: {}", ticket.getTicketName());
                throw new ServiceException("新增失败，请稍后重试");
            }
            logger.info("新增门票成功，门票ID: {}, 门票名称: {}", ticket.getId(), ticket.getTicketName());
        } catch (ServiceException e) {
            logger.error("新增门票失败，门票名称: {}, 错误信息: {}", ticket.getTicketName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("新增门票失败，门票名称: {}, 错误信息: {}", ticket.getTicketName(), e.getMessage(), e);
            throw new ServiceException("新增失败，请稍后重试");
        }
    }

    public void updateTicket(Integer id, Ticket ticket) {
        logger.info("更新门票信息，门票ID: {}", id);
        try {
            // 先从数据库获取现有的门票信息
            Ticket dbTicket = ticketMapper.selectById(id);
            if (dbTicket == null) {
                throw new ServiceException("门票不存在");
            }

            // 只更新传入的非 null 字段
            if (ticket.getScenicId() != null) {
                dbTicket.setScenicId(ticket.getScenicId());
            }
            if (ticket.getTicketName() != null) {
                dbTicket.setTicketName(ticket.getTicketName());
            }
            if (ticket.getPrice() != null) {
                dbTicket.setPrice(ticket.getPrice());
            }
            if (ticket.getDiscountPrice() != null) {
                dbTicket.setDiscountPrice(ticket.getDiscountPrice());
            }
            if (ticket.getTicketType() != null) {
                dbTicket.setTicketType(ticket.getTicketType());
            }
            if (ticket.getValidPeriod() != null) {
                dbTicket.setValidPeriod(ticket.getValidPeriod());
            }
            if (ticket.getDescription() != null) {
                dbTicket.setDescription(ticket.getDescription());
            }
            if (ticket.getStock() != null) {
                dbTicket.setStock(ticket.getStock());
            }
            if (ticket.getStatus() != null) {
                dbTicket.setStatus(ticket.getStatus());
            }

            // 使用完整的 dbTicket 进行更新
            int result = ticketMapper.update(dbTicket);
            logger.info("更新门票信息结果: {}", result);
            logger.info("更新门票信息成功，门票ID: {}", id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("更新门票信息失败，门票ID: {}", id, e);
            throw new ServiceException("更新失败，请稍后重试");
        }
    }

    public void deleteTicket(Integer id) {
        logger.info("删除门票，门票ID: {}", id);
        try {
            int result = ticketMapper.deleteById(id);
            if (result <= 0) {
                throw new ServiceException("删除失败，门票不存在");
            }
            logger.info("删除门票成功，门票ID: {}", id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("删除门票失败，门票ID: {}", id, e);
            throw new ServiceException("删除失败，请稍后重试");
        }
    }

    public List<Ticket> getAll() {
        logger.info("获取所有门票");
        try {
            List<Ticket> list = ticketMapper.selectAll();
            logger.info("查询成功，门票数量: {}", list.size());
            return list;
        } catch (Exception e) {
            logger.error("获取所有门票失败", e);
            throw new ServiceException("查询失败，请稍后重试");
        }
    }
}