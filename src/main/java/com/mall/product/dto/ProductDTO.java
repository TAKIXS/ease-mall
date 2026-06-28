package com.mall.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {

    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    /** 商品描述（可选） */
    private String description;

    /** 封面图 URL */
    private String coverImage;

    /** 图片列表 JSON */
    private String images;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    private Integer stock;

    /** 规格参数 JSON */
    private String specs;

    /** 状态：0=下架, 1=上架 */
    private Integer status;
}
