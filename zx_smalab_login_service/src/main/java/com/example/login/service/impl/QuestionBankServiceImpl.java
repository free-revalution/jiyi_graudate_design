package com.example.login.service.impl;

import com.example.login.dto.request.QuestionBankRequest;
import com.example.login.dto.response.PageResult;
import com.example.login.entity.QuestionBank;
import com.example.login.entity.QuestionBankItem;
import com.example.login.exception.BusinessException;
import com.example.login.repository.QuestionBankItemRepository;
import com.example.login.repository.QuestionBankRepository;
import com.example.login.service.QuestionBankService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 题库服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionBankServiceImpl implements QuestionBankService {

    private final QuestionBankRepository questionBankRepository;
    private final QuestionBankItemRepository questionBankItemRepository;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<QuestionBank> getQuestionBankList(int page, int limit, String name, String category) {
        List<QuestionBank> all = questionBankRepository.findByIsDeleted(0);

        List<QuestionBank> filtered = all.stream()
                .filter(q -> name == null || name.isEmpty() || q.getName().contains(name))
                .filter(q -> category == null || category.isEmpty() || category.equals(q.getCategory()))
                .collect(Collectors.toList());

        long total = filtered.size();
        int start = (page - 1) * limit;
        List<QuestionBank> list = filtered.stream()
                .skip(start)
                .limit(limit)
                .toList();

        return PageResult.<QuestionBank>builder()
                .list(list)
                .total(total)
                .page(page)
                .limit(limit)
                .build();
    }

    @Override
    @Transactional
    public QuestionBank createQuestionBank(QuestionBankRequest request, Long creatorId) {
        QuestionBank bank = QuestionBank.builder()
                .name(request.getName())
                .category(request.getCategory())
                .questionCount(request.getItems() != null ? request.getItems().size() : 0)
                .status("active")
                .creatorId(creatorId)
                .isDeleted(0)
                .createdTime(new Date())
                .modifiedTime(new Date())
                .build();
        QuestionBank saved = questionBankRepository.save(bank);

        if (request.getItems() != null) {
            for (QuestionBankRequest.QuestionItem item : request.getItems()) {
                QuestionBankItem bankItem = QuestionBankItem.builder()
                        .bankId(saved.getId())
                        .type(item.getType())
                        .content(item.getContent())
                        .answer(item.getAnswer())
                        .analysis(item.getAnalysis())
                        .difficulty(item.getDifficulty())
                        .isDeleted(0)
                        .createdTime(new Date())
                        .build();
                // Serialize options to JSON string
                if (item.getOptions() != null) {
                    try {
                        bankItem.setOptions(objectMapper.writeValueAsString(item.getOptions()));
                    } catch (Exception e) {
                        bankItem.setOptions("[]");
                    }
                }
                questionBankItemRepository.save(bankItem);
            }
        }
        return saved;
    }

    @Override
    @Transactional
    public QuestionBank updateQuestionBank(Long id, QuestionBankRequest request) {
        QuestionBank bank = questionBankRepository.findById(id)
                .filter(q -> q.getIsDeleted() == 0)
                .orElseThrow(() -> new BusinessException(404, "题库不存在"));

        if (request.getName() != null) bank.setName(request.getName());
        if (request.getCategory() != null) bank.setCategory(request.getCategory());
        if (request.getItems() != null) bank.setQuestionCount(request.getItems().size());
        bank.setModifiedTime(new Date());
        QuestionBank saved = questionBankRepository.save(bank);

        // If items provided, soft-delete old and create new
        if (request.getItems() != null) {
            List<QuestionBankItem> oldItems = questionBankItemRepository.findByBankIdAndIsDeleted(id, 0);
            for (QuestionBankItem old : oldItems) {
                old.setIsDeleted(1);
                questionBankItemRepository.save(old);
            }
            for (QuestionBankRequest.QuestionItem item : request.getItems()) {
                QuestionBankItem bankItem = QuestionBankItem.builder()
                        .bankId(saved.getId())
                        .type(item.getType())
                        .content(item.getContent())
                        .answer(item.getAnswer())
                        .analysis(item.getAnalysis())
                        .difficulty(item.getDifficulty())
                        .isDeleted(0)
                        .createdTime(new Date())
                        .build();
                if (item.getOptions() != null) {
                    try {
                        bankItem.setOptions(objectMapper.writeValueAsString(item.getOptions()));
                    } catch (Exception e) {
                        bankItem.setOptions("[]");
                    }
                }
                questionBankItemRepository.save(bankItem);
            }
        }
        return saved;
    }

    @Override
    @Transactional
    public void deleteQuestionBank(Long id) {
        QuestionBank bank = questionBankRepository.findById(id)
                .filter(q -> q.getIsDeleted() == 0)
                .orElseThrow(() -> new BusinessException(404, "题库不存在"));
        bank.setIsDeleted(1);
        bank.setModifiedTime(new Date());
        questionBankRepository.save(bank);
    }

    @Override
    public List<Map<String, Object>> getQuestionBankItems(Long bankId) {
        questionBankRepository.findById(bankId)
                .filter(q -> q.getIsDeleted() == 0)
                .orElseThrow(() -> new BusinessException(404, "题库不存在"));

        List<QuestionBankItem> items = questionBankItemRepository.findByBankIdAndIsDeleted(bankId, 0);
        List<Map<String, Object>> result = new ArrayList<>();
        for (QuestionBankItem item : items) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", item.getId());
            map.put("type", item.getType());
            map.put("content", item.getContent());
            map.put("answer", item.getAnswer());
            map.put("analysis", item.getAnalysis());
            map.put("difficulty", item.getDifficulty());
            // Parse options from JSON string
            if (item.getOptions() != null) {
                try {
                    map.put("options", objectMapper.readValue(item.getOptions(), Object.class));
                } catch (Exception e) {
                    map.put("options", item.getOptions());
                }
            }
            result.add(map);
        }
        return result;
    }
}
