package com.mall.product.service;

import com.mall.product.entity.Category;
import com.mall.product.vo.CategoryVO;
import java.util.List;

public interface CategoryService {

    List<CategoryVO> listTree();
    Category getById(Long id);
    Category add(String name, Long parentId, Integer sort, String icon);
    void update(Long id, String name, Integer sort, String icon);
    void delete(Long id);
}
