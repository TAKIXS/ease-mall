-- ============================================================
-- 电商平台 — 数据库初始化脚本
-- 首次启动 docker-compose 时自动执行
-- ============================================================

-- -------------------- 创建数据库 --------------------
CREATE DATABASE IF NOT EXISTS mall
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE mall;

-- ============================================================
-- 用户表
-- ============================================================
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)   NOT NULL                COMMENT '用户名（登录用）',
    `password`    VARCHAR(255)  NOT NULL                COMMENT '密码（BCrypt 加密）',
    `phone`       VARCHAR(20)   DEFAULT NULL            COMMENT '手机号',
    `email`       VARCHAR(100)  DEFAULT NULL            COMMENT '邮箱',
    `nickname`    VARCHAR(50)   DEFAULT NULL            COMMENT '昵称',
    `avatar`      VARCHAR(255)  DEFAULT NULL            COMMENT '头像 URL',
    `gender`      TINYINT       DEFAULT 0               COMMENT '性别：0-未知 1-男 2-女',
    `status`      TINYINT       DEFAULT 1               COMMENT '状态：0-禁用 1-正常',
    `last_login`  DATETIME      DEFAULT NULL            COMMENT '最后登录时间',
    `deleted`     TINYINT       DEFAULT 0               COMMENT '逻辑删除：0-未删除 1-已删除',
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 收货地址表
-- ============================================================
CREATE TABLE IF NOT EXISTS `address` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id`       BIGINT       NOT NULL                COMMENT '用户ID',
    `receiver_name` VARCHAR(50)  NOT NULL                COMMENT '收件人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL                COMMENT '收件人手机号',
    `province`      VARCHAR(50)  NOT NULL                COMMENT '省份',
    `city`          VARCHAR(50)  NOT NULL                COMMENT '城市',
    `district`      VARCHAR(50)  NOT NULL                COMMENT '区/县',
    `detail`        VARCHAR(255) NOT NULL                COMMENT '详细地址',
    `is_default`    TINYINT      DEFAULT 0               COMMENT '是否默认地址',
    `deleted`       TINYINT      DEFAULT 0               COMMENT '逻辑删除',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ============================================================
-- 商品分类表
-- ============================================================
CREATE TABLE IF NOT EXISTS `category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `parent_id`   BIGINT       DEFAULT 0                COMMENT '父分类ID',
    `name`        VARCHAR(50)  NOT NULL                COMMENT '分类名称',
    `sort`        INT          DEFAULT 0               COMMENT '排序',
    `icon`        VARCHAR(255) DEFAULT NULL            COMMENT '分类图标 URL',
    `deleted`     TINYINT      DEFAULT 0               COMMENT '逻辑删除',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ============================================================
