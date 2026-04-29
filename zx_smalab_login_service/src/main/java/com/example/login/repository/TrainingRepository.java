package com.example.login.repository;

import com.example.login.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 实训数据访问层
 */
@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    List<Training> findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(Long courseId, Integer isDeleted);

    Optional<Training> findByIdAndIsDeleted(Long id, Integer isDeleted);
}
