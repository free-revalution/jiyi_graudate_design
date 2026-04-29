package com.example.login.dto.request;

import lombok.Data;

/**
 * 签到请求DTO
 */
@Data
public class CheckInRequest {

    private String name;
    private String type;
    private String date;
    private Long courseId;
    private Long classId;
    private Integer enableLocation;
    private Integer locationRange;
    private Integer autoRefreshQrcode;
    private Integer qrcodeRefreshRate;
    private Integer durationMinutes;
    private Integer manualEnd;
    private Integer lateMinutes;
    private Integer enableSignOut;
}
