package com.mall.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.product.dto.ProductDTO;
import com.mall.product.entity.Product;

public interface ProductService {

    /** 分页搜索商品（关键词 + 分类筛选） */
    Page<Product> search(String keyword, Long categoryId, int page, int size);

    /** 根据ID查商品 */
    Product getById(Long id);

    /** 新增商品 */
    Product add(ProductDTO dto);

    /** 修改商品 */
    void update(Long id, ProductDTO dto);

    /** 上架/下架 */
    void toggleStatus(Long id);

    /** 扣减库存（下单时调用，返回 true 表示扣减成功） */
    boolean deductStock(Long id, int quantity);

    /** 回滚库存（取消订单时调用） */
    void rollbackStock(Long id, int quantity);
}
