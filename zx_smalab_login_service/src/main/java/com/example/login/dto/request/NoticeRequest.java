package com.example.login.dto.request;

import lombok.Data;

/**
 * 课程通知请求DTO
 */
@Data
public class NoticeRequest {

    private String title;
    private String content;
    private Integer isTop;
}
