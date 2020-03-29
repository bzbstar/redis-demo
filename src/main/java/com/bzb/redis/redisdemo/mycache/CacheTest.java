package com.bzb.redis.redisdemo.mycache;

import com.bzb.redis.redisdemo.model.MyUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName CacheTest
 * @Description 缓存测试
 * @Author baizhibin
 * @Date 2020/3/28 14:30
 * @Version 1.0
 */
@RestController
@RequestMapping("/cache")
@Slf4j
public class CacheTest {

    @Cacheable(value = "myCache", condition = "#id < 10")
    @GetMapping("/users/{id}")
    public MyUser get(@PathVariable Long id) {
        log.info("get方法执行了....");
        if (id < 10) {
            return getUser1();
        }
        return getUser2();
    }

    @CachePut(value = "myCache", key = "#result.id")
    @PostMapping("/users/")
    public MyUser update() {
        log.info("update方法执行了...");
        return MyUser.builder().id(1L).name("xjj").age(28).city("河南").email("xjj@139.com").build();
    }

    @DeleteMapping("/users/{id}")
    @CacheEvict(value = "myCache")
    public void delete(@PathVariable Long id) {
        log.info("delete...方法执行了");
    }

    public MyUser getUser1() {
        return MyUser.builder().id(1L).name("bzb").age(28).city("河南").email("15851485932@139.com").build();
    }

    public MyUser getUser2() {
        return MyUser.builder().id(12L).name("xjj").age(35).city("河南").email("xjj@139.com").build();
    }
}
