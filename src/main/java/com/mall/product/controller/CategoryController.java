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
        return R.ok(categoryService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增分类 (管理端)")
    public R<Category> add(@RequestParam String name,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Integer sort,
            @RequestParam(required = false) String icon) {
        return R.ok(categoryService.add(name, parentId, sort, icon));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改分类 (管理端)")
    public R<Void> update(@PathVariable Long id, @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer sort, @RequestParam(required = false) String icon) {
        categoryService.update(id, name, sort, icon);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类 (管理端)")
    public R<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return R.ok();
    }
}
