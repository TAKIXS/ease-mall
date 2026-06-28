package com.mall.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.result.R;
import com.mall.product.dto.ProductDTO;
import com.mall.product.entity.Product;
import com.mall.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品接口
 *
 * GET    /product          → 分页搜索（keyword + categoryId + page + size）
 * GET    /product/{id}     → 查一条
 * POST   /product          → 新增
 * PUT    /product/{id}     → 修改
 * PUT    /product/{id}/status → 上架/下架
 */
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品增删改查、上下架")
public class ProductController {

    private final ProductService productService;

    // ==================== 分页搜索 ====================
    @GetMapping
    @Operation(summary = "分页搜索商品")
    public R<Page<Product>> search(
            @RequestParam(required = false) String keyword,        // ?keyword=手机
            @RequestParam(required = false) Long categoryId,       // ?categoryId=4
            @RequestParam(defaultValue = "1") int page,            // ?page=1
            @RequestParam(defaultValue = "10") int size            // ?size=10
    ) {
        Page<Product> result = productService.search(keyword, categoryId, page, size);
        return R.ok(result);
    }

    // ==================== 查一条 ====================
    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情")
    public R<Product> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        return R.ok(product);
    }

    // ==================== 新增 ====================
    @PostMapping
    @Operation(summary = "新增商品")
    public R<Product> add(@Valid @RequestBody ProductDTO dto) {
        Product product = productService.add(dto);
        return R.ok(product);
    }

    // ==================== 修改 ====================
    @PutMapping("/{id}")
    @Operation(summary = "修改商品")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        productService.update(id, dto);
        return R.ok();
    }

    // ==================== 上架/下架 ====================
    @PutMapping("/{id}/status")
    @Operation(summary = "上架/下架商品")
    public R<Void> toggleStatus(@PathVariable Long id) {
        productService.toggleStatus(id);
        return R.ok();
    }
}
