package com.mall.order.service.impl;

import com.mall.common.exception.BusinessException;
import com.mall.order.dto.CartItemDTO;
import com.mall.order.entity.OrderItem;
import com.mall.order.service.CartService;
import com.mall.product.entity.Product;
import com.mall.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 购物车服务（内存版）
 *
 * 当前用 ConcurrentHashMap 模拟，重启后数据会丢失。
 * 生产环境应换成 Redis Hash：
 *   key   = "cart:" + userId
 *   field = productId
 *   value = JSON(cartItem)
 *
 * 数据结构：
 *   Map<userId, Map<productId, CartItemDTO>>
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    /** 模拟 Redis 的存储 */
    private final Map<Long, Map<Long, CartItemDTO>> cartStore = new ConcurrentHashMap<>();

    private final ProductService productService;

    // ==================== 添加到购物车 ====================
    @Override
    public void addItem(Long userId, CartItemDTO dto) {
        // 验证商品存在
        Product product = productService.getById(dto.getProductId());

        // 获取该用户的购物车（没有就建新的）
        Map<Long, CartItemDTO> userCart = cartStore.get(userId);
        if (userCart == null) {
            userCart = new ConcurrentHashMap<>();
            cartStore.put(userId, userCart);
        }

        // 如果购物车已有这个商品，数量叠加；否则新增
        CartItemDTO existing = userCart.get(dto.getProductId());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + dto.getQuantity());
        } else {
            userCart.put(dto.getProductId(), dto);
        }
    }

    // ==================== 获取购物车列表 ====================
    @Override
    public List<OrderItem> getItems(Long userId) {
        Map<Long, CartItemDTO> userCart = cartStore.get(userId);
        if (userCart == null || userCart.isEmpty()) {
            return List.of();  // 空购物车，返回空列表
        }

        // 把 CartItemDTO 转成 OrderItem，跳过已下架/不存在的商品
        List<OrderItem> items = new ArrayList<>();
        List<Long> invalidIds = new ArrayList<>();
        for (CartItemDTO dto : userCart.values()) {
            Product product;
            try {
                product = productService.getById(dto.getProductId());
            } catch (Exception e) {
                invalidIds.add(dto.getProductId());  // 商品不存在，标记删除
                continue;
            }

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getCoverImage());
            item.setPrice(product.getPrice());
            item.setQuantity(dto.getQuantity());
            items.add(item);
        }
        // ★ 清理已失效的商品
        invalidIds.forEach(userCart::remove);
        return items;
    }

    // ==================== 修改数量 ====================
    @Override
    public void updateQuantity(Long userId, Long productId, int quantity) {
        Map<Long, CartItemDTO> userCart = cartStore.get(userId);
        if (userCart == null || !userCart.containsKey(productId)) {
            throw new BusinessException("购物车中没有该商品");
        }
        userCart.get(productId).setQuantity(quantity);
    }

    // ==================== 移除商品 ====================
    @Override
    public void removeItem(Long userId, Long productId) {
        Map<Long, CartItemDTO> userCart = cartStore.get(userId);
        if (userCart != null) {
            userCart.remove(productId);
        }
    }

    // ==================== 清空购物车 ====================
    @Override
    public void clear(Long userId) {
        cartStore.remove(userId);
    }
}
