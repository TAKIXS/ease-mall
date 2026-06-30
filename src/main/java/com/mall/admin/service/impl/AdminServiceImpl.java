package com.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.admin.dto.AdminLoginDTO;
import com.mall.admin.entity.AdminUser;
import com.mall.admin.mapper.AdminUserMapper;
import com.mall.admin.service.AdminService;
import com.mall.admin.vo.AdminLoginVO;
import com.mall.admin.vo.DashboardVO;
import com.mall.auth.entity.User;
import com.mall.auth.mapper.UserMapper;
import com.mall.common.exception.BusinessException;
import com.mall.common.utils.JwtUtil;
import com.mall.order.entity.Order;
import com.mall.order.mapper.OrderMapper;
import com.mall.product.entity.Product;
import com.mall.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    /**
     * ★ 应用启动时自动检查：如果管理员表为空，创建一个默认管理员
     *    默认账号: admin / admin123
     */
    @PostConstruct
    public void initAdmin() {
        Long count = adminUserMapper.selectCount(null);
        if (count == 0) {
            AdminUser admin = new AdminUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("SUPER_ADMIN");
            admin.setStatus(1);
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());
            adminUserMapper.insert(admin);
            System.out.println("========================================");
            System.out.println("  默认管理员已创建: admin / admin123");
            System.out.println("========================================");
        }
    }

    private final AdminUserMapper adminUserMapper;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    // ==================== 管理员登录 ====================
    @Override
    public AdminLoginVO login(AdminLoginDTO dto) {
        // 查管理员
        AdminUser admin = adminUserMapper.selectOne(
                new LambdaQueryWrapper<AdminUser>()
                        .eq(AdminUser::getUsername, dto.getUsername())
        );
        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (admin.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        // 验密码（复用 BCryptPasswordEncoder）
        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 生成 JWT，含 role 标记用于管理端鉴权
        String token = JwtUtil.generateToken(admin.getId(), admin.getUsername(),
                Map.of("role", admin.getRole()));

        return AdminLoginVO.builder()
                .token(token)
                .adminId(admin.getId())
                .username(admin.getUsername())
                .role(admin.getRole())
                .build();
    }

    // ==================== ★ 数据看板 ====================
    @Override
    public DashboardVO dashboard() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 总用户数
        Long totalUsers = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getStatus, 1)
        );

        // 总订单数
        Long totalOrders = orderMapper.selectCount(null);

        // 今日新增订单
        Long todayOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .between(Order::getCreateTime, todayStart, todayEnd)
        );

        // 总交易额（不包括已取消的订单）
        BigDecimal totalGMV = BigDecimal.ZERO;
        for (Order order : orderMapper.selectList(
                new LambdaQueryWrapper<Order>().ne(Order::getStatus, 5))) {
            totalGMV = totalGMV.add(order.getPayAmount());
        }

        // 今日交易额
        BigDecimal todayGMV = BigDecimal.ZERO;
        for (Order order : orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .ne(Order::getStatus, 5)
                        .between(Order::getCreateTime, todayStart, todayEnd))) {
            todayGMV = todayGMV.add(order.getPayAmount());
        }

        // 待发货数（status = 2，已支付）
        Long pendingShip = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().eq(Order::getStatus, 2)
        );

        // 商品总数
        Long totalProducts = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getStatus, 1)
        );

        return DashboardVO.builder()
                .totalUsers(totalUsers)
                .totalOrders(totalOrders)
                .todayOrders(todayOrders)
                .totalGMV(totalGMV)
                .todayGMV(todayGMV)
                .pendingShip(pendingShip)
                .totalProducts(totalProducts)
                .build();
    }

    // ==================== 用户列表 ====================
    @Override
    public Page<User> listUsers(int page, int size) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(pageParam, wrapper);
    }

    // ==================== 启用/禁用用户 ====================
    @Override
    public void toggleUserStatus(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        userMapper.updateById(user);
    }
}
