package com.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员实体 — 对应 admin_user 表
 *
 * 管理员和普通用户分表存储，职责不同：
 *   user 表 — 前端用户，购物、下单
 *   admin_user 表 — 后台管理员，管理商品/订单/用户
 */
@Data
@TableName("admin_user")
public class AdminUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String password;

    /** 角色：SUPER_ADMIN / ADMIN / OPERATOR */
    private String role;

    /** 状态：0=禁用, 1=正常 */
    private Integer status;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
