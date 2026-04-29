package com.example.login.repository;

import com.example.login.entity.TrainingStudentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 实训学生任务数据访问层
 */
@Repository
public interface TrainingStudentTaskRepository extends JpaRepository<TrainingStudentTask, Long> {

    List<TrainingStudentTask> findByTrainingIdAndIsDeleted(Long trainingId, Integer isDeleted);

    Optional<TrainingStudentTask> findByTrainingIdAndUserIdAndIsDeleted(Long trainingId, Long userId, Integer isDeleted);
}
