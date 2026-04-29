package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 实训阶段成绩实体类
 * 对应数据库表 training_stage_score
 */
@Entity
@Table(name = "training_stage_score")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingStageScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "stage")
    private Integer stage;

    @Column(name = "task_name", length = 200)
    private String taskName;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "code_changes")
    private Integer codeChanges;

    @Column(name = "eval_count")
    private Integer evalCount;

    @Column(name = "finish_time")
    private Date finishTime;

    @Column(name = "training_time")
    private Integer trainingTime;

    @Column(name = "view_answer")
    private Integer viewAnswer;

    @Column(name = "exp")
    private Integer exp;

    @Column(name = "stage_score", precision = 5, scale = 2)
    private BigDecimal stageScore;

    @Column(name = "deduction", precision = 5, scale = 2)
    private BigDecimal deduction;

    @Column(name = "created_time")
    private Date createdTime;

}
