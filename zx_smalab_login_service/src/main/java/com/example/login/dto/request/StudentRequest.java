package com.example.login.dto.request;

import lombok.Data;

/**
 * 学生请求DTO
 */
@Data
public class StudentRequest {

    private Long userId;
    private String studentId;
    private String name;
    private String department;
    private String major;
    private String className;
}
