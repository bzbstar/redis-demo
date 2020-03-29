package com.bzb.redis.redisdemo.singleRedis;

import com.bzb.redis.redisdemo.RedisDemoApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Arrays;
import java.util.List;

public class SingleRedisTest extends RedisDemoApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("name", "xjj");
        System.out.println(valueOperations.get("name"));

        ListOperations listOperations = redisTemplate.opsForList();
        //listOperations.leftPushAll("mylist", "a", "b", "c", 0, 1);
        System.out.println(listOperations.range("mylist", 0, 4));

        BoundHashOperations boundHashOperations = redisTemplate.boundHashOps("myhash");
        //boundHashOperations.put("a", "b");
        //boundHashOperations.put("c", 2);
        System.out.println(boundHashOperations.get("a"));

        // 获取底层的Jedis连接
    }

    /**
     * 测试事务
     */
    @Test
    public void testMulti() {
        String key = "key1";
        redisTemplate.opsForValue().set(key, "value1");
        List list = (List) redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                // 监控key
                redisOperations.watch(key);

                // 开启事务
                redisOperations.multi();

                redisOperations.opsForValue().increment(key, 1);
                redisOperations.opsForValue().set("key2", "value2");
                // 命令没有执行，只是放入队列中
                System.out.println(redisOperations.opsForValue().get("key2"));
                redisOperations.opsForValue().set("key3", "value3");
                return redisOperations.exec();
            }
        });
        System.out.println(list);
    }

    /**
     * pipeline
     */
    @Test
    public void pipelineTest() {
        List list = redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                for (int i = 0; i < 10000; i++) {
                    redisOperations.opsForValue().set("pipeline_" + i, "value_" + i);
                    // 命令还未执行
                    System.out.println(redisOperations.opsForValue().get("pipeline_" + i));
                }
                return null;
            }
        });
        System.out.println(list);
    }

    /**
     * 通过回调函数在一个方法中执行
     */
    @Test
    public void testCall() {
        redisTemplate.execute((RedisCallback) redisConnection -> {
            redisConnection.set("name".getBytes(), "xjj1".getBytes());
            byte[] bytes = redisConnection.get("name".getBytes());
            String s = new String(bytes);
            System.out.println(s);
            redisConnection.hSet("myhash".getBytes(), "field1".getBytes(), "valu1".getBytes());
            System.out.println(redisConnection.hGet("myhash".getBytes(), "field1".getBytes()).toString());
            return null;
        });

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.opsForValue().set("name", "bzb");
                return null;
            }
        });
    }

    /**
     * lua脚本
     */
    @Test
    public void luaTest() {
        DefaultRedisScript<String> rs = new DefaultRedisScript<>();
        // 设置脚本
        rs.setScriptText("return 'Hello World'");
        // 定义返回类型，不设置不会返回结果
        rs.setResultType(String.class);
        RedisSerializer<String> stringRedisSerializer = redisTemplate.getStringSerializer();
        String result = (String) redisTemplate.execute(rs, stringRedisSerializer, stringRedisSerializer, null);
        System.out.println(result);
    }

    @Test
    public void luaTest2() {
        lua("key1", "key2", "1", "1");
    }

    public void lua(String key1, String key2, String arg1, String arg2) {
        // 定义lua脚本
        String lua = "redis.call('set', KEYS[1], ARGV[1]) \n"
                + "redis.call('set', KEYS[2], ARGV[2]) \n"
                + "local str1 = redis.call('get', KEYS[1]) \n"
                + "local str2 = redis.call('get', KEYS[2]) \n"
                + "if str1 == str2 then \n"
                + "return 1 \n"
                + "end \n"
                + "return 0 \n";
        System.out.println(lua);

        DefaultRedisScript<Long> rs = new DefaultRedisScript<>();
        rs.setScriptText(lua);
        rs.setResultType(Long.class);
        RedisSerializer<String> stringRedisSerializer = redisTemplate.getStringSerializer();
        Long result = (Long) redisTemplate.execute(rs, stringRedisSerializer, stringRedisSerializer, Arrays.asList(key1, key2), arg1, arg2);
        System.out.println(result);
    }

}