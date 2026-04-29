package com.example.login.repository;

import com.example.login.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课程数据访问层
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByCreatorIdAndIsDeleted(Long creatorId, Integer isDeleted);

    List<Course> findByStatusAndIsDeleted(String status, Integer isDeleted);

    List<Course> findTop4ByStatusAndIsDeletedOrderByCreatedTimeDesc(String status, Integer isDeleted);

    Optional<Course> findByIdAndIsDeleted(Long id, Integer isDeleted);

    long countByIsDeleted(Integer isDeleted);

    List<Course> findByIsDeleted(Integer isDeleted);
}
