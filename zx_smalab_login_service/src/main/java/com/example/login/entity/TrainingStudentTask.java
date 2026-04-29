package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 实训学生任务实体类
 * 对应数据库表 training_student_task
 */
@Entity
@Table(name = "training_student_task")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingStudentTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "training_id", nullable = false)
    private Long trainingId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "student_id", length = 50)
    private String studentId;

    @Column(name = "class_name", length = 200)
    private String className;

    @Column(name = "training_status", length = 20)
    private String trainingStatus;

    @Column(name = "total_time")
    private Integer totalTime;

    @Column(name = "pass_rate", length = 20)
    private String passRate;

    @Column(name = "eval_count")
    private Integer evalCount;

    @Column(name = "final_score", precision = 5, scale = 2)
    private BigDecimal finalScore;

    @Column(name = "reject_count")
    private Integer rejectCount;

    @Column(name = "review_status", length = 20)
    private String reviewStatus;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_time")
    private Date createdTime;

}
