package com.example.login.repository;

import com.example.login.entity.CourseStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程学生数据访问层
 */
@Repository
public interface CourseStudentRepository extends JpaRepository<CourseStudent, Long> {

    List<CourseStudent> findByClassIdAndIsDeleted(Long classId, Integer isDeleted);

    List<CourseStudent> findByCourseIdAndIsDeleted(Long courseId, Integer isDeleted);

    List<CourseStudent> findByUserIdAndIsDeleted(Long userId, Integer isDeleted);

    long countByClassIdAndIsDeleted(Long classId, Integer isDeleted);
}
