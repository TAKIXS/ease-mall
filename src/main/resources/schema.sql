-- ============================================================
-- H2 自动建表脚本（Spring Boot 启动时自动执行）
-- H2 用 MySQL 兼容模式运行，大部分语法和 MySQL 一样
-- 唯一区别：JSON 类型改用 TEXT
-- ============================================================

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    `username`    VARCHAR(50)  NOT NULL UNIQUE,
    `password`    VARCHAR(255) NOT NULL,
    `phone`       VARCHAR(20),
    `email`       VARCHAR(100),
    `nickname`    VARCHAR(50),
    `avatar`      VARCHAR(255),
    `gender`      TINYINT      DEFAULT 0,
    `status`      TINYINT      DEFAULT 1,
    `last_login`  TIMESTAMP,
    `deleted`     TINYINT      DEFAULT 0,
    `create_time` TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- 收货地址表
CREATE TABLE IF NOT EXISTS `address` (
    `id`             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    `user_id`        BIGINT       NOT NULL,
    `receiver_name`  VARCHAR(50)  NOT NULL,
    `receiver_phone` VARCHAR(20)  NOT NULL,
    `province`       VARCHAR(50)  NOT NULL,
    `city`           VARCHAR(50)  NOT NULL,
    `district`       VARCHAR(50)  NOT NULL,
    `detail`         VARCHAR(255) NOT NULL,
    `is_default`     TINYINT      DEFAULT 0,
    `deleted`        TINYINT      DEFAULT 0,
    `create_time`    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    `update_time`    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- 商品分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id`          BIGINT      AUTO_INCREMENT PRIMARY KEY,
    `parent_id`   BIGINT      DEFAULT 0,
    `name`        VARCHAR(50) NOT NULL,
    `sort`        INT         DEFAULT 0,
    `icon`        VARCHAR(255),
    `deleted`     TINYINT     DEFAULT 0,
    `create_time` TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

-- 商品表（JSON → TEXT）
CREATE TABLE IF NOT EXISTS `product` (
    `id`          BIGINT         AUTO_INCREMENT PRIMARY KEY,
    `category_id` BIGINT         NOT NULL,
    `name`        VARCHAR(100)   NOT NULL,
    `description` TEXT,
    `cover_image` VARCHAR(255),
    `images`      TEXT,                               -- H2 不支持 JSON，用 TEXT 存
    `price`       DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    `stock`       INT            NOT NULL DEFAULT 0,
    `sales`       INT            DEFAULT 0,
    `specs`       TEXT,                               -- H2 不支持 JSON，用 TEXT 存
    `status`      TINYINT        DEFAULT 1,
    `deleted`     TINYINT        DEFAULT 0,
    `create_time` TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
    `id`               BIGINT         PRIMARY KEY,
    `order_no`         VARCHAR(32)    NOT NULL UNIQUE,
    `user_id`          BIGINT         NOT NULL,
    `total_amount`     DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    `pay_amount`       DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    `status`           TINYINT        NOT NULL DEFAULT 1,
    `receiver_name`    VARCHAR(50),
    `receiver_phone`   VARCHAR(20),
    `receiver_address` VARCHAR(255),
    `remark`           VARCHAR(500),
    `pay_time`         TIMESTAMP,
    `ship_time`        TIMESTAMP,
    `complete_time`    TIMESTAMP,
    `cancel_time`      TIMESTAMP,
    `deleted`          TINYINT        DEFAULT 0,
    `create_time`      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    `update_time`      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

-- 订单项表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id`            BIGINT        AUTO_INCREMENT PRIMARY KEY,
    `order_id`      BIGINT        NOT NULL,
    `product_id`    BIGINT        NOT NULL,
    `product_name`  VARCHAR(100)  NOT NULL,
    `product_image` VARCHAR(255),
    `price`         DECIMAL(10,2) NOT NULL,
    `quantity`      INT           NOT NULL DEFAULT 1,
    `create_time`   TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

-- 支付记录表
CREATE TABLE IF NOT EXISTS `payment_record` (
    `id`          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    `order_id`    BIGINT        NOT NULL,
    `order_no`    VARCHAR(32)   NOT NULL,
    `user_id`     BIGINT        NOT NULL,
    `amount`      DECIMAL(10,2) NOT NULL,
    `pay_method`  VARCHAR(20)   DEFAULT 'ALIPAY',
    `status`      TINYINT       DEFAULT 1,
    `trade_no`    VARCHAR(64),
    `pay_time`    TIMESTAMP,
    `create_time` TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

-- 管理员表
CREATE TABLE IF NOT EXISTS `admin_user` (
    `id`          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    `username`    VARCHAR(50)  NOT NULL UNIQUE,
    `password`    VARCHAR(255) NOT NULL,
    `role`        VARCHAR(20)  DEFAULT 'ADMIN',
    `status`      TINYINT      DEFAULT 1,
    `deleted`     TINYINT      DEFAULT 0,
    `create_time` TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
    `id`          BIGINT      AUTO_INCREMENT PRIMARY KEY,
    `admin_id`    BIGINT      NOT NULL,
    `module`      VARCHAR(50) NOT NULL,
    `action`      VARCHAR(50) NOT NULL,
    `detail`      TEXT,
    `ip`          VARCHAR(50),
    `create_time` TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);
