package com.mall.order.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mall.common.exception.BusinessException;
import com.mall.order.dto.CartItemDTO;
import com.mall.order.entity.OrderItem;
import com.mall.order.service.CartService;
import com.mall.product.entity.Product;
import com.mall.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 购物车服务 — Redis Hash 持久化
 *
 * Redis 结构:
 *   Key:   cart:{userId}
 *   Field: productId
 *   Value: JSON(CartItemDTO)
 *
 * 重启不丢失数据（Redis appendonly 已开启）。
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private static final String CART_PREFIX = "cart:";

    private final StringRedisTemplate redisTemplate;
    private final ProductService productService;

    // ==================== 添加到购物车 ====================
    @Override
    public void addItem(Long userId, CartItemDTO dto) {
        productService.getById(dto.getProductId()); // 验证商品存在
        String key = CART_PREFIX + userId;
        String field = String.valueOf(dto.getProductId());

        // 已有 → 叠加数量
        String existingJson = (String) redisTemplate.opsForHash().get(key, field);
        if (StrUtil.isNotBlank(existingJson)) {
            CartItemDTO existing = JSONUtil.toBean(existingJson, CartItemDTO.class);
            existing.setQuantity(existing.getQuantity() + dto.getQuantity());
            redisTemplate.opsForHash().put(key, field, JSONUtil.toJsonStr(existing));
        } else {
            redisTemplate.opsForHash().put(key, field, JSONUtil.toJsonStr(dto));
        }
    }

    // ==================== 获取购物车列表 ====================
    @Override
    public List<OrderItem> getItems(Long userId) {
        String key = CART_PREFIX + userId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) return List.of();

        List<OrderItem> items = new ArrayList<>();
        List<String> invalidFields = new ArrayList<>();

        for (Object field : entries.keySet()) {
            CartItemDTO dto = JSONUtil.toBean((String) entries.get(field), CartItemDTO.class);
            try {
                Product product = productService.getById(dto.getProductId());
                OrderItem item = new OrderItem();
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setProductImage(product.getCoverImage());
                item.setPrice(product.getPrice());
                item.setQuantity(dto.getQuantity());
                items.add(item);
            } catch (Exception e) {
                invalidFields.add((String) field); // 商品已下架
            }
        }
        // 清理失效商品
        invalidFields.forEach(f -> redisTemplate.opsForHash().delete(key, f));
        return items;
    }

    // ==================== 修改数量 ====================
    @Override
    public void updateQuantity(Long userId, Long productId, int quantity) {
        String key = CART_PREFIX + userId;
        String field = String.valueOf(productId);
        String json = (String) redisTemplate.opsForHash().get(key, field);
        if (StrUtil.isBlank(json)) throw new BusinessException("购物车中没有该商品");
        CartItemDTO dto = JSONUtil.toBean(json, CartItemDTO.class);
        dto.setQuantity(quantity);
        redisTemplate.opsForHash().put(key, field, JSONUtil.toJsonStr(dto));
    }

    // ==================== 移除商品 ====================
    @Override
    public void removeItem(Long userId, Long productId) {
        redisTemplate.opsForHash().delete(CART_PREFIX + userId, String.valueOf(productId));
    }

    // ==================== 清空购物车 ====================
    @Override
    public void clear(Long userId) {
        redisTemplate.delete(CART_PREFIX + userId);
    }
}
