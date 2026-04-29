package com.example.login.controller;

import com.example.login.dto.request.CheckInRequest;
import com.example.login.dto.response.ApiResponse;
import com.example.login.entity.CheckIn;
import com.example.login.service.CheckInService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 签到管理控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping("/course/{courseId}/checkin/list")
    public ResponseEntity<ApiResponse<List<CheckIn>>> getCheckInList(
            @PathVariable Long courseId) {
        List<CheckIn> list = checkInService.getCheckInList(courseId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/course/{courseId}/checkin")
    public ResponseEntity<ApiResponse<CheckIn>> createCheckIn(
            @PathVariable Long courseId,
            @RequestBody CheckInRequest request) {
        CheckIn checkIn = CheckIn.builder()
                .courseId(courseId)
                .classId(request.getClassId())
                .name(request.getName())
                .type(request.getType())
                .date(parseDate(request.getDate()))
                .enableLocation(request.getEnableLocation() != null ? request.getEnableLocation() : 0)
                .locationRange(request.getLocationRange())
                .autoRefreshQrcode(request.getAutoRefreshQrcode() != null ? request.getAutoRefreshQrcode() : 0)
                .qrcodeRefreshRate(request.getQrcodeRefreshRate())
                .durationMinutes(request.getDurationMinutes())
                .manualEnd(request.getManualEnd() != null ? request.getManualEnd() : 0)
                .lateMinutes(request.getLateMinutes() != null ? request.getLateMinutes() : 0)
                .enableSignOut(request.getEnableSignOut() != null ? request.getEnableSignOut() : 0)
                .creatorId(getCurrentUserId())
                .build();
        CheckIn created = checkInService.createCheckIn(checkIn);
        return ResponseEntity.ok(ApiResponse.success("创建成功", created));
    }

    @GetMapping("/course/{courseId}/checkin/{id}")
    public ResponseEntity<ApiResponse<CheckIn>> getCheckIn(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(checkInService.getCheckInById(id)));
    }

    @PostMapping("/course/{courseId}/checkin/{id}/start")
    public ResponseEntity<ApiResponse<Void>> startCheckIn(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        checkInService.startCheckIn(id);
        return ResponseEntity.ok(ApiResponse.success("签到已开始", null));
    }

    @PostMapping("/course/{courseId}/checkin/{id}/end")
    public ResponseEntity<ApiResponse<Void>> endCheckIn(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        checkInService.endCheckIn(id);
        return ResponseEntity.ok(ApiResponse.success("签到已结束", null));
    }

    @GetMapping("/course/{courseId}/checkin/{id}/records")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCheckInRecords(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        Map<String, Object> records = checkInService.getCheckInRecords(id);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/course/{courseId}/checkin/{id}/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCheckInStatistics(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        Map<String, Object> statistics = checkInService.getCheckInStatistics(courseId, id);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
