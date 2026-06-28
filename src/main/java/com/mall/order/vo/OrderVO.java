package com.mall.order.vo;

import com.mall.order.entity.OrderItem;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情 VO — 包含订单本身 + 订单项列表
 */
@Data
public class OrderVO {

    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer status;

    /** 状态文字（前端直接展示，不用再转换） */
    private String statusText;

    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;

    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime completeTime;
    private LocalDateTime cancelTime;
    private LocalDateTime createTime;

    /** ★ 订单项列表（一个订单包含多个商品） */
    private List<OrderItem> items;

    /** 数字 → 中文 */
    public static String getStatusText(int status) {
        return switch (status) {
            case 1 -> "待支付";
            case 2 -> "已支付";
            case 3 -> "已发货";
            case 4 -> "已完成";
            case 5 -> "已取消";
            default -> "未知";
        };
    }
}
