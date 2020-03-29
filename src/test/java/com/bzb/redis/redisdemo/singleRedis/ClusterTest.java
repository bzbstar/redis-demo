package com.bzb.redis.redisdemo.singleRedis;

import com.bzb.redis.redisdemo.RedisDemoApplicationTests;
import com.bzb.redis.redisdemo.config.RedisConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisCluster;

/**
 * @ClassName ClusterTest
 * @Description 集群
 * @Author baizhibin
 * @Date 2020/3/29 11:17
 * @Version 1.0
 */
public class ClusterTest extends RedisDemoApplicationTests {

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private RedisConfig redisConfig;

    @Test
    public void test() {
        jedisCluster.set("bzb", "name");
        System.out.println(jedisCluster.get("bzb"));
    }
}
