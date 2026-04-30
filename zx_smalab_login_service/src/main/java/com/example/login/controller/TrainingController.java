package com.example.login.controller;

import com.example.login.dto.request.TrainingNodeRequest;
import com.example.login.dto.request.TrainingRequest;
import com.example.login.dto.response.ApiResponse;
import com.example.login.dto.response.PageResult;
import com.example.login.entity.Training;
import com.example.login.entity.TrainingStudentTask;
import com.example.login.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 实训管理控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ==================== Admin Endpoints ====================

    @GetMapping("/course/{courseId}/training/list")
    public ResponseEntity<ApiResponse<PageResult<Training>>> getTrainingList(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String name) {
        PageResult<Training> result = trainingService.getTrainingList(courseId, page, limit, name);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/course/{courseId}/training")
    public ResponseEntity<ApiResponse<Training>> createTraining(
            @PathVariable Long courseId,
            @RequestBody TrainingRequest request) {
        Training training = Training.builder()
                .name(request.getName())
                .cover(request.getCover())
                .description(request.getDescription())
                .startTime(parseDate(request.getStartTime()))
                .endTime(parseDate(request.getEndTime()))
                .build();
        Training created = trainingService.createTraining(courseId, training, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("创建成功", created));
    }

    @GetMapping("/course/{courseId}/training/{id}")
    public ResponseEntity<ApiResponse<Training>> getTraining(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(trainingService.getTrainingById(id)));
    }

    @PutMapping("/course/{courseId}/training/{id}")
    public ResponseEntity<ApiResponse<Training>> updateTraining(
            @PathVariable Long courseId,
            @PathVariable Long id,
            @RequestBody TrainingRequest request) {
        Training training = Training.builder()
                .name(request.getName())
                .cover(request.getCover())
                .description(request.getDescription())
                .startTime(parseDate(request.getStartTime()))
                .endTime(parseDate(request.getEndTime()))
                .build();
        Training updated = trainingService.updateTraining(id, training);
        return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
    }

    @DeleteMapping("/course/{courseId}/training/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTraining(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        trainingService.deleteTraining(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @PostMapping("/course/{courseId}/training/{id}/publish")
    public ResponseEntity<ApiResponse<Void>> publishTraining(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        trainingService.publishTraining(id);
        return ResponseEntity.ok(ApiResponse.success("发布成功", null));
    }

    @GetMapping("/course/{courseId}/training/{id}/nodes")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTrainingNodes(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        List<Map<String, Object>> nodes = trainingService.getTrainingNodes(id);
        return ResponseEntity.ok(ApiResponse.success(nodes));
    }

    @PostMapping("/course/{courseId}/training/{id}/nodes")
    public ResponseEntity<ApiResponse<Void>> saveTrainingNodes(
            @PathVariable Long courseId,
            @PathVariable Long id,
            @RequestBody List<TrainingNodeRequest> nodes) {
        trainingService.saveTrainingNodes(id, nodes);
        return ResponseEntity.ok(ApiResponse.success("保存成功", null));
    }

    @GetMapping("/course/{courseId}/training/{id}/students")
    public ResponseEntity<ApiResponse<List<TrainingStudentTask>>> getTrainingStudents(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        List<TrainingStudentTask> students = trainingService.getTrainingStudents(id);
        return ResponseEntity.ok(ApiResponse.success(students));
    }

    @GetMapping("/course/{courseId}/training/{id}/student/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTrainingStudentDetail(
            @PathVariable Long courseId,
            @PathVariable Long id,
            @PathVariable Long userId) {
        Map<String, Object> detail = trainingService.getTrainingStudentDetail(id, userId);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    // ==================== User Endpoints ====================

    @GetMapping("/course/{courseId}/training/my")
    public ResponseEntity<ApiResponse<List<Training>>> getMyTrainings(
            @PathVariable Long courseId) {
        List<Training> list = trainingService.getMyTrainings(courseId, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/course/{courseId}/training/{id}/detail")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTrainingDetail(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        Map<String, Object> detail = trainingService.getTrainingDetail(id);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    // ==================== Helpers ====================

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return Date.from(LocalDateTime.parse(dateStr, DATE_FORMAT).atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            return null;
        }
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
