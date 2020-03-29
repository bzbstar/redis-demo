package com.bzb.redis.redisdemo.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName JedisPoolDemo
 * @Description Jedis连接池
 * @Author baizhibin
 * @Date 2020/3/17 21:47
 * @Version 1.0
 */
public class JedisPoolDemo {
    public static void main(String[] args) {

        // common-pool连接池配置
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        // Jedis连接池
        JedisPool jedisPool = new JedisPool(poolConfig, "master136", 6379);

        // 从池中获取连接
        Jedis jedis = jedisPool.getResource();
        try {
            //System.out.println(jedis.get("hello"));

            // 模拟批量删除，不是原子的
            //List<String> keys = Arrays.asList("a", "b", "c");
            //mdel(jedis, keys);

            //String script = "return redis.call('get', KEYS[1])";
            //
            //Object result = jedis.eval(script, 1, "hello");
            //
            //System.out.println(result);
            //
            //String sha1 = jedis.scriptLoad(script);
            //System.out.println(jedis.evalsha(sha1, 1, "hello"));

            // 客户端API
            String key = "hello";
            System.out.println(jedis.get(key));

            TimeUnit.SECONDS.sleep(31);

            System.out.println(jedis.ping());

            TimeUnit.SECONDS.sleep(5);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }


    }

    private static void mdel(Jedis jedis, List<String> keys) {
        // 生成pipeline对象
        Pipeline pipeline = jedis.pipelined();
        // 添加命令，此时并没有执行
        //for (String key : keys) {
        //    pipeline.del(key);
        //}
        //
        //// 真正执行
        //List<Object> results = pipeline.syncAndReturnAll();
        //for (Object result : results) {
        //    System.out.println(result);
        //}

        pipeline.set("hello", "world");
        pipeline.incr("counter");
        List<Object> resultList = pipeline.syncAndReturnAll();
        for (Object object : resultList) {
            System.out.println(object);
        }

    }
}
