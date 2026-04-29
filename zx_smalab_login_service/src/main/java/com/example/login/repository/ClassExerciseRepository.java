package com.example.login.repository;

import com.example.login.entity.ClassExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课堂练习数据访问层
 */
@Repository
public interface ClassExerciseRepository extends JpaRepository<ClassExercise, Long> {

    List<ClassExercise> findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(Long courseId, Integer isDeleted);

    Optional<ClassExercise> findByIdAndIsDeleted(Long id, Integer isDeleted);
}
