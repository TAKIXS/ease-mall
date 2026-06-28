package com.mall.auth.service;

import com.mall.auth.dto.LoginDTO;
import com.mall.auth.dto.RegisterDTO;
import com.mall.auth.vo.LoginVO;

/**
 * 认证服务接口 — 只定义"做什么"，不写"怎么做"
 */
public interface AuthService {

    /**
     * 用户注册
     * @param dto 注册信息（用户名、密码、手机号等）
     */
    void register(RegisterDTO dto);

    /**
     * 用户登录
     * @param dto 登录信息（用户名、密码）
     * @return LoginVO（包含 JWT Token）
     */
    LoginVO login(LoginDTO dto);
}
