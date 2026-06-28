package com.mall.order.service;

import com.mall.order.dto.CartItemDTO;
import com.mall.order.entity.OrderItem;
import java.util.List;

public interface CartService {

    /** 添加商品到购物车 */
    void addItem(Long userId, CartItemDTO dto);

    /** 获取购物车中的商品列表 */
    List<OrderItem> getItems(Long userId);

    /** 修改商品数量 */
    void updateQuantity(Long userId, Long productId, int quantity);

    /** 从购物车移除商品 */
    void removeItem(Long userId, Long productId);

    /** 清空购物车（下单后调用） */
    void clear(Long userId);
}
