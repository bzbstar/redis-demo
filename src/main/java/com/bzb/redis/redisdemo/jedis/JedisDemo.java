package com.bzb.redis.redisdemo.jedis;

import com.bzb.redis.redisdemo.model.Club;
import com.bzb.redis.redisdemo.serializer.ProtostuffSerializer;
import redis.clients.jedis.Jedis;

import java.util.Date;

public class JedisDemo {

    public static void main(String[] args) {
        // 生成jedis实例，和指定的redis实例通信
        try (Jedis jedis = new Jedis("master136", 6379)) {

            ProtostuffSerializer protostuffSerializer = new ProtostuffSerializer();

            //System.out.println(jedis.set("hello", "world"));
            //System.out.println(jedis.get("hello"));
            //System.out.println(jedis.incr("counter"));
            //
            //jedis.hset("myhash", "f1", "v1");
            //jedis.hset("myhash", "f2", "f3");
            //System.out.println(jedis.hgetAll("myhash"));

            // list
            //jedis.rpush("mylist", "1");
            //jedis.rpush("mylist", "2");
            //jedis.rpush("mylist", "3");
            //System.out.println(jedis.lrange("mylist", 0, -1));

            // set

            //jedis.sadd("myset", "a");
            //jedis.sadd("myset", "b");
            //jedis.sadd("myset", "c");
            //System.out.println(jedis.smembers("myset"));

            // zset
            //jedis.zadd("myzset", 99, "tom");
            //jedis.zadd("myzset", 66, "peter");
            //jedis.zadd("myzset", 33.5, "james");
            //System.out.println(jedis.zrangeWithScores("myzset", 0, -1));

            String key = "club:1";

            Club club = new Club(1, "AC", "米兰", new Date(), 1);

            byte[] clubBytes = protostuffSerializer.serialize(club);
            jedis.set(key.getBytes(), clubBytes);

            byte[] resultBytes = jedis.get(key.getBytes());
            Club resultClub = protostuffSerializer.deserialize(resultBytes);
            System.out.println(resultClub);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
