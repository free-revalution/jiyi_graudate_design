package com.example.login.controller;

import com.example.login.dto.response.ApiResponse;
import com.example.login.entity.*;
import com.example.login.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户个人中心控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MyController {

    private final CourseRepository courseRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final HomeworkRepository homeworkRepository;
    private final HomeworkAnswerRepository homeworkAnswerRepository;
    private final TrainingRepository trainingRepository;

    @GetMapping("/my/courses")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMyCourses() {
        Long userId = getCurrentUserId();
        List<CourseStudent> enrollments = courseStudentRepository.findByUserIdAndIsDeleted(userId, 0);

        List<Map<String, Object>> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (CourseStudent enrollment : enrollments) {
            Course course = courseRepository.findByIdAndIsDeleted(enrollment.getCourseId(), 0).orElse(null);
            if (course == null) continue;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", course.getId());
            item.put("name", course.getName());
            item.put("cover", course.getCoverUrl());
            item.put("className", enrollment.getClassName());
            item.put("startTime", course.getCreatedTime() != null ? sdf.format(course.getCreatedTime()) : "");
            item.put("endTime", course.getModifiedTime() != null ? sdf.format(course.getModifiedTime()) : "");

            // Calculate homework stats
            List<Homework> homeworkList = homeworkRepository.findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(course.getId(), 0);
            int pendingCount = 0;
            int submittedCount = 0;
            int unsubmittedCount = 0;

            for (Homework hw : homeworkList) {
                if (!"published".equals(hw.getStatus())) continue;
                Optional<HomeworkAnswer> answer = homeworkAnswerRepository.findByHomeworkIdAndUserId(hw.getId(), userId);
                if (answer.isPresent()) {
                    submittedCount++;
                } else {
                    // Check if deadline passed
                    if (hw.getEndTime() != null && hw.getEndTime().before(new Date())) {
                        unsubmittedCount++;
                    } else {
                        pendingCount++;
                    }
                }
            }

            item.put("pendingCount", pendingCount);
            item.put("submittedCount", submittedCount);
            item.put("unsubmittedCount", unsubmittedCount);
            item.put("status", "进行中");

            result.add(item);
        }

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/my/course/{courseId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyCourseDetail(@PathVariable Long courseId) {
        Long userId = getCurrentUserId();

        Course course = courseRepository.findByIdAndIsDeleted(courseId, 0)
                .orElseThrow(() -> new com.example.login.exception.BusinessException(404, "课程不存在"));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("course", course);

        // Homework stats
        List<Homework> homeworkList = homeworkRepository.findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(courseId, 0);
        int pendingCount = 0;
        int submittedCount = 0;
        int totalHomework = 0;

        for (Homework hw : homeworkList) {
            if (!"published".equals(hw.getStatus())) continue;
            totalHomework++;
            Optional<HomeworkAnswer> answer = homeworkAnswerRepository.findByHomeworkIdAndUserId(hw.getId(), userId);
            if (answer.isPresent()) {
                submittedCount++;
            } else {
                pendingCount++;
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalHomework", totalHomework);
        stats.put("submittedHomework", submittedCount);
        stats.put("pendingHomework", pendingCount);
        data.put("stats", stats);

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/my/course/{courseId}/practice-levels")
    public ResponseEntity<ApiResponse<List<Training>>> getPracticeLevels(@PathVariable Long courseId) {
        Long userId = getCurrentUserId();
        List<Training> trainings = trainingRepository.findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(courseId, 0);
        return ResponseEntity.ok(ApiResponse.success(trainings));
    }

    protected Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
