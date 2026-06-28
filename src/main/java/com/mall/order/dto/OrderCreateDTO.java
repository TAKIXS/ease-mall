package com.mall.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 下单请求 — 选择地址 + 提交订单
 */
@Data
public class OrderCreateDTO {

    @NotBlank(message = "收件人不能为空")
    private String receiverName;

    @NotBlank(message = "手机号不能为空")
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;

    /** 备注（可选） */
    private String remark;
}
