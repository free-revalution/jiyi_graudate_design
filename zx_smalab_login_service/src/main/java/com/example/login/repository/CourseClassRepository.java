package com.example.login.repository;

import com.example.login.entity.CourseClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程班级数据访问层
 */
@Repository
public interface CourseClassRepository extends JpaRepository<CourseClass, Long> {

    List<CourseClass> findByCourseIdAndIsDeleted(Long courseId, Integer isDeleted);
}
