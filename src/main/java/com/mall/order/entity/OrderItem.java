package com.mall.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项 — 一个订单包含多个商品
 *
 * ★ 快照设计：下单时把商品名称、价格、图片存下来，
 *   之后商品改名/涨价，查看历史订单看到的还是下单时的信息。
 */
@Data
@TableName("order_item")
public class OrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属订单ID */
    private Long orderId;

    /** 商品ID */
    private Long productId;

    /** 商品名称（快照） */
    private String productName;

    /** 商品图片（快照） */
    private String productImage;

    /** 购买时的单价（快照） */
    private BigDecimal price;

    /** 购买数量 */
    private Integer quantity;

    private LocalDateTime createTime;
}
