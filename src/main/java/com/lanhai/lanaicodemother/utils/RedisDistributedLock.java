package com.lanhai.lanaicodemother.utils;

import com.lanhai.lanaicodemother.exception.ErrorCode;
import com.lanhai.lanaicodemother.exception.ThrowUtils;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis分布式锁工具类
 * 使用Redisson实现分布式锁
 *
 * @author 积分系统
 */
@Component
public class RedisDistributedLock {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 尝试获取锁
     *
     * @param lockKey  锁的key
     * @param waitTime  等待时间（秒）
     * @param leaseTime 持有锁时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放锁
     *
     * @param lockKey 锁的key
     */
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 执行带锁的业务逻辑
     *
     * @param lockKey   锁的key
     * @param waitTime  等待时间（秒）
     * @param leaseTime 持有锁时间（秒）
     * @param supplier  业务逻辑
     * @param <T>       返回值类型
     * @return 业务逻辑执行结果
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        if (!tryLock(lockKey, waitTime, leaseTime)) {
            ThrowUtils.throwIf(true, ErrorCode.OPERATION_ERROR, "操作过于频繁,请稍后重试");
        }
        try {
            return supplier.get();
        } finally {
            unlock(lockKey);
        }
    }

    /**
     * 执行带锁的业务逻辑（无返回值）
     *
     * @param lockKey   锁的key
     * @param waitTime  等待时间（秒）
     * @param leaseTime 持有锁时间（秒）
     * @param runnable  业务逻辑
     */
    public void executeWithLock(String lockKey, long waitTime, long leaseTime, Runnable runnable) {
        if (!tryLock(lockKey, waitTime, leaseTime)) {
            ThrowUtils.throwIf(true, ErrorCode.OPERATION_ERROR, "操作过于频繁,请稍后重试");
        }
        try {
            runnable.run();
        } finally {
            unlock(lockKey);
        }
    }

}

