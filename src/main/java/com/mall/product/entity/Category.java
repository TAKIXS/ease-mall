package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品分类实体 — 树形结构
 *
 * 树形原理：
 *   一级分类（parent_id = 0）：电子产品、服装鞋帽
 *   二级分类（parent_id = 一级id）：手机、电脑
 *   三级分类（parent_id = 二级id）：智能手机、功能手机
 *
 * 数据库里存的是平铺的行，后端代码组织成树。
 */
@Data
@TableName("category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父分类ID：0 表示一级分类 */
    private Long parentId;

    /** 分类名称 */
    private String name;

    /** 排序值（越小越靠前） */
    private Integer sort;

    /** 分类图标 URL */
    private String icon;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
