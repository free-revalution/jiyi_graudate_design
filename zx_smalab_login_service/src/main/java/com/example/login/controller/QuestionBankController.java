package com.example.login.controller;

import com.example.login.dto.request.QuestionBankRequest;
import com.example.login.dto.response.ApiResponse;
import com.example.login.dto.response.PageResult;
import com.example.login.entity.QuestionBank;
import com.example.login.service.QuestionBankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 题库管理控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionBankController {

    private final QuestionBankService questionBankService;

    @GetMapping("/question-bank/list")
    public ResponseEntity<ApiResponse<PageResult<QuestionBank>>> getQuestionBankList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {
        PageResult<QuestionBank> result = questionBankService.getQuestionBankList(page, limit, name, category);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/question-bank")
    public ResponseEntity<ApiResponse<QuestionBank>> createQuestionBank(
            @RequestBody QuestionBankRequest request) {
        QuestionBank created = questionBankService.createQuestionBank(request, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("创建成功", created));
    }

    @PutMapping("/question-bank/{id}")
    public ResponseEntity<ApiResponse<QuestionBank>> updateQuestionBank(
            @PathVariable Long id,
            @RequestBody QuestionBankRequest request) {
        QuestionBank updated = questionBankService.updateQuestionBank(id, request);
        return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
    }

    @DeleteMapping("/question-bank/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestionBank(
            @PathVariable Long id) {
        questionBankService.deleteQuestionBank(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @GetMapping("/question-bank/{id}/items")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getQuestionBankItems(
            @PathVariable Long id) {
        List<Map<String, Object>> items = questionBankService.getQuestionBankItems(id);
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
