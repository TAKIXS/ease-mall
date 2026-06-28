package com.mall.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 收货地址实体 — 对应数据库 address 表
 *
 * 一个用户可以有多个地址，用 user_id 关联 user 表
 * 这就是"一对多"关系：一个用户 → 多个地址
 */
@Data
@TableName("address")       // 对应哪张表
public class Address {

    @TableId(type = IdType.AUTO)   // 主键，数据库自增
    private Long id;

    /** 属于哪个用户 */
    private Long userId;

    /** 收件人姓名 */
    private String receiverName;

    /** 收件人手机号 */
    private String receiverPhone;

    /** 省份 */
    private String province;

    /** 城市 */
    private String city;

    /** 区/县 */
    private String district;

    /** 详细地址（街道门牌号） */
    private String detail;

    /** 是否默认地址：0=否, 1=是 */
    private Integer isDefault;

    /** 逻辑删除：0=未删除, 1=已删除 */
    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
