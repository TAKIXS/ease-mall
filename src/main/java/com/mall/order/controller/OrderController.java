package com.mall.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.result.R;
import com.mall.common.utils.JwtUtil;
import com.mall.order.dto.OrderCreateDTO;
import com.mall.order.entity.Order;
import com.mall.order.service.OrderService;
import com.mall.order.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Tag(name = "订单管理", description = "下单、查订单、取消、支付、发货、完成")
public class OrderController {

    private final OrderService orderService;

    private Long getUserId(String auth) { return JwtUtil.getUserId(auth.substring(7)); }

    // ==================== 下单 ====================
    @PostMapping
    @Operation(summary = "下单")
    public R<OrderVO> create(@RequestHeader("Authorization") String auth,
                             @Valid @RequestBody OrderCreateDTO dto) {
        OrderVO vo = orderService.create(getUserId(auth), dto);
        return R.ok(vo);
    }

    // ==================== 查自己的订单 ====================
    @GetMapping
    @Operation(summary = "我的订单列表")
    public R<Page<Order>> myOrders(@RequestHeader("Authorization") String auth,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        return R.ok(orderService.listByUser(getUserId(auth), page, size));
    }

    // ==================== 订单详情 ====================
    @GetMapping("/{id}")
    @Operation(summary = "订单详情")
    public R<OrderVO> detail(@PathVariable Long id) {
        return R.ok(orderService.getDetail(id));
    }

    // ==================== 取消订单 ====================
    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消订单")
    public R<Void> cancel(@PathVariable Long id,
                          @RequestHeader("Authorization") String auth) {
        orderService.cancel(id, getUserId(auth));
        return R.ok();
    }

    // ==================== 管理端：支付/发货/完成 ====================
    @PutMapping("/{id}/pay")
    @Operation(summary = "支付订单")
    public R<Void> pay(@PathVariable Long id) {
        orderService.pay(id);
        return R.ok();
    }

    @PutMapping("/{id}/ship")
    @Operation(summary = "发货")
    public R<Void> ship(@PathVariable Long id) {
        orderService.ship(id);
        return R.ok();
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "完成订单")
    public R<Void> complete(@PathVariable Long id) {
        orderService.complete(id);
        return R.ok();
    }
}
