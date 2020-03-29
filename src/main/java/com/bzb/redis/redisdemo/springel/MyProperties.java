package com.bzb.redis.redisdemo.springel;

import com.bzb.redis.redisdemo.model.Student;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @ClassName MyProperties
 * @Description 测试SpringEL表达式
 * @Author baizhibin
 * @Date 2020/3/28 10:41
 * @Version 1.0
 */
@Data
@Configuration
public class MyProperties {

    @Value("#{myUser.name?.toUpperCase()}")
    private String name;

    @Value("#{myUser.findUserName()}")
    private String findUserName;

    @Value("#{9.84E3}")
    private double aDouble;

    @Value("#{false}")
    private boolean aBoolean;

    @Value("#{2 * T(Math).PI ^ myUser.age}")
    private double myDouble;

    @Value("#{'my' + ' name is '[2] + myUser.name}")
    private String join;

    @Value("#{myUser.age == 35}")
    private boolean myBoolean;

    @Value("#{myUser.age > 35 ? 'bzb' : 'xjj'}")
    private String ternory;

    @Value("#{myUser.city ?: 'henan'}")
    private String elvis;

    @Value("#{myUser.email matches '[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]\\.com'}")
    private boolean reg;

    @Value("#{myUser.students[T(Math).random() * myUser.students.size()].name}")
    private String studentName;

    @Value("#{myUser.students.^[name eq 'bzb1']}")
    private Student student;

    @Value("#{myUser.students.![name]}")
    private List<String> names;
}
