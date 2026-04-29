package com.example.login.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 作业请求DTO
 */
@Data
public class HomeworkRequest {

    private String name;
    private String cover;
    private String type;
    private String scoreType;
    private String startTime;
    private String endTime;
    private Long classId;
    private List<QuestionItem> questions;

    @Data
    public static class QuestionItem {
        private String type;
        private String content;
        private List<OptionItem> options;
        private String answer;
        private String analysis;
        private Double difficulty;
        private Integer sortOrder;
    }

    @Data
    public static class OptionItem {
        private String content;
    }
}
