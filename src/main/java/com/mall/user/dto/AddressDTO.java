package com.mall.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 地址请求 DTO — 新增/修改地址时前端传的数据
 *
 * @NotBlank 注解：字段不能为 null，也不能全是空格
 * 在 Controller 上用 @Valid 就能触发校验，不合法会返回 400 错误
 */
@Data
public class AddressDTO {

    @NotBlank(message = "收件人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "手机号不能为空")
    private String receiverPhone;

    @NotBlank(message = "省份不能为空")
    private String province;

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "区/县不能为空")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    private String detail;

    /** 是否设为默认地址（0=否, 1=是） */
    private Integer isDefault;
}
