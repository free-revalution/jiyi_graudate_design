package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 成绩权重实体类
 * 对应数据库表 score_weight
 */
@Entity
@Table(name = "score_weight")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "weights", columnDefinition = "TEXT", nullable = false)
    private String weights;

    @Column(name = "updated_time")
    private Date updatedTime;

}
