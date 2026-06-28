package com.mall.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类 — 和数据库的 user 表一一对应
 *
 * 核心注解解释（先记住这三个，后面的学到再说）：
 *   @Data           = Lombok 自动生成 getter/setter/toString，不用手写
 *   @TableName      = 告诉 MyBatis-Plus 这个类对应哪张表
 *   @TableId        = 标记主键字段，type=IdType.AUTO 表示数据库自增
 *   @TableField     = 标记非主键字段，exist=false 表示数据库里没有这个字段
 *   @TableLogic     = 逻辑删除标记（deleted=1 表示"已删除"，但数据还在）
 */
@Data
@TableName("user")  // 对应 mall_user 数据库的 user 表
public class User {

    /** 用户ID，数据库自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名（登录用），唯一 */
    private String username;

    /** 密码（存储时要加密，明文绝对不能存！） */
    private String password;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 昵称（显示用） */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 性别：0=未知, 1=男, 2=女 */
    private Integer gender;

    /** 账号状态：0=禁用, 1=正常 */
    private Integer status;

    /** 最后登录时间 */
    private LocalDateTime lastLogin;

    /** 逻辑删除标记：0=未删除, 1=已删除（MyBatis-Plus 会自动过滤已删除数据） */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
