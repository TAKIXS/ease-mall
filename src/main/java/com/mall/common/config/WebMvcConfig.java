package com.mall.common.config;

import com.mall.common.interceptor.TokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ★ 注册 Token 拦截器
 *
 * 放行白名单（这些接口不需要登录）：
 *   /auth/login, /auth/register  → 登录注册
 *   /admin/login                  → 管理员登录
 *   /doc.html, /v3/**, /swagger** → API 文档
 *   /h2-console/**                → 数据库控制台
 *   /error                        → Spring 错误页
 *   /product/category, /product   → 商品分类和搜索（GET，浏览不需要登录）
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")                  // 拦截所有
                .excludePathPatterns(                     // 但放行以下：
                        "/auth/login",                    // 登录
                        "/auth/register",                 // 注册
                        "/admin/login",                   // 管理员登录
                        "/product/category",              // 浏览分类树
                        "/product",                       // 浏览商品列表
                        "/product/{id:[0-9]+}",           // 浏览商品详情（RESTful 路径参数）
                        "/doc.html",                      // Knife4j
                        "/v3/**",                         // OpenAPI
                        "/swagger-ui/**",                 // Swagger
                        "/webjars/**",                    // Knife4j 静态资源
                        "/h2-console/**",                 // H2 控制台
                        "/error"                          // 错误页面
                );
    }
}
