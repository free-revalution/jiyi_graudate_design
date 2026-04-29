package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作业作答实体类
 * 对应数据库表 homework_answer
 */
@Entity
@Table(name = "homework_answer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "homework_id", nullable = false)
    private Long homeworkId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "answers", columnDefinition = "TEXT", nullable = false)
    private String answers;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "submit_time")
    private Date submitTime;

    @Column(name = "created_time")
    private Date createdTime;

}
