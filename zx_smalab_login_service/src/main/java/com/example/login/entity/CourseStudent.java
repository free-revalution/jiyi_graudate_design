package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 课程学生实体类
 * 对应数据库表 course_student
 */
@Entity
@Table(name = "course_student")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "student_id", length = 50)
    private String studentId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "major", length = 100)
    private String major;

    @Column(name = "class_name", length = 200)
    private String className;

    @Column(name = "join_time")
    private Date joinTime;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "is_deleted")
    private Integer isDeleted;

}
