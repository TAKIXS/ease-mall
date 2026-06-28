package com.mall.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.order.dto.OrderCreateDTO;
import com.mall.order.entity.Order;
import com.mall.order.vo.OrderVO;

public interface OrderService {

    /** 下单 */
    OrderVO create(Long userId, OrderCreateDTO dto);

    /** 用户查自己的订单（分页） */
    Page<Order> listByUser(Long userId, int page, int size);

    /** 查所有订单（管理端） */
    Page<Order> listAll(int page, int size);

    /** 订单详情（含订单项） */
    OrderVO getDetail(Long orderId);

    /** 取消订单（待支付状态才能取消） */
    void cancel(Long orderId, Long userId);

    /** 支付订单 */
    void pay(Long orderId);

    /** 发货 */
    void ship(Long orderId);

    /** 完成订单 */
    void complete(Long orderId);
}
