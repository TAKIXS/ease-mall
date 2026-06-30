package com.mall.user.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/** 个人信息返回 */
@Data
@Builder
public class UserProfileVO {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Integer gender;
    private Integer status;
    private LocalDateTime createTime;
}
