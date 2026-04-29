package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 课堂练习实体类
 * 对应数据库表 class_exercise
 */
@Entity
@Table(name = "class_exercise")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "score_type", length = 20)
    private String scoreType;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "completed_count")
    private Integer completedCount;

    @Column(name = "uncompleted_count")
    private Integer uncompletedCount;

    @Column(name = "question_count")
    private Integer questionCount;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "modified_time")
    private Date modifiedTime;

    @Column(name = "is_deleted")
    private Integer isDeleted;

}
