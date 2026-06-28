package com.mall.auth.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 登录成功后的返回对象
 *
 * @Builder 注解：让我们可以用"建造者模式"构建对象，代码更可读：
 *   LoginVO vo = LoginVO.builder()
 *       .token("xxx")
 *       .userId(1L)
 *       .username("admin")
 *       .build();
 */
@Data
@Builder
public class LoginVO {

    /** JWT Token，前端存起来，后续请求都要带上 */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;
}
