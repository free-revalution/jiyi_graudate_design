package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 课程学期实体类
 * 对应数据库表 course_term
 */
@Entity
@Table(name = "course_term")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "info", length = 500)
    private String info;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "is_deleted")
    private Integer isDeleted;

}
