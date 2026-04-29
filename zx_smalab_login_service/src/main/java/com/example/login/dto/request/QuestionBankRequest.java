package com.example.login.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 题库请求DTO
 */
@Data
public class QuestionBankRequest {

    private String name;
    private String category;
    private List<QuestionItem> items;

    @Data
    public static class QuestionItem {
        private String type;
        private String content;
        private Object options;
        private String answer;
        private String analysis;
        private BigDecimal difficulty;
    }
}
