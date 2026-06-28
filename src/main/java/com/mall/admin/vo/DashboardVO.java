package com.mall.admin.vo;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 数据看板 — 后台首页的数据概览
 */
@Data
@Builder
public class DashboardVO {

    /** 总用户数 */
    private Long totalUsers;

    /** 总订单数 */
    private Long totalOrders;

    /** 今日新增订单 */
    private Long todayOrders;

    /** 总交易额（GMV） */
    private BigDecimal totalGMV;

    /** 今日交易额 */
    private BigDecimal todayGMV;

    /** 待发货订单数 */
    private Long pendingShip;

    /** 商品总数 */
    private Long totalProducts;
}
