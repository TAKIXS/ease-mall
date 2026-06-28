package com.mall.common.interceptor;

import com.mall.common.service.TokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * ★ 统一认证拦截器
 *
 * 之前每个 Controller 都要自己写 getUserId() 解析 Token，
 * 现在所有请求先过这个拦截器，通过后把 userId/username 放入 request attribute，
 * Controller 直接从 request 拿，不用再重复写解析代码。
 *
 * 放行规则（在 WebMvcConfig 中配置）：
 *   /auth/login      → 放行（登录不需要登录）
 *   /auth/register   → 放行（注册不需要登录）
 *   /doc.html        → 放行（接口文档）
 *   /h2-console      → 放行（数据库控制台）
 *   其他所有接口      → 必须带有效 Token
 */
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 1. 从请求头拿 Token
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"请先登录\",\"data\":null}");
            return false;  // 拦截
        }

        String token = authHeader.substring(7);

        // 2. ★ 双重验证（JWT + Redis黑名单）
        Claims claims = tokenService.validateToken(token);
        if (claims == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"登录已过期，请重新登录\",\"data\":null}");
            return false;  // 拦截
        }

        // 3. 把用户信息放入 request，Controller 直接用
        request.setAttribute("userId", Long.valueOf(claims.getId()));
        request.setAttribute("username", claims.getSubject());

        return true;  // 放行
    }
}
