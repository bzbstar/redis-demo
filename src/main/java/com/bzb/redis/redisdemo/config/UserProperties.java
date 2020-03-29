package com.bzb.redis.redisdemo.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName UserProperties
 * @Description
 * @Author baizhibin
 * @Date 2020/3/28 20:09
 * @Version 1.0
 */
@ConfigurationProperties(prefix = "user")
@Setter
@Getter
@ToString
public class UserProperties {
    private String name;
    private String password;
}
