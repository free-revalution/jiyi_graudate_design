package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作业题目实体类
 * 对应数据库表 homework_question
 */
@Entity
@Table(name = "homework_question")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "homework_id", nullable = false)
    private Long homeworkId;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "type", length = 20, nullable = false)
    private String type;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "options", columnDefinition = "TEXT")
    private String options;

    @Column(name = "answer", length = 500)
    private String answer;

    @Column(name = "analysis", columnDefinition = "TEXT")
    private String analysis;

    @Column(name = "difficulty", precision = 3, scale = 2)
    private BigDecimal difficulty;

    @Column(name = "knowledge_points", columnDefinition = "TEXT")
    private String knowledgePoints;

    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "is_deleted")
    private Integer isDeleted;

}
