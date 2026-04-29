package com.example.login.repository;

import com.example.login.entity.ScoreWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 成绩权重数据访问层
 */
@Repository
public interface ScoreWeightRepository extends JpaRepository<ScoreWeight, Long> {

    Optional<ScoreWeight> findByCourseId(Long courseId);
}
