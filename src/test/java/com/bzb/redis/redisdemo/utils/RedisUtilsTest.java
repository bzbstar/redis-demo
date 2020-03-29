package com.bzb.redis.redisdemo.utils;

import com.bzb.redis.redisdemo.RedisDemoApplicationTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
class RedisUtilsTest extends RedisDemoApplicationTests {

    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void getTest() {
        String result = redisUtils.get("name");
        log.info("key: name, value: {}", result);
    }

    @Test
    public void setTest() {
        redisUtils.set("name", "xjj");
    }

    @Test
    public void getAndSetTest() {
        String result = redisUtils.getAndSet("name", "bzb");
        log.info("getAndSet, value : {}", result);
    }

    @Test
    public void delTest() {
        redisUtils.delete("name");
        log.info("{}", redisUtils.get("name"));
    }
}