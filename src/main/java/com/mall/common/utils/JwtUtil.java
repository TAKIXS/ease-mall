package com.mall.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT（JSON Web Token）工具类
 *
 * 密钥不再硬编码在代码里，而是由 JwtConfig 启动时调用 init() 传入
 */
@Slf4j
public class JwtUtil {

    /** JWT 签名密钥（由 JwtConfig.init() 设置） */
    private static SecretKey KEY;

    /** Token 有效期（毫秒，由 JwtConfig.init() 设置） */
    private static long EXPIRE_TIME;

    /**
     * ★ 初始化方法 —— 由 JwtConfig 在启动时调用
     *
     * @param secret     从 application.yml 读取的 jwt.secret
     * @param expireDays 从 application.yml 读取的 jwt.expire-days
     */
    public static void init(String secret, int expireDays) {
        KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        EXPIRE_TIME = expireDays * 24L * 60 * 60 * 1000;
        log.info("JWT 初始化完成，Token 有效期: {} 天", expireDays);
    }

    // ==================== 生成 Token ====================

    public static String generateToken(Long userId, String username, Map<String, Object> claims) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRE_TIME);

        JwtBuilder builder = Jwts.builder()
                .id(String.valueOf(userId))
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(KEY);

        if (claims != null && !claims.isEmpty()) {
            builder.claims(claims);
        }
        return builder.compact();
    }

    public static String generateToken(Long userId, String username) {
        return generateToken(userId, username, null);
    }

    // ==================== 解析 Token ====================

    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token 已过期: {}", e.getMessage());
            return null;
        } catch (JwtException e) {
            log.warn("Token 解析失败: {}", e.getMessage());
            return null;
        }
    }

    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return Long.valueOf(claims.getId());
    }

    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.getSubject();
    }

    public static boolean isTokenValid(String token) {
        return parseToken(token) != null;
    }
}
