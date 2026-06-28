package com.mall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mall.common.exception.BusinessException;
import com.mall.user.dto.AddressDTO;
import com.mall.user.entity.Address;
import com.mall.user.mapper.AddressMapper;
import com.mall.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地址服务实现
 *
 * 核心业务规则：
 *   1. 每个用户只能有一个默认地址
 *   2. 新地址设为默认时 → 先把旧的默认地址取消
 *   3. 只能操作自己的地址（用 userId 校验）
 */
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    // ==================== 新增地址 ====================
    @Override
    @Transactional  // 事务：保证"取消旧默认 + 插入新地址"一起成功或一起失败
    public Address add(Long userId, AddressDTO dto) {
        // 1. 把 DTO 转成 Entity
        Address address = new Address();
        address.setUserId(userId);              // 当前登录用户的 ID
        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetail(dto.getDetail());
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());

        // 2. 处理默认地址逻辑
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            // 先把该用户旧默认地址取消
            cancelOldDefault(userId);
            address.setIsDefault(1);
        } else {
            address.setIsDefault(0);
        }

        // 3. 插入数据库
        addressMapper.insert(address);
        return address;
    }

    // ==================== 查地址列表 ====================
    @Override
    public List<Address> listByUser(Long userId) {
        // select * from address where user_id = ? and deleted = 0
        // order by is_default desc, create_time desc（默认地址排前面）
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)           // WHERE user_id = ?
               .orderByDesc(Address::getIsDefault)        // 默认地址排第一
               .orderByDesc(Address::getCreateTime);      // 最新创建的排在前面
        return addressMapper.selectList(wrapper);
    }

    // ==================== 查一条地址 ====================
    @Override
    public Address getById(Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        return address;
    }

    // ==================== 修改地址 ====================
    @Override
    @Transactional
    public void update(Long addressId, Long userId, AddressDTO dto) {
        Address address = getById(addressId);

        // ★ 安全检查：只能修改自己的地址
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权修改他人地址");
        }

        // 如果设为默认 → 先取消旧的默认
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            cancelOldDefault(userId);
        }

        // 更新字段
        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetail(dto.getDetail());
        address.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : 0);
        address.setUpdateTime(LocalDateTime.now());

        addressMapper.updateById(address);
    }

    // ==================== 删除地址 ====================
    @Override
    public void delete(Long addressId, Long userId) {
        Address address = getById(addressId);

        // ★ 安全检查：只能删除自己的地址
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权删除他人地址");
        }

        // MyBatis-Plus 的 deleteById 会执行逻辑删除（deleted 置为 1）
        // 因为 Address 类上有 @TableLogic 注解
        addressMapper.deleteById(addressId);
    }

    // ==================== 私有方法 ====================

    /**
     * 取消该用户的所有默认地址
     *
     * SQL 等价于：
     * UPDATE address SET is_default = 0 WHERE user_id = ? AND is_default = 1
     */
    private void cancelOldDefault(Long userId) {
        LambdaUpdateWrapper<Address> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Address::getUserId, userId)     // WHERE user_id = ?
               .eq(Address::getIsDefault, 1)        // AND is_default = 1
               .set(Address::getIsDefault, 0);       // SET is_default = 0
        addressMapper.update(null, wrapper);         // 第一个参数 null 表示不传实体
    }
}
