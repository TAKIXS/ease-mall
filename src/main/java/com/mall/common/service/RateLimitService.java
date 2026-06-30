package com.mall.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 简易限流服务 — Redis 计数器
 *
 * 原理：key = "limit:login:{ip}", 每次失败 +1, 超过阈值则拒绝
 * 成功后删除计数（或过期自动清理）
 */
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    private static final int MAX_ATTEMPTS = 5;
    private static final int WINDOW_MINUTES = 5;

    /**
     * 检查是否被限流
     * @return true=被限制, false=允许
     */
    public boolean isBlocked(String key) {
        String count = redisTemplate.opsForValue().get(key);
        if (count == null) return false;
        return Integer.parseInt(count) >= MAX_ATTEMPTS;
    }

    /** 记录一次失败 */
    public void recordFailure(String key) {
        try {
            redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, WINDOW_MINUTES, TimeUnit.MINUTES);
        } catch (Exception ignored) { /* Redis 不可用时跳过限流 */ }
    }

    /** 成功后清除计数 */
    public void clear(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception ignored) {}
    }
}
