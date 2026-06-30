package com.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.exception.BusinessException;
import com.mall.product.entity.Category;
import com.mall.product.mapper.CategoryMapper;
import com.mall.product.service.CategoryService;
import com.mall.product.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    // ==================== 查分类树 ====================
    @Override
    public List<CategoryVO> listTree() {
        // 1. 查出全部分类（按 sort 排序）
        List<Category> all = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .orderByAsc(Category::getSort)
        );

        // 2. 找出所有一级分类（parentId == 0），逐个递归找孩子
        List<CategoryVO> tree = new ArrayList<>();
        for (Category category : all) {
            if (category.getParentId() == 0) {
                CategoryVO vo = toVO(category);
                vo.setChildren(findChildren(vo.getId(), all));
                tree.add(vo);
            }
        }
        return tree;
    }

    // ==================== 新增分类 ====================
    @Override
    public Category add(String name, Long parentId, Integer sort, String icon) {
        Category category = new Category();
        category.setName(name);
        category.setParentId(parentId != null ? parentId : 0L);
        category.setSort(sort != null ? sort : 0);
        category.setIcon(icon);
        categoryMapper.insert(category);
        return category;
    }

    // ==================== 修改分类 ====================
    @Override
    public void update(Long id, String name, Integer sort, String icon) {
        Category category = getById(id);
        if (name != null) category.setName(name);
        if (sort != null) category.setSort(sort);
        if (icon != null) category.setIcon(icon);
        categoryMapper.updateById(category);
    }

    // ==================== 删除分类 ====================
    @Override
    public void delete(Long id) {
        // 检查是否有子分类
        Long count = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>().eq(Category::getParentId, id));
        if (count > 0) throw new BusinessException("请先删除子分类");
        categoryMapper.deleteById(id);
    }

    // ==================== 递归找子分类 ====================
    /**
     * 从 all 列表中找出 parentId == id 的所有记录
     * 再对每条记录递归找它的子分类
     *
     * @param parentId 当前分类ID
     * @param all      全部分类列表（内存中查找，不查数据库）
     */
    private List<CategoryVO> findChildren(Long parentId, List<Category> all) {
        List<CategoryVO> children = new ArrayList<>();
        for (Category category : all) {
            if (category.getParentId().equals(parentId)) {
                CategoryVO vo = toVO(category);
                // ★ 递归：继续找当前分类的孩子
                vo.setChildren(findChildren(vo.getId(), all));
                children.add(vo);
            }
        }
        return children;
    }

    // ==================== 根据ID查分类 ====================
    @Override
    public Category getById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return category;
    }

    // ==================== Entity → VO 转换 ====================
    private CategoryVO toVO(Category category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setParentId(category.getParentId());
        vo.setName(category.getName());
        vo.setSort(category.getSort());
        vo.setIcon(category.getIcon());
        vo.setCreateTime(category.getCreateTime());
        return vo;
    }
}
