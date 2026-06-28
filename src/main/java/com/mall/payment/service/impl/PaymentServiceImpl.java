package com.mall.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.exception.BusinessException;
import com.mall.order.entity.Order;
import com.mall.order.mapper.OrderMapper;
import com.mall.order.service.OrderService;
import com.mall.payment.dto.PayDTO;
import com.mall.payment.entity.PaymentRecord;
import com.mall.payment.mapper.PaymentRecordMapper;
import com.mall.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * ★ 模拟支付服务
 *
 * 真实场景下，这里会调用支付宝/微信的 SDK 发起支付请求。
 * 本项目是学习项目，所以直接模拟支付成功。
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRecordMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final OrderService orderService;

    // ==================== ★ 模拟支付 ====================
    @Override
    @Transactional
    public PaymentRecord pay(Long userId, PayDTO dto) {
        // 1. 查订单是否存在
        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null) throw new BusinessException("订单不存在");
        if (!order.getUserId().equals(userId)) throw new BusinessException("无权操作他人订单");
        if (order.getStatus() != 1) throw new BusinessException("订单状态不正确，无法支付");

        // 2. 创建支付记录
        PaymentRecord record = new PaymentRecord();
        record.setOrderId(order.getId());
        record.setOrderNo(order.getOrderNo());
        record.setUserId(userId);
        record.setAmount(order.getPayAmount());
        record.setPayMethod(dto.getPayMethod());
        record.setStatus(2);            // ★ 模拟：直接支付成功
        record.setTradeNo("TRD" + UUID.randomUUID().toString().replace("-", "").substring(0, 20));
        record.setPayTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        paymentMapper.insert(record);

        // 3. ★ 更新订单状态 → 已支付
        orderService.pay(order.getId());

        return record;
    }

    // ==================== 查用户支付记录 ====================
    @Override
    public List<PaymentRecord> listByUser(Long userId) {
        return paymentMapper.selectList(
                new LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getUserId, userId)
                        .orderByDesc(PaymentRecord::getCreateTime)
        );
    }

    // ==================== 查订单支付记录 ====================
    @Override
    public PaymentRecord getByOrderId(Long orderId) {
        return paymentMapper.selectOne(
                new LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getOrderId, orderId)
        );
    }
}
