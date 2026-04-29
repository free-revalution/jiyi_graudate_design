package com.example.login.repository;

import com.example.login.entity.ExerciseQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 练习题目数据访问层
 */
@Repository
public interface ExerciseQuestionRepository extends JpaRepository<ExerciseQuestion, Long> {

    List<ExerciseQuestion> findByExerciseIdAndIsDeletedOrderBySortOrder(Long exerciseId, Integer isDeleted);
}
