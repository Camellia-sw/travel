package com.example.springboot.service;

import jakarta.annotation.Resource;

import com.example.springboot.common.PageResult;
import com.example.springboot.entity.Order;
import com.example.springboot.entity.Ticket;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.OrderMapper;
import com.example.springboot.mapper.TicketMapper;
import com.example.springboot.util.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private TicketMapper ticketMapper;

    @Transactional
    public Order createOrder(Order order) {
        logger.info("创建订单，用户ID: {}, 门票ID: {}", order.getUserId(), order.getTicketId());
        try {
            // 生成订单编号
            String orderNo = "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            order.setOrderNo(orderNo);

            // 查询门票信息
            Ticket ticket = ticketMapper.selectById(order.getTicketId());
            if (ticket == null) {
                throw new ServiceException("门票不存在");
            }

            // 检查库存
            if (ticket.getStock() < order.getQuantity()) {
                throw new ServiceException("库存不足");
            }

            // 计算订单金额
            BigDecimal price = ticket.getDiscountPrice() != null ? ticket.getDiscountPrice() : ticket.getPrice();
            order.setTotalAmount(price.multiply(new BigDecimal(order.getQuantity())));

            // 设置订单状态为待支付
            order.setStatus(0);

            int result = orderMapper.insert(order);
            if (result <= 0) {
                throw new ServiceException("创建订单失败，请稍后重试");
            }

            // 扣减库存
            ticketMapper.updateStock(order.getTicketId(), -order.getQuantity());

            logger.info("创建订单成功，订单ID: {}, 订单号: {}", order.getId(), order.getOrderNo());
            return order;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("创建订单失败", e);
            throw new ServiceException("创建订单失败，请稍后重试");
        }
    }

    @Transactional
    public void payOrder(Integer id, String paymentMethod) {
        logger.info("支付订单，订单ID: {}, 支付方式: {}", id, paymentMethod);
        try {
            Order order = orderMapper.selectById(id);
            if (order == null) {
                throw new ServiceException("订单不存在");
            }
            if (order.getStatus() != 0) {
                throw new ServiceException("订单状态不正确，无法支付");
            }

            order.setStatus(1);
            order.setPaymentTime(LocalDateTime.now());
            order.setPaymentMethod(paymentMethod);

            int result = orderMapper.updateStatus(order);
            if (result <= 0) {
                throw new ServiceException("支付失败，请稍后重试");
            }

            logger.info("支付订单成功，订单ID: {}", id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("支付订单失败", e);
            throw new ServiceException("支付失败，请稍后重试");
        }
    }

    @Transactional
    public void cancelOrder(Integer id) {
        logger.info("取消订单，订单ID: {}", id);
        try {
            Order order = orderMapper.selectById(id);
            if (order == null) {
                throw new ServiceException("订单不存在");
            }
            if (order.getStatus() != 0 && order.getStatus() != 1) {
                throw new ServiceException("订单状态不正确，无法取消");
            }

            int result = orderMapper.updateStatusOnly(id, 2);
            if (result <= 0) {
                throw new ServiceException("取消订单失败，请稍后重试");
            }

            // 恢复库存
            ticketMapper.updateStock(order.getTicketId(), order.getQuantity());

            logger.info("取消订单成功，订单ID: {}", id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("取消订单失败", e);
            throw new ServiceException("取消订单失败，请稍后重试");
        }
    }

    @Transactional
    public void refundOrder(Integer id) {
        logger.info("退款订单，订单ID: {}", id);
        try {
            Order order = orderMapper.selectById(id);
            if (order == null) {
                throw new ServiceException("订单不存在");
            }
            if (order.getStatus() != 1) {
                throw new ServiceException("订单状态不正确，无法退款");
            }

            int result = orderMapper.updateStatusOnly(id, 3);
            if (result <= 0) {
                throw new ServiceException("退款失败，请稍后重试");
            }

            // 恢复库存
            ticketMapper.updateStock(order.getTicketId(), order.getQuantity());

            logger.info("退款订单成功，订单ID: {}", id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("退款订单失败", e);
            throw new ServiceException("退款失败，请稍后重试");
        }
    }

    @Transactional
    public void completeOrder(Integer id) {
        logger.info("完成订单，订单ID: {}", id);
        try {
            Order order = orderMapper.selectById(id);
            if (order == null) {
                throw new ServiceException("订单不存在");
            }
            if (order.getStatus() != 1) {
                throw new ServiceException("订单状态不正确，无法完成");
            }

            int result = orderMapper.updateStatusOnly(id, 4);
            if (result <= 0) {
                throw new ServiceException("完成订单失败，请稍后重试");
            }

            logger.info("完成订单成功，订单ID: {}", id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("完成订单失败", e);
            throw new ServiceException("完成订单失败，请稍后重试");
        }
    }

    public PageResult<Order> getPage(String orderNo, String visitorName, String visitorPhone, Integer status, Integer currentPage, Integer size) {
        logger.info("分页查询订单列表, 当前页: {}, 每页条数: {}", currentPage, size);
        try {
            int offset = (currentPage - 1) * size;
            List<Order> records = orderMapper.selectPage(orderNo, visitorName, visitorPhone, status, offset, size);
            // 填充门票名称
            for (Order order : records) {
                if (order.getTicketId() != null) {
                    Ticket ticket = ticketMapper.selectById(order.getTicketId());
                    if (ticket != null) {
                        order.setTicketName(ticket.getTicketName());
                    }
                }
            }
            Long total = orderMapper.count(orderNo, visitorName, visitorPhone, status);
            logger.info("查询成功, 总条数: {}", total);
            return new PageResult<>(records, total, currentPage, size);
        } catch (Exception e) {
            logger.error("分页查询订单列表失败", e);
            throw new ServiceException("查询订单列表失败，请稍后重试");
        }
    }

    public PageResult<Order> getCurrentUserOrders(Integer status, Integer currentPage, Integer size) {
        User currentUser = JwtTokenUtils.getCurrentUser();
        if (currentUser == null) {
            throw new ServiceException("请先登录");
        }
        logger.info("查询当前用户订单列表, 用户ID: {}, 当前页: {}, 每页条数: {}", currentUser.getId(), currentPage, size);
        try {
            int offset = (currentPage - 1) * size;
            List<Order> records = orderMapper.selectByUserId(currentUser.getId(), status, offset, size);
            // 填充门票名称
            for (Order order : records) {
                if (order.getTicketId() != null) {
                    Ticket ticket = ticketMapper.selectById(order.getTicketId());
                    if (ticket != null) {
                        order.setTicketName(ticket.getTicketName());
                    }
                }
            }
            Long total = orderMapper.countByUserId(currentUser.getId(), status);
            logger.info("查询成功, 总条数: {}", total);
            return new PageResult<>(records, total, currentPage, size);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("查询当前用户订单列表失败", e);
            throw new ServiceException("查询订单列表失败，请稍后重试");
        }
    }

    public Order getById(Integer id) {
        logger.info("根据ID获取订单，订单ID: {}", id);
        try {
            Order order = orderMapper.selectById(id);
            if (order == null) {
                throw new ServiceException("订单不存在");
            }
            logger.info("获取订单成功，订单ID: {}", id);
            return order;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("获取订单失败，订单ID: {}", id, e);
            throw new ServiceException("获取订单失败，请稍后重试");
        }
    }

    public Order getByOrderNo(String orderNo) {
        logger.info("根据订单号获取订单，订单号: {}", orderNo);
        try {
            Order order = orderMapper.selectByOrderNo(orderNo);
            if (order == null) {
                throw new ServiceException("订单不存在");
            }
            logger.info("获取订单成功，订单号: {}", orderNo);
            return order;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("获取订单失败，订单号: {}", orderNo, e);
            throw new ServiceException("获取订单失败，请稍后重试");
        }
    }

    public void deleteById(Integer id) {
        logger.info("删除订单，订单ID: {}", id);
        try {
            int result = orderMapper.deleteById(id);
            if (result <= 0) {
                throw new ServiceException("删除失败，订单不存在");
            }
            logger.info("删除订单成功，订单ID: {}", id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("删除订单失败，订单ID: {}", id, e);
            throw new ServiceException("删除失败，请稍后重试");
        }
    }
}