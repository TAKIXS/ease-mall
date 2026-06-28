package com.mall.payment.service;

import com.mall.payment.dto.PayDTO;
import com.mall.payment.entity.PaymentRecord;

import java.util.List;

public interface PaymentService {

    /** 模拟支付 */
    PaymentRecord pay(Long userId, PayDTO dto);

    /** 查某用户的所有支付记录 */
    List<PaymentRecord> listByUser(Long userId);

    /** 查某订单的支付记录 */
    PaymentRecord getByOrderId(Long orderId);
}
