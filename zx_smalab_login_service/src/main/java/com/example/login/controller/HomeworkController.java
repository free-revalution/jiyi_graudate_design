package com.example.login.controller;

import com.example.login.dto.request.HomeworkRequest;
import com.example.login.dto.request.HomeworkSubmitRequest;
import com.example.login.dto.response.ApiResponse;
import com.example.login.dto.response.PageResult;
import com.example.login.entity.Homework;
import com.example.login.entity.HomeworkAnswer;
import com.example.login.entity.HomeworkQuestion;
import com.example.login.repository.HomeworkQuestionRepository;
import com.example.login.service.HomeworkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 作业管理控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;
    private final HomeworkQuestionRepository homeworkQuestionRepository;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ==================== Admin Endpoints ====================

    @GetMapping("/course/{courseId}/homework/list")
    public ResponseEntity<ApiResponse<PageResult<Homework>>> getHomeworkList(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {
        PageResult<Homework> result = homeworkService.getHomeworkList(courseId, page, limit, name, status);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/course/{courseId}/homework")
    public ResponseEntity<ApiResponse<Homework>> createHomework(
            @PathVariable Long courseId,
            @RequestBody HomeworkRequest request) {
        Homework homework = Homework.builder()
                .name(request.getName())
                .cover(request.getCover())
                .type(request.getType())
                .scoreType(request.getScoreType())
                .startTime(parseDate(request.getStartTime()))
                .endTime(parseDate(request.getEndTime()))
                .classId(request.getClassId())
                .build();

        List<HomeworkQuestion> questions = new ArrayList<>();
        if (request.getQuestions() != null) {
            for (HomeworkRequest.QuestionItem item : request.getQuestions()) {
                HomeworkQuestion question = HomeworkQuestion.builder()
                        .type(item.getType())
                        .content(item.getContent())
                        .answer(item.getAnswer())
                        .analysis(item.getAnalysis())
                        .difficulty(item.getDifficulty() != null ?
                                java.math.BigDecimal.valueOf(item.getDifficulty()) : null)
                        .sortOrder(item.getSortOrder())
                        .build();
                // Serialize options to JSON string
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

        Homework created = homeworkService.createHomework(courseId, homework, questions, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("创建成功", created));
    }

    @GetMapping("/course/{courseId}/homework/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHomework(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        Homework homework = homeworkService.getHomeworkById(id);
        List<HomeworkQuestion> questions = homeworkQuestionRepository
                .findByHomeworkIdAndIsDeletedOrderBySortOrder(id, 0);
        Map<String, Object> result = new HashMap<>();
        result.put("id", homework.getId());
        result.put("name", homework.getName());
        result.put("cover", homework.getCover());
        result.put("type", homework.getType());
        result.put("scoreType", homework.getScoreType());
        result.put("status", homework.getStatus());
        result.put("startTime", homework.getStartTime());
        result.put("endTime", homework.getEndTime());
        result.put("classId", homework.getClassId());
        result.put("courseId", homework.getCourseId());
        result.put("creatorId", homework.getCreatorId());
        result.put("questions", questions);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/course/{courseId}/homework/{id}")
    public ResponseEntity<ApiResponse<Homework>> updateHomework(
            @PathVariable Long courseId,
            @PathVariable Long id,
            @RequestBody HomeworkRequest request) {
        Homework homework = Homework.builder()
                .name(request.getName())
                .cover(request.getCover())
                .type(request.getType())
                .scoreType(request.getScoreType())
                .startTime(parseDate(request.getStartTime()))
                .endTime(parseDate(request.getEndTime()))
                .classId(request.getClassId())
                .build();

        List<HomeworkQuestion> questions = new ArrayList<>();
        if (request.getQuestions() != null) {
            for (HomeworkRequest.QuestionItem item : request.getQuestions()) {
                HomeworkQuestion question = HomeworkQuestion.builder()
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

        Homework updated = homeworkService.updateHomework(id, homework, questions);
        return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
    }

    @DeleteMapping("/course/{courseId}/homework/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHomework(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        homeworkService.deleteHomework(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @PostMapping("/course/{courseId}/homework/{id}/publish")
    public ResponseEntity<ApiResponse<Void>> publishHomework(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        homeworkService.publishHomework(id);
        return ResponseEntity.ok(ApiResponse.success("发布成功", null));
    }

    @GetMapping("/course/{courseId}/homework/{id}/answers")
    public ResponseEntity<ApiResponse<List<HomeworkAnswer>>> getHomeworkAnswers(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(homeworkService.getHomeworkAnswers(id)));
    }

    @PutMapping("/course/{courseId}/homework/{id}/answer/{userId}/score")
    public ResponseEntity<ApiResponse<Void>> gradeHomework(
            @PathVariable Long courseId,
            @PathVariable Long id,
            @PathVariable Long userId,
            @RequestBody Map<String, Object> body) {
        homeworkService.gradeHomework(id, userId, body.get("score") != null ?
                new java.math.BigDecimal(body.get("score").toString()) : null);
        return ResponseEntity.ok(ApiResponse.success("批改成功", null));
    }

    // ==================== User Endpoints ====================

    @GetMapping("/course/{courseId}/homework/my")
    public ResponseEntity<ApiResponse<List<Homework>>> getMyHomework(
            @PathVariable Long courseId) {
        List<Homework> list = homeworkService.getMyHomework(courseId, getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/course/{courseId}/homework/{id}/questions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getQuestions(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        List<Map<String, Object>> questions = homeworkService.getQuestionsForUser(id);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    @PostMapping("/course/{courseId}/homework/{id}/submit")
    public ResponseEntity<ApiResponse<Void>> submitHomework(
            @PathVariable Long courseId,
            @PathVariable Long id,
            @RequestBody HomeworkSubmitRequest request) {
        homeworkService.submitHomework(id, getCurrentUserId(), request.getAnswers());
        return ResponseEntity.ok(ApiResponse.success("提交成功", null));
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

    protected Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
