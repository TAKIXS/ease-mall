package com.mall.product.service;

import com.mall.product.entity.Category;
import com.mall.product.vo.CategoryVO;
import java.util.List;

public interface CategoryService {

    /** 查全部分类，返回树形结构 */
    List<CategoryVO> listTree();

    /** 根据ID查一个分类 */
    Category getById(Long id);
}
