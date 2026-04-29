package com.example.login.service;

import com.example.login.dto.request.QuestionBankRequest;
import com.example.login.dto.response.PageResult;
import com.example.login.entity.QuestionBank;
import com.example.login.entity.QuestionBankItem;

import java.util.List;
import java.util.Map;

/**
 * 题库服务接口
 */
public interface QuestionBankService {

    PageResult<QuestionBank> getQuestionBankList(int page, int limit, String name, String category);

    QuestionBank createQuestionBank(QuestionBankRequest request, Long creatorId);

    QuestionBank updateQuestionBank(Long id, QuestionBankRequest request);

    void deleteQuestionBank(Long id);

    List<Map<String, Object>> getQuestionBankItems(Long bankId);
}
