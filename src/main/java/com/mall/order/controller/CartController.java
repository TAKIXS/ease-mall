package com.mall.order.controller;

import com.mall.common.result.R;
import com.mall.order.dto.CartItemDTO;
import com.mall.order.entity.OrderItem;
import com.mall.order.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "购物车", description = "购物车增删改查")
public class CartController {

    private final CartService cartService;

    /** ★ Token 已被拦截器验证，直接拿 userId */
    private Long getUserId(HttpServletRequest req) {
        return (Long) req.getAttribute("userId");
    }

    @PostMapping
    @Operation(summary = "加入购物车")
    public R<Void> add(HttpServletRequest req, @Valid @RequestBody CartItemDTO dto) {
        cartService.addItem(getUserId(req), dto);
        return R.ok();
    }

    @GetMapping
    @Operation(summary = "查看购物车")
    public R<List<OrderItem>> list(HttpServletRequest req) {
        return R.ok(cartService.getItems(getUserId(req)));
    }

    @PutMapping("/{productId}")
    @Operation(summary = "修改数量")
    public R<Void> updateQuantity(HttpServletRequest req, @PathVariable Long productId, @RequestParam int quantity) {
        cartService.updateQuantity(getUserId(req), productId, quantity);
        return R.ok();
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "移除商品")
    public R<Void> remove(HttpServletRequest req, @PathVariable Long productId) {
        cartService.removeItem(getUserId(req), productId);
        return R.ok();
    }
}
