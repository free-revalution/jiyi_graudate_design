package com.example.login.repository;

import com.example.login.entity.CourseNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程通知数据访问层
 */
@Repository
public interface CourseNoticeRepository extends JpaRepository<CourseNotice, Long> {

    List<CourseNotice> findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(Long courseId, Integer isDeleted);
}
