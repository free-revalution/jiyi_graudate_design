package com.example.login.repository;

import com.example.login.entity.HomeworkAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 作业作答数据访问层
 */
@Repository
public interface HomeworkAnswerRepository extends JpaRepository<HomeworkAnswer, Long> {

    Optional<HomeworkAnswer> findByHomeworkIdAndUserId(Long homeworkId, Long userId);

    List<HomeworkAnswer> findByHomeworkIdAndIsDeleted(Long homeworkId, Integer isDeleted);
}
