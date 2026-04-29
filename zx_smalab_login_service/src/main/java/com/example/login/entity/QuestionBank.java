package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 题库实体类
 * 对应数据库表 question_bank
 */
@Entity
@Table(name = "question_bank")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "creator", length = 100)
    private String creator;

    @Column(name = "department", length = 150)
    private String department;

    @Column(name = "question_count")
    private Integer questionCount;

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
