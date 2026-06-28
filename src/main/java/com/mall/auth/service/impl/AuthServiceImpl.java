package com.mall.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.auth.dto.LoginDTO;
import com.mall.auth.dto.RegisterDTO;
import com.mall.auth.entity.User;
import com.mall.auth.mapper.UserMapper;
import com.mall.auth.service.AuthService;
import com.mall.auth.vo.LoginVO;
import com.mall.common.exception.BusinessException;
import com.mall.common.service.TokenService;
import com.mall.common.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 *
 * 核心注解：
 *   @Service               = 告诉 Spring：这是一个 Service，帮我创建和管理它（单例）
 *   @RequiredArgsConstructor = Lombok：自动生成带 final 字段的构造器，用于依赖注入
 *   @Transactional          = 声明式事务：方法内所有数据库操作要么全成功，要么全回滚
 *   @Slf4j                  = Lombok：自动生成 log 对象，可以直接用 log.info() 打印日志
 *
 * 依赖注入解释：
 *   下面的 userMapper 和 passwordEncoder 不用写 @Autowired，
 *   因为 @RequiredArgsConstructor 会基于 final 字段自动生成含参构造器，
 *   Spring 会自动把 Bean 注入进来（构造器注入，最推荐的方式）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    // ==================== 注册 ====================

    @Override
    @Transactional  // 保证原子性：中间任何一步失败，前面已执行的 SQL 自动回滚
    public void register(RegisterDTO dto) {
        // 1. 检查用户名是否已存在
        //    LambdaQueryWrapper 是 MyBatis-Plus 的类型安全查询器，避免手写字段名字符串
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, dto.getUsername())
        );
        if (count > 0) {
            throw new BusinessException("用户名已被注册");
        }

        // 2. 把 DTO 转成 Entity（用构造器也可以，字段少时手动 set 更清晰）
        User user = new User();
        user.setUsername(dto.getUsername());
        // ★ 密码加密：绝不能把明文密码存进数据库！
        //   BCrypt 是单向哈希，即使数据库被泄露，也无法反向推导出原始密码
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getUsername());  // 默认昵称 = 用户名
        user.setStatus(1);                     // 1 = 正常状态
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 3. 插入数据库
        //    BaseMapper 自带的 insert 方法，不需要写 SQL
        int rows = userMapper.insert(user);
        if (rows <= 0) {
            throw new BusinessException("注册失败，请稍后重试");
        }

        log.info("新用户注册成功: {}", dto.getUsername());
    }

    // ==================== 登录 ====================

    @Override
    public LoginVO login(LoginDTO dto) {
        // 1. 根据用户名查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 检查账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 3. 验证密码
        //    BCryptPasswordEncoder.matches(明文, 密文)
        //    把用户输入的明文和数据库里的密文比对，BCrypt 会自动处理盐值
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 4. 生成 JWT Token
        //    把用户ID和用户名编码进 Token，后续请求从 Token 就能知道是谁
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        // 5. 更新最后登录时间
        user.setLastLogin(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户登录成功: {}", dto.getUsername());

        // 6. 构建返回对象
        return LoginVO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }

    // ==================== 退出登录 ====================
    @Override
    public void logout(String token) {
        tokenService.addToBlacklist(token);
        log.info("用户已退出，Token 加入黑名单");
    }
}
