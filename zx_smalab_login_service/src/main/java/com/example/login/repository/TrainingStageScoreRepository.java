package com.example.login.repository;

import com.example.login.entity.TrainingStageScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 实训阶段成绩数据访问层
 */
@Repository
public interface TrainingStageScoreRepository extends JpaRepository<TrainingStageScore, Long> {

    List<TrainingStageScore> findByTaskId(Long taskId);
}
