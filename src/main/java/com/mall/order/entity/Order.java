package com.mall.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体 — 对应 orders 表
 *
 * 订单状态机（记住这个流转规则）：
 *   1 待支付 → 2 已支付 → 3 已发货 → 4 已完成
 *   ↓                    ↓
 *   5 已取消             5 已取消（退款）
 */
@Data
@TableName("orders")
public class Order {

    /** 订单ID — 雪花算法生成，JS 精度不够，序列化为 String */
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /** 订单编号（展示用，如 ORD20240628001） */
    private String orderNo;

    /** 下单用户ID */
    private Long userId;

    /** 订单总金额 */
    private BigDecimal totalAmount;

    /** 实付金额 */
    private BigDecimal payAmount;

    /** 订单状态：1=待支付 2=已支付 3=已发货 4=已完成 5=已取消 */
    private Integer status;

    /** 收件人信息（下单时从地址快照过来） */
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;

    /** 用户备注 */
    private String remark;

    /** 各状态时间节点 */
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime completeTime;
    private LocalDateTime cancelTime;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
