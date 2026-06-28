package com.mall.user.controller;

import com.mall.common.result.R;
import com.mall.common.utils.JwtUtil;
import com.mall.user.dto.AddressDTO;
import com.mall.user.entity.Address;
import com.mall.user.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址接口 Controller
 *
 * RESTful 接口设计规范（记住这个模式，业界通用）：
 *   GET    /address          → 查列表
 *   GET    /address/{id}     → 查一条
 *   POST   /address          → 新增
 *   PUT    /address/{id}     → 修改
 *   DELETE /address/{id}     → 删除
 */
@RestController
@RequestMapping("/user/address")      // 所有接口都以这个开头
@RequiredArgsConstructor
@Tag(name = "收货地址", description = "地址的增删改查")
public class AddressController {

    private final AddressService addressService;

    /**
     * ★ 提取当前登录用户ID
     *
     * token 的格式：Bearer eyJhbGciOiJIUzI1NiJ9.xxx...
     * 所以要去掉前 7 个字符 "Bearer "，剩下的才是真正的 token
     */
    private Long getCurrentUserId(String authHeader) {
        // "Bearer eyJ..." → 去掉前面 "Bearer "（7个字符）
        String token = authHeader.substring(7);
        return JwtUtil.getUserId(token);
    }

    // ==================== 新增地址 ====================
    @PostMapping
    @Operation(summary = "新增收货地址")
    public R<Address> add(@RequestHeader("Authorization") String authHeader,
                          @Valid @RequestBody AddressDTO dto) {
        // 从 Token 中拿到"是谁在操作"
        Long userId = getCurrentUserId(authHeader);
        // 调用 Service 保存
        Address address = addressService.add(userId, dto);
        return R.ok(address);
    }

    // ==================== 查地址列表 ====================
    @GetMapping
    @Operation(summary = "获取当前用户的所有地址")
    public R<List<Address>> list(@RequestHeader("Authorization") String authHeader) {
        Long userId = getCurrentUserId(authHeader);
        List<Address> list = addressService.listByUser(userId);
        return R.ok(list);
    }

    // ==================== 查一条地址 ====================
    @GetMapping("/{id}")   // {id} 是路径参数，Spring 自动取出来赋给 @PathVariable
    @Operation(summary = "获取地址详情")
    public R<Address> getById(@PathVariable("id") Long addressId) {
        Address address = addressService.getById(addressId);
        return R.ok(address);
    }

    // ==================== 修改地址 ====================
    @PutMapping("/{id}")
    @Operation(summary = "修改地址")
    public R<Void> update(@PathVariable("id") Long addressId,
                          @RequestHeader("Authorization") String authHeader,
                          @Valid @RequestBody AddressDTO dto) {
        Long userId = getCurrentUserId(authHeader);
        addressService.update(addressId, userId, dto);
        return R.ok();
    }

    // ==================== 删除地址 ====================
    @DeleteMapping("/{id}")
    @Operation(summary = "删除地址")
    public R<Void> delete(@PathVariable("id") Long addressId,
                          @RequestHeader("Authorization") String authHeader) {
        Long userId = getCurrentUserId(authHeader);
        addressService.delete(addressId, userId);
        return R.ok();
    }
}
