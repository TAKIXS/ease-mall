package com.mall.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayDTO {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /** 支付方式，默认 ALIPAY */
    @NotBlank(message = "支付方式不能为空")
    private String payMethod;
}
