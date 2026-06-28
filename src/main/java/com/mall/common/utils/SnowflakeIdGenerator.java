package com.mall.common.utils;

/**
 * 雪花算法 ID 生成器
 *
 * 为什么需要它？
 *   分布式系统中，多台服务器同时生成 ID，
 *   如果用数据库自增 ID 会产生冲突。
 *   雪花算法保证生成的 ID 全局唯一，且趋势递增（数据库索引友好）。
 *
 * ID 结构（64 位）：
 *   1 位  — 符号位（始终为 0，保证 ID 为正数）
 *   41 位 — 时间戳（毫秒级，可用 69 年）
 *   10 位 — 工作机器 ID（5 位数据中心 + 5 位机器 = 最多 1024 台机器）
 *   12 位 — 序列号（同一毫秒内最多生成 4096 个不同 ID）
 *
 * 使用示例：
 *   SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
 *   long orderId = generator.nextId();
 */
public class SnowflakeIdGenerator {

    /** 起始时间戳（2024-01-01 00:00:00），避免浪费时间戳位数 */
    private static final long START_TIMESTAMP = 1704067200000L;

    /** 每一部分占用的位数 */
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    /** 最大值 */
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);  // 31
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);          // 31
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);            // 4095

    /** 每一部分向左的位移 */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;                           // 12
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;      // 17
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS; // 22

    /** 数据中心 ID */
    private final long datacenterId;

    /** 工作机器 ID */
    private final long workerId;

    /** 序列号（同一毫秒内的计数器） */
    private long sequence = 0L;

    /** 上次生成 ID 的时间戳 */
    private long lastTimestamp = -1L;

    /**
     * 构造器
     *
     * @param datacenterId 数据中心 ID (0~31)
     * @param workerId     工作机器 ID (0~31)
     */
    public SnowflakeIdGenerator(long datacenterId, long workerId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("数据中心 ID 不能大于 " + MAX_DATACENTER_ID + " 或小于 0");
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("工作机器 ID 不能大于 " + MAX_WORKER_ID + " 或小于 0");
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    /**
     * 生成下一个唯一 ID（线程安全）
     */
    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();

        // 时钟回拨检测：如果当前时间小于上次生成 ID 的时间，说明服务器时钟被调整过
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException(
                    "时钟回拨了 " + (lastTimestamp - currentTimestamp) + " 毫秒，拒绝生成 ID");
        }

        // 同一毫秒内，序列号递增
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 如果这一毫秒的序列号用完了，等待下一毫秒
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒，序列号重置为 0
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        // 组装 64 位 ID
        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /** 自旋等待直到下一毫秒 */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
