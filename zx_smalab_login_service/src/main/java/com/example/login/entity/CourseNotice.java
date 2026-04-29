package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 课程通知实体类
 * 对应数据库表 course_notice
 */
@Entity
@Table(name = "course_notice")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_top")
    private Integer isTop;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "is_deleted")
    private Integer isDeleted;

}
