package com.bzb.redis.redisdemo.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * @ClassName Student
 * @Description
 * @Author baizhibin
 * @Date 2020/3/28 11:42
 * @Version 1.0
 */
@Data
@Builder
public class Student {
    private String name;
    private Integer age;

    @Tolerate
    public Student() {
    }
}
