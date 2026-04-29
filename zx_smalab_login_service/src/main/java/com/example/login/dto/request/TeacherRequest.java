package com.example.login.dto.request;

import lombok.Data;

/**
 * 教师请求DTO
 */
@Data
public class TeacherRequest {

    private String name;
    private String role;
    private String workNo;
    private String department;
}
