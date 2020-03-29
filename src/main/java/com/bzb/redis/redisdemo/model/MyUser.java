package com.bzb.redis.redisdemo.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MyUser
 * @Description
 * @Author baizhibin
 * @Date 2020/3/28 10:49
 * @Version 1.0
 */
@Data
@Component
@Builder
public class MyUser implements Serializable {
    private Long id;
    private String name = "bzb";
    private int age = 35;
    private String city;
    private String email = "163@qq.com";
    private List<Student> students;

    public String findUserName() {
        return "xjj";
    }

    @Tolerate
    public MyUser() {
    }

    public List<Student> getStudents() {
        List<Student> students = new ArrayList<>();
        students.add(Student.builder().name("bzb").age(25).build());
        students.add(Student.builder().name("xjj").age(24).build());
        students.add(Student.builder().name("wjq").age(26).build());
        return students;
    }

}
