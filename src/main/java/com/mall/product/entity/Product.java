package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体 — 对应 product 表
 *
 * 金额用 BigDecimal 而不是 float/double，因为：
 *   float 有精度问题（0.1 + 0.2 != 0.3），
 *   BigDecimal 精确无误差，金融场景必须用它。
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属分类ID */
    private Long categoryId;

    /** 商品名称 */
    private String name;

    /** 商品描述 */
    private String description;

    /** 封面图 URL */
    private String coverImage;

    /** 图片列表（JSON 字符串，如 ["url1","url2"]） */
    private String images;

    /** 价格（BigDecimal 精确计算） */
    private BigDecimal price;

    /** 库存数量 */
    private Integer stock;

    /** 销量 */
    private Integer sales;

    /** 规格参数（JSON，如 {"颜色":"红","尺寸":"XL"}） */
    private String specs;

    /** 状态：0=下架, 1=上架 */
    private Integer status;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
