package com.mall.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录 — 对应 payment_record 表
 */
@Data
@TableName("payment_record")
public class PaymentRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单ID */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;

    /** 订单编号 */
    private String orderNo;

    /** 用户ID */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /** 支付金额 */
    private BigDecimal amount;

    /** 支付方式：ALIPAY / WEIXIN / BANK_CARD */
    private String payMethod;

    /** 状态：1=待支付, 2=成功, 3=失败 */
    private Integer status;

    /** 交易流水号 */
    private String tradeNo;

    /** 支付时间 */
    private LocalDateTime payTime;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
