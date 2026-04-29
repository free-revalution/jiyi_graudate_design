package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 实训实体类
 * 对应数据库表 training
 */
@Entity
@Table(name = "training")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "cover", length = 500)
    private String cover;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "pending_count")
    private Integer pendingCount;

    @Column(name = "submitted_count")
    private Integer submittedCount;

    @Column(name = "unsubmitted_count")
    private Integer unsubmittedCount;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "modified_time")
    private Date modifiedTime;

    @Column(name = "is_deleted")
    private Integer isDeleted;

}
