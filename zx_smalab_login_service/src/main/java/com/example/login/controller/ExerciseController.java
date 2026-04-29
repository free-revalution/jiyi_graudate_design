package com.example.login.controller;

import com.example.login.dto.request.ExerciseRequest;
import com.example.login.dto.request.ExerciseSubmitRequest;
import com.example.login.dto.response.ApiResponse;
import com.example.login.dto.response.PageResult;
import com.example.login.entity.ClassExercise;
import com.example.login.entity.ExerciseQuestion;
import com.example.login.service.ExerciseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 课堂练习管理控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final ObjectMapper objectMapper;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // ==================== Admin Endpoints ====================

    @GetMapping("/course/{courseId}/exercise/list")
    public ResponseEntity<ApiResponse<PageResult<ClassExercise>>> getExerciseList(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {
        PageResult<ClassExercise> result = exerciseService.getExerciseList(courseId, page, limit, name, status);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/course/{courseId}/exercise")
    public ResponseEntity<ApiResponse<ClassExercise>> createExercise(
            @PathVariable Long courseId,
            @RequestBody ExerciseRequest request) {
        ClassExercise exercise = ClassExercise.builder()
                .name(request.getName())
                .type(request.getType())
                .scoreType(request.getScoreType())
                .startTime(parseDate(request.getStartTime()))
                .endTime(parseDate(request.getEndTime()))
                .build();

        List<ExerciseQuestion> questions = new ArrayList<>();
        if (request.getQuestions() != null) {
            for (ExerciseRequest.QuestionItem item : request.getQuestions()) {
                ExerciseQuestion question = ExerciseQuestion.builder()
                        .type(item.getType())
                        .content(item.getContent())
                        .answer(item.getAnswer())
                        .analysis(item.getAnalysis())
                        .difficulty(item.getDifficulty() != null ?
                                java.math.BigDecimal.valueOf(item.getDifficulty()) : null)
                        .sortOrder(item.getSortOrder())
                        .build();
                if (item.getOptions() != null) {
                    try {
                        question.setOptions(objectMapper.writeValueAsString(item.getOptions()));
                    } catch (Exception e) {
                        question.setOptions("[]");
                    }
                }
                questions.add(question);
            }
        }

        ClassExercise created = exerciseService.createExercise(courseId, exercise, questions, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("创建成功", created));
    }

    @GetMapping("/course/{courseId}/exercise/{id}")
    public ResponseEntity<ApiResponse<ClassExercise>> getExercise(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(exerciseService.getExerciseById(id)));
    }

    @PutMapping("/course/{courseId}/exercise/{id}")
    public ResponseEntity<ApiResponse<ClassExercise>> updateExercise(
            @PathVariable Long courseId,
            @PathVariable Long id,
            @RequestBody ExerciseRequest request) {
        ClassExercise exercise = ClassExercise.builder()
                .name(request.getName())
                .type(request.getType())
                .scoreType(request.getScoreType())
                .startTime(parseDate(request.getStartTime()))
                .endTime(parseDate(request.getEndTime()))
                .build();

        List<ExerciseQuestion> questions = new ArrayList<>();
        if (request.getQuestions() != null) {
            for (ExerciseRequest.QuestionItem item : request.getQuestions()) {
                ExerciseQuestion question = ExerciseQuestion.builder()
                        .type(item.getType())
                        .content(item.getContent())
                        .answer(item.getAnswer())
                        .analysis(item.getAnalysis())
                        .difficulty(item.getDifficulty() != null ?
                                java.math.BigDecimal.valueOf(item.getDifficulty()) : null)
                        .sortOrder(item.getSortOrder())
                        .build();
                if (item.getOptions() != null) {
                    try {
                        question.setOptions(objectMapper.writeValueAsString(item.getOptions()));
                    } catch (Exception e) {
                        question.setOptions("[]");
                    }
                }
                questions.add(question);
            }
        }

        ClassExercise updated = exerciseService.updateExercise(id, exercise, questions);
        return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
    }

    @DeleteMapping("/course/{courseId}/exercise/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExercise(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @PostMapping("/course/{courseId}/exercise/{id}/publish")
    public ResponseEntity<ApiResponse<Void>> publishExercise(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        exerciseService.publishExercise(id);
        return ResponseEntity.ok(ApiResponse.success("发布成功", null));
    }

    // ==================== User Endpoints ====================

    @GetMapping("/course/{courseId}/exercise/my")
    public ResponseEntity<ApiResponse<List<ClassExercise>>> getMyExercises(
            @PathVariable Long courseId) {
        List<ClassExercise> list = exerciseService.getMyExercises(courseId, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/course/{courseId}/exercise/{id}/questions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getQuestions(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        List<Map<String, Object>> questions = exerciseService.getQuestionsForUser(id);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    @PostMapping("/course/{courseId}/exercise/{id}/submit")
    public ResponseEntity<ApiResponse<Void>> submitExercise(
            @PathVariable Long courseId,
            @PathVariable Long id,
            @RequestBody ExerciseSubmitRequest request) {
        exerciseService.submitExercise(id, getCurrentUserId(), request.getAnswers());
        return ResponseEntity.ok(ApiResponse.success("提交成功", null));
    }

    // ==================== Helpers ====================

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

    protected Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
