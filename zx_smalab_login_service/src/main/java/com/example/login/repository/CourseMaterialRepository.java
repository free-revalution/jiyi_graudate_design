package com.example.login.repository;

import com.example.login.entity.CourseMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程资料数据访问层
 */
@Repository
public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {

    List<CourseMaterial> findByCourseIdAndIsDeleted(Long courseId, Integer isDeleted);
}
