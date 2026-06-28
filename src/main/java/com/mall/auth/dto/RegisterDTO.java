package com.mall.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求 DTO — 前端注册时传过来的数据
 *
 * @Valid 注解解释（后面在 Controller 里用 @Valid 触发校验）：
 *   @NotBlank  = 不能为 null 且不能是空字符串
 *   @Size      = 限制字符串长度
 *   @Pattern   = 正则表达式匹配（如手机号格式）
 */
@Data
public class RegisterDTO {

    /** 用户名：不能为空，长度 3~20 */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度需在3~20之间")
    private String username;

    /** 密码：不能为空，长度 6~30 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 30, message = "密码长度需在6~30之间")
    private String password;

    /** 手机号：正则校验格式 */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 邮箱：可选 */
    private String email;
}
