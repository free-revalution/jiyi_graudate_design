package com.example.login.dto.request;

import lombok.Data;

import java.util.Map;

/**
 * 课堂练习提交请求DTO
 */
@Data
public class ExerciseSubmitRequest {

    private Map<String, Object> answers;
}
