package com.mall.user.controller;

import com.mall.common.result.R;
import com.mall.user.dto.UserProfileDTO;
import com.mall.user.service.UserService;
import com.mall.user.vo.UserProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户信息", description = "个人信息查看与修改")
public class UserController {

    private final UserService userService;

    private Long getUserId(HttpServletRequest req) { return (Long) req.getAttribute("userId"); }

    @GetMapping("/profile")
    @Operation(summary = "获取个人信息")
    public R<UserProfileVO> getProfile(HttpServletRequest req) {
        return R.ok(userService.getProfile(getUserId(req)));
    }

    @PutMapping("/profile")
    @Operation(summary = "修改个人信息")
    public R<Void> updateProfile(HttpServletRequest req, @RequestBody UserProfileDTO dto) {
        userService.updateProfile(getUserId(req), dto);
        return R.ok();
    }
}