-- 商品表
-- ============================================================
CREATE TABLE IF NOT EXISTS `product` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `category_id` BIGINT        NOT NULL                COMMENT '所属分类ID',
    `name`        VARCHAR(100)  NOT NULL                COMMENT '商品名称',
    `description` TEXT          DEFAULT NULL            COMMENT '商品描述',
    `cover_image` VARCHAR(255)  DEFAULT NULL            COMMENT '封面图 URL',
    `images`      JSON          DEFAULT NULL            COMMENT '商品图片列表',
    `price`       DECIMAL(10,2) NOT NULL DEFAULT 0.00   COMMENT '价格',
    `stock`       INT           NOT NULL DEFAULT 0      COMMENT '库存数量',
    `sales`       INT           DEFAULT 0               COMMENT '销量',
    `specs`       JSON          DEFAULT NULL            COMMENT '规格参数',
    `status`      TINYINT       DEFAULT 1               COMMENT '状态：0-下架 1-上架',
    `deleted`     TINYINT       DEFAULT 0               COMMENT '逻辑删除',
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ============================================================
-- 订单表
-- ============================================================
CREATE TABLE IF NOT EXISTS `orders` (
    `id`              BIGINT        NOT NULL COMMENT '订单ID',
    `order_no`        VARCHAR(32)   NOT NULL                COMMENT '订单编号',
    `user_id`         BIGINT        NOT NULL                COMMENT '用户ID',
    `total_amount`    DECIMAL(10,2) NOT NULL DEFAULT 0.00   COMMENT '订单总金额',
    `pay_amount`      DECIMAL(10,2) NOT NULL DEFAULT 0.00   COMMENT '实付金额',
    `status`          TINYINT       NOT NULL DEFAULT 1      COMMENT '1-待支付 2-已支付 3-已发货 4-已完成 5-已取消',
    `receiver_name`   VARCHAR(50)   DEFAULT NULL            COMMENT '收件人姓名',
    `receiver_phone`  VARCHAR(20)   DEFAULT NULL            COMMENT '收件人手机号',
    `receiver_address` VARCHAR(255) DEFAULT NULL            COMMENT '收货地址',
    `remark`          VARCHAR(500)  DEFAULT NULL            COMMENT '用户备注',
    `pay_time`        DATETIME      DEFAULT NULL            COMMENT '支付时间',
    `ship_time`       DATETIME      DEFAULT NULL            COMMENT '发货时间',
    `complete_time`   DATETIME      DEFAULT NULL            COMMENT '完成时间',
    `cancel_time`     DATETIME      DEFAULT NULL            COMMENT '取消时间',
    `deleted`         TINYINT       DEFAULT 0               COMMENT '逻辑删除',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ============================================================
-- 订单项表
-- ============================================================
CREATE TABLE IF NOT EXISTS `order_item` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
    `order_id`      BIGINT        NOT NULL                COMMENT '订单ID',
    `product_id`    BIGINT        NOT NULL                COMMENT '商品ID',
    `product_name`  VARCHAR(100)  NOT NULL                COMMENT '商品名称（快照）',
    `product_image` VARCHAR(255)  DEFAULT NULL            COMMENT '商品图片（快照）',
    `price`         DECIMAL(10,2) NOT NULL                COMMENT '购买时单价',
    `quantity`      INT           NOT NULL DEFAULT 1      COMMENT '购买数量',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- ============================================================
-- 支付记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS `payment_record` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
    `order_id`      BIGINT        NOT NULL                COMMENT '订单ID',
    `order_no`      VARCHAR(32)   NOT NULL                COMMENT '订单编号',
    `user_id`       BIGINT        NOT NULL                COMMENT '用户ID',
    `amount`        DECIMAL(10,2) NOT NULL                COMMENT '支付金额',
    `pay_method`    VARCHAR(20)   DEFAULT 'ALIPAY'        COMMENT '支付方式',
    `status`        TINYINT       DEFAULT 1               COMMENT '1-待支付 2-成功 3-失败',
    `trade_no`      VARCHAR(64)   DEFAULT NULL            COMMENT '交易流水号',
    `pay_time`      DATETIME      DEFAULT NULL            COMMENT '支付时间',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- ============================================================
-- 管理员表
-- ============================================================
CREATE TABLE IF NOT EXISTS `admin_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username`    VARCHAR(50)  NOT NULL                COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL                COMMENT '密码（BCrypt 加密）',
    `role`        VARCHAR(20)  DEFAULT 'ADMIN'         COMMENT '角色',
    `status`      TINYINT      DEFAULT 1               COMMENT '状态：0-禁用 1-正常',
    `deleted`     TINYINT      DEFAULT 0               COMMENT '逻辑删除',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- ============================================================
-- 操作日志表
-- ============================================================
CREATE TABLE IF NOT EXISTS `operation_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `admin_id`    BIGINT       NOT NULL                COMMENT '管理员ID',
    `module`      VARCHAR(50)  NOT NULL                COMMENT '操作模块',
    `action`      VARCHAR(50)  NOT NULL                COMMENT '操作动作',
    `detail`      TEXT         DEFAULT NULL            COMMENT '操作详情',
    `ip`          VARCHAR(50)  DEFAULT NULL            COMMENT '操作IP',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_admin_id` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ============================================================
-- 测试数据请通过 API 或 MySQL 客户端手动插入
-- 示例: curl -X POST http://localhost:8080/product -H "Content-Type: application/json" -d '...'
-- ============================================================
