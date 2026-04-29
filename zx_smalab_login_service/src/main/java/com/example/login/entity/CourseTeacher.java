package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 课程教师实体类
 * 对应数据库表 course_teacher
 */
@Entity
@Table(name = "course_teacher")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseTeacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "work_no", length = 50)
    private String workNo;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "join_time")
    private Date joinTime;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "is_deleted")
    private Integer isDeleted;

}
