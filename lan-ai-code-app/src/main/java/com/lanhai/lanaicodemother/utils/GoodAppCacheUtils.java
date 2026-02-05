package com.lanhai.lanaicodemother.utils;

import jakarta.annotation.Resource;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 精选应用缓存工具类
 * 用于管理精选应用列表的缓存
 */
@Component
public class GoodAppCacheUtils {

    /**
     * 缓存名称
     */
    private static final String CACHE_NAME = "good_app_page";
    @Resource
    private CacheManager cacheManager;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 删除所有精选应用列表缓存
     * 由于缓存key是基于查询请求的MD5值，无法精确匹配，所以需要删除整个缓存区域
     */
    public void evictAllGoodAppCache() {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * 使用Redis直接删除缓存（更彻底的方式）
     * 删除所有以 "good_app_page::" 开头的key
     */
    public void evictAllGoodAppCacheByRedis() {
        try {
            // 优先使用 StringRedisTemplate
            Set<String> keys = stringRedisTemplate.keys(CACHE_NAME + "::*");
            if (keys != null && !keys.isEmpty()) {
                stringRedisTemplate.delete(keys);
            }
        } catch (Exception e) {
            // 记录日志但不抛出异常，避免影响主流程
            // log.error("删除精选应用缓存失败", e);
            // 降级使用 CacheManager 的方式
            evictAllGoodAppCache();
        }
    }
}

