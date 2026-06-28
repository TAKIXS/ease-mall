package com.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.exception.BusinessException;
import com.mall.common.utils.SnowflakeIdGenerator;
import com.mall.order.dto.OrderCreateDTO;
import com.mall.order.entity.Order;
import com.mall.order.entity.OrderItem;
import com.mall.order.mapper.OrderItemMapper;
import com.mall.order.mapper.OrderMapper;
import com.mall.order.service.CartService;
import com.mall.order.service.OrderService;
import com.mall.order.vo.OrderVO;
import com.mall.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartService cartService;
    private final ProductService productService;

    /** 雪花ID生成器（workerId=1, datacenterId=1） */
    private final SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);

    // ==================== ★ 下单（核心流程） ====================
    @Override
    @Transactional  // ★ 事务：扣库存、创建订单、创建订单项、清空购物车，任何一步失败全部回滚
    public OrderVO create(Long userId, OrderCreateDTO dto) {
        // 1. 获取购物车内容
        List<OrderItem> cartItems = cartService.getItems(userId);
        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车为空，无法下单");
        }

        // 2. 计算总金额，同时扣减库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : cartItems) {
            // ★ 扣库存（失败会抛异常，事务回滚）
            productService.deductStock(item.getProductId(), item.getQuantity());
            // 累加金额：单价 × 数量
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // 3. 生成订单ID 和 订单编号
        long orderId = idGenerator.nextId();
        String orderNo = generateOrderNo();

        // 4. 创建订单
        Order order = new Order();
        order.setId(orderId);
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);          // 实付 = 总金额（无优惠）
        order.setStatus(1);                        // 1 = 待支付
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setReceiverAddress(dto.getReceiverAddress());
        order.setRemark(dto.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);

        // 5. 创建订单项（每条订单项关联订单ID）
        for (OrderItem item : cartItems) {
            item.setOrderId(orderId);
            item.setCreateTime(LocalDateTime.now());
            orderItemMapper.insert(item);
        }

        // 6. 清空购物车
        cartService.clear(userId);

        // 7. 返回订单详情
        return buildVO(order, cartItems);
    }

    // ==================== 用户查自己的订单 ====================
    @Override
    public Page<Order> listByUser(Long userId, int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
               .orderByDesc(Order::getCreateTime);
        return orderMapper.selectPage(pageParam, wrapper);
    }

    // ==================== 查所有订单（管理端） ====================
    @Override
    public Page<Order> listAll(int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Order::getCreateTime);
        return orderMapper.selectPage(pageParam, wrapper);
    }

    // ==================== 订单详情 ====================
    @Override
    public OrderVO getDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        // 查订单项
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId)
        );
        return buildVO(order, items);
    }

    // ==================== 取消订单 ====================
    @Override
    @Transactional
    public void cancel(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (!order.getUserId().equals(userId)) throw new BusinessException("无权操作他人订单");
        if (order.getStatus() != 1) throw new BusinessException("只有待支付订单才能取消");

        order.setStatus(5);  // 5 = 已取消
        order.setCancelTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // ★ 回滚库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );
        for (OrderItem item : items) {
            productService.rollbackStock(item.getProductId(), item.getQuantity());
        }
    }

    // ==================== 支付 ====================
    @Override
    public void pay(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 1) throw new BusinessException("订单状态不正确，无法支付");

        order.setStatus(2);  // 2 = 已支付
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    // ==================== 发货 ====================
    @Override
    public void ship(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 2) throw new BusinessException("只有已支付订单才能发货");

        order.setStatus(3);  // 3 = 已发货
        order.setShipTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    // ==================== 完成 ====================
    @Override
    public void complete(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (order.getStatus() != 3) throw new BusinessException("只有已发货订单才能完成");

        order.setStatus(4);  // 4 = 已完成
        order.setCompleteTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    // ==================== 私有方法 ====================

    /** 生成订单编号 ORD20240628153045xxx */
    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD" + dateStr + (System.currentTimeMillis() % 1000);
    }

    /** 组装 OrderVO */
    private OrderVO buildVO(Order order, List<OrderItem> items) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setStatus(order.getStatus());
        vo.setStatusText(OrderVO.getStatusText(order.getStatus()));
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setRemark(order.getRemark());
        vo.setPayTime(order.getPayTime());
        vo.setShipTime(order.getShipTime());
        vo.setCompleteTime(order.getCompleteTime());
        vo.setCancelTime(order.getCancelTime());
        vo.setCreateTime(order.getCreateTime());
        vo.setItems(items);
        return vo;
    }
}
