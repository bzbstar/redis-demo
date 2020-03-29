package com.bzb.redis.redisdemo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @ClassName RedisUtils
 * @Description redis操作工具类
 * @Author baizhibin
 * @Date 2020/3/27 21:09
 * @Version 1.0
 */
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public String get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void set(final String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 更新缓存, 返回之前的值
     *
     * @param key
     * @param value
     * @return
     */
    public String getAndSet(final String key, String value) {
        try {
            return redisTemplate.opsForValue().getAndSet(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除缓存
     *
     * @param key
     * @return
     */
    public boolean delete(final String key) {
        return redisTemplate.delete(key);
    }

}
