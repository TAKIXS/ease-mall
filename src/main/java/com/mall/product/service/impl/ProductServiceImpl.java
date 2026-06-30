package com.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.exception.BusinessException;
import com.mall.product.dto.ProductDTO;
import com.mall.product.entity.Category;
import com.mall.product.entity.Product;
import com.mall.product.mapper.CategoryMapper;
import com.mall.product.mapper.ProductMapper;
import com.mall.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    // ==================== 分页搜索 ====================
    @Override
    public Page<Product> search(String keyword, Long categoryId, int page, int size) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Product::getName, keyword);
        }
        // ★ 分类筛选：包含该分类及其所有子分类
        if (categoryId != null) {
            List<Long> ids = new ArrayList<>();
            ids.add(categoryId);
            collectChildIds(categoryId, ids);
            wrapper.in(Product::getCategoryId, ids);
        }
        wrapper.eq(Product::getStatus, 1)
               .orderByDesc(Product::getCreateTime);

        Page<Product> pageParam = new Page<>(page, size);
        return productMapper.selectPage(pageParam, wrapper);
    }

    /** 递归收集子分类ID */
    private void collectChildIds(Long parentId, List<Long> ids) {
        List<Category> children = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().eq(Category::getParentId, parentId));
        for (Category child : children) {
            ids.add(child.getId());
            collectChildIds(child.getId(), ids);  // 递归
        }
    }

    // ==================== 查一条 ====================
    @Override
    public Product getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return product;
    }

    // ==================== 新增 ====================
    @Override
    public Product add(ProductDTO dto) {
        Product product = new Product();
        copyFromDTO(product, dto);
        product.setSales(0);                      // 新商品销量为0
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : 1); // 默认上架
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());

        productMapper.insert(product);
        return product;
    }

    // ==================== 修改 ====================
    @Override
    public void update(Long id, ProductDTO dto) {
        Product product = getById(id);
        copyFromDTO(product, dto);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
    }

    // ==================== 上架/下架 ====================
    @Override
    public void toggleStatus(Long id) {
        Product product = getById(id);
        // 1 变 0，0 变 1
        product.setStatus(product.getStatus() == 1 ? 0 : 1);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
    }

    // ==================== ★ 扣减库存（下单时调用） ====================
    @Override
    @Transactional
    public boolean deductStock(Long id, int quantity) {
        // 用 SQL 保证原子性：WHERE stock >= ? 防止超卖
        // SQL: UPDATE product SET stock = stock - ? WHERE id = ? AND stock >= ?
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, id)
               .ge(Product::getStock, quantity)           // stock >= quantity
               .setSql("stock = stock - " + quantity);     // stock = stock - 数量

        int rows = productMapper.update(null, wrapper);
        if (rows == 0) {
            throw new BusinessException("库存不足");
        }

        // 扣减成功，增加销量
        Product product = productMapper.selectById(id);
        product.setSales(product.getSales() + quantity);
        productMapper.updateById(product);
        return true;
    }

    // ==================== ★ 回滚库存（取消订单时调用） ====================
    @Override
    public void rollbackStock(Long id, int quantity) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, id)
               .setSql("stock = stock + " + quantity);     // 把库存加回去

        productMapper.update(null, wrapper);
    }

    // ==================== 私有方法 ====================
    private void copyFromDTO(Product product, ProductDTO dto) {
        product.setCategoryId(dto.getCategoryId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCoverImage(dto.getCoverImage());
        product.setImages(dto.getImages());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setSpecs(dto.getSpecs());
    }
}
