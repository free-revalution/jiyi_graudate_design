package com.example.login.repository;

import com.example.login.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 签到数据访问层
 */
@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    List<CheckIn> findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(Long courseId, Integer isDeleted);

    Optional<CheckIn> findByIdAndIsDeleted(Long id, Integer isDeleted);
}
