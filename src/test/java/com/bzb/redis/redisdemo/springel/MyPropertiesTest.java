package com.bzb.redis.redisdemo.springel;

import com.bzb.redis.redisdemo.RedisDemoApplicationTests;
import com.bzb.redis.redisdemo.config.UserProperties;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MyPropertiesTest extends RedisDemoApplicationTests {

    @Autowired
    private MyProperties myProperties;

    @Autowired
    private UserProperties userProperties;

    @Test
    public void test() {
        System.out.println(myProperties);
    }

    @Test
    public void test2() {
        System.out.println(userProperties);
    }
}