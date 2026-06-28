package com.mall.user.controller;

import com.mall.common.result.R;
import com.mall.user.dto.AddressDTO;
import com.mall.user.entity.Address;
import com.mall.user.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/address")
@RequiredArgsConstructor
@Tag(name = "收货地址", description = "地址的增删改查")
public class AddressController {

    private final AddressService addressService;

    private Long getUserId(HttpServletRequest req) { return (Long) req.getAttribute("userId"); }

    @PostMapping
    @Operation(summary = "新增收货地址")
    public R<Address> add(HttpServletRequest req, @Valid @RequestBody AddressDTO dto) {
        return R.ok(addressService.add(getUserId(req), dto));
    }

    @GetMapping
    @Operation(summary = "获取当前用户的所有地址")
    public R<List<Address>> list(HttpServletRequest req) {
        return R.ok(addressService.listByUser(getUserId(req)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取地址详情")
    public R<Address> getById(@PathVariable("id") Long addressId) {
        return R.ok(addressService.getById(addressId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改地址")
    public R<Void> update(@PathVariable("id") Long addressId,
                          HttpServletRequest req,
                          @Valid @RequestBody AddressDTO dto) {
        addressService.update(addressId, getUserId(req), dto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除地址")
    public R<Void> delete(@PathVariable("id") Long addressId,
                          HttpServletRequest req) {
        addressService.delete(addressId, getUserId(req));
        return R.ok();
    }
}
