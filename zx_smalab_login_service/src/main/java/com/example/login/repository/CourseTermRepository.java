package com.example.login.repository;

import com.example.login.entity.CourseTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程学期数据访问层
 */
@Repository
public interface CourseTermRepository extends JpaRepository<CourseTerm, Long> {

    List<CourseTerm> findByCourseIdAndIsDeleted(Long courseId, Integer isDeleted);
}
