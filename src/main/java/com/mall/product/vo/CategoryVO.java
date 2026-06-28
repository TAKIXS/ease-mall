package com.mall.product.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类返回对象 — 支持树形结构
 *
 * 返回 JSON 示例：
 * [
 *   {
 *     "id": 1,
 *     "name": "电子产品",
 *     "children": [
 *       { "id": 4, "name": "手机", "children": [] },
 *       { "id": 5, "name": "电脑", "children": [] }
 *     ]
 *   }
 * ]
 */
@Data
public class CategoryVO {

    private Long id;
    private Long parentId;
    private String name;
    private Integer sort;
    private String icon;
    private LocalDateTime createTime;

    /** ★ 子分类列表 — 递归嵌套，形成树 */
    private List<CategoryVO> children;
}
