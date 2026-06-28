package com.mall.common.service;

import com.mall.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Token 服务 — JWT 解析 + Redis 黑名单双重验证
 *
 * ★ 黑名单机制：
 *   用户退出登录时，Token 被加入 Redis 黑名单（key=blacklist:token, 过期时间=Token剩余有效期）
 *   每次请求除了验证 JWT 签名，还要查黑名单——在黑名单中的 Token 即使未过期也拒绝
 *
 * ★ 降级策略：
 *   如果 Redis 不可用（未安装 Docker），自动跳过黑名单检查，仅依赖 JWT 签名验证
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final StringRedisTemplate redisTemplate;

    /** Redis 黑名单 key 前缀 */
    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    /** ★ 双重验证：解析 JWT + 查黑名单 */
    public Claims validateToken(String token) {
        // 第一层：JWT 签名 + 过期校验
        Claims claims = JwtUtil.parseToken(token);
        if (claims == null) {
            return null;  // Token 无效或已过期
        }

        // 第二层：Redis 黑名单检查
        if (isBlacklisted(token)) {
            log.warn("Token 在黑名单中，已失效: {}", token.substring(0, 20));
            return null;
        }

        return claims;
    }

    /** 把 Token 加入黑名单（退出登录时调用） */
    public void addToBlacklist(String token) {
        try {
            Claims claims = JwtUtil.parseToken(token);
            if (claims == null) return;

            // 计算剩余有效时间（毫秒）
            long remaining = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (remaining <= 0) return;  // 已过期，不需要加黑名单

            String key = BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(key, "1",
                    remaining, java.util.concurrent.TimeUnit.MILLISECONDS);

            log.info("Token 已加入黑名单，剩余有效期: {} 秒", remaining / 1000);
        } catch (Exception e) {
            // ★ Redis 不可用时记录警告，不影响退出流程
            log.warn("Redis 不可用，跳过黑名单写入: {}", e.getMessage());
        }
    }

    /** 检查 Token 是否在黑名单中 */
    private boolean isBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            // ★ Redis 不可用时返回 false（不拦），降级为纯 JWT 校验
            return false;
        }
    }
}
