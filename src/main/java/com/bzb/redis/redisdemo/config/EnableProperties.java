package com.bzb.redis.redisdemo.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName EnableProperties
 * @Description
 * @Author baizhibin
 * @Date 2020/3/28 20:14
 * @Version 1.0
 */
@Configuration
@EnableConfigurationProperties(UserProperties.class)
public class EnableProperties {
}
