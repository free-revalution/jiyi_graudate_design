package com.example.login.dto.request;

import lombok.Data;

import java.util.Map;

/**
 * 作业提交请求DTO
 */
@Data
public class HomeworkSubmitRequest {

    private Map<String, Object> answers;
}
