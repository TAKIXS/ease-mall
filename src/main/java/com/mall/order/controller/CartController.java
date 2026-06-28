package com.mall.order.controller;

import com.mall.common.result.R;
import com.mall.common.utils.JwtUtil;
import com.mall.order.dto.CartItemDTO;
import com.mall.order.entity.OrderItem;
import com.mall.order.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    private Long getUserId(String auth) { return JwtUtil.getUserId(auth.substring(7)); }

    @PostMapping
    @Operation(summary = "加入购物车")
    public R<Void> add(@RequestHeader("Authorization") String auth,
                       @Valid @RequestBody CartItemDTO dto) {
        cartService.addItem(getUserId(auth), dto);
        return R.ok();
    }

    @GetMapping
    @Operation(summary = "查看购物车")
    public R<List<OrderItem>> list(@RequestHeader("Authorization") String auth) {
        return R.ok(cartService.getItems(getUserId(auth)));
    }

    @PutMapping("/{productId}")
    @Operation(summary = "修改数量")
    public R<Void> updateQuantity(@RequestHeader("Authorization") String auth,
                                  @PathVariable Long productId,
                                  @RequestParam int quantity) {
        cartService.updateQuantity(getUserId(auth), productId, quantity);
        return R.ok();
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "移除商品")
    public R<Void> remove(@RequestHeader("Authorization") String auth,
                          @PathVariable Long productId) {
        cartService.removeItem(getUserId(auth), productId);
        return R.ok();
    }
}
