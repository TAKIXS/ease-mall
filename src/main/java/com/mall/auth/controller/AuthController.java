package com.mall.auth.controller;

import com.mall.auth.dto.LoginDTO;
import com.mall.auth.dto.RegisterDTO;
import com.mall.auth.service.AuthService;
import com.mall.auth.vo.LoginVO;
import com.mall.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口 Controller
 *
 * 核心注解：
 *   @RestController      = @Controller + @ResponseBody（返回值直接写 JSON，不走视图）
 *   @RequestMapping       = 给这个类里所有接口统一加一个路径前缀
 *   @PostMapping          = 只处理 HTTP POST 请求
 *   @Valid                = 触发 DTO 里的参数校验（@NotBlank, @Size 等）
 *   @RequestBody          = 把请求体里的 JSON 自动转成 Java 对象
 *   @Operation            = Knife4j/Swagger 接口文档的说明文字
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证接口", description = "用户注册、登录相关")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     *
     * 请求示例（POST /auth/register）：
     * {
     *   "username": "zhangsan",
     *   "password": "123456",
     *   "phone": "13800138000",
     *   "email": "zhangsan@example.com"
     * }
     *
     * 返回格式（统一用 R 包装）：
     * 成功：{ "code": 200, "message": "操作成功", "data": null }
     * 失败：{ "code": 500, "message": "用户名已被注册", "data": null }
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public R<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return R.ok("注册成功", null);
    }

    /**
     * 用户登录
     *
     * 请求示例（POST /auth/login）：
     * {
     *   "username": "zhangsan",
     *   "password": "123456"
     * }
     *
     * 成功返回：
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": {
     *     "token": "eyJhbGciOiJIUzI1NiJ9...",
     *     "userId": 1,
     *     "username": "zhangsan",
     *     "nickname": "zhangsan"
     *   }
     * }
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO vo = authService.login(dto);
        return R.ok(vo);
    }

    /**
     * 退出登录 — Token 加入 Redis 黑名单
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public R<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        authService.logout(token);
        return R.ok();
    }
}
