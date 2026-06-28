-- ============================================================
-- H2 测试数据（Spring Boot 启动时自动插入）
-- ============================================================

-- 商品分类
INSERT INTO category (id, parent_id, name, sort) VALUES
(1, 0, '电子产品', 1),
(2, 0, '服装鞋帽', 2),
(3, 0, '图书音像', 3),
(4, 1, '手机', 1),
(5, 1, '电脑', 2),
(6, 2, '男装', 1),
(7, 2, '女装', 2);

-- 测试商品
INSERT INTO product (category_id, name, description, price, stock, specs) VALUES
(4, 'iPhone 15 Pro Max', '苹果最新旗舰手机，A17 Pro 芯片', 9999.00, 100, '{"颜色":"原色钛金属","存储":"256GB"}'),
(4, '华为 Mate 60 Pro', '华为旗舰手机，卫星通信', 6999.00, 50, '{"颜色":"雅丹黑","存储":"512GB"}'),
(5, 'MacBook Pro 14寸', 'M3 Pro 芯片，18GB 内存', 14999.00, 30, '{"颜色":"深空黑","内存":"18GB"}'),
(6, '男士商务夹克', '春秋季新款，修身版型', 399.00, 200, '{"颜色":"黑色","尺码":"M/L/XL"}'),
(7, '女士连衣裙', '碎花系列，夏季新款', 299.00, 150, '{"颜色":"碎花蓝","尺码":"S/M/L"}');

-- 管理员（密码 admin123，BCrypt 加密）
INSERT INTO admin_user (username, password, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'SUPER_ADMIN');
