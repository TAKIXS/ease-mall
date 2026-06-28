package com.mall.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.dto.AdminLoginDTO;
import com.mall.admin.service.AdminService;
import com.mall.admin.vo.AdminLoginVO;
import com.mall.admin.vo.DashboardVO;
import com.mall.auth.entity.User;
import com.mall.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "后台管理", description = "管理员登录、数据看板、用户管理")
public class AdminController {

    private final AdminService adminService;

    // ==================== 登录 ====================
    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public R<AdminLoginVO> login(@Valid @RequestBody AdminLoginDTO dto) {
        AdminLoginVO vo = adminService.login(dto);
        return R.ok(vo);
    }

    // ==================== 数据看板 ====================
    @GetMapping("/dashboard")
    @Operation(summary = "数据看板")
    public R<DashboardVO> dashboard() {
        DashboardVO vo = adminService.dashboard();
        return R.ok(vo);
    }

    // ==================== 用户列表 ====================
    @GetMapping("/users")
    @Operation(summary = "用户列表")
    public R<Page<User>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> result = adminService.listUsers(page, size);
        return R.ok(result);
    }

    // ==================== 启用/禁用用户 ====================
    @PutMapping("/users/{id}/status")
    @Operation(summary = "启用/禁用用户")
    public R<Void> toggleUserStatus(@PathVariable Long id) {
        adminService.toggleUserStatus(id);
        return R.ok();
    }
}
