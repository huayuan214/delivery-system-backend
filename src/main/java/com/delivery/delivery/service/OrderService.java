package com.delivery.delivery.service;

import com.delivery.delivery.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled; // 加了这行
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List; // 加了这行
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Transactional
    public String grabOrder(Long orderId, Long riderId) {
        String lockKey = "lock:order:" + orderId;
        String lockValue = UUID.randomUUID().toString();

        // 1. 尝试加锁，过期时间 10 秒
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(locked)) {
            return "手慢了，请重试";
        }

        try {
            // 2. 抢到锁，执行数据库条件更新
            int rows = orderMapper.grabOrder(orderId, riderId);
            if (rows == 0) {
                return "订单已被抢";
            }
            return "抢单成功";
        } finally {
            // 3. 释放锁：用 Lua 脚本保证“判断自己的锁”和“删除”的原子性
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) else return 0 end";
            redisTemplate.execute(
                    new DefaultRedisScript<>(script, Long.class),
                    Collections.singletonList(lockKey),
                    lockValue
            );
        }
    }

    // 确认送达
    public String completeOrder(Long orderId, Long riderId) {
        int rows = orderMapper.completeOrder(orderId, riderId);
        if (rows == 0) {
            return "送达失败，订单状态异常或非本人订单";
        }
        return "确认送达成功";
    }

    // 超时释放（定时任务）
    @Scheduled(fixedRate = 10000) // 每 10 秒执行一次
    public void releaseTimeoutOrders() {
        List<Long> timeoutOrderIds = orderMapper.selectTimeoutOrders();
        if (timeoutOrderIds != null && !timeoutOrderIds.isEmpty()) {
            for (Long orderId : timeoutOrderIds) {
                // 这里可以复用之前的 Redis 锁逻辑，保证释放时的并发安全
                String lockKey = "lock:order:" + orderId;
                String lockValue = UUID.randomUUID().toString();
                Boolean locked = redisTemplate.opsForValue()
                        .setIfAbsent(lockKey, lockValue, 5, TimeUnit.SECONDS);

                if (Boolean.TRUE.equals(locked)) {
                    try {
                        // 释放订单：状态改回 0，清空骑手
                        // 注意：为了省事，直接在这里写个 mapper 更新。
                        // 真实业务里这里应该用带条件的 UPDATE 兜底。
                        orderMapper.releaseOrder(orderId);
                        System.out.println("订单 " + orderId + " 超时，已自动释放");
                    } finally {
                        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                        redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                                Collections.singletonList(lockKey), lockValue);
                    }
                }
            }
        }
    }
}