package com.mall.common.config;

import com.mall.common.utils.JwtUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置类
 *
 * 启动时自动从 application.yml 读取 jwt.secret 和 jwt.expire-days，
 * 然后调用 JwtUtil.init() 初始化密钥。
 *
 * 为什么这样做？
 *   密钥不再硬编码在代码里，而是放在配置文件。
 *   配置文件可以按环境切换（dev / prod），生产环境用更强的密钥。
 *
 *   .gitignore 里可以加 application-prod.yml 来保护生产密钥，
 *   但 application.yml 里的 dev 密钥提交到 Git 是安全的。
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")  // 读取 application.yml 中 jwt.* 的配置
public class JwtConfig {

    private String secret;
    private int expireDays;

    /**
     * 初始化 JWT 工具类
     * 优先使用环境变量 JWT_SECRET，无则回退到配置文件
     * 生产环境: export JWT_SECRET="your-256-bit-secret"
     */
    @PostConstruct
    public void init() {
        String actualSecret = System.getenv("JWT_SECRET");
        if (actualSecret == null || actualSecret.isBlank()) {
            actualSecret = secret;
        }
        JwtUtil.init(actualSecret, expireDays);
    }
}
