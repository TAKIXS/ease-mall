package com.mall.payment.controller;

import com.mall.common.result.R;
import com.mall.common.utils.JwtUtil;
import com.mall.payment.dto.PayDTO;
import com.mall.payment.entity.PaymentRecord;
import com.mall.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Tag(name = "支付管理", description = "模拟支付、支付记录")
public class PaymentController {

    private final PaymentService paymentService;

    private Long getUserId(String auth) { return JwtUtil.getUserId(auth.substring(7)); }

    /** 支付 */
    @PostMapping("/pay")
    @Operation(summary = "模拟支付")
    public R<PaymentRecord> pay(@RequestHeader("Authorization") String auth,
                                @Valid @RequestBody PayDTO dto) {
        PaymentRecord record = paymentService.pay(getUserId(auth), dto);
        return R.ok(record);
    }

    /** 我的支付记录 */
    @GetMapping
    @Operation(summary = "我的支付记录")
    public R<List<PaymentRecord>> list(@RequestHeader("Authorization") String auth) {
        return R.ok(paymentService.listByUser(getUserId(auth)));
    }

    /** 查某订单的支付记录 */
    @GetMapping("/order/{orderId}")
    @Operation(summary = "查订单支付记录")
    public R<PaymentRecord> getByOrderId(@PathVariable Long orderId) {
        return R.ok(paymentService.getByOrderId(orderId));
    }
}
