package com.example.login.controller;

import com.example.login.dto.response.ApiResponse;
import com.example.login.entity.*;
import com.example.login.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MyController {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final CourseRepository courseRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final CourseClassRepository courseClassRepository;
    private final UserInfoRepository userInfoRepository;
    private final HomeworkRepository homeworkRepository;
    private final HomeworkAnswerRepository homeworkAnswerRepository;
    private final TrainingRepository trainingRepository;

    @GetMapping("/my/courses")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMyCourses() {
        Long userId = getCurrentUserId();
        List<CourseStudent> enrollments = courseStudentRepository.findByUserIdAndIsDeleted(userId, 0);

        List<Map<String, Object>> result = new ArrayList<>();

        for (CourseStudent enrollment : enrollments) {
            Course course = courseRepository.findByIdAndIsDeleted(enrollment.getCourseId(), 0).orElse(null);
            if (course == null) continue;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", course.getId());
            item.put("name", course.getName());
            item.put("cover", course.getCoverUrl());
            item.put("className", enrollment.getClassName());
            item.put("startTime", course.getCreatedTime() != null ? formatDate(course.getCreatedTime()) : "");
            item.put("endTime", course.getModifiedTime() != null ? formatDate(course.getModifiedTime()) : "");

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

    @PostMapping("/my/courses/{courseId}/enroll")
    public ResponseEntity<ApiResponse<Void>> enrollCourse(@PathVariable Long courseId,
            @RequestBody(required = false) Map<String, Object> body) {
        Long userId = getCurrentUserId();
        log.info("【选课】userId={}, courseId={}, body={}", userId, courseId, body);

        Course course = courseRepository.findByIdAndIsDeleted(courseId, 0)
                .orElse(null);
        if (course == null || !"published".equals(course.getStatus())) {
            return ResponseEntity.ok(ApiResponse.error(404, "课程不存在或未发布"));
        }

        if (courseStudentRepository.existsByCourseIdAndUserIdAndIsDeleted(courseId, userId, 0)) {
            return ResponseEntity.ok(ApiResponse.error(400, "已加入该课程"));
        }

        final Long[] classIdHolder = { 0L };
        if (body != null && body.get("classId") != null) {
            classIdHolder[0] = Long.valueOf(body.get("classId").toString());
        }
        String className = "";
        if (classIdHolder[0] > 0) {
            CourseClass courseClass = courseClassRepository.findById(classIdHolder[0]).orElse(null);
            if (courseClass == null || !courseClass.getCourseId().equals(courseId)) {
                return ResponseEntity.ok(ApiResponse.error(400, "班级不存在"));
            }
            className = courseClass.getName();
        }

        final String[] nameHolder = { "" };
        userInfoRepository.findByUserId(userId).ifPresent(user -> nameHolder[0] = user.getUserName());

        try {
            CourseStudent student = CourseStudent.builder()
                    .courseId(courseId)
                    .classId(classIdHolder[0])
                    .userId(userId)
                    .name(nameHolder[0])
                    .className(className)
                    .joinTime(new Date())
                    .createdTime(new Date())
                    .isDeleted(0)
                    .build();
            courseStudentRepository.save(student);
            if (classIdHolder[0] > 0) {
                courseClassRepository.findById(classIdHolder[0]).ifPresent(cls -> {
                    cls.setStudentCount((int) courseStudentRepository.countByClassIdAndIsDeleted(classIdHolder[0], 0));
                    courseClassRepository.save(cls);
                });
            }
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.warn("【选课】重复选课: userId={}, courseId={}, classId={}", userId, courseId, classIdHolder[0]);
            return ResponseEntity.ok(ApiResponse.error(400, "已加入该课程"));
        }

        return ResponseEntity.ok(ApiResponse.success("加入成功", null));
    }

    @DeleteMapping("/my/courses/{courseId}/unenroll")
    public ResponseEntity<ApiResponse<Void>> unenrollCourse(@PathVariable Long courseId) {
        Long userId = getCurrentUserId();
        log.info("【退课】userId={}, courseId={}", userId, courseId);

        CourseStudent enrollment = courseStudentRepository
                .findByCourseIdAndUserIdAndIsDeleted(courseId, userId, 0)
                .stream().findFirst().orElse(null);
        if (enrollment == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "未加入该课程"));
        }

        Long classId = enrollment.getClassId();
        enrollment.setIsDeleted(1);        courseStudentRepository.save(enrollment);

        if (classId != null && classId > 0) {
            courseClassRepository.findById(classId).ifPresent(cls -> {
                cls.setStudentCount((int) courseStudentRepository.countByClassIdAndIsDeleted(classId, 0));
                courseClassRepository.save(cls);
            });
        }

        return ResponseEntity.ok(ApiResponse.success("退课成功", null));
    }

    @GetMapping("/my/course/{courseId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyCourseDetail(@PathVariable Long courseId) {
        Long userId = getCurrentUserId();

        Course course = courseRepository.findByIdAndIsDeleted(courseId, 0)
                .orElseThrow(() -> new com.example.login.exception.BusinessException(404, "课程不存在"));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("course", course);

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

    private String formatDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).format(DATE_FMT);
    }

    protected Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
