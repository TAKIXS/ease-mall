package com.mall.payment.controller;

import com.mall.common.result.R;
import com.mall.payment.dto.PayDTO;
import com.mall.payment.entity.PaymentRecord;
import com.mall.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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

    private Long getUserId(HttpServletRequest req) { return (Long) req.getAttribute("userId"); }

    @PostMapping("/pay")
    @Operation(summary = "模拟支付")
    public R<PaymentRecord> pay(HttpServletRequest req, @Valid @RequestBody PayDTO dto) {
        return R.ok(paymentService.pay(getUserId(req), dto));
    }

    @GetMapping
    @Operation(summary = "我的支付记录")
    public R<List<PaymentRecord>> list(HttpServletRequest req) {
        return R.ok(paymentService.listByUser(getUserId(req)));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "查订单支付记录")
    public R<PaymentRecord> getByOrderId(@PathVariable Long orderId) {
        return R.ok(paymentService.getByOrderId(orderId));
    }
}
