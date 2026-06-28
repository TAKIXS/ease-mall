package com.mall.user.service;

import com.mall.user.dto.AddressDTO;
import com.mall.user.entity.Address;
import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService {

    /** 新增地址 */
    Address add(Long userId, AddressDTO dto);

    /** 查询当前用户的所有地址 */
    List<Address> listByUser(Long userId);

    /** 根据ID查一条地址 */
    Address getById(Long addressId);

    /** 修改地址 */
    void update(Long addressId, Long userId, AddressDTO dto);

    /** 删除地址 */
    void delete(Long addressId, Long userId);
}
