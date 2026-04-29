package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 课程班级实体类
 * 对应数据库表 course_class
 */
@Entity
@Table(name = "course_class")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "student_count")
    private Integer studentCount;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "is_deleted")
    private Integer isDeleted;

}
