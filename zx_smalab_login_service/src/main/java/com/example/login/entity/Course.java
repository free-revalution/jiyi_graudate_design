package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 课程实体类
 * 对应数据库表 course
 */
@Entity
@Table(name = "course")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "course_code", length = 50)
    private String courseCode;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "english_name", length = 200)
    private String englishName;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "belong_unit", length = 100)
    private String belongUnit;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "teacher_name", length = 100)
    private String teacherName;

    @Column(name = "total_hours")
    private Integer totalHours;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "modified_time")
    private Date modifiedTime;

    @Column(name = "is_deleted")
    private Integer isDeleted;

}
