package com.example.login.dto.request;

import lombok.Data;

/**
 * 实训请求DTO
 */
@Data
public class TrainingRequest {

    private String name;
    private String cover;
    private String description;
    private String startTime;
    private String endTime;
}
