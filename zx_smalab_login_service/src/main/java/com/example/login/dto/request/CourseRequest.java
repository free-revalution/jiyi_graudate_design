package com.example.login.dto.request;

import lombok.Data;

/**
 * 课程请求DTO
 */
@Data
public class CourseRequest {

    private String courseCode;
    private String name;
    private String englishName;
    private String coverUrl;
    private String description;
    private String category;
    private String belongUnit;
    private String department;
    private String teacherName;
    private Integer totalHours;
}
