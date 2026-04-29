package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 课程资料实体类
 * 对应数据库表 course_material
 */
@Entity
@Table(name = "course_material")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "is_deleted")
    private Integer isDeleted;

}
