package com.example.login.repository;

import com.example.login.entity.QuestionBankItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 题库条目数据访问层
 */
@Repository
public interface QuestionBankItemRepository extends JpaRepository<QuestionBankItem, Long> {

    List<QuestionBankItem> findByBankIdAndIsDeleted(Long bankId, Integer isDeleted);
}
