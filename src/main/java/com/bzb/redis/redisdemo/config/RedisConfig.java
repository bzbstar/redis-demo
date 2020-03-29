package com.bzb.redis.redisdemo.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName RedisConfig
 * @Description Redis配置
 * @Author baizhibin
 * @Date 2020/3/28 16:00
 * @Version 1.0
 */
@Configuration
@EnableCaching // 开启缓存
@Slf4j
public class RedisConfig extends CachingConfigurerSupport {

    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;

    @Value("${spring.redis.timeout}")
    private int timeout;

    /**
     * @return org.springframework.cache.interceptor.KeyGenerator
     * @Author bzb
     * @Description Key的生成策略
     * @Date 2020/3/28 16:23
     * @Param
     **/
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName())
                    .append(method.getName());
            Arrays.stream(params).forEach(param -> sb.append(param));
            log.info("keyGenerator: {}", sb.toString());
            return sb.toString();
        };
    }

    /**
     * Redis集群的配置
     * @return RedisClusterConfiguration
     * @throws
     */
    @Bean
    public RedisClusterConfiguration redisClusterConfiguration(){
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        //Set<RedisNode> clusterNodes
        String[] serverArray = clusterNodes.split(",");
        Set<RedisNode> nodes = new HashSet<RedisNode>();
        for(String ipPort:serverArray){
            String[] ipAndPort = ipPort.split(":");
            nodes.add(new RedisNode(ipAndPort[0].trim(),Integer.valueOf(ipAndPort[1])));
        }
        redisClusterConfiguration.setClusterNodes(nodes);
        //redisClusterConfiguration.setMaxRedirects(maxRedirects);
        //redisClusterConfiguration.setPassword(RedisPassword.of(password));
        return redisClusterConfiguration;
    }

    /**
     * @param
     * @return
     * @Description:redis连接工厂类
     * @date 2018/10/25 19:45
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        //集群模式
        JedisConnectionFactory  factory = new JedisConnectionFactory(redisClusterConfiguration);
        return factory;
    }

    @Autowired
    private RedisClusterConfiguration redisClusterConfiguration;

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    @Bean
    public CacheManager cacheManager() {
        // 缓存配置对象
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // 设置缓存的默认超时时间
                .disableCachingNullValues() // 不缓存空值
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))         //设置key序列化器
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer((valueSerializer())));  //设置value序列化器

        return  RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(jedisConnectionFactory))
                .cacheDefaults(redisCacheConfiguration).build();

    }

    /**
     * 配置RedisTemplate
     * 设置添加序列化器
     * key 使用string序列化器
     * value 使用Json序列化器
     * 还有一种简答的设置方式，改变defaultSerializer对象的实现。
     *
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        //StringRedisTemplate的构造方法中默认设置了stringSerializer
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setKeySerializer(keySerializer());
        template.setHashKeySerializer(keySerializer());
        template.setValueSerializer(valueSerializer());
        template.setHashValueSerializer(valueSerializer());
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }

    public RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    public RedisSerializer<Object> valueSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    @Bean
    @Primary
    public JedisCluster getJedisCluster() {
        // 建议使用单例

        // 初始化所有节点
        Set<HostAndPort> jedisClusterNode = new HashSet<>();
        jedisClusterNode.add(new HostAndPort("master136", 6379));
        //jedisClusterNode.add(new HostAndPort("master136", 6380));
        //jedisClusterNode.add(new HostAndPort("master136", 6385));
        // 初始化common-pool连接池，并设置相关参数
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        // 初始JedisCluster
        JedisCluster jedisCluster = new JedisCluster(jedisClusterNode, 1000, 1000, 5, poolConfig) {
            public JedisSlotBasedConnectionHandler getConnectionHandler() {
                return (JedisSlotBasedConnectionHandler) super.connectionHandler;
            }
        };
        return jedisCluster;
    }

    //@Bean
    //@ConfigurationProperties(prefix="spring.redis")
    //public JedisPoolConfig getRedisConfig(){
    //    JedisPoolConfig config = new JedisPoolConfig();
    //    return config;
    //}
}
