package com.mall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.user.entity.Address;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地址 Mapper
 *
 * 继承 BaseMapper<Address> 后免费获得：
 *   insert(address)      — 新增地址
 *   deleteById(id)       — 按ID删地址
 *   updateById(address)  — 按ID更新地址
 *   selectById(id)       — 按ID查一条
 *   selectList(wrapper)  — 条件查多条（比如查某用户的所有地址）
 */
@Mapper
public interface AddressMapper extends BaseMapper<Address> {
    // 基础 CRUD 都有了，这里先空着
}
