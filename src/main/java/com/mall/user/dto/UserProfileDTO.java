package com.mall.user.dto;

import lombok.Data;

/** 更新个人信息的请求 */
@Data
public class UserProfileDTO {
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Integer gender;
}
