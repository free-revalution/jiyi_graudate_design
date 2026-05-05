package com.example.login.repository;

import com.example.login.entity.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 作业数据访问层
 */
@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Long> {

    List<Homework> findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(Long courseId, Integer isDeleted);

    Optional<Homework> findByIdAndIsDeleted(Long id, Integer isDeleted);

    long countByIsDeleted(Integer isDeleted);
}
