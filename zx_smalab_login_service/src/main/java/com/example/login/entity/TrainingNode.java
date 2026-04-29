package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 实训节点实体类
 * 对应数据库表 training_node
 */
@Entity
@Table(name = "training_node")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "training_id", nullable = false)
    private Long trainingId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "node_index", length = 20)
    private String nodeIndex;

    @Column(name = "label", length = 200, nullable = false)
    private String label;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "is_deleted")
    private Integer isDeleted;

}
