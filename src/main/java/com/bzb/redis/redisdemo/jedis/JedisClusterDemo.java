package com.bzb.redis.redisdemo.jedis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;
import redis.clients.util.JedisClusterCRC16;

import java.util.*;

/**
 * @ClassName JedisClusterDemo
 * @Description jedis集群
 * @Author baizhibin
 * @Date 2020/3/24 21:28
 * @Version 1.0
 */
@Slf4j
public class JedisClusterDemo {

    public static void main(String[] args) {

        // 建议使用单例

        // 初始化所有节点
        Set<HostAndPort> jedisClusterNode = new HashSet<>();
        jedisClusterNode.add(new HostAndPort("master136", 6379));
        //jedisClusterNode.add(new HostAndPort("master136", 6380));
        //jedisClusterNode.add(new HostAndPort("master136", 6385));
        // 初始化common-pool连接池，并设置相关参数
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        // 初始JedisCluster
        JedisCluster jedisCluster = new JedisCluster(jedisClusterNode, 1000, 1000, 5, poolConfig);

        jedisCluster.set("hello", "world");

        System.out.println(jedisCluster.get("hello"));

        //delRedisClusterByPattern(jedisCluster, "user*", 1000);

        //pipelineOnAsk();

    }

    public static void mgetOnAsk() {
        JedisCluster jedisCluster = new JedisCluster(new HostAndPort("master136", 6379));
        List<String> results = jedisCluster.mget("key:test:68253", "key:test:79212");
        System.out.println(results);
        results = jedisCluster.mget("key:test:5028", "key:test:68253", "key:test:79212");
        System.out.println(results);
    }

    public static void pipelineOnAsk() {
        // 暴漏slot到jedis的查询
        JedisSlotBasedConnectionHandler connectionHandler =  new JedisCluster(new
                HostAndPort ("127.0.0.1", 6379)) {
            public JedisSlotBasedConnectionHandler getConnectionHandler() {
                return (JedisSlotBasedConnectionHandler) super.connectionHandler;
            }
        }.getConnectionHandler();

        List<String> keys = Arrays.asList("key:test:5028", "key:test:68253", "key:test:79212");

        Jedis jedis = connectionHandler.getConnectionFromSlot(JedisClusterCRC16.getSlot(keys.get(2)));

        Pipeline pipeline = jedis.pipelined();
        for (String key : keys) {
            pipeline.get(key);
        }

        try {
            List<Object> results = pipeline.syncAndReturnAll();
            for (Object result : results) {
                System.out.println(result);
            }
        } finally {
            jedis.close();
        }

    }

    /**
     * @Author bzb
     * @Description 事务执行
     * @Date 2020/3/24 22:09
     * @Param
 * @param jedisCluster
     * @return void
     **/
    public void transact(JedisCluster jedisCluster) {
        String hashtag = "{user}";
        // 用户A的关注列表
        String userAFollowKey = hashtag + ":a:follow";
        // 用户B的粉丝表
        String userBFollowKey = hashtag + ":b:fans";
        // 计算hashtag对应的slot
        int slot = JedisClusterCRC16.getSlot(hashtag);
        // 获取slot的JedisPool
    }

    /**
     * 从JedisCluster删除指定pattern的键
     * @param jedisCluster
     * @param pattern
     * @param scanCounter
     */
    public static void delRedisClusterByPattern(JedisCluster jedisCluster, String pattern, int scanCounter) {

        // 获取所有节点的JedisPool
        Map<String, JedisPool> jedisPoolMap = jedisCluster.getClusterNodes();

        for (Map.Entry<String, JedisPool> entry : jedisPoolMap.entrySet()) {
            // 获取每个节点的jedis连接
            Jedis jedis = entry.getValue().getResource();
            // 只删除主节点的数据

            if (!isMaster(jedis)) {
                continue;
            }

            // 使用Pipeline删除
            Pipeline pipeline = jedis.pipelined();

            // 指定扫描参数：扫描个数和pattern
            ScanParams scanParams = new ScanParams().count(scanCounter).match(pattern);
            String cursor = "0";
            while (true) {
                // 执行扫描
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                List<String> keys = scanResult.getResult();
                if (keys != null && !keys.isEmpty()) {
                    for (String key : keys) {
                        pipeline.del(key);
                    }
                    // 批量删除
                    pipeline.syncAndReturnAll();
                }

                cursor = scanResult.getStringCursor();
                // 为0则扫描完毕
                if ("0".equals(cursor)) {
                    break;
                }
            }

        }


    }

    /**
     * 判断当前节点是否为主节点
     * @param jedis
     * @return
     */
    private static boolean isMaster(Jedis jedis) {
        String replication = jedis.info("Replication");
        log.info("info replication: {}", replication);

        String[] data = replication.split("\r\n");
        for (String line : data) {
            if ("role:master".equals(line.trim())) {
                return true;
            }
        }
        return false;
    }

}
