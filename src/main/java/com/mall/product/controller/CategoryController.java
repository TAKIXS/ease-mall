package com.mall.product.controller;

import com.mall.common.result.R;
import com.mall.product.entity.Category;
import com.mall.product.service.CategoryService;
import com.mall.product.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类接口
 *
 * GET /product/category          → 查分类树
 * GET /product/category/{id}     → 查一条分类
 */
@RestController
@RequestMapping("/product/category")
@RequiredArgsConstructor
@Tag(name = "商品分类", description = "分类树查询")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "获取分类树")
    public R<List<CategoryVO>> listTree() {
        List<CategoryVO> tree = categoryService.listTree();
        return R.ok(tree);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情")
    public R<Category> getById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return R.ok(category);
    }
}
