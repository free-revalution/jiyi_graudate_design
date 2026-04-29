package com.example.login.repository;

import com.example.login.entity.HomeworkQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 作业题目数据访问层
 */
@Repository
public interface HomeworkQuestionRepository extends JpaRepository<HomeworkQuestion, Long> {

    List<HomeworkQuestion> findByHomeworkIdAndIsDeletedOrderBySortOrder(Long homeworkId, Integer isDeleted);
}
