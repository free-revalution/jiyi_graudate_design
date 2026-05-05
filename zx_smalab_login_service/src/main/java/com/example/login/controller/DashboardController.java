package com.example.login.controller;

import com.example.login.dto.response.ApiResponse;
import com.example.login.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 仪表盘统计控制器
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final CourseRepository courseRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final HomeworkRepository homeworkRepository;
    private final ClassExerciseRepository classExerciseRepository;
    private final TrainingRepository trainingRepository;
    private final CheckInRepository checkInRepository;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview() {
        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("courseCount", courseRepository.countByIsDeleted(0));
        overview.put("studentCount", courseStudentRepository.countByIsDeleted(0));
        overview.put("homeworkCount", homeworkRepository.countByIsDeleted(0));
        overview.put("exerciseCount", classExerciseRepository.countByIsDeleted(0));
        overview.put("trainingCount", trainingRepository.countByIsDeleted(0));
        overview.put("checkinCount", checkInRepository.countByIsDeleted(0));
        return ResponseEntity.ok(ApiResponse.success(overview));
    }

    @GetMapping("/trend")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTrend(
            @RequestParam(defaultValue = "7") int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Map<String, Object> dayData = new LinkedHashMap<>();
            dayData.put("date", fmt.format(date));
            dayData.put("courseCount", 0);
            dayData.put("studentCount", 0);
            dayData.put("homeworkCount", 0);
            trend.add(dayData);
        }

        return ResponseEntity.ok(ApiResponse.success(trend));
    }
}
