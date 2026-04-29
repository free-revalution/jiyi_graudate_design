package com.example.login.repository;

import com.example.login.entity.CourseTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程教师数据访问层
 */
@Repository
public interface CourseTeacherRepository extends JpaRepository<CourseTeacher, Long> {

    List<CourseTeacher> findByCourseIdAndIsDeleted(Long courseId, Integer isDeleted);

    long countByCourseIdAndIsDeleted(Long courseId, Integer isDeleted);

    long countByIsDeleted(Integer isDeleted);
}
