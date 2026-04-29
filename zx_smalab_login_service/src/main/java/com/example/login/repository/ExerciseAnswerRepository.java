package com.example.login.repository;

import com.example.login.entity.ExerciseAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 练习作答数据访问层
 */
@Repository
public interface ExerciseAnswerRepository extends JpaRepository<ExerciseAnswer, Long> {

    Optional<ExerciseAnswer> findByExerciseIdAndUserId(Long exerciseId, Long userId);
}
