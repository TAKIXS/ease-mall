package com.mall.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密配置
 *
 * 为什么要把 BCryptPasswordEncoder 声明为 Bean？
 *   1. 单例：整个应用只需要一个加密器实例，不用每次 new
 *   2. 可管理：交给 Spring 管理生命周期
 *   3. 可替换：以后想换加密算法，只改这一个地方
 *
 * BCrypt 原理（了解即可）：
 *   同样的密码每次加密结果都不一样（因为内置随机"盐值"），
 *   所以即使两个用户密码相同，数据库里存的密文也不同。
 *   验证时用 matches(明文, 密文) 方法，它会自动提取盐值再比较。
 */
@Configuration
public class PasswordConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // 构造函数参数：加密强度（4~31，默认10）
        // 数字越大越安全，但也越慢。10 是平衡点。
        return new BCryptPasswordEncoder(10);
    }
}
